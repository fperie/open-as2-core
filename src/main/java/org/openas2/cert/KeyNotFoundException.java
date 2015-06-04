package org.openas2.cert;

import java.security.cert.X509Certificate;

import org.openas2.OpenAS2Exception;

public class KeyNotFoundException extends OpenAS2Exception
{
	/**
	 * Version of serialization.
	 */
	private static final long serialVersionUID = 1L;

	public KeyNotFoundException(X509Certificate cert, String alias)
	{
		this(cert, alias, null);
	}
	
	public KeyNotFoundException(X509Certificate cert, String alias, Throwable cause)
	{
		super("Certificate: " + cert + ", Alias: " + alias);
		initCause(cause);
	}
}
