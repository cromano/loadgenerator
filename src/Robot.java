import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.PropertyResourceBundle;
import java.util.logging.Filter;
import java.util.logging.Logger;

/**
 * Abstract class holding the fields and methods shared across the robot types
 * @author Cromano
 *
 */
public abstract class Robot {

	protected int timer;
	protected int Duration;
	protected int Iteration;
	protected String scriptLocation;
	protected String errorLogLocation;
	protected String resultLogLocation;
	protected String consoleLogLocation;
	//default period between each step
	protected long defaultRobotWait;
	protected PropertyResourceBundle robotPreferences;
	protected String currentElement;
	protected HashMap<String,String> currentAttributes;
	protected FileReader scriptReader;
	//initial sleep time given by the loadGenerator
	protected long initialSleepTime;
	protected Logger errorLog;
	protected Logger resultLog;
	protected Logger consoleLog;
	
	//Constructor for use by extending classes
	protected Robot(Long sleep){
		defaultRobotWait=0;
		initialSleepTime = sleep;
		currentElement="";
		currentAttributes = new HashMap<String,String>();
		errorLog = Logger.getLogger(Robot.class.getName());
		errorLog.setFilter(new Filter());
		this.setDefaultRobotPreferences();
	}
	
	protected Robot( String prefsLocation, Long sleep) {
		defaultRobotWait=0;
		initialSleepTime = sleep;
		currentElement="";
		currentAttributes = new HashMap<String,String>();
		this.setRobotPreferences( prefsLocation);
	}
	
	/**
	 * Method must be implemented by extending classes, it provides the functionality of
	 * parsing the input file for steps
	 * 
	 * @throws IOException This Exception is throw when the FileReader has an error reading the file
	 */
	public abstract void runRobot() throws IOException;
	
	/**
	 * Use a user specified preferences file
	 * @param prefsLocation 
	 */
	private void setRobotPreferences(String prefsLocation) {
		
		InputStream preferencesStream = null;

		File preferencesFile = new File (prefsLocation);

		try {
			preferencesStream = new FileInputStream(preferencesFile);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
			System.exit(2);
		}

		try {
			robotPreferences = new PropertyResourceBundle(preferencesStream);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(2);
		}
		
		scriptLocation = robotPreferences.getString("scriptLocation");
		errorLogLocation = robotPreferences.getString("errorLogLocation");
		resultLogLocation = robotPreferences.getString("resultLogLocation");
		consoleLogLocation = robotPreferences.getString("consoleLogLocation");
		defaultRobotWait = Long.parseLong(robotPreferences.getString("defaultRobotWait"));
	}
	
	/**
	 * Use the default local preferences
	 */
	private void setDefaultRobotPreferences() {

		InputStream preferencesStream = null;
		try {
			//Default preferences
			preferencesStream = new FileInputStream("Robot.preferences");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(2);
		}
	
		try {
			robotPreferences = new PropertyResourceBundle(preferencesStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(2);
		}
		
		scriptLocation = robotPreferences.getString("scriptLocation");
		errorLogLocation = robotPreferences.getString("errorLogLocation");
		resultLogLocation = robotPreferences.getString("resultLogLocation");
		consoleLogLocation = robotPreferences.getString("consoleLogLocation");
		defaultRobotWait = Long.parseLong(robotPreferences.getString("defaultRobotWait"));
		
		
	}
}
