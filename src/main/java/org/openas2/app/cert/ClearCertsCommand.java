package org.openas2.app.cert;

import org.openas2.OpenAS2Exception;
import org.openas2.cert.AliasedCertificateFactory;
import org.openas2.cmd.CommandResult;

public class ClearCertsCommand extends AliasedCertCommand
{
	@Override
	public String getDefaultDescription()
	{
		return "Deletes all certificates from the store";
	}

	@Override
	public String getDefaultName()
	{
		return "clear";
	}

	@Override
	public String getDefaultUsage()
	{
		return "clear";
	}

	@Override
	public CommandResult execute(AliasedCertificateFactory certFx,
			Object[] params) throws OpenAS2Exception
	{
		synchronized (certFx)
		{
			certFx.clearCertificates();
			return new CommandResult(CommandResult.TYPE_OK, "cleared");
		}
	}
}
