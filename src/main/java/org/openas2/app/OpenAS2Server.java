package org.openas2.app;

import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
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

			session = createSession(args);
			assert session != null;
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

	private XMLSession createSession(String[] args) throws Exception
	{
		XMLSession session;

		if (args.length == 0)
		{
			errorConfFileNotExists("The parameter with the configuration file is not specified.");
			return null;
		}
		else
		{
			String path = args[0];
			
			if (StringUtils.isBlank(path))
			{
				errorConfFileNotExists("The parameter with the configuration file is blank.");
			}

			final InputStream is;
			if (StringUtils.startsWithIgnoreCase(path, "classpath:"))
			{
				is = getClass().getResourceAsStream(StringUtils.substring(path, 10));
				
				if (is == null)
				{
					errorConfFileNotExists("The parameter with the configuration file does not contain a path to an existing file readable...");
				}
				session = new XMLSession(is, args.length > 1 ? args[1] : null);
			}
			else
			{
				File pathFile = new File(path);
				if (!pathFile.isFile() || !pathFile.exists() || !pathFile.canRead())
				{
					errorConfFileNotExists("The parameter with the configuration file does not contain a path to an existing file readable...");
				}
				
				is = FileUtils.openInputStream(pathFile);
				session = new XMLSession(is, pathFile.getParent());
			}

			if (is != null )
			{
				is.close();
			}
		}
		return session;
	}

	private void errorConfFileNotExists(String errorMessage) throws Exception {
		LOGGER.error(errorMessage);
		LOGGER.info("Usage:");
		LOGGER.info("java org.openas2.app.OpenAS2Server <configuration file>");
		throw new Exception("Missing configuration file");
	}
}
