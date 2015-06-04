package org.openas2.message;

import org.openas2.OpenAS2Exception;

public class InvalidMessageException extends OpenAS2Exception
{
	/**
	 * Version of serialization.
	 */
	private static final long serialVersionUID = 1L;

	public InvalidMessageException()
	{
		this(null);
	}

	public InvalidMessageException(String msg)
	{
		super(msg);
	}
}
