package org.openas2.lib.util;

import org.dom4j.Element;

public class XMLException extends Exception
{
	/**
	 * Version of serialization.
	 */
	private static final long serialVersionUID = 1L;

	private Element element;

	public XMLException()
	{
		this(null, (Throwable)null);
	}

	public XMLException(Element element, String message)
	{
		this(element, message, null);
	}

	public XMLException(Element element, Throwable cause)
	{
		this(element, null, cause);
	}

	public XMLException(Element element, String message, Throwable cause)
	{
		super(message, cause);
		this.element = element;
	}

	public String getMessage()
	{
		StringBuffer msgBuf = new StringBuffer();
		String superMsg = super.getMessage();
		if (superMsg != null)
		{
			msgBuf.append(superMsg);
		}
		if (element != null)
		{
			if (msgBuf.length() > 0)
			{
				msgBuf.append(" - ");
			}
			msgBuf.append(element.asXML());
		}

		return msgBuf.toString();
	}

	public Element getElement()
	{
		return element;
	}

	public void setElement(Element element)
	{
		this.element = element;
	}
}
