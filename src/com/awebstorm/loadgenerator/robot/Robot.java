package com.awebstorm.loadgenerator.robot;
import java.util.HashMap;
import java.util.PriorityQueue;
import org.apache.log4j.Logger;


/**
 * Generic Robot operations include Script parsing, general preferences, and stop boolean.
 * @author Cromano
 * @version 1.0
 *
 */
public abstract class Robot extends Thread {

	protected int timer;
	protected String jobID;
	protected int stepID;
	protected int Duration;
	protected int Iteration;
	protected int timeout;
	protected String domain;
	protected String scriptLocation;
	protected long defaultWaitStep;
	protected String currentElement;
	private boolean stop;
	protected Logger consoleLog;
	protected Logger resultLog;
	PriorityQueue<Step> stepQueue;
	HashMap<String,String> prefs;
	
	public static final long	 DEFAULT_DURATION = 60000;
	public static final int 	 DEFAULT_ITERATION = 1;
	public static final long	 DEFAULT_TIMEOUT = 5000;
	public static final long	 DEFAULT_WAIT_STEP = 1000;
	
	/**
	 * Default robot Constructor
	 * Child classes should call this to setup the Robot fields and parse the Script
	 * 
	 * @param scriptLocation Location of the Robot Script
	 * @param consoleLog Logger to be used for general debugging and info
	 * @param resultLog Logger to be used for results
	 * @param errorLog Logger to be used for all errors in or out of debugging
	 */
	protected Robot( String scriptLocation, Logger consoleLog, Logger resultLog){
		
		if(consoleLog.isDebugEnabled()) {
			consoleLog.debug("Build a default Robot.");
		}
		
		currentElement="";
		this.stop = false;
		this.scriptLocation = scriptLocation;
	
		this.consoleLog = consoleLog;
		this.resultLog = resultLog;
		
		stepQueue = new PriorityQueue<Step>();
		prefs = new HashMap<String,String>();
		new ScriptReader().run(scriptLocation,consoleLog,stepQueue,prefs );
		this.setDefaultRobotPreferences();
		
	}
	
	/**
	 * Method must be implemented by extending classes, it provides the functionality of
	 * parsing the input file for steps.
	 */
	public abstract void run();
	
	/**
	 * Should be implemented by extending classed to load their particular Robot preferences.
	 */
	public abstract void configureRobot();
	
	/**
	 * Load the generic robot preferences.
	 * @param prefsLocation Location of the preferences file
	 */
	private void setDefaultRobotPreferences( ) {
		
		Duration = Integer.parseInt(prefs.get("duration"));
		Iteration = Integer.parseInt(prefs.get("iteration"));
		timeout = Integer.parseInt(prefs.get("timeout"));
		defaultWaitStep = Integer.parseInt(prefs.get("waitstep"));
		jobID = prefs.get("jobID");
		domain = prefs.get("domain");
		
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Preferences loaded successfully for " + jobID);
		}
	}

	/**
	 * Get the value of the stop thread boolean.
	 * @return stop thread value
	 */
	public synchronized boolean getStop() {
		return stop;
	}
	
	/**
	 * Set the value of the stop thread boolean.
	 * @param value New value
	 */
	public synchronized void setStop(boolean value) {
		this.stop=value;
	}

}
