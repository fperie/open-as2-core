package org.openas2.processor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;

import org.openas2.OpenAS2Exception;

public class ProcessorException extends OpenAS2Exception
{
	/** Version of serialization. */
	private static final long serialVersionUID = 1L;

	private Processor processor;

	private List<Exception> causes = new ArrayList<>();

	public ProcessorException(Processor processor)
	{
		super();
		this.processor = processor;
	}

	public List<Exception> getCauses()
	{
		return causes;
	}

	public Processor getProcessor()
	{
		return processor;
	}

	public void setCauses(@Nonnull final List<Exception> list)
	{
		causes = list;
	}

	public void setProcessor(Processor processor)
	{
		this.processor = processor;
	}

	@Override
	public String getMessage()
	{
		StringWriter strWriter = new StringWriter();
		PrintWriter writer = new PrintWriter(strWriter);
		writer.print(super.getMessage());

		Iterator<Exception> causesIt = getCauses().iterator();

		while (causesIt.hasNext())
		{
			Exception e = causesIt.next();
			writer.println();
			e.printStackTrace(writer);

		}
		writer.flush();
		return strWriter.toString();
	}

}
