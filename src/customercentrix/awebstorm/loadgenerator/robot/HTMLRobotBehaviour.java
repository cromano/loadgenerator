package customercentrix.awebstorm.loadgenerator.robot;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.httpclient.NameValuePair;
import org.jbehave.core.Block;
import org.jbehave.core.Ensure;
import org.jbehave.core.behaviour.BehaviourClass;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;


/**
 * Test the behaviour of the HTMLRobot class
 * @author Cromano
 *
 */
public class HTMLRobotBehaviour {
	
	private HTMLRobot newRobot;
	private Page currentPage;
	private TopLevelWindow testWindow;
	
	
	public void shouldGetAPageRequestTime() throws Exception {
		WebRequestSettings newSettings = new WebRequestSettings(new URL("http://www.customercentrix.com"),SubmitMethod.GET);
		currentPage = newRobot.getPage(testWindow, newSettings);
		if(currentPage != null) {
			long testTime = currentPage.getWebResponse().getLoadTimeInMilliSeconds();
			Ensure.that(testTime > 0);
		} else {
			System.err.println("Failed to load page:" + newSettings.getURL());
		}

	}
	public void shouldGetASuccessfulPageRequest() throws Exception {
		WebRequestSettings newSettings = new WebRequestSettings(new URL("http://www.customercentrix.com"),SubmitMethod.GET);
		currentPage = newRobot.getPage(testWindow, newSettings);
		if(currentPage != null) {
			int statusCode = currentPage.getWebResponse().getStatusCode();
			Ensure.that(statusCode > 199 && statusCode < 300);
		} else {
			System.err.println("Failed to load page:" + newSettings.getURL());
		}

	}
	public void shouldGetAPageNotFoundCode() throws Exception {
		currentPage = null;
		WebRequestSettings newSettings = new WebRequestSettings(new URL("http://www.customercentrix.com"),SubmitMethod.GET);
		try {
			currentPage = newRobot.getPage(testWindow, newSettings);
		} catch ( Exception e ) {
			
		}
		if (currentPage != null ) {
			int statusCode = currentPage.getWebResponse().getStatusCode();
			Ensure.that(statusCode != 404 || statusCode != 410 );
		} else {
			System.err.println("Failed to load page:" + newSettings.getURL());
		}

	}
	
	public void shouldGetALoginNotFoundError() throws Exception {
		currentPage = null;
		WebRequestSettings newSettings = new WebRequestSettings(new URL("https://webmail.unm.edu/"),SubmitMethod.POST);
		
		
		ArrayList<NameValuePair> testList = new ArrayList<NameValuePair>();
		testList.add(new NameValuePair("Username","kieshma"));
		testList.add(new NameValuePair("Password",""));
		testList.add(new NameValuePair("login",""));
		newSettings.setRequestParameters(testList);
		//newSettings.setEncodingType(FormEncodingType.URL_ENCODED);
		
		currentPage = newRobot.getPage(testWindow, newSettings);

		if (currentPage != null ) {
			int statusCode = currentPage.getWebResponse().getStatusCode();
			System.out.println(statusCode);
			System.out.println(currentPage.getWebResponse().getStatusMessage());
			System.out.println(currentPage.getWebResponse().getResponseBody());
			List<NameValuePair> resultHeaderList;
			resultHeaderList = currentPage.getWebResponse().getResponseHeaders();
			ListIterator tempIterator = resultHeaderList.listIterator();
			while(tempIterator.hasNext()){
				NameValuePair next = (NameValuePair) tempIterator.next();
				System.out.println(next.getName() + ": " + next.getValue());
			}
			System.out.println(currentPage.getWebResponse().getContentAsString());
			//currentPage.getWebResponse().
			//PageCreator.createPage(currentPage.getWebResponse(),testWindow);
			
			//Go through the pieces of the content
			final HtmlPage parserPage = 
				new HtmlPage(new URL("https://webmail.unm.edu/webmail.jpg"), currentPage.getWebResponse(), testWindow);
			List anchors = parserPage.getAnchors();
			tempIterator = anchors.listIterator();
			while(tempIterator.hasNext()) {
				HtmlAnchor testAnchor = (HtmlAnchor) tempIterator.next();
				System.out.println(testAnchor.asText());
			}
			
			Ensure.that(statusCode > 199 && statusCode < 300 );
			Ensure.that(currentPage.getWebResponse().getResponseBody().length > 0);
		} else {
			System.err.println("Failed to load page:" + newSettings.getURL());
		}
		
	}
	

	public void setUp() {

		newRobot = new HTMLRobot();

		newRobot.htmlRobotClient.setIgnoreOutsideContent(true);
		newRobot.htmlRobotClient.setJavaScriptEnabled(false);
		newRobot.htmlRobotClient.setPopupBlockerEnabled(false);
		newRobot.htmlRobotClient.setRedirectEnabled(true);
		newRobot.htmlRobotClient.setThrowExceptionOnFailingStatusCode(false);
		newRobot.htmlRobotClient.setThrowExceptionOnScriptError(false);
		try {
			newRobot.htmlRobotClient.setUseInsecureSSL(false);
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		newRobot.htmlRobotClient.setTimeout(5000);
		Cache testCache = new Cache();
		testCache.setMaxSize(50);
		newRobot.htmlRobotClient.setCache(testCache);
		testWindow = new TopLevelWindow("testWindow",newRobot.htmlRobotClient);
		newRobot.htmlRobotClient.setCurrentWindow(testWindow);
		
		//newRobot.htmlRobotClient.
		
		
		//newRobot.htmlRobotClient.setJavaScriptEngine(new JavaScriptEngine());
		
		//newRobot.htmlRobotClient.
		
		System.out.println(newRobot.htmlRobotClient.isCookiesEnabled());
        //Returns true if cookies are enabled. 
		System.out.println(newRobot.htmlRobotClient.isJavaScriptEnabled());
        //Returns true if JavaScript is enabled and the script engine was loaded successfully. 
		System.out.println(newRobot.htmlRobotClient.isPopupBlockerEnabled());
        //Returns true if the popup window blocker is enabled. 
		System.out.println(newRobot.htmlRobotClient.isRedirectEnabled()); 
        //Returns whether or not redirections will be followed automatically on receipt of a redirect status code from the server. 
		System.out.println(newRobot.htmlRobotClient.isThrowExceptionOnFailingStatusCode()); 
        //Returns true if an exception will be thrown in the event of a failing response code. 
		System.out.println(newRobot.htmlRobotClient.isThrowExceptionOnScriptError()); 
		//newRobot.htmlRobotClient.setCurrentWindow(new WebWindowRobot());
		
		//newRobot.htmlRobotClient.HTMLUNIT_COOKIE_POLICY = "BROWSER_COMPATIBILITY";
		
	}
	
	public void tearDown() {
		newRobot = null;
	}
	
	/*

	*//**
	 * The target should return a failing code in this test
	 * @throws FailingHttpStatusCodeException
	 *//*
	public void shouldCatchTargetFailures() throws FailingHttpStatusCodeException {
		try {
			newRobot.setTargetURI(new URI ("http:", "www.thishtmltargetdoesnotexist.com","/",""));
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		final WebClient testClient = newRobot.getRobotClient();
		testClient.setTimeout(5000);

		try {
			testURL = new URL("http://www.customercentrix.com/");
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Ensure.throwsException(FailingHttpStatusCodeException.class, new Block() {
				public void run() throws Exception {
					URL testURL = null;
					try {
						 testURL = new URL("http://www.unknownnon-existantserver.com/");
					} catch (MalformedURLException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					HtmlPage testPage = (HtmlPage) testClient.getPage(testURL);
				}
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public void shouldCatchTargetTimeout() {
		
	}
	
	public void shouldHandleATargetGoodResponse() {
		
	}
	
	public void shouldAutoRedirect() {
		
	}
	
	public void shouldUseBrowserCaching(){
		
	}*/
	
	
}
