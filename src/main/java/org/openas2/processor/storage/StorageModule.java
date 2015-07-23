package org.openas2.processor.storage;

import org.openas2.processor.ProcessorModule;

public interface StorageModule extends ProcessorModule
{
	public static final String DO_STORE = "store";

	public static final String DO_STOREMDN = "store_mdn";

	public static final String DO_ARCHIVE = "store_archive";

	public static final String DO_STORE_ERROR = "store_error";
}
