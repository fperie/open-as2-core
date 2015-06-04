package org.openas2.message;

public class AS1Message extends BaseMessage
{
	public static final String PROTOCOL_AS1 = "as1";

	@Override
	public String getProtocol()
	{
		return PROTOCOL_AS1;
	}

	@Override
	public boolean isRequestingMDN()
	{
		return false;
	}

	@Override
	public boolean isRequestingAsynchMDN()
	{
		return false;
	}

	@Override
	public String generateMessageID()
	{
		return null;
	}
}
