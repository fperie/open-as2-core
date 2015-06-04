package org.openas2.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.mail.Header;
import javax.mail.MessagingException;
import javax.mail.internet.InternetHeaders;
import javax.mail.internet.MimeBodyPart;

import org.openas2.OpenAS2Exception;
import org.openas2.WrappedException;
import org.openas2.partner.Partnership;

public abstract class BaseMessage implements Message
{
	private DataHistory history;

	private InternetHeaders headers;

	private Map attributes;

	private MessageMDN mdn;

	private MimeBodyPart data;

	private Partnership partnership;

	public BaseMessage()
	{
		super();
	}

	@Override
	public void setAttribute(String key, String value)
	{
		getAttributes().put(key, value);
	}

	@Override
	public String getAttribute(String key)
	{
		return (String)getAttributes().get(key);
	}

	@Override
	public void setAttributes(Map attributes)
	{
		this.attributes = attributes;
	}

	@Override
	public Map getAttributes()
	{
		if (attributes == null)
		{
			attributes = new HashMap();
		}

		return attributes;
	}

	@Override
	public void setContentType(String contentType)
	{
		setHeader("Content-Type", contentType);
	}

	@Override
	public String getContentType()
	{
		return getHeader("Content-Type");
	}

	/**
	 * @since 2007-06-01
	 * @param contentDisposition
	 */
	@Override
	public void setContentDisposition(String contentDisposition)
	{
		setHeader("Content-Disposition", contentDisposition);
	}

	/**
	 * @since 2007-06-01
	 * @return
	 */
	@Override
	public String getContentDisposition()
	{
		return getHeader("Content-Disposition");
	}

	@Override
	public void setData(MimeBodyPart data, DataHistoryItem historyItem)
	{
		this.data = data;

		if (data != null)
		{
			try
			{
				setContentType(data.getContentType());
			}
			catch (MessagingException e)
			{
				setContentType(null);
			}

			try
			{
				setContentDisposition(data.getHeader("Content-Disposition", null));
			}
			catch (MessagingException e)
			{
				setContentDisposition(null);
			}
		}

		if (historyItem != null)
		{
			getHistory().getItems().add(historyItem);
		}
	}

	@Override
	public DataHistoryItem setData(MimeBodyPart data) throws OpenAS2Exception
	{
		try
		{
			DataHistoryItem historyItem = new DataHistoryItem(data.getContentType());
			setData(data, historyItem);

			return historyItem;
		}
		catch (Exception e)
		{
			throw new WrappedException(e);
		}
	}

	@Override
	public MimeBodyPart getData()
	{
		return data;
	}

	@Override
	public void setHeader(String key, String value)
	{
		getHeaders().setHeader(key, value);
	}

	@Override
	public String getHeader(String key)
	{
		return getHeader(key, ", ");
	}

	@Override
	public String getHeader(String key, String delimiter)
	{
		return getHeaders().getHeader(key, delimiter);
	}

	@Override
	public void setHeaders(InternetHeaders headers)
	{
		this.headers = headers;
	}

	@Override
	public InternetHeaders getHeaders()
	{
		if (headers == null)
		{
			headers = new InternetHeaders();
		}

		return headers;
	}

	@Override
	public void setHistory(DataHistory history)
	{
		this.history = history;
	}

	@Override
	public DataHistory getHistory()
	{
		if (history == null)
		{
			history = new DataHistory();
		}

		return history;
	}

	@Override
	public void setMDN(MessageMDN mdn)
	{
		this.mdn = mdn;
	}

	@Override
	public MessageMDN getMDN()
	{
		return mdn;
	}

	@Override
	public void setMessageID(String messageID)
	{
		setHeader("Message-ID", messageID);
	}

	@Override
	public String getMessageID()
	{
		return getHeader("Message-ID");
	}

	@Override
	public void setPartnership(Partnership partnership)
	{
		this.partnership = partnership;
	}

	@Override
	public Partnership getPartnership()
	{
		if (partnership == null)
		{
			partnership = new Partnership();
		}

		return partnership;
	}

	@Override
	public abstract String generateMessageID();

	@Override
	public void setSubject(String subject)
	{
		setHeader("Subject", subject);
	}

	@Override
	public String getSubject()
	{
		return getHeader("Subject");
	}

	@Override
	public void addHeader(String key, String value)
	{
		getHeaders().addHeader(key, value);
	}

	@Override
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append("Message From:").append(getPartnership().getSenderIDs());
		buf.append("To:").append(getPartnership().getReceiverIDs());

		Enumeration headerEn = getHeaders().getAllHeaders();
		buf.append("\r\nHeaders:{");

		while (headerEn.hasMoreElements())
		{
			Header header = (Header)headerEn.nextElement();
			buf.append(header.getName()).append("=").append(header.getValue());

			if (headerEn.hasMoreElements())
			{
				buf.append(", ");
			}
		}

		buf.append("}");
		buf.append("\r\nAttributes:").append(getAttributes());

		MessageMDN mdn = getMDN();

		if (mdn != null)
		{
			buf.append("\r\nMDN:");
			buf.append(mdn.toString());
		}

		return buf.toString();
	}

	@Override
	public void updateMessageID()
	{
		setMessageID(generateMessageID());
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException
	{
		// read in partnership
		partnership = (Partnership)in.readObject();

		// read in attributes
		attributes = (Map)in.readObject();

		// read in data history
		history = (DataHistory)in.readObject();

		try
		{
			// read in message headers
			headers = new InternetHeaders(in);

			// read in mime body
			if (in.read() == 1)
			{
				data = new MimeBodyPart(in);
			}
		}
		catch (MessagingException me)
		{
			throw new IOException("Messaging exception: " + me.getMessage(), me);
		}

		// read in MDN
		mdn = (MessageMDN)in.readObject();

		if (mdn != null)
		{
			mdn.setMessage(this);
		}
	}

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException
	{
		// write partnership info
		out.writeObject(partnership);

		// write attributes
		out.writeObject(attributes);

		// write data history
		out.writeObject(history);

		// write message headers
		Enumeration en = headers.getAllHeaderLines();

		while (en.hasMoreElements())
		{
			out.writeBytes(en.nextElement().toString() + "\r\n");
		}

		out.writeBytes(new String("\r\n"));

		// write the mime body
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try
		{
			if (data != null)
			{
				baos.write(1);
				data.writeTo(baos);
			}
			else
			{
				baos.write(0);
			}
		}
		catch (MessagingException e)
		{
			throw new IOException("Messaging exception: " + e.getMessage(), e);
		}

		out.write(baos.toByteArray());
		baos.close();

		// write the message's MDN
		out.writeObject(mdn);
	}

	@Override
	public String getLoggingText()
	{
		return " [" + getMessageID() + "]";
	}
}
