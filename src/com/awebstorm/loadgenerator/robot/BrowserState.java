package com.awebstorm.loadgenerator.robot;

import java.util.ArrayList;
import java.util.Stack;

import org.apache.commons.httpclient.NameValuePair;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.TopLevelWindow;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequestSettings;
import com.gargoylesoftware.htmlunit.WebResponse;
import com.gargoylesoftware.htmlunit.WebWindow;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class BrowserState {

	private WebClient vUser;
	private Stack<WebResponse> responses;
	private Stack<Page> pages;
	private HtmlPage currentPage;
	private WebRequestSettings vUserSettings;
	private WebWindow currentWindow;
	private ArrayList<NameValuePair> postList;
	private String domain;
	
	/**
	 * Default Constructor ensures no NullPointerExceptions
	 */
	public BrowserState() {
		
		vUser = new WebClient();
		responses = new Stack<WebResponse>();
		pages = new Stack<Page>();
		currentWindow = new TopLevelWindow("none", vUser);
		postList = new ArrayList<NameValuePair>();
	}
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public WebClient getVUser() {
		return vUser;
	}
	public void setVUser(WebClient user) {
		vUser = user;
	}
	public Stack<WebResponse> getResponses() {
		return responses;
	}
	public void setResponses(Stack<WebResponse> responses) {
		this.responses = responses;
	}
	public Stack<Page> getPages() {
		return pages;
	}
	public void setPages(Stack<Page> pages) {
		this.pages = pages;
	}
	public HtmlPage getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(HtmlPage currentPage) {
		this.currentPage = currentPage;
	}
	public WebRequestSettings getVUserSettings() {
		return vUserSettings;
	}
	public void setVUserSettings(WebRequestSettings userSettings) {
		vUserSettings = userSettings;
	}
	public WebWindow getCurrentWindow() {
		return currentWindow;
	}
	public void setCurrentWindow(WebWindow currentWindow) {
		this.currentWindow = currentWindow;
	}
	public ArrayList<NameValuePair> getPostList() {
		return postList;
	}
	public void setPostList(ArrayList<NameValuePair> postList) {
		this.postList = postList;
	}

}