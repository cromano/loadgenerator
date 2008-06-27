package customercentrix.awebstorm.loadgenerator;

import java.net.URI;


/**
 * LoadGenerator Interface
 * 
 * @author Cromano
 * @version 1.0
 *
 */
public interface LoadGenerator {
	
	public void run();
	
	public void configureLogs(String configLocation);
	
	public void systemScan();
	
	public void optimize();
	
	public void loadPreferences(String prefsLocation);
	
	public URI getSchedulerURI();
	
	public void syncWithScheduler();
	
	public void retrieveScripts();
	
	public void sendLogs();
	
	public void createRobot(String ScriptLocation);
	
}
