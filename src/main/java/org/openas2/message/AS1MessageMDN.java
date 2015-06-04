package org.openas2.message;

public class AS1MessageMDN extends BaseMessageMDN
{
	public AS1MessageMDN(AS1Message msg)
	{
		super(msg);
	}

	@Override
	public String generateMessageID()
	{
		return null;
	}
}
