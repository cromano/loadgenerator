package com.awebstorm.loadgenerator.robot;
import java.io.IOException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.xml.sax.Attributes;

import com.gargoylesoftware.htmlunit.*;





/**
 * Robot for handling HTML calls
 * @author Cromano
 * @version 1.0
 *
 */
public class HTMLRobot extends Robot {

	//the WebClient used for all of this HTMLRobot's operations
	private WebClient htmlRobotClient;
	private long requestTime;
	//The ArrayList of NameValuePairs to be used in the next POST operation
	private ArrayList<NameValuePair> postList;
	private TopLevelWindow basicHtmlRobotWindow;
	private WebResponse htmlRobotResponse;
	private WebRequestSettings htmlRobotSettings;
	private String browserType;
	private static boolean ignoreOutsideContent;
	private boolean javaScriptEnabled;
	private boolean redirectEnabled;
	private boolean throwExceptionOnScriptError;
	private int cacheSize;
	private boolean useInsecureSSL;
	private boolean popupBlockerEnabled;
	private boolean throwExceptionOnFailingStatusCode;
	private boolean printContentOnFailingStatusCode;
	private PageCreator htmlRobotPageCreator;
	private int totalStepBytes;
	private BrowserVersion htmlRobotBrowserVersion;
	private String proxyHost;
	private int proxyPort;
	private static final String ROBOT_PROPS_LOC = "Robot.properties";
	//private boolean shouldReconfigureRobotClient;
	//private boolean shouldReloadRobotClient;

	
	//Permitted actions that the robot is capable of performing
	private static enum ActionTypes {
		
		GET,
		POST,
		VERIFY,
		WAIT
		
	}
	
	/**
	 * Constructor defines the location of the XML RobotScript. All other variables will be
	 * set to default settings
	 * 
	 * @param scriptLocation Location of the Robot Script
	 * @param consoleLog Logger to be used for general debugging and info
	 * @param resultLog Logger to be used for results
	 * @param errorLog Logger to be used for all errors in or out of debugging
	 */
	public HTMLRobot(String scriptLocation, Logger consoleLog, Logger resultLog, Logger errorLog){
		super(scriptLocation, consoleLog, resultLog, errorLog);
		if ( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Robot is an HTMLRobot");
		}
		postList = new ArrayList<NameValuePair>();
		requestTime = 0;
		setDefaultHTMLRobotPreferences();
		//Too much trouble to place default BrowserVersion in prefs
		htmlRobotBrowserVersion = BrowserVersion.INTERNET_EXPLORER_7_0;

	}
	
	/**
	 * Constructor defines the locations of the robot script, result, error, and console logs
	 * 
	 * @param scriptLocation Location of the Robot Script
	 * @param prefsLocation Location of the properties file
	 * @param consoleLog Logger to be used for general debugging and info
	 * @param resultLog Logger to be used for results
	 * @param errorLog Logger to be used for all errors in or out of debugging
	 */
	public HTMLRobot(String scriptLocation, String prefsLocation, Logger consoleLog, Logger resultLog, Logger errorLog){
		super(scriptLocation, prefsLocation, consoleLog, resultLog, errorLog);
		if ( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Robot is an HTMLRobot");
		}
		postList = new ArrayList<NameValuePair>();
		requestTime = 0;
		setDefaultHTMLRobotPreferences();
		//Too much trouble to place default BrowserVersion in prefs
		htmlRobotBrowserVersion = BrowserVersion.INTERNET_EXPLORER_7_0;
		
	}
	
	/**
	 * Configures the WebClient with the preferences saved in the class fields
	 */
	private void configureHtmlRobotClient() {
		
		htmlRobotClient.setJavaScriptEnabled(javaScriptEnabled);
		htmlRobotClient.setRedirectEnabled(redirectEnabled);
		htmlRobotClient.setPopupBlockerEnabled(popupBlockerEnabled);
		try {
			htmlRobotClient.setUseInsecureSSL(useInsecureSSL);
		} catch (GeneralSecurityException e) {
			//errorLog.log(Level.SEVERE, "Security Exception during configuration.");
			errorLog.error("Security Exception during configuration.", e);
			e.printStackTrace();
		}
		//Should be set by the loadGenerator
		//WebClient.setIgnoreOutsideContent(ignoreOutsideContent);
		htmlRobotClient.setThrowExceptionOnFailingStatusCode(throwExceptionOnFailingStatusCode);
		htmlRobotClient.setThrowExceptionOnScriptError(throwExceptionOnScriptError);
		htmlRobotClient.setPrintContentOnFailingStatusCode(printContentOnFailingStatusCode);
		htmlRobotClient.setTimeout(defaultTimeout);
		htmlRobotClient.setCurrentWindow(basicHtmlRobotWindow);
		htmlRobotClient.setPageCreator(htmlRobotPageCreator);
		
	}
	
	/**
	 * Loads the preferences specific to an HTMLRobot
	 */
	private void setDefaultHTMLRobotPreferences() {
		
		if ( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Loading HTMLRobot default properties");
		}
			
		browserType = robotProperties.getString("defaultBrowserType");
		ignoreOutsideContent = Boolean.parseBoolean(robotProperties.getString("defaultIgnoreOutsideContent"));
		javaScriptEnabled = Boolean.parseBoolean(robotProperties.getString("defaultJavaScriptEnabled"));
		popupBlockerEnabled = Boolean.parseBoolean(robotProperties.getString("defaultPopupBlockerEnabled"));
		redirectEnabled = Boolean.parseBoolean(robotProperties.getString("defaultRedirectEnabled"));
		throwExceptionOnFailingStatusCode = Boolean.parseBoolean(robotProperties.getString("defaultThrowExceptionOnFailingStatusCode"));
		throwExceptionOnScriptError = Boolean.parseBoolean(robotProperties.getString("defaultThrowExceptionOnScriptError"));
		useInsecureSSL = Boolean.parseBoolean(robotProperties.getString("defaultUseInsecureSSL"));
		cacheSize = Integer.parseInt(robotProperties.getString("defaultCacheSize"));
		printContentOnFailingStatusCode = Boolean.parseBoolean(robotProperties.getString("defaultPrintContentOnFailingStatusCode"));
		proxyHost = robotProperties.getString("defaultProxyHost");
		proxyPort = Integer.parseInt(robotProperties.getString("defaultProxyPort"));
		
	}
	
	/**
	 * Executes a step passed in with its attributes
	 * 
	 * @param actionType The enum ActionTypes to be executed
	 * @param action The encoded action information
	 */
	private boolean executeScriptStep(ActionTypes stepType, Attributes actionAttributes) {

		
		
		boolean tempReturnStatus = false;
		
		switch (stepType) {
		case GET:
			//Get the page and any resources
			//Return a success or fail
			//TODO
			break;
		case POST:
			//Post the information to a page
			//Return a success or fail
			//TODO
			break;
		case VERIFY:
			//Check for some appropriate information
			//Return a success or fail
			//TODO
			break;
		case WAIT:
			//Wait for the given amount of time
			//BEWARE - the index of the wait length may change as the script format is developed
			if (actionAttributes.getValue(0).equals("")) {
				try {
					this.wait(defaultWaitStep);
				} catch (InterruptedException e) {
					//errorLog.log(Level.SEVERE, "Interrupted Exception during a default WAIT step");
					errorLog.error("Interrupted Exception during a default WAIT step", e);
					e.printStackTrace();
				}
			} else {
				try {
					this.wait(Integer.parseInt(actionAttributes.getValue(0)));
				} catch (NumberFormatException e) {
					//errorLog.log(Level.SEVERE, "Could not determine the wait length during a WAIT Step");
					errorLog.error("Could not determine the wait length during a WAIT Step", e);
					e.printStackTrace();
				} catch (InterruptedException e) {
					//errorLog.log(Level.SEVERE, "Interrupted Exception during a WAIT step");
					errorLog.error("Interrupted Exception during a WAIT step", e);
					e.printStackTrace();
				}
			}
			break;
		default:
			//Nothing should read this code unless there is a bad script step
			//Everything should have a particular ScriptStep to execute
			//errorLog.log(Level.SEVERE, "A Bad Step was taken.");
			errorLog.warn("A bad step was taken by a robot.");
		break;

		}
		//Only reachable by WAIT step or error
		return tempReturnStatus;
		
	}
	
	/**
	 * Retrieves the accumulated amount of time to load all of the requested page and resources so far
	 * @return The accumulated time performing a step
	 */
	private long getLoadTime() {
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
/*	private Page getPage(WebWindow window, WebRequestSettings request) throws Exception {
		Long tempTime = System.currentTimeMillis();
		Page page = htmlRobotClient.getPage(window,request);
		requestTime = System.currentTimeMillis() - tempTime;
		return page;
	}*/
	
/*	public Page postPage(WebWindow window, WebRequestSettings request) {
		
		Long tempTime = System.currentTimeMillis();
		//Page page = htmlRobotClient.loadWebResponseInto(webResponse, htmlRobotWindow)
		
	}*/
	
	/**
	 * Collects the parameters to be passed in the next POST operation
	 * 
	 * @param name The name of the parameter to be posted
	 * @param value The value of the parameter to be posted
	 */
	private void addInputParameter(String name, String value) {

			postList.add(new NameValuePair(name,value));

	}
	
	/**
	 * Clears the list of input parameters for the next POST operation
	 */
	private void clearInputParameters() {
		postList.clear();
	}
	
	/**
	 * The primary method of any robot
	 * This method will load the settings from the settings file, then parse the script
	 * 
	 */
	public void run() {

		currentElement = "";
		//True is a success, false is a failure
		//WAIT steps are not counted and VERIFY steps are uncertain
		boolean stepReturnStatus = false;
		Attributes stepAttributes = null;
		Long tempTime;
		int tempAmount;
		int stepID = 0;
		StringBuffer tempResult = new StringBuffer();
		String jobID = "";
		URL pageRequested = null;

		//TODO
		//Read/Parse Header
		//Configure the fields from the Header settings
		
		if (proxyHost == null) {
			htmlRobotClient = new WebClient(htmlRobotBrowserVersion);
		} else {
			htmlRobotClient = new WebClient(htmlRobotBrowserVersion, proxyHost, proxyPort);
		}
		
		configureHtmlRobotClient();
		
		//Begin the step loop
		try {
			while(scriptReader.ready() && isContinueExecuting()) {

/*			if(shouldReloadRobotClient) {
					if (proxyHost == null) {
						htmlRobotClient = new WebClient(htmlRobotBrowserVersion);
					} else {
						htmlRobotClient = new WebClient(htmlRobotBrowserVersion, proxyHost, proxyPort);
					}
				} else if (shouldReconfigureRobotClient) {
					configureHtmlRobotClient();
				}*/

				//TODO
				//Each XML element will be a step with some attributes
				//(These steps are HTML Robot specific, but the idea remains the same
				//A GET element will have all the relevant information
				//A POST element will have all the relevant information to send
				//A VERIFY element may or may not be equivalent to a unique step
				//A WAIT element will have one attribute (the time to wait in milliseconds)

				if ( consoleLog.isDebugEnabled()) {
					consoleLog.debug("Begin Executing Step: " + jobID + ',' + stepID + ',' + ActionTypes.valueOf(currentElement));
				}
				//Log the return status if the Step was a GET, POST, or VERIFY in resultsLog
				stepReturnStatus = executeScriptStep(ActionTypes.valueOf(currentElement), stepAttributes);
				
				//Log the retrievaltime
				tempTime = getLoadTime();
				
				//Log the raw data amount transferred
				tempAmount = getTotalStepBytes();
				
				//The above steps may need an inner loop in the case the VERIFY is 
				//enclosed in its own Script Step.
				//The final log to file will be done here
				//jobID				Unique job identifier for grouping a job's results
				//StepID			Step Number (0->Integer_MAXVALUE)(May not include WAIT or VERIFY)
				//URL				Anchor page requested or null if WAIT step
				//currentTime 		The time the step completed requests and validation
				//tempTime			The total amount of time to load page and resources
				//tempAmount		The total amount of data received to complete the Step
				//throughput/sec	tempAmount/tempTime
				//stepReturnStatus	The final success or failure label given to a Step
				if ( consoleLog.isDebugEnabled()) {
					consoleLog.debug("Writing Step Results");
				}
				tempResult.append(jobID);
				tempResult.append(',');
				tempResult.append(stepID);
				tempResult.append(',');
				if (pageRequested == null ){
					tempResult.append(pageRequested);
				} else {
					tempResult.append(pageRequested.getRef());
				}
				tempResult.append(',');
				tempResult.append(System.currentTimeMillis());
				tempResult.append(',');
				tempResult.append(tempTime);
				tempResult.append(',');
				tempResult.append(tempAmount);
				tempResult.append(',');
				tempResult.append(tempAmount/tempTime);
				tempResult.append(',');
				if (stepReturnStatus) {
					tempResult.append("success");
				} else {
					tempResult.append("failure");
				}
				//tempResult.append('\n');
				if ( consoleLog.isDebugEnabled()) {
					consoleLog.debug(tempResult);
				}
				resultLog.info(tempResult);

			}
		} catch (IOException e) {
			errorLog.fatal("ScriptReader could not read the Robot's Script.",e);
			e.printStackTrace();
		}
		
		//TODO
		//Perform closing operations
		//Close resultsLog
		//Close input and output streams if necessary
		if ( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Robot closing");
		}
		
	}

	/**
	 * Retrieve the accumulated bytes from a steps transfers
	 * @return The accumulated bytes from a step
	 */
	private int getTotalStepBytes() {
		return totalStepBytes;
	}
	
}
