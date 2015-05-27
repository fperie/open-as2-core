package org.openas2.cmd;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openas2.OpenAS2Exception;
import org.openas2.Session;

public class MultiCommand extends BaseCommand
{
	@Nonnull
	private List<Command> cmds = new ArrayList<>();

	@Override
	public void init(Session session, Map parameters) throws OpenAS2Exception
	{
		super.init(session, parameters);
		getParameter(PARAM_NAME, true);
		getParameter(PARAM_DESCRIPTION, true);
		if (getUsage() == null)
		{
			setUsage(getName() + " <command> <parameters>");
		}
	}

	@Nullable
	public Command getCommand(String name)
	{
		name = name.toLowerCase();

		List<Command> commands = getCommands();
		Command cmd;

		for (int i = 0; i < commands.size(); i++)
		{
			cmd = commands.get(i);

			if (cmd.getName().equals(name))
			{
				return cmd;
			}
		}

		return null;
	}

	@Nonnull
	public List<Command> getCommands()
	{
		return cmds;
	}

	@Nullable
	public String getDescription(String name)
	{
		Command cmd = getCommand(name);

		if (cmd != null)
		{
			return cmd.getDescription();
		}

		return null;
	}

	@Nullable
	public String getUsage(String name)
	{
		Command cmd = getCommand(name);

		if (cmd != null)
		{
			return cmd.getUsage();
		}

		return null;
	}

	@Override
	public CommandResult execute(Object[] params)
	{
		if (params.length > 0)
		{
			String subName = params[0].toString();
			Command subCmd = getCommand(subName);

			if (subCmd != null)
			{
				List<Object> paramList = Arrays.asList(params);
				List<Object> subParams = new ArrayList<Object>(paramList);

				subParams.remove(0);

				return subCmd.execute(subParams.toArray());
			}
		}

		CommandResult listCmds = new CommandResult(CommandResult.TYPE_ERROR, "List of valid subcommands:");
		Iterator<Command> cmdIt = getCommands().iterator();
		Command currentCmd;

		while (cmdIt.hasNext())
		{
			currentCmd = cmdIt.next();
			listCmds.getResults().add(currentCmd.getName());
		}

		return listCmds;
	}

	public CommandResult execute(String name, Object[] params) throws OpenAS2Exception
	{
		Command cmd = getCommand(name);

		if (cmd != null)
		{
			return cmd.execute(params);
		}
		throw new CommandException("Command doesn't exist: " + name);
	}

	public boolean supports(String name)
	{
		return getCommand(name) != null;
	}

	@Override
	@Nullable
	public String getDefaultName()
	{
		return null;
	}

	@Override
	@Nullable
	public String getDefaultDescription()
	{
		return null;
	}

	@Override
	@Nullable
	public String getDefaultUsage()
	{
		return null;
	}
}
