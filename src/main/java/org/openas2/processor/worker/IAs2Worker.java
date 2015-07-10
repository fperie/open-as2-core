package org.openas2.processor.worker;

import javax.annotation.Nonnull;

import org.openas2.message.AS2Message;

/**
 * Allows to execute a custom behavior when a message was received.
 */
public interface IAs2Worker
{
	/**
	 * Process an operation to execute a custom behavior.
	 * 
	 * @param msg
	 *        as2 message received.
	 */
	void processMessage(final @Nonnull AS2Message msg);
}
