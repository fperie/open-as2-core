package org.openas2.app;

import java.io.BufferedWriter;
import java.util.List;

import org.openas2.OpenAS2Exception;
import org.openas2.Session;
import org.openas2.XMLSession;
import org.openas2.cmd.CommandManager;
import org.openas2.cmd.CommandRegistry;
import org.openas2.cmd.processor.BaseCommandProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * original author unknown
 * 
 * in this release added ability to have multiple command processors.
 * 
 * @author joseph mcverry
 */
public class OpenAS2Server 
{
	/** Logger for the class. */
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenAS2Server.class);
	
	protected BufferedWriter sysOut;

	
	public static void main(String[] args) {
		OpenAS2Server server = new OpenAS2Server();
		server.start(args);
	}

	public void start(String[] args) {
		BaseCommandProcessor cmd = null;
		XMLSession session = null;

		try 
		{
			LOGGER.info(Session.TITLE);
			LOGGER.info("Starting Server...");

			// create the OpenAS2 Session object
			// this is used by all other objects to access global configs and functionality
			LOGGER.info("Loading configuration...");

			if (args.length > 0) {
				session = new XMLSession(args[0]);
			} else {
				LOGGER.info("Usage:");
				LOGGER.info("java org.openas2.app.OpenAS2Server <configuration file>");
				throw new Exception("Missing configuration file");
			}
			// create a command processor

			// get a registry of Command objects, and add Commands for the Session
			LOGGER.info("Registering Session to Command Processor...");

			CommandRegistry reg = session.getCommandRegistry();

			// start the active processor modules
			LOGGER.info("Starting Active Modules...");
			session.getProcessor().startActiveModules();

			// enter the command processing loop
			LOGGER.info("OpenAS2 Started");

			
			CommandManager cmdMgr = session.getCommandManager();
			List processors = cmdMgr.getProcessors();
			for (int i = 0; i < processors.size(); i++) {
				LOGGER.info("Loading Command Processor..." + processors.toString());
				cmd = (BaseCommandProcessor) processors.get(i);
				cmd.init();
				cmd.addCommands(reg);
				cmd.start();
			}
			breakOut : while (true) {
				for (int i = 0; i < processors.size(); i++) {
					cmd = (BaseCommandProcessor) processors.get(i);
					if (cmd.isTerminated())
						break breakOut;
					Thread.sleep(100); 
				}
			}
			LOGGER.info("- OpenAS2 Stopped -");
		} 
		catch (Exception e) 
		{
			LOGGER.error("Exception occurred during the getting started: ", e);
		} 
		catch (Error err) 
		{
			LOGGER.error("Error occurred during the getting started: ", err);
		} 
		finally 
		{

			if (session != null) 
			{
				try {
					session.getProcessor().stopActiveModules();
				} catch (OpenAS2Exception same) {
					same.terminate();
				}
			}

			if (cmd != null) {
				try {
					cmd.deInit();
				} catch (OpenAS2Exception cdie) {
					cdie.terminate();
				}
			}

			LOGGER.info("OpenAS2 has shut down\r\n");
			System.exit(0);
		}
	}
}
