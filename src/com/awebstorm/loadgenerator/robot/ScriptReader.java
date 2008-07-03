package com.awebstorm.loadgenerator.robot;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.PriorityQueue;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;


/** Script Parser
 * 
 * @author Cromano
 * @version 1.0
 *
 */
public class ScriptReader extends DefaultHandler {
	
	private String _script;
	private Logger _consoleLog;
	private String tempString;
	private Step tempStep;
	private PriorityQueue<Step> stepQueue;
	private HashMap<String,String> prefs;
	private boolean propertiesMode;
	private int stepCounter;

		public void run(String script,Logger consoleLog, PriorityQueue<Step> stepQueue, HashMap<String,String> prefs) {

			_script = script;
			SAXParserFactory newFactory;
			newFactory = SAXParserFactory.newInstance();
			SAXParser myXMLParser = null;
			File scriptFile = new File(_script);
			this.stepQueue = stepQueue;
			this.prefs = prefs;
			propertiesMode = true;
			stepCounter = 0;
			_consoleLog = consoleLog;
			
			try {
				myXMLParser = newFactory.newSAXParser();
			} catch (ParserConfigurationException e) {
				consoleLog.fatal("Error creating new parser.",e);
				e.printStackTrace();
			} catch (SAXException e) {
				consoleLog.fatal("SAX Parser exception, Script is unsafe.",e);
				e.printStackTrace();
			}

			try {
				myXMLParser.parse(scriptFile,this);
			} catch (FileNotFoundException e) {
				consoleLog.fatal("Script file not found.");
				e.printStackTrace();
			} catch (SAXException e) {
				consoleLog.fatal("SAX Parser exception, Script is unsafe.",e);
				e.printStackTrace();
			} catch (IOException e) {
				consoleLog.fatal("SAX Parser exception, Script is unsafe.",e);
				e.printStackTrace();
			}
			
		}
	
		public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {

			if (!propertiesMode) {
				Attributes newList = new AttributesImpl(attributes);
				tempStep = new Step(qName,stepCounter,newList,_consoleLog);
				_consoleLog.debug("Added Step: " + qName + " Value :" + attributes.getValue(0));
				stepQueue.add(tempStep);
				stepCounter++;
			}
		}


		public void characters(char[] ch, int start, int length) throws SAXException {
			tempString = new String(ch,start,length);
		}

		public void endElement(String uri, String localName,
				String qName) throws SAXException {

			if ( propertiesMode) {
				if (qName.equals("Properties")) {
					propertiesMode = false;
					return;
				}
				_consoleLog.debug("Added Pref: " + qName + " Value:" + tempString);
				prefs.put(qName,tempString);
			}

		}
		
		/**
		 * Runs the .run() method
		 * @param args Should contain the input XML file and the output file
		 */
		public static void main(String[] args) {
			
			Logger _consoleLog2 = Logger.getLogger("loadgenerator.consoleLog.ScriptReader");
			PriorityQueue<Step> stepQueue = new PriorityQueue<Step>();
			HashMap<String,String> prefs = new HashMap<String,String>();
			Long tempTime = System.currentTimeMillis();
			new ScriptReader().run(args[0],_consoleLog2, stepQueue, prefs );
			tempTime = System.currentTimeMillis() - tempTime;
			System.out.println("Time: " + tempTime);
			prefs.values().iterator();
			while ( !stepQueue.isEmpty()) {
				System.out.println(stepQueue.poll().getName());
			}
		}

}
