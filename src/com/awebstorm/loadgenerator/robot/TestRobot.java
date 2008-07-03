package com.awebstorm.loadgenerator.robot;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.xml.sax.Attributes;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.DefaultPageCreator;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.PageCreator;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlAttr;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class TestRobot extends Robot {

	protected TestRobot(String scriptLocation, Logger consoleLog,
			Logger resultLog, Logger errorLog) {
		super(scriptLocation, consoleLog, resultLog, errorLog);
		if ( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Robot is an HTMLRobot");
		}
		postList = new ArrayList<NameValuePair>();
		requestTime = 0;
		setDefaultHTMLRobotPreferences();
		//Too much trouble to place default BrowserVersion in prefs
		htmlRobotBrowserVersion = BrowserVersion.INTERNET_EXPLORER_7_0;
		// TODO Auto-generated constructor stub
	}

	private WebClient htmlRobotClient;
	private long requestTime;
	//The ArrayList of NameValuePairs to be used in the next POST operation
	private ArrayList<NameValuePair> postList;
	private TopLevelWindow basicHtmlRobotWindow;
	private WebResponse htmlRobotResponse;
	private WebRequestSettings htmlRobotSettings;
	//private String browserType;
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
	private static Logger consoleLog;
	private static Logger resultLog;
	private static Logger errorLog;
	//private static PropertyResourceBundle loadGeneratorProperties;
	//private static final String LOAD_GEN_PROPS_LOC = "LoadGenerator.properties";
	private static final String LOAD_GEN_LOG_PROPS_LOC = "LoadGeneratorLog.properties";
	private static final String ROBOT_PROPS_LOC = "Robot.properties";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//PropertyConfigurator.configureAndWatch();
		
		consoleLog = Logger.getLogger("loadgenerator.consoleLog");
		resultLog = Logger.getLogger("loadgenerator.consoleLog.resultLog");
		errorLog = Logger.getLogger("loadgenerator.consoleLog.errorLog");
		
		
		if( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Logs configured.");
			errorLog.debug("ErrorLog GO!!!");
		}
		
		//Load Properties
		//loadGeneratorProperties = (PropertyResourceBundle) ResourceBundle.getBundle(LOAD_GEN_PROPS_LOC);
		
		
		new TestRobot("C:\\Users\\Cromano\\workspace\\Robot\\bin\\com\\awebstorm\\loadgenerator\\robot\\Script.xml",consoleLog,resultLog,errorLog).run();
		
	}
	
	/**
	 * Loads the preferences specific to an HTMLRobot
	 */
	private void setDefaultHTMLRobotPreferences() {
		
		if ( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Loading HTMLRobot default properties");
		}
			
		//browserType = robotProperties.getString("defaultBrowserType");
		ignoreOutsideContent = Boolean.parseBoolean(robotProperties.getString("defaultIgnoreOutsideContent"));
		this.javaScriptEnabled = Boolean.parseBoolean(robotProperties.getString("defaultJavaScriptEnabled"));
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
	
	private void configureHtmlRobotClient() {

		htmlRobotClient.setJavaScriptEnabled(this.javaScriptEnabled);
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
	 * Test runner to create generic html requests
	 */
	public void run() {

		
		currentElement = "";
		//True is a success, false is a failure
		//WAIT steps are not counted and VERIFY steps are uncertain
		boolean stepReturnStatus = false;
		Attributes stepAttributes = null;
		Long tempTime = Long.valueOf("0");
		int tempAmount = 0;
		int stepID = 0;
		StringBuffer tempResult = new StringBuffer();
		String jobID = "";
		URL pageRequested = null;

		//TODO
		//Read/Parse the head of the XML doc
		//Configure the fields from the Header settings
		
		if (proxyHost.equals("null")) {
			htmlRobotClient = new WebClient(htmlRobotBrowserVersion);
		} else {
			htmlRobotClient = new WebClient(htmlRobotBrowserVersion, proxyHost, proxyPort);
		}
		basicHtmlRobotWindow = new TopLevelWindow("default",htmlRobotClient);
		htmlRobotPageCreator = new DefaultPageCreator();
		
		try {
			pageRequested = new URL("http","customercentrix.com",-1,"/products.htm");
		} catch (MalformedURLException e) {
			consoleLog.error("Malformed URL passed to WebClient.",e);
			e.printStackTrace();
		}
		
		htmlRobotSettings = new WebRequestSettings(pageRequested);
		jobID = pageRequested.toExternalForm();
		stepID = 0;

		configureHtmlRobotClient();
		HtmlPage tempPage = null;
		try {
			tempPage = (HtmlPage) htmlRobotClient.getPage(basicHtmlRobotWindow, htmlRobotSettings);
			ArrayList<HtmlForm> temp = (ArrayList<HtmlForm>) tempPage.getForms();
			for( int i =0; i < temp.size(); i++) {
				System.out.println(((HtmlAttr)(temp.get(i).getAttributesCollection().toArray()[0])).getLocalName());
			}
		} catch (FailingHttpStatusCodeException e) {
			consoleLog.error("Bad Status Code Returned",e);
			e.printStackTrace();
		} catch (IOException e) {
			consoleLog.error("Could not communicate with the page: " + pageRequested.toExternalForm(),e);
			e.printStackTrace();
		}
		tempTime = tempPage.getWebResponse().getLoadTimeInMilliSeconds();
		try {
			tempAmount = tempPage.getWebResponse().getContentAsStream().available();
			tempAmount = tempPage.getWebResponse().getResponseBody().length;
			
		} catch (IOException e) {
			consoleLog.error("Could not communicate with the page: " + pageRequested.toExternalForm(),e);
			e.printStackTrace();
		}
		
		//tempPage.
		
	/*	try {
			//tempPage = (HtmlPage) htmlRobotClient.getPage(basicHtmlRobotWindow, htmlRobotSettings);
			tempPage = (HtmlPage) htmlRobotClient.getPage("http://www.customercentrix.com");
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
		//Sample Results
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
				tempResult.append("Amount: " + tempAmount);
				tempResult.append(',');
				tempResult.append(tempAmount/tempTime);
				tempResult.append(',');
				if (stepReturnStatus) {
					tempResult.append("success");
				} else {
					tempResult.append("failure");
				}

				resultLog.info(tempResult);

		
		
	}

}
