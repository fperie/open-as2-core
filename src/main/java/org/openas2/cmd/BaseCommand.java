package org.openas2.cmd;

import java.util.Map;

import org.openas2.BaseComponent;
import org.openas2.OpenAS2Exception;
import org.openas2.Session;
import org.openas2.params.InvalidParameterException;

public abstract class BaseCommand extends BaseComponent implements Command
{
	public static final String PARAM_NAME = "name";

	public static final String PARAM_DESCRIPTION = "description";

	public static final String PARAM_USAGE = "usage";

	@Override
	public void init(Session session, Map parameters) throws OpenAS2Exception
	{
		super.init(session, parameters);
		if (getName() == null)
		{
			setName(getDefaultName());
		}

		if (getDescription() == null)
		{
			setDescription(getDefaultDescription());
		}
		if (getUsage() == null)
		{
			setUsage(getDefaultUsage());
		}
	}

	@Override
	public String getDescription()
	{
		try
		{
			return getParameter(PARAM_DESCRIPTION, false);
		}
		catch (InvalidParameterException e)
		{
			return null;
		}
	}

	@Override
	public String getName()
	{
		try
		{
			return getParameter(PARAM_NAME, false);
		}
		catch (InvalidParameterException e)
		{
			return null;
		}
	}

	@Override
	public String getUsage()
	{
		try
		{
			return getParameter(PARAM_USAGE, false);
		}
		catch (InvalidParameterException e)
		{
			return null;
		}
	}

	public abstract String getDefaultName();

	public abstract String getDefaultDescription();

	public abstract String getDefaultUsage();

	@Override
	public abstract CommandResult execute(Object[] params);

	@Override
	public void setDescription(String desc)
	{
		setParameter(PARAM_DESCRIPTION, desc);
	}

	@Override
	public void setName(String name)
	{
		setParameter(PARAM_NAME, name);
	}

	@Override
	public void setUsage(String usage)
	{
		setParameter(PARAM_USAGE, usage);
	}
}
