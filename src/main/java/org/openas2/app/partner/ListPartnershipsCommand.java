package org.openas2.app.partner;

import java.util.Iterator;
import java.util.List;

import org.openas2.OpenAS2Exception;
import org.openas2.cmd.CommandResult;
import org.openas2.partner.Partnership;
import org.openas2.partner.PartnershipFactory;

/**
 * list partnerships in partnership store by names
 * 
 * @author joseph mcverry
 * 
 */
public class ListPartnershipsCommand extends AliasedPartnershipsCommand
{
	@Override
	public String getDefaultDescription()
	{
		return "List all partnerships in the current partnership store";
	}

	@Override
	public String getDefaultName()
	{
		return "list";
	}

	@Override
	public String getDefaultUsage()
	{
		return "list";
	}

	@Override
	public CommandResult execute(PartnershipFactory partFx, Object[] params)
			throws OpenAS2Exception
	{
		synchronized (partFx)
		{
			List parts = partFx.getPartnerships();
			Iterator partIt = parts.iterator();
			CommandResult cmdRes = new CommandResult(CommandResult.TYPE_OK);

			while (partIt.hasNext())
			{
				Partnership part = (Partnership) partIt.next();
				cmdRes.getResults().add(part.getName());
			}

			if (cmdRes.getResults().size() == 0)
			{
				cmdRes.getResults().add("No partnerships available");
			}

			return cmdRes;
		}
	}
}
