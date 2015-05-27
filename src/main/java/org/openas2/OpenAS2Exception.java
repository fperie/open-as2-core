package org.openas2;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.openas2.partner.XMLPartnershipFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OpenAS2Exception extends Exception
{
	/**
	 * Version of serialization.
	 */
	private static final long serialVersionUID = 1L;

	public static final String SOURCE_MESSAGE = "message";

	public static final String SOURCE_FILE = "file";

	/** Logger for the class. */
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLPartnershipFactory.class);

	@Nonnull
	private Map<String, Object> sources = new LinkedHashMap<>();

	public OpenAS2Exception()
	{
		log(false);
	}

	public OpenAS2Exception(String msg)
	{
		super(msg);
	}

	public OpenAS2Exception(String msg, Throwable cause)
	{
		super(msg, cause);
	}

	public OpenAS2Exception(Throwable cause)
	{
		super(cause);
	}

	public Object getSource(String id)
	{
		return getSources().get(id);
	}

	public void setSources(@Nonnull final Map<String, Object> sources)
	{
		this.sources = sources;
	}

	@Nonnull
	public Map<String, Object> getSources()
	{
		return sources;
	}

	public void addSource(String id, Object source)
	{
		getSources().put(id, source);
	}

	public void terminate()
	{
		log(true);
	}

	protected void log(boolean terminated)
	{
	}
}
