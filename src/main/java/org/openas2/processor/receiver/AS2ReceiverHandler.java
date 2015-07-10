package org.openas2.processor.receiver;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.activation.DataHandler;
import javax.annotation.Nonnull;
import javax.mail.MessagingException;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeBodyPart;

import org.openas2.DispositionException;
import org.openas2.OpenAS2Exception;
import org.openas2.WrappedException;
import org.openas2.cert.CertificateFactory;
import org.openas2.lib.helper.ICryptoHelper;
import org.openas2.message.AS2Message;
import org.openas2.message.Message;
import org.openas2.message.MessageMDN;
import org.openas2.message.NetAttribute;
import org.openas2.partner.AS2Partnership;
import org.openas2.partner.ASXPartnership;
import org.openas2.partner.Partnership;
import org.openas2.processor.sender.SenderModule;
import org.openas2.processor.storage.StorageModule;
import org.openas2.processor.worker.IAs2Worker;
import org.openas2.processor.worker.WorkerRegistrer;
import org.openas2.util.AS2UtilOld;
import org.openas2.util.ByteArrayDataSource;
import org.openas2.util.DispositionType;
import org.openas2.util.HTTPUtil;
import org.openas2.util.IOUtilOld;
import org.openas2.util.Profiler;
import org.openas2.util.ProfilerStub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AS2ReceiverHandler implements NetModuleHandler
{
	private AS2ReceiverModule module;

	/** Logger for the class. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AS2ReceiverHandler.class);

	public AS2ReceiverHandler(AS2ReceiverModule module)
	{
		super();
		this.module = module;
	}

	public String getClientInfo(InetAddress remoteIp, int remotePort)
	{
		return " " + remoteIp.getHostAddress() + " " + remotePort;
	}

	public AS2ReceiverModule getModule()
	{
		return module;
	}

	@Override
	public void handle(NetModule owner, Socket s)
	{
		InputStream inputStream = null;
		OutputStream outputStream = null;

		try
		{
			inputStream = s.getInputStream();
			outputStream = s.getOutputStream();

		}
		catch (IOException ioe)
		{
			AS2Message msg = createMessage(s.getInetAddress(), s.getPort(), s.getLocalAddress(), s.getLocalPort());

			DispositionType dispositionType = new DispositionType("automatic-action", "MDN-sent-automatically ?",
					"processed", "Error", "unexpected-processing-error");

			DispositionException dispositionException = new DispositionException(dispositionType,
					"internal error occured to process the demand", ioe);

			getModule().handleError(msg, dispositionException);
			return;
		}

		handle(s.getInetAddress(), s.getPort(), s.getLocalAddress(), s.getLocalPort(), inputStream, outputStream);
	}

	@Override
	public void handle(final InetAddress remoteIp, final int remotePort, final InetAddress localIp,
			final int localPort,
			final InputStream inputStream, final OutputStream outputStream)
	{
		LOGGER.info("incoming connection {}", getClientInfo(remoteIp, remotePort));

		AS2Message msg = createMessage(remoteIp, remotePort, localIp, localPort);

		byte[] data = null;

		// Time the transmission
		ProfilerStub transferStub = Profiler.startProfile();

		// Read in the message request, headers, and data
		try
		{
			data = readMessage(inputStream, outputStream, msg);

		}
		catch (Exception e)
		{
			NetException ne = new NetException(remoteIp, remotePort, e);
			ne.terminate();
			LOGGER.error("impossible to read ciphered message", ne);
		}

		Profiler.endProfile(transferStub);

		if (data != null)
		{
			LOGGER.info("received {}{}{}", IOUtilOld.getTransferRate(data.length, transferStub),
					getClientInfo(remoteIp, remotePort),
					msg.getLoggingText());

			// TODO store HTTP request, headers, and data to file in Received folder -> use message-id for filename?
			try
			{
				// Put received data in a MIME body part
				ContentType receivedContentType = null;

				try
				{
					receivedContentType = new ContentType(msg.getHeader("Content-Type"));

					MimeBodyPart receivedPart = new MimeBodyPart();
					receivedPart.setDataHandler(new DataHandler(new ByteArrayDataSource(data, receivedContentType
							.toString(), null)));
					receivedPart.setHeader("Content-Type", receivedContentType.toString());
					msg.setData(receivedPart);
				}
				catch (Exception e)
				{
					throw new DispositionException(new DispositionType("automatic-action", "MDN-sent-automatically",
							"processed", "Error", "unexpected-processing-error"),
							AS2ReceiverModule.DISP_PARSING_MIME_FAILED, e);
				}

				// Extract AS2 ID's from header, find the message's partnership and update the message
				try
				{
					msg.getPartnership().setSenderID(AS2Partnership.PID_AS2, msg.getHeader("AS2-From"));
					msg.getPartnership().setReceiverID(AS2Partnership.PID_AS2, msg.getHeader("AS2-To"));

					getModule().getSession().getPartnershipFactory().updatePartnership(msg, false);
				}
				catch (OpenAS2Exception oae)
				{
					throw new DispositionException(new DispositionType("automatic-action", "MDN-sent-automatically",
							"processed", "Error", "authentication-failed"),
							AS2ReceiverModule.DISP_PARTNERSHIP_NOT_FOUND, oae);
				}

				// Decrypt and verify signature of the data, and attach data to the message
				decryptAndVerify(msg);

				// Process the received message
				try
				{
					getModule().getSession().getProcessor().handle(StorageModule.DO_STORE, msg, null);
				}
				catch (OpenAS2Exception oae)
				{
					throw new DispositionException(new DispositionType("automatic-action", "MDN-sent-automatically",
							"processed", "Error", "unexpected-processing-error"),
							AS2ReceiverModule.DISP_STORAGE_FAILED, oae);
				}
					
				final IAs2Worker worker = WorkerRegistrer.getWorker();

				if (worker == null)
				{
					LOGGER.debug("no specific worker, process by default");
				}
				else
				{
					LOGGER.debug("a specific worker has been found...");
					
					try
					{
						worker.processMessage(msg);
					} 
					catch (Exception e)
					{
						LOGGER.error("Error occured during the worker processing because : ", e);
					}
				}

				

				// Transmit a success MDN if requested
				try
				{
					if (msg.isRequestingMDN())
					{
						sendMDN(remoteIp, remotePort, outputStream, msg, new DispositionType("automatic-action",
								"MDN-sent-automatically", "processed"),
								AS2ReceiverModule.DISP_SUCCESS);
					}
					else
					{
						BufferedOutputStream out = new BufferedOutputStream(outputStream);
						HTTPUtil.sendHTTPResponse(out, HttpURLConnection.HTTP_OK, false);
						out.flush();
						out.close();
						LOGGER.info("sent HTTP OK {}{}", getClientInfo(remoteIp, remotePort), msg.getLoggingText());
					}
				}
				catch (Exception e)
				{
					throw new WrappedException("Error creating and returning MDN, message was stilled processed", e);
				}
			}
			catch (DispositionException de)
			{
				sendMDN(remoteIp, remotePort, outputStream, msg, de.getDisposition(), de.getText());
				getModule().handleError(msg, de);
			}
			catch (OpenAS2Exception oae)
			{
				getModule().handleError(msg, oae);
			}
		}
	}

	// Create a new message and record the source ip and port
	protected AS2Message createMessage(InetAddress remoteIp, int remotePort, InetAddress localIp, int localPort)
	{
		AS2Message msg = new AS2Message();

		msg.setAttribute(NetAttribute.MA_SOURCE_IP, localIp.toString());
		msg.setAttribute(NetAttribute.MA_SOURCE_PORT, remotePort + "");
		msg.setAttribute(NetAttribute.MA_DESTINATION_IP, remoteIp.toString());
		msg.setAttribute(NetAttribute.MA_DESTINATION_PORT, localPort + "");

		return msg;
	}

	protected byte[] readMessage(final InputStream inputStream, final OutputStream outputStream,
			@Nonnull final AS2Message msg) throws IOException, MessagingException
	{
		return HTTPUtil.readData(inputStream, outputStream, msg);
	}

	protected void decryptAndVerify(Message msg) throws OpenAS2Exception
	{
		CertificateFactory certFx = getModule().getSession().getCertificateFactory();
		ICryptoHelper ch;

		try
		{
			ch = AS2UtilOld.getCryptoHelper();
		}
		catch (Exception e)
		{
			throw new WrappedException(e);
		}

		try
		{
			if (ch.isEncrypted(msg.getData()))
			{
				// Decrypt
				LOGGER.debug("decrypting {}", msg.getLoggingText());

				X509Certificate receiverCert = certFx.getCertificate(msg, Partnership.PTYPE_RECEIVER);
				PrivateKey receiverKey = certFx.getPrivateKey(msg, receiverCert);
				msg.setData(AS2UtilOld.getCryptoHelper().decrypt(msg.getData(), receiverCert, receiverKey));
				new ContentType(msg.getData().getContentType());
			}
		}
		catch (Exception e)
		{
			throw new DispositionException(new DispositionType("automatic-action", "MDN-sent-automatically",
					"processed", "Error", "decryption-failed"), AS2ReceiverModule.DISP_DECRYPTION_ERROR, e);
		}

		try
		{
			if (ch.isSigned(msg.getData()))
			{
				LOGGER.debug("verifying signature {}", msg.getLoggingText());

				X509Certificate senderCert = certFx.getCertificate(msg, Partnership.PTYPE_SENDER);
				msg.setData(AS2UtilOld.getCryptoHelper().verify(msg.getData(), senderCert));
			}
		}
		catch (Exception e)
		{
			throw new DispositionException(new DispositionType("automatic-action", "MDN-sent-automatically",
					"processed", "Error", "integrity-check-failed"), AS2ReceiverModule.DISP_VERIFY_SIGNATURE_FAILED, e);
		}
	}

	protected void sendMDN(InetAddress remoteIp, int remotePort, OutputStream outputStream,
			AS2Message msg, DispositionType disposition, String text)
	{
		boolean mdnBlocked = false;

		mdnBlocked = (msg.getPartnership().getAttribute(ASXPartnership.PA_BLOCK_ERROR_MDN) != null);

		if (!mdnBlocked)
		{
			try
			{
				final MessageMDN mdn = AS2UtilOld.createMDN(getModule().getSession(), msg, disposition, text);

				BufferedOutputStream out;
				out = new BufferedOutputStream(outputStream);

				// if asyncMDN requested, close connection and initiate separate MDN send
				if (msg.isRequestingAsynchMDN())
				{
					populateResponse(out, mdn, 0);
					out.flush();
					out.close();
					LOGGER.info("setup to send asynch MDN [{}]{}{}", disposition.toString(),
							getClientInfo(remoteIp, remotePort),
							msg.getLoggingText());
					getModule().getSession().getProcessor().handle(SenderModule.DO_SENDMDN, msg, null);
					return;
				}

				// make sure to set the content-length header
				ByteArrayOutputStream data = new ByteArrayOutputStream();
				MimeBodyPart part = mdn.getData();
				IOUtilOld.copy(part.getInputStream(), data);
				populateResponse(out, mdn, data.size());
				populateResponseHeaders(mdn, out);

				data.writeTo(out);
				out.flush();
				out.close();

				// Save sent MDN for later examination
				getModule().getSession().getProcessor().handle(StorageModule.DO_STOREMDN, msg, null);
				LOGGER.info("sent MDN [{}] {}{}", disposition.toString(), getClientInfo(remoteIp, remotePort),
						msg.getLoggingText());
			}
			catch (Exception e)
			{
				WrappedException we = new WrappedException("Error sending MDN", e);
				we.addSource(OpenAS2Exception.SOURCE_MESSAGE, msg);
				we.terminate();

				LOGGER.error(we.getMessage(), e);
			}
		}
	}

	protected void populateResponse(OutputStream out, MessageMDN mdn, int size) throws IOException
	{
		HTTPUtil.sendHTTPResponse(out, HttpURLConnection.HTTP_OK, false);
		mdn.setHeader("Content-Length", Integer.toString(size));
		out.write(mdn.getHeader("Content-Length").getBytes());
	}

	protected void populateResponseHeaders(final MessageMDN mdn, BufferedOutputStream out) throws IOException
	{
		Enumeration<String> headers = mdn.getHeaders().getAllHeaderLines();
		String header;

		while (headers.hasMoreElements())
		{
			header = headers.nextElement() + "\r\n";
			out.write(header.getBytes());
		}

		out.write("\r\n".getBytes());
	}

}
