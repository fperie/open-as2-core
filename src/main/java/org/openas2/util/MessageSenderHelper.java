package org.openas2.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.mail.internet.MimeBodyPart;

import org.apache.commons.lang3.StringUtils;
import org.openas2.Session;
import org.openas2.message.AS2Message;
import org.openas2.partner.AS2Partnership;
import org.openas2.processor.sender.AS2SenderModule;
import org.openas2.processor.sender.SenderModule;

/**
 * Utility class to send easy a message in as2.
 * 
 * @author Fabien PÉRIÉ
 */
public class MessageSenderHelper 
{
	
	/**
	 * Default constructor.
	 */
	public MessageSenderHelper()
	{
		
	}
	
	/**
	 * Send a message.
	 * 
	 * @param idAs2Sender identifiant as2 of sender.
	 * @param idAs2Receiver identifiant as2 of receiver.
	 * @param messageId identifiant of message. 
	 * @param subject subject of message.
	 * @param body content of message.
	 * @param session as2 session.
	 * @throws Exception exception occured during the processing.
	 */
	public void sendMessage(@Nonnull final String idAs2Sender, @Nonnull final String idAs2Receiver, 
		@Nonnull final String messageId, @Nonnull final String subject, 
		@Nonnull final MimeBodyPart body, @Nonnull final Session session) throws Exception
	{
		final AS2Message as2 = new AS2Message();
		as2.setContentType(body.getContentType());
	
		as2.getPartnership().setSenderID(AS2Partnership.PID_AS2, idAs2Sender);
		as2.getPartnership().setReceiverID(AS2Partnership.PID_AS2, idAs2Receiver);
		session.getPartnershipFactory().updatePartnership(as2, false);
		
		as2.setMessageID(messageId);
		as2.setSubject(subject);
		as2.setData(body);
		
		final AS2SenderModule senderModule = new AS2SenderModule();
		senderModule.init(session, null);
		senderModule.handle(SenderModule.DO_SEND, as2, null);
	}
	
	
	/**
	 * Prepare message body to send.
	 * 
	 * @param strMessage message to send.
	 * @param subContentType sub content type of message (ex: plain, xml).
	 * @return mime body part which contains the prepared message.
	 * @throws Exception if an exception occured during the building of message body. 
	 */
	@Nonnull
	public MimeBodyPart prepareMessage(@Nonnull final String strMessage, @Nullable final String subContentType) throws Exception
	{
		final String encoding = "UTF-8";
        final MimeBodyPart body = new MimeBodyPart();
        body.setText(strMessage, encoding, StringUtils.defaultIfBlank(subContentType, "plain"));
        body.setHeader("Content-Transfer-Encoding", encoding);
		return body;
	}
}
