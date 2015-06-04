package org.openas2.app.partner;

import java.util.Iterator;

import org.openas2.OpenAS2Exception;
import org.openas2.cmd.CommandResult;
import org.openas2.partner.Partnership;
import org.openas2.partner.PartnershipFactory;

/**
 * view the partnership in the partnership stores
 * 
 * @author Don Hillsberry
 */
public class ViewPartnershipCommand extends AliasedPartnershipsCommand
{

	@Override
	public String getDefaultDescription()
	{
		return "View the partnership entry in partnership store.";
	}

	@Override
	public String getDefaultName()
	{
		return "view";
	}

	@Override
	public String getDefaultUsage()
	{
		return "view <name>";
	}

	@Override
	protected CommandResult execute(PartnershipFactory partFx, Object[] params)
			throws OpenAS2Exception
	{
		if (params.length < 1)
		{
			return new CommandResult(CommandResult.TYPE_INVALID_PARAM_COUNT,
					getUsage());
		}

		synchronized (partFx)
		{
			String name = params[0].toString();
			Iterator parts = partFx.getPartnerships().iterator();

			while (parts.hasNext())
			{
				Partnership part = (Partnership) parts.next();
				if (part.getName().equals(name))
				{
					return new CommandResult(CommandResult.TYPE_OK, part
							.toString());
				}
			}

			return new CommandResult(CommandResult.TYPE_ERROR,
					"Unknown partnership name");
		}
	}
}
