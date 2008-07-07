package com.awebstorm.loadgenerator.robot;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.SubmitMethod;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlImage;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlParameter;
import com.gargoylesoftware.htmlunit.javascript.host.Attribute;

/**
 * Holds the script step information.
 * Details of a step are stored in an Attributes List
 * @author Cromano
 */
public class Step implements Comparable<Step> {
	
	public static enum ActionTypes {
		
		PROPERTIES,
		INVOKE,
		VERIFY_TITLE,
		SET_INPUT_FIELD,
		BUTTON,
		WAIT
		
	}
	
	private BrowserState _state;
	private String _name;
	private int _value;
	private Attributes _list;
	private Logger consoleLog;
	private Logger resultLog;
	private long loadTime;
	private int loadAmount;

	public Step(String name, int value, Attributes list, Logger log) {
		
		_name = name;
		_value = value;
		_list = list;
		consoleLog=log;
		resultLog = Logger.getLogger("loadgenerator.consoleLog.resultLog");
	}
	
	public String getName() {
		return _name;
	}
	
	public Attributes getList() {
		return _list;
	}

	public int compareTo(Step o) {
		return _value - o.get_value();
	}

	public void execute(String jobID, BrowserState browserState) {
		loadTime = 0;
		loadAmount = 0;
		_state = browserState;
		ActionTypes currentType = null;
		boolean stepReturnStatus = true;
		try {
			currentType = ActionTypes.valueOf(_name);
		} catch (IllegalArgumentException e) {
			return;
		}
		if ( currentType == null ) {
			return;
		}
		
		switch (currentType) {
		case WAIT:
			this.waitStep();
			break;
		case INVOKE:
			stepReturnStatus = this.invoke();
			break;
		case VERIFY_TITLE:
			stepReturnStatus = this.verifyTitle();
			break;
		case SET_INPUT_FIELD:
			this.setInputFields();
			break;
		case BUTTON:
			stepReturnStatus = this.button();
			break;
		default:
			//Impossible
			break;
		}
		
		report(stepReturnStatus, jobID);
		
	}

	private boolean button() {
		HtmlButton tempButton = null;
		List<HtmlForm> tempForms = _state.getCurrentPage().getForms();
		
		if(tempForms == null) {
			consoleLog.error("No Forms to search for buttons.");
			return false;
		}
		for (HtmlForm i: tempForms) {
			try {
				tempButton = i.getButtonByName(_list.getValue(0));
			} catch (ElementNotFoundException e) {
				consoleLog.error("No such Button exists");
				return false;
			}
		}
		if(tempButton == null) {
			consoleLog.error("No Button to click.");
			return false;
		}
		try {
			tempButton.click();
		} catch (IOException e) {
			consoleLog.error("Bad Button Clicked.");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void setInputFields() {
		Attributes tempList = _list;
		for (int i = 0;i < tempList.getLength(); i++) {
			_state.getPostList().add(new NameValuePair(tempList.getLocalName(i),tempList.getValue(i)));
		}
	}

	private boolean invoke() {
		String currentPath = _list.getValue(0);
		currentPath = _state.getDomain() + currentPath;
		consoleLog.debug(currentPath);
		boolean tempStatus = true;
		HtmlPage invokePage = null;
		try {
			consoleLog.debug("Get VUser is redirect enabled: " + _state.getVUser().isRedirectEnabled());
			invokePage = (HtmlPage) _state.getVUser().getPage(currentPath);
		} catch (FailingHttpStatusCodeException e) {
			consoleLog.error("Bad Status Code.");
			e.printStackTrace();
			return false;
		} catch (MalformedURLException e) {
			consoleLog.error("MalformedURL");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			consoleLog.error("IO Error during Invoke.");
			e.printStackTrace();
			return false;
		}
		_state.setCurrentPage(invokePage);
		_state.getResponses().add(invokePage.getWebResponse());
		loadTime = invokePage.getWebResponse().getLoadTimeInMilliSeconds();
		loadAmount = invokePage.getWebResponse().getResponseBody().length;
		//consoleLog.debug("Header length: " + invokePage.getWebResponse().getResponseHeaders().size());
/*		List<NameValuePair> tempHeaders = invokePage.getWebResponse().getResponseHeaders();
		for ( int i = 0; i < invokePage.getWebResponse().getResponseHeaders().size(); i++ ) {
			System.out.println("Name: " + tempHeaders.get(i).getName());
			System.out.println("Value: " + tempHeaders.get(i).getValue());
		}*/
		
		//List<HtmlImage> tempImgList = (List<HtmlImage>) invokePage.getDocumentElement().getHtmlElementsByTagName("img");
/*		for(HtmlImage temp: tempImgList) {
			try {
				WebResponse temporary = _state.getVUser().getPage(currentPath + temp.getSrcAttribute()).getWebResponse();
				
				loadTime += temporary.getLoadTimeInMilliSeconds();
				loadAmount += temporary.getResponseBody().length;
			} catch (FailingHttpStatusCodeException e) {
				e.printStackTrace();
				return false;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}*/
		Iterable<HtmlElement> tempList = invokePage.getDocumentElement().getAllHtmlChildElements();
		StringBuffer resourcesCollector = new StringBuffer();
		for(HtmlElement temp: tempList) {
			try {
				WebResponse temporary = null;
				NamedNodeMap tempAttrs = temp.getAttributes();
				String tempAttr = temp.getAttribute("src");
				if ( tempAttrs.getNamedItem("src") != null ) {
					if ( tempAttr.startsWith("http") ) {
						temporary = _state.getVUser().getPage(tempAttr).getWebResponse();
						if ( consoleLog.isDebugEnabled() ) {
							resourcesCollector.append("Resources obtained: " + tempAttr + '\n');
						}
					} else {
						temporary = _state.getVUser().getPage(currentPath + tempAttr).getWebResponse();
						if ( consoleLog.isDebugEnabled() ) {
							resourcesCollector.append("Resources obtained: " + currentPath + tempAttr + '\n');
						}
					}
					loadTime += temporary.getLoadTimeInMilliSeconds();
					loadAmount += temporary.getResponseBody().length;
				}
				//System.out.println(temp.hasAttribute("src"));
			} catch (FailingHttpStatusCodeException e) {
				e.printStackTrace();
				return false;
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return false;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}
		System.out.println(resourcesCollector.toString());
			
		//System.out.println(temp.asText());
		return tempStatus;
	}

	private boolean verifyTitle() {
/*		NodeList temp = _state.getCurrentPage().getElementsByTagName("title");
		for ( int i = 0; i < temp.getLength(); i++) {
			System.out.println(temp.item(i).getNodeValue());
		}*/
		//System.out.println("test1 " + _state.getCurrentPage().getTitleText());
		//System.out.println("test2 " + _list.getValue("title"));
		//System.out.println("test3 " + _list.getValue(0));
		return _state.getCurrentPage().getTitleText().equals(_list.getValue(0));
	}

	private void waitStep() {
		if (_list.getValue(0).equals("")) {
/*			try {
				this.wait(Robot.DEFAULT_WAIT_STEP);
			} catch (InterruptedException e) {
				consoleLog.error("Interrupted Exception during a default WAIT step", e);
				e.printStackTrace();
			}*/
		} else {
/*			try {
				this.wait(Integer.parseInt(_list.getValue(0)));
			} catch (NumberFormatException e) {
				consoleLog.error("Could not determine the wait length during a WAIT Step", e);
				try {
					this.wait(Robot.DEFAULT_WAIT_STEP);
				} catch (InterruptedException e1) {
					consoleLog.error("Interrupted Exception during a WAIT step", e);
					e1.printStackTrace();
				}
				e.printStackTrace();
			} catch (InterruptedException e) {
				consoleLog.error("Interrupted Exception during a WAIT step", e);
				e.printStackTrace();
			}*/
		}
	}
	
	private void report(boolean stepStatus, String jobID) {
		
		StringBuffer tempResult = new StringBuffer();
		
		if ( consoleLog.isDebugEnabled()) {
			consoleLog.debug("Writing Step Results");
		}
		
		tempResult.append(jobID);
		tempResult.append(',');
		tempResult.append(_name);
		tempResult.append(',');
		tempResult.append(_value);
		tempResult.append(',');
		tempResult.append(System.currentTimeMillis());
		tempResult.append(',');
		tempResult.append(loadTime);
		tempResult.append(',');
		tempResult.append(loadAmount);
		tempResult.append(',');
		if ( loadTime == 0 ) {
			loadTime = 1;
		}
		tempResult.append(loadAmount/loadTime);
		tempResult.append(',');
		if (stepStatus) {
			tempResult.append("success");
		} else {
			tempResult.append("failure");
		}
		resultLog.info(tempResult);
	}

	public int get_value() {
		return _value;
	}

	public void set_state(BrowserState _state) {
		this._state = _state;
	}

}
