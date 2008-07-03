package com.awebstorm.loadgenerator.robot;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jbehave.core.behaviour.Behaviours;




/**
 * Test the behaviour of the HTMLRobot class
 * @author Cromano
 *
 */
public class HTMLRobotBehaviour {
	
	private HTMLRobot newRobot;
	private Logger consoleLog;
	private Logger resultLog;
	private String scriptLocation;
	private static URL scheduler;
	private PropertyResourceBundle loadGeneratorProperties;
	private static final String LOAD_GEN_PROPS_LOC = "LoadGenerator";
	private static final String LOAD_GEN_LOG_PROPS_LOC = "log4j.properties";
	
	public void shouldGenerateGoodResults() {
		
		newRobot.run();
	}
	
/*	public void shouldThrowScriptNotFound( String scriptLocation ) throws Exception {

		scriptLocation = "";
		
	}*/
	
	public void shouldUseDefaultPreferences() throws Exception {
		
		scriptLocation = "Script.xml";
		
	}
	
	public void shouldAppendGoodResults() {

		scriptLocation = "Script.xml";
		
	}
	
	/**
	 * Incomplete - Unknown implementation
	 */
	public void shouldKillRobot() {
		
		scriptLocation = "Script.xml";
		
	}
	
	public void setUp() {
		scriptLocation = "Script.xml";


		consoleLog = Logger.getLogger("loadgenerator.consoleLog");
		resultLog = Logger.getLogger("loadgenerator.consoleLog.resultLog");
		PropertyConfigurator.configureAndWatch(LOAD_GEN_LOG_PROPS_LOC);
		
		if( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Logs configured.");
		}
		
		//Load Properties
		loadGeneratorProperties = (PropertyResourceBundle) ResourceBundle.getBundle(LOAD_GEN_PROPS_LOC);

		try {
			scheduler = new URL (
					loadGeneratorProperties.getString("schedulerProtocol"),
					loadGeneratorProperties.getString("schedulerHost"),
					Integer.parseInt(loadGeneratorProperties.getString("schedulerPort")),
					loadGeneratorProperties.getString("schedulerFile")
			);
		} catch (NumberFormatException e) {
			// Bad port number
			consoleLog.fatal("Bad Port Number receieved from properties.", e);
			e.printStackTrace();
			System.exit(3);
		} catch (MalformedURLException e) {
			// Bad URL parameters
			consoleLog.fatal("Bad URL parameters received from properties.", e);
			e.printStackTrace();
			System.exit(3);
		}
		
		if( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Properties configured.");
		}
		
		newRobot = new HTMLRobot(scriptLocation,consoleLog,resultLog);
		
	}
	
	public void tearDown() {
		LogManager.shutdown();
		newRobot = null;
	}

}

