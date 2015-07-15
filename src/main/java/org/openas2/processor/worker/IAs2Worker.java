package org.openas2.processor.worker;

import javax.annotation.Nonnull;

import org.openas2.Session;
import org.openas2.message.AS2Message;

/**
 * Allows to execute a custom behavior when a message was received.
 */
public interface IAs2Worker
{
	/**
	 * Process an operation to execute a custom behavior.
	 * 
	 * @param session current as2 session.
	 * @param msg
	 *        as2 message received.
	 */
	void processMessage(@Nonnull final Session session, @Nonnull final AS2Message msg);
}
