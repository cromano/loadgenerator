package com.awebstorm.loadgenerator;

import java.io.IOException;
import java.io.InputStream;
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
	 * Retrieve the URL of the Scheduler
	 * @return The URI of the Scheduler
	 */
	public URL getSchedulerURL() {
		return scheduler;
	}

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
		
		schedulerSync();
		
		while (!stop) {
			try {
				this.wait(60000);
			} catch (InterruptedException e) {
				consoleLog.error("LoadGenerator inerrupted during a wait cycle",e);
			}
			try {
				retrieveScripts();
			} catch (IOException e) {
				consoleLog.fatal("The LoadGenerator failed to retrieve scripts from the scheduler.",e);
				System.exit(3);
			}
			while (!scriptList.isEmpty()) {
				createRobot(scriptList.poll());
			}
		}
		
		if ( consoleLog.isDebugEnabled() ) {
			consoleLog.debug("Closing Load Generator");
		}
		
	}
	
	/**
	 * Synchronize LoadGenerator system variables with those of the scheduler.
	 */
	private void schedulerSync() {
		//TODO -Sync with the scheduler for time and any other requirements
	}

	/**
	 * Retrieve scripts from the scheduler when available
	 * @throws IOException
	 */
	private void retrieveScripts() throws IOException {
		InputStream scriptStream = null;
		StringBuffer scriptBuffer = new StringBuffer();
		//TODO -Open Stream...
		while ( scriptStream.available() > 0 ) {
			scriptBuffer.append((char)scriptStream.read());
			//TODO -Write the script to file uniquely...
		}
	}

	/**
	 * Chooses a robotType to start based on undetermined factors.
	 * @param scriptLocation
	 * @return
	 */
	private Robot selectRobotType(String scriptLocation) {
			return new HTMLRobot(scriptLocation,consoleLog,resultLog);
	}
	
	/**
	 * Kill the robot using the specified thread name / Script location
	 * @param scriptLocation The name of the robot's thread
	 * @return True if the robot was asked to die, false if the robot was not found or otherwise unable to be stopped
	 */
	private boolean killRobot (String scriptLocation) {
		
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
	
	/**
	 * Kill all robots
	 */
	private void killAllRobots () {
		
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
