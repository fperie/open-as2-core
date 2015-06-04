package org.openas2.cmd;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.openas2.BaseComponent;

public class BaseCommandRegistry extends BaseComponent implements CommandRegistry
{
	@Nonnull
	private List<Command> commands = new ArrayList<>();

	@Override
	@Nonnull
	public List<Command> getCommands()
	{
		return commands;
	}

	public void setCommands(@Nonnull final List<Command> commands)
	{
		this.commands = commands;
	}
}
