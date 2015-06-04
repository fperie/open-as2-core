package org.openas2.processor.worker;

import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerRegistrer
{
	/** Logger for the class. */
	private static final Logger LOGGER = LoggerFactory.getLogger(WorkerRegistrer.class);

	@Nonnull
	private static AtomicReference<IAs2Worker> arWorker = new AtomicReference<IAs2Worker>(null);

	private WorkerRegistrer()
	{

	}

	/**
	 * Allows to register specific worker to process a message.
	 * 
	 * @param worker
	 *        specific worker to register.
	 */
	public static void registerWorker(@Nonnull final IAs2Worker worker)
	{
		LOGGER.info("Register of worker following: " + worker.getClass().getName());
		arWorker.getAndSet(worker);
	}

	/**
	 * Return the specific worker to process message.
	 * 
	 * @return specific worker to process message.
	 */
	@Nullable
	public static IAs2Worker getWorker()
	{
		return arWorker.get();
	}
}
