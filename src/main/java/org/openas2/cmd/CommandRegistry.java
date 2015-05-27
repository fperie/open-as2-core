package org.openas2.cmd;

import java.util.List;

import javax.annotation.Nonnull;

import org.openas2.Component;

public interface CommandRegistry extends Component
{
	@Nonnull
	List<Command> getCommands();
}
