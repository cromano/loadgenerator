package com.awebstorm.loadgenerator.robot;

import java.net.URL;
import java.util.LinkedList;

/**
 * Parses StyleElement text for URL resources
 * @author Cromano
 *
 */
public class StyleParsers {
	
	/**
	 * Parse a String containing some external css resources.
	 * @param text String to parse
	 * @return LinkedList holding the string representations of the relative/absolute style resources
	 */
	public static LinkedList<String> parseStyleElementText ( String text ) {
		
		LinkedList<String> sheetResources = new LinkedList<String>();
		
		StringBuffer parserBuffer = new StringBuffer(text);
		int start = 0;
		for ( int i = 0; i < parserBuffer.length(); i++) {
			if ( parserBuffer.charAt(i) == '@' && parserBuffer.substring(i+1, i+7).equalsIgnoreCase("import") ) {
				for ( int j = i+7; j < parserBuffer.length(); j++ ) {
					char quote = parserBuffer.charAt(j);
					if ( quote == '\'' || quote == '\"' ) {
						if ( start == 0 ) {
							start = j+1;
						} else {
							sheetResources.add(parserBuffer.substring(start, j ));
							start = 0;
							i = j;
							break;
						}
					}
				}
			}
		}
		return sheetResources;
	}
	
	/**
	 * Reads a Style sheet and pulls out the url tags and adds them to a String List.
	 * @param text Style sheet
	 * @return List of url tags
	 */
	public static LinkedList<String> parseStyleSheetText ( String text ) {
		
		LinkedList<String> sheetResources = new LinkedList<String>();
		StringBuffer parserBuffer = new StringBuffer(text);
		StringBuffer urlBuilder = new StringBuffer();
		for( int i = 0; i < parserBuffer.length(); i++ ) {
			if ( parserBuffer.charAt(i) == 'u' &&  parserBuffer.charAt(i + 1) == 'r' && parserBuffer.charAt(i + 2) == 'l' ) {
				if ( parserBuffer.charAt(i + 3) == '(' ) {
					urlBuilder = new StringBuffer();
					for( int j = i+4; j < parserBuffer.length(); j++ ) {
						char tempChar = parserBuffer.charAt(j);
						if ( tempChar == ')' ) {
							sheetResources.add(urlBuilder.toString());
							i=j;
							break;	
						} else if ( tempChar != '\"' && tempChar != '\'' ) {
							urlBuilder.append(tempChar);
						}
					}
				}
			}
		}
		return sheetResources;
	}
	
	/**
	 * Takes the relative/absolute URL of a style sheet and strips off the file leaving only the directory.
	 * @param styleSheet URL of the style sheet
	 * @return
	 */
	public static String subDirBuilder( URL styleSheet ) {
		return styleSheet.toExternalForm().substring(0, styleSheet.toExternalForm().lastIndexOf('/')+1);
	}
	
	/**
	 * Test the functionality of StyleParser
	 * @param args
	 */
	public static void main ( String[] args ) {
		//Element Test
		long start = System.currentTimeMillis();
		LinkedList<String> test = StyleParsers.parseStyleElementText("@import \"found1\" @import avnower[onvaso[vnsdvobasvvaservoin\'found2\'");
		System.out.println("@import \"found1\" @import avnower[onvaso[vnsdvobasvvaservoin\'found2\'");
		for ( int i = 0; i < test.size(); i++) {
			System.out.println(test.get(i));
		}
		//Sheet Test
		test = StyleParsers.parseStyleSheetText("url(hello1) ((())) asdiovnaovavuiobnauipvqaviburl(hello2\")");
		for ( int i = 0; i < test.size(); i++) {
			System.out.println(test.get(i));
		}
		System.out.println((System.currentTimeMillis()-start));
	}
}
