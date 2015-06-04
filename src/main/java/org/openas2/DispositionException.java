package org.openas2;

import org.openas2.util.DispositionType;

public class DispositionException extends OpenAS2Exception
{
	/**
	 * Version de sérialisation.
	 */
	private static final long serialVersionUID = 1L;
	
	private final DispositionType disposition;

	private String text;

	public DispositionException(DispositionType disposition, String text)
	{
		this(disposition, text, null);
	}
	
	public DispositionException(DispositionType disposition, String text, Throwable cause)
	{
		super(disposition.toString());
		this.disposition = disposition;
		this.text = text;
		initCause(cause);
	}
	
	public DispositionType getDisposition()
	{
		return disposition;
	}

	public String getText()
	{
		return text;
	}

	public void setText(String text)
	{
		this.text = text;
	}

}
