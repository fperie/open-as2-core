package org.openas2.cmd.processor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openas2.Component;
import org.openas2.OpenAS2Exception;
import org.openas2.Session;
import org.openas2.cmd.Command;
import org.openas2.cmd.CommandRegistry;

public abstract class BaseCommandProcessor extends Thread implements CommandProcessor, Component
{
	@Override
	@Nonnull
	public Map<String, Serializable> getParameters()
	{
		return new LinkedHashMap<>();
	}

	@Override
	public Session getSession()
	{
		return null;
	}

	@Override
	public void init(Session session, Map parameters) throws OpenAS2Exception
	{

	}

	@Nonnull
	private List<Command> commands = new ArrayList<>();

	private boolean terminated;

	public BaseCommandProcessor()
	{
		super();
		terminated = false;
	}

	public void setCommands(@Nonnull final List<Command> list)
	{
		commands = list;
	}

	@Override
	@Nonnull
	public List<Command> getCommands()
	{
		return commands;
	}

	@Nullable
	public Command getCommand(String name)
	{
		Command currentCmd;
		Iterator<Command> commandIt = getCommands().iterator();
		while (commandIt.hasNext())
		{
			currentCmd = commandIt.next();
			if (currentCmd.getName().equals(name))
			{
				return currentCmd;
			}
		}
		return null;
	}

	@Override
	public boolean isTerminated()
	{
		return terminated;
	}

	@Override
	public void processCommand() throws OpenAS2Exception
	{
		throw new OpenAS2Exception("super class method call, not initialized correctly");
	}

	@Override
	public void addCommands(CommandRegistry reg)
	{
		List<Command> regCmds = reg.getCommands();

		if (regCmds.size() > 0)
		{
			getCommands().addAll(regCmds);
		}
	}

	@Override
	public void terminate()
	{
		terminated = true;
	}
}
