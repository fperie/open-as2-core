package org.openas2;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.annotation.Nonnull;

import org.openas2.params.InvalidParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BaseComponent implements Component
{
	/** Logger for the class. */
	private static final Logger LOGGER = LoggerFactory.getLogger(BaseComponent.class);

	@Nonnull
	private Map<String, Serializable> parameters = new LinkedHashMap<>();

	private Session session;

	@Override
	public String getName()
	{
		String clippedName = this.getClass().getName();

		// this clips off the package information
		StringTokenizer classParts = new StringTokenizer(clippedName, ".", false);

		while (classParts.hasMoreTokens())
		{
			clippedName = classParts.nextToken();
		}

		return clippedName;
	}

	public void setParameter(@Nonnull final String key, final String value)
	{
		getParameters().put(key, value);
	}

	public void setParameter(@Nonnull final String key, int value)
	{
		getParameters().put(key, value);
	}

	public String getParameter(@Nonnull final String key, boolean required) throws InvalidParameterException
	{
		String parameter = (String)getParameters().get(key);

		if (required && (parameter == null))
		{
			throw new InvalidParameterException(this, key, null);
		}

		return parameter;
	}

	public String getParameter(@Nonnull final String key, @Nonnull final String defaultValue)
	{
		try
		{
			return getParameter(key, false);
		}
		catch (InvalidParameterException ipe)
		{
			LOGGER.debug("the parameter with key: " + key + " has not value. Default value used: " + defaultValue + ".",
					ipe);
			return defaultValue;
		}
	}

	public int getParameterInt(@Nonnull final String key, final boolean required) throws InvalidParameterException
	{
		String value = getParameter(key, required);

		if (value != null)
		{
			return Integer.parseInt(value);
		}

		return 0;
	}

	@Override
	@Nonnull
	public Map<String, Serializable> getParameters()
	{
		return parameters;
	}

	@Override
	public Session getSession()
	{
		return session;
	}

	public void init(Session session, Map parameters) throws OpenAS2Exception
	{
		this.session = session;
		this.parameters = parameters;
	}

}
