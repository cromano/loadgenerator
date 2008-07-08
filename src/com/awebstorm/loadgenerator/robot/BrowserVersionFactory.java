package com.awebstorm.loadgenerator.robot;

import com.gargoylesoftware.htmlunit.BrowserVersion;

/**
 * Creates a BrowserVersion from a comma-delimited String.
 * @author Cromano
 *
 */
public class BrowserVersionFactory {

	private String applicationName;
	private String applicationVersion;
	private String userAgent;
	private String applicationJavaVersion;
	private float  applicationVersionFloat;
	
	/**
	 * Construct a BrowserVersionFactory using a comma-delimited String.
	 * @param parsedData Data String
	 */
	public BrowserVersionFactory (String parsedData) {
		
		int tempIndex1 = 0;
		int tempIndex2 = 0;
		tempIndex1 = parsedData.indexOf(',');
		tempIndex2 = parsedData.indexOf(',', tempIndex1 + 1);
		applicationName = parsedData.substring(0, tempIndex1);
		applicationVersion = parsedData.substring(tempIndex1 + 1, tempIndex2);
		tempIndex1 = parsedData.indexOf(',', tempIndex2 + 1);
		userAgent = parsedData.substring(tempIndex2 + 1, tempIndex1);
		tempIndex2 = parsedData.indexOf(',', tempIndex1 + 1);
		applicationJavaVersion = parsedData.substring(tempIndex1 + 1, tempIndex2);
		applicationVersionFloat = Float.parseFloat(parsedData.substring(tempIndex2 + 1));
		
	}
	
	/**
	 * Retrieve a new BrowserVersion from this factory.
	 * @return A new BrowserVersion
	 */
	public BrowserVersion getNewBrowserVersion () {
		
		return new BrowserVersion(
				applicationName,
				applicationVersion,
				userAgent,
				applicationJavaVersion,
				applicationVersionFloat
		);
		
	}
	
}
