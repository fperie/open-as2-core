package org.openas2;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;

public class OpenAS2Exception extends Exception
{
	/**
	 * Version of serialization.
	 */
	private static final long serialVersionUID = 1L;

	public static final String SOURCE_MESSAGE = "message";

	public static final String SOURCE_FILE = "file";

	@Nonnull
	private final Map<String, Object> sources = new LinkedHashMap<>();

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
		// do nothing !!!
	}
}
