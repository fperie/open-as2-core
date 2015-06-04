package org.openas2.lib.cert;

import org.openas2.lib.OpenAS2Exception;

public class CertificateException extends OpenAS2Exception
{

	/**
	 * Version of serialization.
	 */
	private static final long serialVersionUID = 1L;

	public CertificateException()
	{
		this((Throwable)null);
	}

	public CertificateException(String msg)
	{
		this(msg, null);
	}

	public CertificateException(Throwable cause)
	{
		this(null, cause);
	}

	public CertificateException(String msg, Throwable cause)
	{
		super(msg, cause);
	}

}
