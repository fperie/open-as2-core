package org.openas2;

public class ComponentNotFoundException extends OpenAS2Exception
{
	/**
	 * Version of serialization.
	 */
	private static final long serialVersionUID = 1L;

	private final String componentName;

	public ComponentNotFoundException(String componentName)
	{
		super(componentName);
		this.componentName = componentName;
	}

	public String getComponentName()
	{
		return componentName;
	}
}
