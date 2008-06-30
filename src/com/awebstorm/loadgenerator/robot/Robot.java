package com.awebstorm.loadgenerator.robot;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;


/**
 * Abstract class holding the fields and methods shared across the robot types
 * @author Cromano
 * @version 1.0
 *
 */
public abstract class Robot extends Thread {

	protected int timer;
	protected int Duration;
	protected int Iteration;
	protected int defaultTimeout;
	protected String defaultScriptLocation;
	protected String errorLogLocation;
	protected String resultLogLocation;
	protected String consoleLogLocation;
	//default period between each step
	protected long defaultWaitStep;
	protected PropertyResourceBundle robotProperties;
	protected String currentElement;
	protected HashMap<String,String> currentAttributes;
	//May be replaced with an XML parser
	protected FileReader scriptReader;
	//initial sleep time given by the loadGenerator
	protected long initialSleepTime;
	private boolean continueExecuting;
	private static final String ROBOT_PROPS_LOC = "Robot.properties";
	//Old Loggers
/*	private FileHandler resultLogFileHandler;
	private MemoryHandler resultLogMemHandler;
	protected static Logger errorLog;
	protected static Logger resultLog;
	protected static Logger consoleLog;
	protected static MemoryHandler errorLogMemHandler;
	protected static FileHandler errorLogFileHandler;*/
	//protected LogManager logManager;
	//protected Handler logHandler;
	
	//New Loggers
	//LAYOUT-The logs are inherited from the loadgenerator to allow easier
	//synchronized writing to the logs, this can be changed in later versions
	//Furthermore, using a Logger for results may have performance issues...
	Logger consoleLog;
	Logger resultLog;
	Logger errorLog;
	
	/**
	 * Default robot Constructor
	 * Child classes should call one of the 2 Robot constructors to setup the Robot fields
	 * 
	 * @param scriptLocation Location of the Robot Script
	 * @param consoleLog Logger to be used for general debugging and info
	 * @param resultLog Logger to be used for results
	 * @param errorLog Logger to be used for all errors in or out of debugging
	 */
	protected Robot( String scriptLocation, Logger consoleLog, Logger resultLog, Logger errorLog){
		if(consoleLog.isDebugEnabled()) {
			consoleLog.debug("Building a default Robot");
		}
		
		currentElement="";
		currentAttributes = new HashMap<String,String>();
		continueExecuting = true;
		
		//initialize logging activity
/*		logManager = LogManager.getLogManager();
		logManager.addLogger(errorLog);
		logManager.addLogger(resultLog);
		logManager.addLogger(consoleLog);*/
		//consoleLog = Logger.getLogger(Robot.class.getName());
		//errorLog = Logger.getLogger(Robot.class.getName());
		//errorLog.setParent(consoleLog);
		//resultLog = Logger.getLogger(Robot.class.getName());
		//resultLog.setParent(consoleLog);
		
		//Initialize the Handlers for the logs and add them
/*		try {
			errorLogFileHandler = new FileHandler("%h/loadGeneratorError%u.log", 0, 1, true);
		} catch (SecurityException e1) {
			System.err.println("Could not open error log.");
			e1.printStackTrace();
			System.exit(3);
		} catch (IOException e1) {
			System.err.println("Could not open error log.");
			e1.printStackTrace();
			System.exit(3);
		}
		try {
			resultLogFileHandler = new FileHandler("%h/loadGeneratorResults%u.log", 0, 1, true);
		} catch (SecurityException e1) {
			System.err.println("Could not open results log.");
			e1.printStackTrace();
			System.exit(3);
		} catch (IOException e1) {
			System.err.println("Could not open results log.");
			e1.printStackTrace();
			System.exit(3);
		}
		errorLogMemHandler = new MemoryHandler(errorLogFileHandler,10,Level.OFF);
		resultLogMemHandler = new MemoryHandler(resultLogFileHandler,10,Level.OFF);
		errorLog.addHandler(errorLogMemHandler);
		resultLog.addHandler(resultLogMemHandler);*/
		
		//New logging framework using log4j
		//Logger consoleLog = Logger.getLogger("loadgenerator.log.robot.console");
		//Logger resultLog = Logger.getLogger("loadgenerator.log.robot.console.result");
		//Logger errorLog = Logger.getLogger("loadgenerator.log.robot.console.error");
		this.consoleLog = consoleLog;
		this.errorLog = errorLog;
		this.resultLog = resultLog;
		
		//Use the default location of the preferences file
		this.setDefaultRobotPreferences(ROBOT_PROPS_LOC);
		
		//Initialize the fileReader for the script
		if ( scriptLocation == null ) {
			try {
				scriptReader = new FileReader(defaultScriptLocation);
			} catch (FileNotFoundException e) {
				//errorLog.log(Level.SEVERE, "Could not open the default script.");
				e.printStackTrace();
				System.exit(2);
			}
		//The passed location was not null, attempt to open the Reader
		} else {
			try {
				scriptReader = new FileReader(scriptLocation);
			} catch (FileNotFoundException e) {
				//errorLog.log(Level.SEVERE, "Could not open the script passed.");
				e.printStackTrace();
				System.exit(2);
			}
		}
	}
	
	/**
	 * Constructs a robot using the absolute location of the preferences
	 * 
	 * @param scriptLocation Location of the Robot Script
	 * @param prefsLocation Location of the folder holding the preferences
	 * @param consoleLog Logger to be used for general debugging and info
	 * @param resultLog Logger to be used for results
	 * @param errorLog Logger to be used for all errors in or out of debugging
	 */
	protected Robot( String scriptLocation, String prefsLocation, Logger consoleLog, Logger resultLog, Logger errorLog ) {
		if ( consoleLog.isDebugEnabled() ) {
			consoleLog.debug("Building a configured Robot");
		}
		currentElement="";
		currentAttributes = new HashMap<String,String>();
		continueExecuting = true;

		//Initialize logging activity
/*		logManager = LogManager.getLogManager();
		logManager.addLogger(errorLog);
		logManager.addLogger(resultLog);
		logManager.addLogger(consoleLog);*/
/*		consoleLog = Logger.getLogger(Robot.class.getName());
		errorLog = Logger.getLogger(Robot.class.getName());
		errorLog.setParent(consoleLog);
		resultLog = Logger.getLogger(Robot.class.getName());
		resultLog.setParent(consoleLog);*/
		
		//Initialize the Handlers for the logs and add them
/*		try {
			errorLogFileHandler = new FileHandler("%h/loadGeneratorError%u.log", 0, 1, true);
		} catch (SecurityException e1) {
			System.err.println("Could not open error log.");
			e1.printStackTrace();
			System.exit(3);
		} catch (IOException e1) {
			System.err.println("Could not open error log.");
			e1.printStackTrace();
			System.exit(3);
		}
		try {
			resultLogFileHandler = new FileHandler("%h/loadGeneratorResults%u.log", 0, 1, true);
		} catch (SecurityException e1) {
			System.err.println("Could not open results log.");
			e1.printStackTrace();
			System.exit(3);
		} catch (IOException e1) {
			System.err.println("Could not open results log.");
			e1.printStackTrace();
			System.exit(3);
		}
		errorLogMemHandler = new MemoryHandler(errorLogFileHandler,10,Level.OFF);
		resultLogMemHandler = new MemoryHandler(resultLogFileHandler,10,Level.OFF);
		errorLog.addHandler(errorLogMemHandler);
		resultLog.addHandler(resultLogMemHandler);*/
		
		//New loggers passed
		this.consoleLog = consoleLog;
		this.errorLog = errorLog;
		this.resultLog = resultLog;
		
		//Use a specific location for the preferences file
		this.setDefaultRobotPreferences( prefsLocation);
		
		//Initialize the fileReader for the script
		if ( scriptLocation == null ) {
			try {
				scriptReader = new FileReader(defaultScriptLocation);
			} catch (FileNotFoundException e) {
				//errorLog.log(Level.SEVERE, "Could not open the default script.");
				e.printStackTrace();
				System.exit(2);
			}
		//The passed location was not null, attempt to open the Reader
		} else {
			try {
				scriptReader = new FileReader(scriptLocation);
			} catch (FileNotFoundException e) {
				//errorLog.log(Level.SEVERE, "Could not open the script passed.");
				errorLog.fatal("Could not open the script passed.");
				e.printStackTrace();
				System.exit(2);
			}
		}
		
	}
	
	/**
	 * Method must be implemented by extending classes, it provides the functionality of
	 * parsing the input file for steps
	 * 
	 */
	public abstract void run();
	
	
	/**
	 * Load the preferences
	 * @param prefsLocation Location of the preferences file
	 */
	private void setDefaultRobotPreferences( String prefsLocation ) {
		
		//Load all of the default preferences from prefsLocation
		robotProperties = (PropertyResourceBundle) ResourceBundle.getBundle(prefsLocation);
		defaultScriptLocation = robotProperties.getString("defaultScriptLocation");
		errorLogLocation = robotProperties.getString("defaultErrorLogLocation");
		resultLogLocation = robotProperties.getString("defaultResultLogLocation");
		consoleLogLocation = robotProperties.getString("defaultConsoleLogLocation");
		Duration = Integer.parseInt(robotProperties.getString("defaultDuration"));
		Iteration = Integer.parseInt(robotProperties.getString("defaultIteration"));
		defaultWaitStep = Integer.parseInt(robotProperties.getString("defaultWaitStep"));
		defaultTimeout = Integer.parseInt(robotProperties.getString("defaultTimeout"));
		//Logging configuration moved to LoadGenerator
		//consoleLog.setLevel(Level.toLevel(robotPreferences.getString("consoleLogLevel")));
		//resultLog.setLevel(Level.toLevel(robotPreferences.getString("resultLogLevel")));
		//errorLog.setLevel(Level.toLevel(robotPreferences.getString("errorLogLevel")));
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Preferences loaded successfully");
		}
	}

	public boolean isContinueExecuting() {
		return continueExecuting;
	}

	public void setContinueExecuting(boolean continueExecuting) {
		this.continueExecuting = continueExecuting;
	}
}
