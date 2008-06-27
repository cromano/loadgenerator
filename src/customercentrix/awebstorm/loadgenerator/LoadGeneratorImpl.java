package customercentrix.awebstorm.loadgenerator;

import java.net.URI;

import org.apache.log4j.Logger;

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
public class LoadGeneratorImpl implements LoadGenerator {

	private Logger consoleLog;
	private Logger resultLog;
	private Logger errorLog;
	
	/**
	 * Main method constructs a new LoadGeneratorImpl and calls run() on it 
	 * @param args
	 */
	public static void main(String[] args) {
		//TODO
		//May add a console parser later
		new LoadGeneratorImpl().run();
		
	}
	
	/**
	 * Configures the 3 logs used by loadgenerator and its robots
	 * @param configLocation The location of the configuration file
	 */
	public void configureLogs( String configLocation ) {
		//TODO
	}

	@Override
	public URI getSchedulerURI() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void optimize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void retrieveScripts() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendLogs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void syncWithScheduler() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void systemScan() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createRobot(String ScriptLocation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadPreferences(String prefsLocation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
