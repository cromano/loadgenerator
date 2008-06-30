package com.awebstorm.loadgenerator.robot;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;




/**
 * Test the behaviour of the HTMLRobot class
 * @author Cromano
 *
 */
public class HTMLRobotBehaviour {
	
	private HTMLRobot newRobot;
	//private Page currentPage;
	//private TopLevelWindow testWindow;
	private Logger errorLog;
	private Logger consoleLog;
	private Logger resultLog;
	private String scriptLocation;
	private String prefsLocation;
	
	public void shouldGenerateGoodResults() {
		scriptLocation = "Script.XML";
		prefsLocation = "Robot.preferences";
		newRobot = new HTMLRobot(scriptLocation,consoleLog,resultLog,errorLog);
		newRobot.run();
	}
	
	public void shouldThrowScriptNotFound( String scriptLocation ) throws Exception {

		scriptLocation = "";
		newRobot = new HTMLRobot(scriptLocation,consoleLog,resultLog,errorLog);
		newRobot.run();
		
	}
	
	public void shouldUseDefaultPreferences() throws Exception {
		
		scriptLocation = "Script.XML";
		prefsLocation = "";
		newRobot = new HTMLRobot(scriptLocation,prefsLocation, consoleLog,resultLog,errorLog);
		newRobot.run();
		
	}
	
	public void shouldAppendGoodResults() {

		scriptLocation = "Script.XML";
		prefsLocation = "Robot.preferences";
		newRobot = new HTMLRobot(scriptLocation,consoleLog,resultLog,errorLog);
		newRobot.run();
		
	}
	
	/**
	 * Incomplete - Unknown implementation
	 */
	public void shouldKillRobot() {
		
		scriptLocation = "Script.XML";
		prefsLocation = "Robot.preferences";
		newRobot = new HTMLRobot(scriptLocation,consoleLog,resultLog,errorLog);
		//newRobot.run();
		
	}
	
	public void setUp() {
		//Set-up Loggers
		//New logging framework using log4j
		consoleLog = Logger.getLogger("loadgenerator.log.robot.console");
		resultLog = Logger.getLogger("loadgenerator.log.robot.console.result");
		errorLog = Logger.getLogger("loadgenerator.log.robot.console.error");
		//Construct Robot under test
		//newRobot = new HTMLRobot("Robot.preferences",consoleLog,resultLog,errorLog);
	}
	
	public void tearDown() {
		LogManager.shutdown();
		newRobot = null;
	}
	
}

