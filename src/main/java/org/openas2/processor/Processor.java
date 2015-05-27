package org.openas2.processor;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.openas2.Component;
import org.openas2.OpenAS2Exception;
import org.openas2.message.Message;

public interface Processor extends Component
{
	public static final String COMPID_PROCESSOR = "processor";

	public void handle(@Nonnull final String action, @Nonnull final Message msg,
			@Nullable final Map<String, Object> options) throws OpenAS2Exception;

	public List<ProcessorModule> getModules();

	public void setModules(@Nonnull final List<ProcessorModule> modules);

	public void startActiveModules();

	public void stopActiveModules();

	public List<ActiveModule> getActiveModules();
}
