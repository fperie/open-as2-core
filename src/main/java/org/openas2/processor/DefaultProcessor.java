package org.openas2.processor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openas2.OpenAS2Exception;
import org.openas2.message.Message;

public class DefaultProcessor extends BaseProcessor
{
	@Nonnull
	private List<ProcessorModule> modules = new ArrayList<>();

	@Override
	@Nonnull
	public List<ActiveModule> getActiveModules()
	{
		List<ActiveModule> activeMods = new ArrayList<>();
		Iterator<ProcessorModule> moduleIt = getModules().iterator();
		ProcessorModule procMod;

		while (moduleIt.hasNext())
		{
			procMod = moduleIt.next();

			if (procMod instanceof ActiveModule)
			{
				activeMods.add((ActiveModule)procMod);
			}
		}

		return activeMods;
	}

	@Override
	public void setModules(@Nonnull final List<ProcessorModule> modules)
	{
		this.modules = modules;
	}

	@Override
	@Nonnull
	public List<ProcessorModule> getModules()
	{
		return modules;
	}

	@Override
	public void handle(@Nonnull final String action, @Nonnull final Message msg,
			@Nullable final Map<String, Object> options) throws OpenAS2Exception
	{
		Iterator<ProcessorModule> moduleIt = getModules().iterator();
		ProcessorModule module;
		ProcessorException pex = null;
		boolean moduleFound = false;

		while (moduleIt.hasNext())
		{
			module = moduleIt.next();

			if (module.canHandle(action, msg, options))
			{
				try
				{
					moduleFound = true;
					module.handle(action, msg, options);
				}
				catch (OpenAS2Exception oae)
				{
					if (pex == null)
					{
						pex = new ProcessorException(this);
						pex.getCauses().add(oae);
					}
				}
			}
		}

		if (pex != null)
		{
			throw pex;
		}
		else if (!moduleFound)
		{
			throw new NoModuleException(action, msg, options);
		}
	}

	@Override
	public void startActiveModules()
	{
		Iterator<ActiveModule> activeIt = getActiveModules().iterator();

		while (activeIt.hasNext())
		{
			try
			{
				((ActiveModule)activeIt.next()).start();
			}
			catch (OpenAS2Exception e)
			{
				e.terminate();
			}
		}
	}

	@Override
	public void stopActiveModules()
	{
		Iterator<ActiveModule> activeIt = getActiveModules().iterator();

		while (activeIt.hasNext())
		{
			try
			{
				((ActiveModule)activeIt.next()).stop();
			}
			catch (OpenAS2Exception e)
			{
				e.terminate();
			}
		}
	}
}
