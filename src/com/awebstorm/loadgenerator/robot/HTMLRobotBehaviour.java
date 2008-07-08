package com.awebstorm.loadgenerator.robot;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;




/**
 * Test the behaviour of the HTMLRobot class.
 * @author Cromano
 *
 */
public class HTMLRobotBehaviour {
	
	private HTMLRobot newRobot;
	private Logger consoleLog;
	private Logger resultLog;
	private String scriptLocation;
	private PropertyResourceBundle loadGeneratorProperties;
	private static final String LOAD_GEN_PROPS_LOC = "LoadGenerator";
	private static final String LOAD_GEN_LOG_PROPS_LOC = "log4j.properties";
	
	public void shouldGenerateGoodResults() {
		scriptLocation = "Script.xml";
		newRobot = new HTMLRobot(scriptLocation,consoleLog,resultLog);
		newRobot.run();
	}

	public void shouldAppendGoodResultsLog() throws Exception {
		scriptLocation = "Script2.xml";
		newRobot = new HTMLRobot(scriptLocation,consoleLog,resultLog);
		newRobot.run();
	}
	
	public void shouldTimeoutSuccessfully() {
		scriptLocation = "Script3.xml";
		newRobot = new HTMLRobot(scriptLocation,consoleLog,resultLog);
		newRobot.run();
		
	}
	
	public void shouldFailButNotThrowException() {
		scriptLocation = "Script4.xml";
		newRobot = new HTMLRobot(scriptLocation,consoleLog,resultLog);
		newRobot.run();
	}
	
	public void setUp() {
		
		consoleLog = Logger.getLogger("loadgenerator.consoleLog");
		resultLog = Logger.getLogger("loadgenerator.consoleLog.resultLog");
		PropertyConfigurator.configureAndWatch(LOAD_GEN_LOG_PROPS_LOC);
		URL scheduler = null;
		
		if( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Logs configured.");
		}
		
		loadGeneratorProperties = (PropertyResourceBundle) ResourceBundle.getBundle(LOAD_GEN_PROPS_LOC);

		try {
			scheduler = new URL (
					loadGeneratorProperties.getString("schedulerProtocol"),
					loadGeneratorProperties.getString("schedulerHost"),
					Integer.parseInt(loadGeneratorProperties.getString("schedulerPort")),
					loadGeneratorProperties.getString("schedulerFile")
			);
		} catch (NumberFormatException e) {
			consoleLog.fatal("Bad Port Number receieved from properties: " + loadGeneratorProperties.getString("schedulerPort"), e);// Bad port number
			System.exit(3);
		} catch (MalformedURLException e) {
			consoleLog.fatal("Bad URL parameters received from properties.", e);// Bad URL parameters
			System.exit(3);
		}
		
		if( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Properties configured for: " + scheduler.toExternalForm());
		}
		
	}
	
	public void tearDown() {
		LogManager.shutdown();
		newRobot = null;
	}

}

