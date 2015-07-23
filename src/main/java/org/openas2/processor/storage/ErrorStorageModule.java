package org.openas2.processor.storage;

public class ErrorStorageModule extends MessageFileModule
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getModuleAction()
	{
		return DO_STORE_ERROR;
	}
}
