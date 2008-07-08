package com.awebstorm.loadgenerator.robot;

import java.security.GeneralSecurityException;

import org.apache.log4j.Logger;

import com.gargoylesoftware.htmlunit.Cache;
import com.gargoylesoftware.htmlunit.WebClient;


/**
 * Robot to handle HTML calls.
 * @author Cromano
 * @version 1.0
 *
 */
public class HTMLRobot extends Robot {

	private BrowserState _state;
	
	private boolean javaScriptEnabled;
	private boolean redirectEnabled;
	private boolean throwExceptionOnScriptError;
	protected int cacheSize;
	private boolean useInsecureSSL;
	private boolean popupBlockerEnabled;
	private boolean throwExceptionOnFailingStatusCode;
	private boolean printContentOnFailingStatusCode;
	private String proxyHost;
	private int proxyPort;
	private String browVersionString;

	public static final boolean DEFAULT_IGNORE_OUTSIDE_CONTENT = true;
	public static final boolean DEFAULT_JAVASCRIPT_ENABLED = false;
	public static final boolean DEFAULT_POPUPBLOCKER_ENABLED = false;
	public static final boolean DEFAULT_REDIRECT_ENABLED = true;
	public static final boolean DEFAULT_THROW_EXCEPTION_ON_FAILING_STATUS_CODE = false;
	public static final boolean DEFAULT_THROW_EXCEPTION_ON_SCRIPT_ERROR = false;
	public static final boolean DEFAULT_USE_IN_INSECURE_SSL = false;
	public static final int 	DEFAULT_CACHE_SIZE = 50;
	public static final boolean DEFAULT_PRINT_CONTENT_ON_FAILING_STATUS_CODE = false;
	public static final String  DEFAULT_PROXY_HOST = null;
	public static final int	    DEFAULT_PROXY_PORT = 8099;
	
	/**
	 * Constructor defines the location of the XML RobotScript. All other variables will be
	 * set to default settings.
	 * 
	 * @param scriptLocation Location of the Robot Script
	 * @param consoleLog Logger to be used for general debugging and info
	 * @param resultLog Logger to be used for results
	 * @param errorLog Logger to be used for all errors in or out of debugging
	 */
	public HTMLRobot(String scriptLocation, Logger consoleLog, Logger resultLog){
		
		super(scriptLocation, consoleLog, resultLog);
		if ( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Robot is an HTMLRobot");
		}
		_state = new BrowserState();

	}
	
	/**
	 * Configures the WebClient with the preferences parsed from the Script.
	 */
	public void configureRobot() {
		javaScriptEnabled = Boolean.parseBoolean(prefs.get("javaScriptEnabled"));
		consoleLog.debug("Found pref redirectenabled: " + prefs.get("redirectEnabled"));
		redirectEnabled = Boolean.parseBoolean(prefs.get("redirectEnabled"));
		throwExceptionOnScriptError = Boolean.parseBoolean(prefs.get("throwExceptionOnScriptError"));
		cacheSize = Integer.parseInt(prefs.get("cacheSize"));
		useInsecureSSL = Boolean.parseBoolean(prefs.get("useInsecureSSL"));
		popupBlockerEnabled = Boolean.parseBoolean(prefs.get("popupBlockerEnabled"));
		throwExceptionOnFailingStatusCode = Boolean.parseBoolean(prefs.get("throwExceptionOnFailingStatusCode"));
		printContentOnFailingStatusCode = Boolean.parseBoolean(prefs.get("printContentOnFailingStatusCode"));
		browVersionString = prefs.get("htmlRobotBrowserVersion");
		proxyHost = prefs.get("proxyHost");
		proxyPort = Integer.parseInt(prefs.get("proxyPort"));
		if ( !browVersionString.equals("none")) {
			
		}
		WebClient client;
		if ( proxyHost.equalsIgnoreCase("none") && !browVersionString.equals("none")) {
			BrowserVersionFactory browVerFactory = new BrowserVersionFactory(browVersionString);
			client = new WebClient(browVerFactory.getNewBrowserVersion());
		} else if (!browVersionString.equals("none")){
			BrowserVersionFactory browVerFactory = new BrowserVersionFactory(browVersionString);
			client = new WebClient(browVerFactory.getNewBrowserVersion(),proxyHost,proxyPort);
		} else {
			client = new WebClient();
		}
		
		client.setJavaScriptEnabled(javaScriptEnabled);
		consoleLog.debug("Set pref redirecteEnabled: " + redirectEnabled);
		client.setPopupBlockerEnabled(popupBlockerEnabled);
		client.setRedirectEnabled(redirectEnabled);
		client.setCache(new Cache());
		client.setPrintContentOnFailingStatusCode(printContentOnFailingStatusCode);
		client.setThrowExceptionOnFailingStatusCode(throwExceptionOnFailingStatusCode);
		client.setThrowExceptionOnScriptError(throwExceptionOnScriptError);
		client.setTimeout(timeout);
		try {
			client.setUseInsecureSSL(useInsecureSSL);
		} catch (GeneralSecurityException e) {
			consoleLog.error("Failed Attempt to change InsecureSSL");
			e.printStackTrace();
		}
		
		_state.setVUser(client);
		_state.setDomain(domain);
	}

	/**
	 * Configures the robot, then executes the list of Steps.
	 */
	public void run() {

		int stepNum = 0;
		configureRobot();
		while(!stepQueue.isEmpty()) {
			Step tempStep = stepQueue.poll();
				if ( consoleLog.isDebugEnabled()) {
					consoleLog.debug("Begin Executing Step: " + jobID + ',' + tempStep.getName() + ',' + stepNum );
				}
				tempStep.execute(jobID, _state);
				tempStep = null;
				stepNum++;
		}
		if ( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Robot is closing: " + this.getName());
		}
		
	}
	
}
