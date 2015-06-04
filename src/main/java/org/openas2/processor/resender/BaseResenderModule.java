package org.openas2.processor.resender;

import java.util.Timer;
import java.util.TimerTask;

import org.openas2.OpenAS2Exception;
import org.openas2.processor.BaseActiveModule;

public abstract class BaseResenderModule extends BaseActiveModule implements ResenderModule
{
	public static final int TICK_INTERVAL = 30 * 1000;

	private Timer timer;

	public abstract void resend();

	@Override
	public void doStart() throws OpenAS2Exception
	{
		timer = new Timer(true);
		timer.scheduleAtFixedRate(new PollTask(), 0, TICK_INTERVAL);
	}

	@Override
	public void doStop() throws OpenAS2Exception
	{
		if (timer != null)
		{
			timer.cancel();
			timer = null;
		}
	}

	private class PollTask extends TimerTask
	{
		@Override
		public void run()
		{
			resend();
		}
	}
}
