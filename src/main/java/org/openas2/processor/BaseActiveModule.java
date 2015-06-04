package org.openas2.processor;

import java.util.Map;

import org.openas2.OpenAS2Exception;
import org.openas2.message.Message;

public abstract class BaseActiveModule extends BaseProcessorModule implements ActiveModule
{
	private boolean running;

	@Override
	public boolean isRunning()
	{
		return running;
	}

	public abstract void doStart() throws OpenAS2Exception;

	public abstract void doStop() throws OpenAS2Exception;

	@Override
	public boolean canHandle(String action, Message msg, Map options)
	{
		return false;
	}

	public void forceStop(Exception cause)
	{
		try
		{
			throw new ForcedStopException(cause);
		}
		catch (ForcedStopException fse)
		{
			fse.terminate();
		}

		try
		{
			stop();
		}
		catch (OpenAS2Exception oae)
		{
			oae.terminate();
		}
	}

	@Override
	public void handle(String action, Message msg, Map options)
			throws OpenAS2Exception
	{
		throw new UnsupportedException("Active modules don't handle anything by default");
	}

	@Override
	public void start() throws OpenAS2Exception
	{
		setRunning(true);
		doStart();
	}

	@Override
	public void stop() throws OpenAS2Exception
	{
		setRunning(false);
		doStop();
	}

	@Override
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(getClass().getName() + ": " + getParameters());

		return buf.toString();
	}

	private void setRunning(boolean running)
	{
		this.running = running;
	}
}
