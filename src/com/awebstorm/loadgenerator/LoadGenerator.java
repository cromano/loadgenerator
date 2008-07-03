package com.awebstorm.loadgenerator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.awebstorm.loadgenerator.robot.HTMLRobot;
import com.awebstorm.loadgenerator.robot.Robot;


/**
 * Main class for the loadgenerator slaves. Performs system scan for optimization and 
 * then queries the Scheduler for Robot Scripts. Each Robot Script is handed to a 
 * Robot thread of the necessary subclass. Also, capable of returning results to 
 * the Scheduler.
 * 
 * @author Cromano
 * @version 1.0
 *
 */
public class LoadGenerator {

	private static Logger consoleLog;
	private static Logger resultLog;
	private static URL scheduler;
	private PropertyResourceBundle loadGeneratorProperties;
	private static final String LOAD_GEN_PROPS_LOC = "LoadGenerator.properties";
	private static final String LOAD_GEN_LOG_PROPS_LOC = "LoadGeneratorLog.properties";
	private PriorityBlockingQueue<String> scriptList;
	private boolean stop;
	private PriorityBlockingQueue<Robot> aliveRobotList;
	
	/**
	 * Main method constructs a new LoadGeneratorImpl and calls run() on it 
	 * @param args Console parsing not functional
	 */
	public static void main(String[] args) {
		new LoadGenerator().run();
	}
	
	/**
	 * Configures the properties of this loadGenerator
	 * 
	 */
	public void loadProperties( ) {
		
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
		
		
	}

	/**
	 * Can be implemented using a static constant or a configuration value
	 * @return The URI of the Scheduler
	 */
	public URL getSchedulerURL() {

		return scheduler;
	}

/*	*//**
	 * Unimplemented optimization constraints that the LoadGenerator may send to the
	 * Scheduler or perform locally.
	 *//*
	public void optimize() {
		// TODO Auto-generated method stub
		
	}*/

/*	*//**
	 * Ask and retrieve Scripts to createRobots with
	 *//*
	public void retrieveScripts() {
		// TODO Auto-generated method stub
		
	}*/

/*	*//**
	 * Send the necessary logs back to the Scheduler
	 *//*
	public void sendLogs() {
		// TODO Auto-generated method stub
		
	}*/

/*	*//**
	 * Communicates with the scheduler to perform necessary tasks such as 
	 * syncWithScheduler() or sendLogs(). May be implemented to synchronize the
	 * System clock with the server's for additional accuracy.
	 *//*
	public void syncWithScheduler() {
		// TODO Auto-generated method stub
		
	}*/

/*	*//**
	 * Part of an unimplemented requirement of the LoadGenerator to ensure
	 * it is loading into a sustainable architecture.
	 *//*
	public void systemScan() {
		// TODO Auto-generated method stub
		
	}*/

	/**
	 * Create a robot
	 * @param ScriptLocation Location of the Robot's Script
	 * @param prefsLocation Location of the Robot's preferences
	 */
	public void createRobot(String ScriptLocation) {
		
		if( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Creating new Robot: " + ScriptLocation);
		}
		
		Thread newRobot = selectRobotType(ScriptLocation);
		
		if (newRobot != null) {
			newRobot.setName(ScriptLocation);
			newRobot.run();
		} else {
			consoleLog.warn("Bad robotType found for: " + ScriptLocation);
		}
		
	}

	/**
	 * Run a LoadGenerator 
	 */
	public void run() {
		
		if ( consoleLog.isDebugEnabled() ) {
			consoleLog.debug("Starting Load Generator");
		}

		this.loadProperties();
		scriptList = new PriorityBlockingQueue<String>();
		stop = false;
		aliveRobotList = new PriorityBlockingQueue<Robot>();
		
		// Sync
		while (stop) {
			//Ask for Scripts
			//Return Data
			//Start Scripts
		}
		
		if ( consoleLog.isDebugEnabled() ) {
			consoleLog.debug("Closing Load Generator");
		}
		
	}
	
	private Robot selectRobotType(String scriptLocation) {
		
			return new HTMLRobot(scriptLocation,consoleLog,resultLog);

	}
	
	/**
	 * Kill the robot using the specified thread name / Script location
	 * @param scriptLocation The name of the robot's thread
	 * @return True if the robot was asked to die, false if the robot was not found or otherwise unable to be stopped
	 */
	public boolean killRobot (String scriptLocation) {
		
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Attempting to kill: " + scriptLocation);
		}
		
		Thread[] tarray = new Thread[100];
		Thread.enumerate(tarray);
		int i = 0;
		while( tarray[i] != null ) {
			
			if (consoleLog.isDebugEnabled()) {
				consoleLog.debug("Robot found, trying to kill it...");
			}
			if( scriptLocation.equals(tarray[i].getName()) ) {
				((Robot) tarray[i]).setStop(true);
			}
			i++;
		}
		
		return false;
	}
	
	public void killAllRobots () {
		
		if (consoleLog.isDebugEnabled()) {
			consoleLog.debug("Attempting to kill all robots.");
		}

		while( !aliveRobotList.isEmpty() ) {
			
			if (consoleLog.isDebugEnabled()) {
				consoleLog.debug("Robot found, trying to kill it...");
			}
			Robot tempRobot = aliveRobotList.poll();
			if( tempRobot.isAlive() ) {
				aliveRobotList.poll().setStop(true);
			}

		}
		
	}

}
