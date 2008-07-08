package com.awebstorm.loadgenerator.robot;

import java.util.LinkedList;

public class StyleParser {

	private LinkedList<String> sheetResources;
	
	public StyleParser() {
		sheetResources = new LinkedList<String>();
	}
	
	public void parseStyle ( String text ) {
		
		StringBuffer parserBuffer = new StringBuffer(text.trim());
		int start = 0;
		for ( int i = 0; i < parserBuffer.length(); i++) {
			if ( parserBuffer.charAt(i) == '@' && parserBuffer.substring(i+1, i+7).equalsIgnoreCase("import") ) {
				for ( int j = i+7; j < parserBuffer.length(); j++ ) {
					char quote = parserBuffer.charAt(j);
					if ( quote == '\'' || quote == '\"' ) {
						if ( start == 0 ) {
							start = quote;
						} else {
							sheetResources.add(parserBuffer.substring(start, quote + 1));
							start = 0;
							break;
						}
					}
				}
			}
		}
	}
	
	public LinkedList<String> getSheetResources() {
		return sheetResources;
	}
	
	//Tester
	public static void main ( String[] args ) {
		StyleParser test = new StyleParser();
		test.parseStyle("@import \"found1\" @import avnower[onvaso[vnsdvobasvvaservoin\'found2\'");
		System.out.println(test.getSheetResources().size());
		for ( int i = 0; i < test.getSheetResources().size(); i++) {
			System.out.println(test.getSheetResources().get(i));
		}
			
	}
}
