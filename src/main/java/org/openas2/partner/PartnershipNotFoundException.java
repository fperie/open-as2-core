package org.openas2.partner;

import org.openas2.OpenAS2Exception;

public class PartnershipNotFoundException extends OpenAS2Exception
{

	/**
	 * Version of serialization.
	 */
	private static final long serialVersionUID = 1L;

	public PartnershipNotFoundException(Partnership p)
	{
		this("Partnership not found: " + p);
	}

	public PartnershipNotFoundException(String msg)
	{
		super(msg);
	}



}
