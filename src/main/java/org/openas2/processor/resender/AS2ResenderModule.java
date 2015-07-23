package org.openas2.processor.resender;

import java.util.Map;

import org.openas2.OpenAS2Exception;
import org.openas2.message.AS2Message;
import org.openas2.message.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class use to resend an AS2Message.
 * 
 * @author MARY Olivier.
 */
public class AS2ResenderModule extends BaseResenderModule
{
	/** Logger for the class. */
	private static final Logger LOGGER = LoggerFactory.getLogger(AS2ResenderModule.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canHandle(String action, Message msg, Map options)
	{
		if (!action.equals(ResenderModule.DO_RESEND))
		{
			return false;
		}

		return (msg instanceof AS2Message);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings({ "unchecked", "null" })
	@Override
	public void handle(String action, Message msg, Map options) throws OpenAS2Exception
	{
		LOGGER.info("message resubmitted {}", msg.getLoggingText());

		int retries = retries(options);
		LOGGER.info("retries remaining [{}]", retries >= 0 ? retries : "infinite");

		LOGGER.info("retries cause [{}]", options.get(ResenderModule.OPTION_CAUSE));
		LOGGER.info("retries from initial sender [{}]", options.get(ResenderModule.OPTION_INITIAL_SENDER));
		LOGGER.info("retries from methods [{}]", options.get(ResenderModule.OPTION_RESEND_METHOD));

		if (!(msg instanceof AS2Message))
		{
			throw new OpenAS2Exception("Can't resend non-AS2 message");
		}

		try
		{
			// wait for retry
			sleep(options);
			// it will recall resend if fail again and retries != 0.
			getSession().getProcessor().handle((String)options.get(ResenderModule.OPTION_RESEND_METHOD), msg, options);
		}
		catch (Exception e)
		{
			LOGGER.error("resubmit impossible cause : {}", e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resend()
	{
		// do nothing.
	}

	/**
	 * Get number of retries remaining.
	 * 
	 * @param options
	 *        Map which contains options.
	 * @return number of retries remaining.
	 */
	protected int retries(Map options)
	{
		String left;
		if (options == null || (left = (String)options.get(ResenderModule.OPTION_RETRIES)) == null)
		{
			try
			{
				left = getParameter(ResenderModule.OPTION_RETRIES, false);
			}
			catch (OpenAS2Exception e)
			{
				// Can't actualy happen, but try convincing Java of that.
				// *FIXME* should have two versions of getParameter, one that can't throw.
				left = null;
				LOGGER.debug("One error occured, left variable has been assigned to null.", e);
			}

			if (left == null)
			{
				left = "0";
			}
		}

		return Integer.parseInt(left);
	}

	/**
	 * Sleep some duration.
	 * 
	 * @param options
	 *        Map which contains options.
	 * @throws InterruptedException
	 * @throws NumberFormatException
	 */
	protected void sleep(Map options) throws NumberFormatException, InterruptedException
	{
		String sleepDuration;
		int iSleepDuration = 0;
		if (options == null
				|| (sleepDuration = (String)options.get(ResenderModule.OPTION_SLEEP_DURATION)) == null)
		{
			try
			{
				sleepDuration = getParameter(ResenderModule.OPTION_SLEEP_DURATION, false);
			}
			catch (OpenAS2Exception e)
			{
				// Can't actualy happen, but try convincing Java of that.
				// *FIXME* should have two versions of getParameter, one that can't throw.
				sleepDuration = null;
				LOGGER.debug("One error occured, left variable has been assigned to null.", e);
			}

			if (sleepDuration == null)
			{
				iSleepDuration = ResenderModule.OPTION_MAX_SLEEP_DURATION;
			}
			else
			{
				iSleepDuration = Integer.parseInt(sleepDuration);
			}

			if (iSleepDuration > ResenderModule.OPTION_MAX_SLEEP_DURATION || iSleepDuration <= 0)
			{
				iSleepDuration = ResenderModule.OPTION_MAX_SLEEP_DURATION;
			}

			LOGGER.info("retries waiting [{} milliseconds]", iSleepDuration);
			Thread.sleep(iSleepDuration);
		}
	}

	@Override
	public void doStart() throws OpenAS2Exception
	{
		throw new IllegalArgumentException("Forbidden to start a thread!");
	}

	@Override
	public void doStop() throws OpenAS2Exception
	{
		throw new IllegalArgumentException("Forbidden to stop a thread!");
	}

}
