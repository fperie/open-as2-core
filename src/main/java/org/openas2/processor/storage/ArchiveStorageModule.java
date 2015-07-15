package org.openas2.processor.storage;

public class ArchiveStorageModule extends MessageFileModule
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getModuleAction()
	{
		return DO_ARCHIVE;
	}
}
