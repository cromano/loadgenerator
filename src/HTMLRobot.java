import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.commons.httpclient.NameValuePair;

import com.gargoylesoftware.htmlunit.*;





/**
 * Robot for handling HTML calls
 * @author Cromano
 *
 */
public class HTMLRobot extends Robot {

	//the WebClient used for all of this HTMLRobot's operations
	private WebClient htmlRobotClient;
	private long requestTime;
	//The ArrayList of NameValuePairs to be used in the next POST operation
	private ArrayList<NameValuePair> postList;
	private TopLevelWindow htmlRobotWindow;
	private WebResponse htmlRobotResponse;
	private WebRequestSettings htmlRobotSettings;

	//private final 
	
	//Permitted actions that the robot is capable of performing
	public static enum ActionTypes {
		
		GET,
		POST,
		WAIT
		
	}
	
	/**
	 * Default Constructor creates a default WebClient
	 * 
	 */
/*	public HTMLRobot(){
		htmlRobotClient = new WebClient();
		postList = new ArrayList<NameValuePair>();
		requestTime = -1;
		htmlRobotWindow = new TopLevelWindow("New",htmlRobotClient);
		//htmlRobotResponse = new WebResponse();
	}*/
	
	/**
	 * Constructor defines the location of the XML RobotScript. All other variables will be
	 * set to default settings
	 * 
	 * @param inputLocation The location of the script log
	 */
	public HTMLRobot(String inputLocation, Long sleep){
		super(sleep);
		htmlRobotClient = new WebClient();
		postList = new ArrayList<NameValuePair>();
		requestTime = -1;
		htmlRobotWindow = new TopLevelWindow("New",htmlRobotClient);
		//htmlRobotResponse = new WebResponse();
		//this.setDefaultRobotPreferences();
	}
	
	/**
	 * Constructor defines the locations of the robot script, result, error, and console logs
	 * 
	 * @param scriptLogLocation The location of the script log
	 * @param prefsLocation The location of the preferences file
	 */
	public HTMLRobot(String scriptLogLocation, String prefsLocation, Long sleep){
		super(prefsLocation,sleep);
		htmlRobotClient = new WebClient();
		postList = new ArrayList<NameValuePair>();
		requestTime = -1;
		htmlRobotWindow = new TopLevelWindow("New",htmlRobotClient);
		//htmlRobotResponse = new WebResponse();

		//this.setRobotPreferences(prefsLocation);
		
	}
	
	/**
	 * TODO - Will either POST, GET, or wait depending on the step
	 * @param actionType The enum ActionTypes to be executed
	 * @param action The encoded action information
	 */
	public void executeScriptStep(ActionTypes actionType, String action) {
		
		switch (actionType) {
		case GET:
			//Get the page and any resources
			//TODO
			break;
		case POST:
			//Post the information to a page
			//TODO
			break;
		case WAIT:
			//Wait for the given amount of time
			if (action.equals("")) {
				try {
					this.wait(defaultRobotWait);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			try {
					this.wait(Integer.parseInt(action));
				} catch (NumberFormatException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			break;
		default:
		//Impossible
		//TODO - Write to error log
		break;
			
		}
		
	}
	
	public long getPageTime() {
		return requestTime;
	}
	
	/**
	 * Requests a page with the current settings
	 * 
	 * @param window The window to be used for the GET
	 * @param request The settings to be used for the GET
	 * @return The page retrieved by the GET
	 * @throws Exception
	 */
	public Page getPage(WebWindow window, WebRequestSettings request) throws Exception {
		Long tempTime = System.currentTimeMillis();
		Page page = htmlRobotClient.getPage(window,request);
		requestTime = System.currentTimeMillis() - tempTime;
		return page;
	}
	
/*	public Page postPage(WebWindow window, WebRequestSettings request) {
		
		Long tempTime = System.currentTimeMillis();
		//Page page = htmlRobotClient.loadWebResponseInto(webResponse, htmlRobotWindow)
		
	}*/
	
	/**
	 * Used to add data to the HTMLRobot on its current step to perform a POST operation
	 * 
	 * @param name The name of the parameter to be posted
	 * @param value The value of the parameter to be posted
	 */
	public void addInputParameter(String name, String value) {

			postList.add(new NameValuePair(name,value));

	}
	
	/**
	 * Clears the entire list of input parameters for the next POST operation
	 */
	public void clearInputParameters() {
		postList.clear();
	}
	
	/**
	 * Specify where the XML Robot Script is located
	 * @param inputLocation the absolute location of the Robot Script
	 */
	public void setInputLocation( String inputLocation ) {
		
	}
	
	public void runRobot() throws IOException {
		//TODO - actually needs to read XML Files
		scriptReader = null;
		try {
			scriptReader = new FileReader(scriptLocation);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(2);
		}
		currentElement = "";

		while(scriptReader.ready()) {

			//TODO
			//Read/Parse Header
			//
			//Each XML element will be a step with some attributes
			//(These steps are HTML Robot specific, but the idea remains the same
			//A GET element will have all the relevant information
			//A POST element will have all the relevant information to send
			//A WAIT element will have one attribute (the time to wait in milliseconds)

			//When the element is determined, it will send the element in form of a String
			//And the attributes in the form of a HashMap

			//executeScriptStep(currentElement);

		}
	}
}
