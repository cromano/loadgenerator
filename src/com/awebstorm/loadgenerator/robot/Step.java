package com.awebstorm.loadgenerator.robot;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.Attributes;

import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.SubmitMethod;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.html.HtmlButton;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlLink;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlStyle;

/**
 * Holds the script step information and operations.
 * Details of a step are stored in an Attributes List
 * @author Cromano
 */
public class Step implements Comparable<Step> {
	
	public static enum ActionTypes {
		
		STEPS,
		INVOKE,
		VERIFY_TITLE,
		SET_INPUT_FIELD,
		BUTTON,
		WAIT,
		POST,
		FILL_FORM,
		CLICK_LINK
		
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
	
	/**
	 * Name of the Step
	 * @return
	 */
	public String getName() {
		return _name;
	}
	
	/**
	 * Attributes of the Step
	 * @return
	 */
	public Attributes getList() {
		return _list;
	}

	/**
	 * Compare two steps by value
	 */
	public int compareTo(Step o) {
		return _value - o.get_value();
	}

	/**
	 * Execute a Step
	 * @param jobID Robot job ID
	 * @param browserState currentState of the robot's browser
	 */
	public void execute(String jobID, BrowserState browserState) {
		loadTime = 0;
		loadAmount = 0;
		_state = browserState;
		ActionTypes currentType = null;
		boolean stepReturnStatus = true;
		try {
			currentType = ActionTypes.valueOf(_name);
		} catch (IllegalArgumentException e) {
			consoleLog.error("Unknown Step type found.",e);
			return;
		}
		if ( currentType == null ) {
			return;
		}
		
		switch (currentType) {
		case STEPS:
			return;
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
		case FILL_FORM:
			stepReturnStatus = this.fillForm();
			break;
		case POST:
			stepReturnStatus = this.post();
			break;
		case CLICK_LINK:
			stepReturnStatus = this.clickLink();
			break;
		default:
			consoleLog.warn("Unknown Step type found.");
			stepReturnStatus = false;
			break;
		}
		
		report(stepReturnStatus, jobID);
		
	}
	
	/**
	 * Click a link on a webpage.
	 * Not Yet Implemented
	 * @return
	 */
	private boolean clickLink() {
		
		
		
		return false;
	}

	/**
	 * Standard POST operation using the parameters stored in the postList
	 * @return Success?
	 */
	private boolean post() {
		boolean tempStatus = true;
		WebRequestSettings newSettings = null;
		HtmlPage postPage = null;
		String currentPath = _state.getDomain() + _list.getValue(0);
		try {
			newSettings = new WebRequestSettings(new URL(currentPath),SubmitMethod.POST);
		} catch (MalformedURLException e1) {
			consoleLog.error("Bad URL passed to a POST operation.",e1);
			return false;
		}
		newSettings.setRequestParameters(_state.getPostList());
		try {
			postPage = (HtmlPage) _state.getVUser().getPage(newSettings);
		} catch (FailingHttpStatusCodeException e) {
			consoleLog.error("POST operation, " + _name + " has a bad status message.",e);
			return false;
		} catch (IOException e) {
			consoleLog.error("IOException thrown during a POST operation.",e);
			return false;
		}
		_state.getPostList().clear();
		loadTime = postPage.getWebResponse().getLoadTimeInMilliSeconds();
		try {
			loadAmount = postPage.getWebResponse().getContentAsStream().available();
		} catch (IOException e1) {
			consoleLog.error("IOException while reading content as from post stream to count loadAmount.", e1);
		}
		
		Iterable<HtmlElement> tempList = postPage.getDocumentElement().getAllHtmlChildElements();
		StringBuffer resourcesCollector = new StringBuffer();
		String tempAttr;
		WebResponse temporary;
		NamedNodeMap tempAttrs;
		for(HtmlElement temp: tempList) {
			try {
				temporary = null;
				tempAttrs = temp.getAttributes();
				tempAttr = temp.getAttribute("src");
				if ( tempAttrs.getNamedItem("src") != null ) {
					if ( !tempAttr.startsWith("http") ) {
						if ( tempAttr.charAt(0) == '/' ) {
							tempAttr = _state.getDomain() + tempAttr;
						} else {
							tempAttr = _state.getDomain() + '/' + tempAttr;
						}
					}
					if (_state.addUrlToHistory(tempAttr)) {
						temporary = _state.getVUser().getPage(tempAttr).getWebResponse();
						if ( consoleLog.isDebugEnabled() ) {
							resourcesCollector.append("Resources obtained: " + tempAttr + '\n');
						}
						loadTime += temporary.getLoadTimeInMilliSeconds();
						loadAmount += temporary.getResponseBody().length;
						if ( temporary.getStatusCode() != 200 ) {
							tempStatus = false;
						}
						
					}
				} else if (temp.getClass() == HtmlStyle.class ) {
					tempAttrs = ((HtmlStyle) temp).getAttributes();
					tempAttr = ((HtmlStyle) temp).getTextContent();
					String aResource;
					LinkedList<String> styleResourceList = StyleParsers.parseStyleElementText(tempAttr);
					while (!styleResourceList.isEmpty()){
						aResource = styleResourceList.poll();
						if ( !aResource.startsWith("http") ) {
							if ( aResource.charAt(0) == '/' ) {
								aResource = _state.getDomain() + aResource;
							} else {
								aResource = _state.getDomain() + '/' + aResource;
							}
						}
						if (_state.addUrlToHistory(aResource)) {
							temporary = _state.getVUser().getPage(aResource).getWebResponse();
							if ( consoleLog.isDebugEnabled() ) {
								resourcesCollector.append("Import Resources obtained: " + aResource + '\n');
							}

							loadTime += temporary.getLoadTimeInMilliSeconds();
							loadAmount += temporary.getResponseBody().length;
							if ( temporary.getStatusCode() != 200 ) {
								tempStatus = false;
							}
							if(aResource.endsWith(".css")) {
								LinkedList<String> tempHolder = StyleParsers.parseStyleSheetText(temporary.getContentAsString());
								String path = StyleParsers.subDirBuilder(temporary.getUrl());
								while (!tempHolder.isEmpty()) {
									styleResourceList.add(path + tempHolder.poll());
								}
							}
						}

					}
				} else if (temp.getClass() == HtmlLink.class ) {
					String aResource;
					tempAttrs = ((HtmlLink) temp).getAttributes();
					for ( int i = 0; i < tempAttrs.getLength(); i++ ) {
						if ( tempAttrs.getNamedItem("rel").getNodeValue().equals("stylesheet") ) {
							aResource = tempAttrs.getNamedItem("href").getNodeValue();
							if ( !aResource.startsWith("http") ) {
								if ( aResource.charAt(0) == '/' ) {
									aResource = _state.getDomain() + aResource;
								} else {
									aResource = _state.getDomain() + '/' + aResource;
								}
							}
							if (_state.addUrlToHistory(aResource)) {
								temporary = _state.getVUser().getPage(aResource).getWebResponse();
								if ( consoleLog.isDebugEnabled() ) {
									resourcesCollector.append("Link Resources obtained: " + aResource + '\n');
								}
								loadTime += temporary.getLoadTimeInMilliSeconds();
								loadAmount += temporary.getResponseBody().length;
								if ( temporary.getStatusCode() != 200 ) {
									tempStatus = false;
								}

								LinkedList<String> styleResourceList = StyleParsers.parseStyleSheetText(temporary.getContentAsString());
								while (!styleResourceList.isEmpty()){
									aResource = StyleParsers.subDirBuilder(temporary.getUrl()) + styleResourceList.poll();
									if ( !aResource.startsWith("http") ) {
										if ( aResource.charAt(0) == '/' ) {
											aResource = _state.getDomain() + aResource;
										} else {
											aResource = _state.getDomain() + '/' + aResource;
										}
									}
									if (_state.addUrlToHistory(aResource)) {
										temporary = _state.getVUser().getPage(aResource).getWebResponse();
										if ( consoleLog.isDebugEnabled() ) {
											resourcesCollector.append("Style Sheet Pic Resources obtained: " + aResource + '\n');
										}
										loadTime += temporary.getLoadTimeInMilliSeconds();
										loadAmount += temporary.getResponseBody().length;
										if ( temporary.getStatusCode() != 200 ) {
											tempStatus = false;
										}
									}
								}
							}
						}
					}
				}
			} catch (FailingHttpStatusCodeException e) {
				consoleLog.error("Bad Status Code.",e);
				tempStatus = false;
			} catch (MalformedURLException e) {
				consoleLog.error("Bad URL Entered during resource retrieval.",e);
				tempStatus = false;
			} catch (IOException e) {
				consoleLog.error("IO Error Occured during resource retrieval.",e);
				tempStatus = false;
			}
		}
		if ( consoleLog.isDebugEnabled() ) {
			consoleLog.debug(resourcesCollector.toString());
		}
		return tempStatus;
	}
	
	/**
	 * Fill a Form with information. Usually followed by a BUTTON or CLICK.
	 * Not Yet Implemented.
	 * @return
	 */
	private boolean fillForm() {
		boolean tempStatus = true;
		
		
		return tempStatus;
	}

	/**
	 * Press the button whose name is given
	 * @return
	 */
	private boolean button() {
		HtmlButton tempButton = null;
		if( _state.getCurrentPage() == null )
			return false;
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

	/**
	 * Place the parameters for a POST operation in the postList.
	 */
	private void setInputFields() {
		Attributes tempList = _list;
		for (int i = 0;i < tempList.getLength(); i++) {
			_state.getPostList().add(new NameValuePair(tempList.getLocalName(i),tempList.getValue(i)));
		}
	}

	/**
	 * Standard GET operation
	 * @return
	 */
	private boolean invoke() {
		String currentPath = _list.getValue(0);
		currentPath = _state.getDomain() + currentPath;
		boolean tempStatus = true;
		HtmlPage invokePage = null;
		try {
			invokePage = (HtmlPage) _state.getVUser().getPage(currentPath);
		} catch (FailingHttpStatusCodeException e) {
			consoleLog.error("Bad Status Code.",e);
			return false;
		} catch (MalformedURLException e) {
			consoleLog.error("MalformedURL",e);
			return false;
		} catch (SocketTimeoutException e ) {
			consoleLog.info("Socket Timed Out from licit/illicit factors.");
			return false;
		} catch (IOException e) {
			consoleLog.error("IO Error during Invoke.",e);
			return false;
		}
		_state.addUrlToHistory(currentPath);
		_state.setCurrentPage(invokePage);
		_state.getResponses().add(invokePage.getWebResponse());
		loadTime = invokePage.getWebResponse().getLoadTimeInMilliSeconds();
		loadAmount = invokePage.getWebResponse().getResponseBody().length;
		
		Iterable<HtmlElement> tempList = invokePage.getDocumentElement().getAllHtmlChildElements();
		StringBuffer resourcesCollector = new StringBuffer();
		String tempAttr;
		WebResponse temporary;
		NamedNodeMap tempAttrs;
		for(HtmlElement temp: tempList) {
			try {
				temporary = null;
				tempAttrs = temp.getAttributes();
				tempAttr = temp.getAttribute("src");
				if ( tempAttrs.getNamedItem("src") != null ) {
					if ( !tempAttr.startsWith("http") ) {
						if ( tempAttr.charAt(0) == '/' ) {
							tempAttr = _state.getDomain() + tempAttr;
						} else {
							tempAttr = _state.getDomain() + '/' + tempAttr;
						}
					}
					if (_state.addUrlToHistory(tempAttr)) {
						temporary = _state.getVUser().getPage(tempAttr).getWebResponse();
						if ( consoleLog.isDebugEnabled() ) {
							resourcesCollector.append("Resources obtained: " + tempAttr + '\n');
						}
						loadTime += temporary.getLoadTimeInMilliSeconds();
						loadAmount += temporary.getResponseBody().length;
						if ( temporary.getStatusCode() != 200 ) {
							tempStatus = false;
						}
					}
				//Download css components
				} else if (temp.getClass() == HtmlStyle.class ) {
					tempAttrs = ((HtmlStyle) temp).getAttributes();
					tempAttr = ((HtmlStyle) temp).getTextContent();
					LinkedList<String> styleResourceList = StyleParsers.parseStyleElementText(tempAttr);
					String aResource;
					while (!styleResourceList.isEmpty()){
						aResource = styleResourceList.poll();
						if ( !aResource.startsWith("http") ) {
							if ( aResource.charAt(0) == '/' ) {
								aResource = _state.getDomain() + aResource;
							} else {
								aResource = _state.getDomain() + '/' + aResource;
							}
						}
						if (_state.addUrlToHistory(aResource)) {
							temporary = _state.getVUser().getPage(aResource).getWebResponse();
							if ( consoleLog.isDebugEnabled() ) {
								resourcesCollector.append("Import resources obtained: " + aResource + '\n');
							}
							loadTime += temporary.getLoadTimeInMilliSeconds();
							loadAmount += temporary.getResponseBody().length;
							if ( temporary.getStatusCode() != 200 ) {
								tempStatus = false;
							}
							if(aResource.endsWith(".css")) {
								LinkedList<String> tempHolder = StyleParsers.parseStyleSheetText(temporary.getContentAsString());
								String path = StyleParsers.subDirBuilder(temporary.getUrl());
								while (!tempHolder.isEmpty()) {
									System.out.println(tempHolder.peek());
									styleResourceList.add(path + tempHolder.poll());
								}
							}
						}
					}
				} else if (temp.getClass() == HtmlLink.class ) {
					String aResource;
					tempAttrs = ((HtmlLink) temp).getAttributes();
					for ( int i = 0; i < tempAttrs.getLength(); i++ ) {
						if ( tempAttrs.getNamedItem("rel").getNodeValue().equals("stylesheet") ) {
							aResource = tempAttrs.getNamedItem("href").getNodeValue();
							if ( !aResource.startsWith("http") ) {
								if ( aResource.charAt(0) == '/' ) {
									aResource = _state.getDomain() + aResource;
								} else {
									aResource = _state.getDomain() + '/' + aResource;
								}
							}
							if (_state.addUrlToHistory(aResource)) {
								temporary = _state.getVUser().getPage(aResource).getWebResponse();
								if ( consoleLog.isDebugEnabled() ) {
									resourcesCollector.append("Link Resources obtained: " + aResource + '\n');
								}
								loadTime += temporary.getLoadTimeInMilliSeconds();
								loadAmount += temporary.getResponseBody().length;
								if ( temporary.getStatusCode() != 200 ) {
									tempStatus = false;
								}
								LinkedList<String> styleResourceList = StyleParsers.parseStyleSheetText(temporary.getContentAsString());
								while (!styleResourceList.isEmpty()){
									aResource = StyleParsers.subDirBuilder(temporary.getUrl()) + styleResourceList.poll();
									if ( !aResource.startsWith("http") ) {
										if ( aResource.charAt(0) == '/' ) {
											aResource = _state.getDomain() + aResource;
										} else {
											aResource = _state.getDomain() + '/' + aResource;
										}
									}
									if (_state.addUrlToHistory(aResource)) {
										temporary = _state.getVUser().getPage(aResource).getWebResponse();
										if ( consoleLog.isDebugEnabled() ) {
											resourcesCollector.append("Style Sheet Pic Resources obtained: " + aResource + '\n');
										}
										loadTime += temporary.getLoadTimeInMilliSeconds();
										loadAmount += temporary.getResponseBody().length;
										if ( temporary.getStatusCode() != 200 ) {
											tempStatus = false;
										}
									}
								}
							}
						}
					}
				}
			} catch (FailingHttpStatusCodeException e) {
				consoleLog.error("Bad Status Code.",e);
				tempStatus = false;
			} catch (MalformedURLException e) {
				consoleLog.error("Bad URL Entered during resource retrieval.",e);
				tempStatus = false;
			} catch (IOException e) {
				consoleLog.error("IO Error Occured during resource retrieval.",e);
				tempStatus = false;
			}
		}
		if ( consoleLog.isDebugEnabled() ) {
			consoleLog.debug(resourcesCollector.toString());
		}
		return tempStatus;
	}

	/**
	 * Verify the currentpage title with an Attribute value.
	 * @return
	 */
	private boolean verifyTitle() {
		if ( _state.getCurrentPage() != null )
			return _state.getCurrentPage().getTitleText().equals(_list.getValue(0));
		return false;
	}

	/**
	 * Instruct this thread to wait for a given period of time.
	 */
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
	
	/**
	 * Report the results of a Step.
	 * @param stepStatus Success if true, Failure if false
	 * @param jobID Current job ID
	 */
	private void report(boolean stepStatus, String jobID) {
		
		StringBuffer tempResult = new StringBuffer();
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

	/**
	 * Retrieve the value of a Step.
	 * @return
	 */
	public int get_value() {
		return _value;
	}

	/**
	 * Set the browser state of a Step.
	 * @param _state
	 */
	public void set_state(BrowserState _state) {
		this._state = _state;
	}

}
