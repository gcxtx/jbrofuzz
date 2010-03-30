/**
 * JBroFuzz 2.0
 *
 * JBroFuzz - A stateless network protocol fuzzer for web applications.
 * 
 * Copyright (C) 2007 - 2010 subere@uncon.org
 *
 * This file is part of JBroFuzz.
 * 
 * JBroFuzz is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * JBroFuzz is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with JBroFuzz.  If not, see <http://www.gnu.org/licenses/>.
 * Alternatively, write to the Free Software Foundation, Inc., 51 
 * Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 * Verbatim copying and distribution of this entire program file is 
 * permitted in any medium without royalty provided this notice 
 * is preserved. 
 * 
 */
package org.owasp.jbrofuzz.core;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;
import org.owasp.jbrofuzz.system.Logger;

/**
 * <p><code>Verifier</code> checks and loads the 
 * fuzzers from file.</p>
 * 
 * <p>The structure of this code is inspired from 
 * Elliotte Rusty Harold, XOM nu.xom.Verifier 
 * class, without the nice optimizations</p>
 * 
 * @author subere@uncon.org
 * @version 2.0
 * @since 2.0
 * 
 */
public final class Verifier {

	private Verifier() {}
	
	// The maximum number of chars to be read from file, regardless
	private static final int MAX_CHARS = Character.MAX_VALUE;
	// The maximum number of lines allowed to be read from the file
	private static final int MAX_LINES = 4096;
	// The maximum length of a line allowed
	private static final int MAX_LINE_LENGTH = 2048;

	// The maximum name length for a prototype
	private static final int MAX_PROTO_NAME_LENGTH = Byte.MAX_VALUE;
	// The maximum number of payloads in a prototype
	private static final int MAX_NO_OF_PAYLOADS = 1024;
	// The maximum number of categories of a prototype
	private static final int MAX_NO_OF_CATEGORIES = Byte.MAX_VALUE;
	
	private static final String ERROR_MSG = "\n\n\tBroken JBroFuzz Installation:\n\t";


	private static Map<String, Prototype> prototypes, headers = null;
	
	
	/**
	 * <p>
	 * Method called from the Database constructor to load the
	 * fuzzers from file.
	 * </p>
	 * 
	 * <p>This method calls the internal method 
	 * parseFile(ClassLoader).</p>
	 * 
	 * @return void
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	public static Map<String, Prototype> loadFile(String fileName){
		
		// Validate string first of all
		
		if ("fuzzers.jbrf".equalsIgnoreCase(fileName)) {
		
//			if (prototypes == null) {
				
				// Check for the existence of fuzzers.jbrf within the
				// current user directory
				final boolean extFilePresent = checkExternalFile(fileName);
				String inputContents;
				
				if(extFilePresent){
			
					inputContents = Verifier.parseExtFile(fileName);
					
				} else {
					
					inputContents = Verifier.parseFile(fileName);
					
				}
	    		
	    		prototypes = new HashMap<String, Prototype>();
	        	
	        	Verifier.parsePrototypes(prototypes, inputContents);
//	        }

	        return prototypes;
	        
		} else if ("headers.jbrf".equalsIgnoreCase(fileName)) {
			
//			if (headers == null) {
				String headerContents = Verifier.parseFile(fileName);
				
				headers = new HashMap<String, Prototype>();
				
				Verifier.parsePrototypes(headers, headerContents);
//			}
			
			return headers; 
			
		} else {
		
			throw new RuntimeException(ERROR_MSG
					+ "is not a valid name to load " + fileName);
			
		}
        
	}
	
	/**
	 * <p>Checks for the presence of an external file within the 
	 * current directory.</p>
	 * 
	 * @param fileName
	 * @return	true if the file is present and can be read, false
	 * 			otherwise.
	 * 
	 * @author subere@uncon.org
	 * @version 2.1
	 * @since 2.1
	 */
	private static boolean checkExternalFile(String fileName) {
		
		String dirString;
		try {
			
			dirString = System.getProperty("user.dir");
			
		} catch (SecurityException e) {
			
			return false;
			
		}
		final File inputFile = new File(dirString + File.separator + fileName);
		
		if (inputFile.exists()) {
			
			if (inputFile.isDirectory()) {
				
				return false;
			}
			if (!inputFile.canRead()) {
				
				return false;
				
			}
			
			return true;
			
		} else {
			
			return false;
			
		}
		
	}
	
	/**
	 * <p>Return the contents of an internal file a String.</p>
	 *  
	 * @param fileName e.g. fuzzers.jbrf; headers.jbrf
	 * @return the contents of the file as a String
	 * 
	 * @author subere@uncon.org
	 * @version 2.1
	 * @since 2.1
	 */
	private static String parseExtFile(String fileName) {
		
		final File inputFile = new File(System.getProperty("user.dir") + File.separator + fileName);
				
		if (inputFile.exists()) {
			if (inputFile.isDirectory()) {
				
				return "File is a directory:\n\n" + fileName;
			}
			if (!inputFile.canRead()) {
				
				return "File cannot be read:\n\n" + fileName;
				
			}
		} else {
			
			return "File does not exist:\n\n" + fileName;
			
		}
		
		int counter = 0;
		InputStream in = null;
		FileInputStream fis = null;
		StringBuffer fileContents = new StringBuffer();
		try {
			fis = new FileInputStream(inputFile);
			in = new BufferedInputStream(fis);

			int c;
			// Read, having as upper maximum the int maximum
			while (((c = in.read()) > 0) && (counter <= MAX_CHARS)) {
				// Allow the character only if its printable ascii or \n
				if ((CharUtils.isAsciiPrintable((char) c))
						|| (((char) c) == '\n')) {
					fileContents.append((char) c);
				}
				counter++;
				
			}

			in.close();
			fis.close();

		} catch (IOException e) {
			
			return "Attempting to open the file caused an I/O Error:\n\n" + fileName;

		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(fis);
		}
		
		if(counter == MAX_CHARS) {
			final String maxMessage = "\n... stopped reading file after " + MAX_CHARS + " characters.\n";
			fileContents.append(maxMessage);
			Logger.log(maxMessage, 3);
		}
		return fileContents.toString();

	}
	
	/**
	 * <p>Return the contents of an internal file a String.</p>
	 *  
	 * @param fileName e.g. fuzzers.jbrf; headers.jbrf
	 * @return the contents of the file as a String
	 * 
	 * @author subere@uncon.org
	 * @version 2.0
	 * @since 2.0
	 */
	private static String parseFile(String fileName) {
		
		final StringBuffer fileContents = new StringBuffer();

		// Attempt to read from the jar file
		final URL fileURL = ClassLoader.getSystemClassLoader().getResource(fileName);

		if (fileURL == null) {
			throw new RuntimeException(ERROR_MSG
					+ "could not find " + fileName);
		}

		// Read the characters from the file
		BufferedReader in = null;
		try {
			final URLConnection connection = fileURL.openConnection();
			connection.connect();

			in = new BufferedReader(new InputStreamReader(connection
					.getInputStream()));

			int counter = 0;
			int c;
			while (((c = in.read()) > 0) && (counter < MAX_CHARS)) {
				// Allow the character only if its printable ascii or \n
				if ((CharUtils.isAsciiPrintable((char) c))
						|| (((char) c) == '\n')) {
					fileContents.append((char) c);
				}
				counter++;
			}
			in.close();

			if(counter == MAX_CHARS) {

				throw new RuntimeException(ERROR_MSG
						+ "\n... stopped reading file :" + fileName + "\nafter " + MAX_CHARS + " characters.\n\n");
				
			}
			
		} catch (final IOException e1) {
			throw new RuntimeException(ERROR_MSG
					+ "could not read " + fileName);
		} finally {
			IOUtils.closeQuietly(in);
		}
		
		return fileContents.toString();
	}

	/**
	 * <p>Method responsible for parsing the printable
	 * String contents of the file fuzzers.jbrf to
	 * the prototype HashMap.</p>
	 * 
	 * @param input the .jbrf file in String input
	 */
	private static void parsePrototypes(Map<String, Prototype> map, String input) {
	
		// Break down the file contents into lines
		final String[] fileInput = input.split("\n");
	
		if (fileInput.length > MAX_LINES) {
			throw new RuntimeException(ERROR_MSG +
					"fuzzers.jbrf has more than " + MAX_LINES + " lines.");
		}
		
		if (fileInput.length < 3) {
			throw new RuntimeException(ERROR_MSG +
					"fuzzers.jbrf does not have enough lines.");
		}
			
		for (int i = 0; i < fileInput.length; i++) {
	
			// Ignore comment lines starting with '#'
			if (fileInput[i].startsWith("#")) {
				continue;
			}
	
			// Ignore lines of length greater than MAX_LINE_LENGTH
			if (fileInput[i].length() > MAX_LINE_LENGTH) {
				continue;
			}
	
			// Check 1 indicating a likely prototype candidate
			try {
				if (fileInput[i].charAt(1) != ':') {
					continue;
				}
				if (fileInput[i].charAt(13) != ':') {
					continue;
				}
	
			} catch (IndexOutOfBoundsException e1) {
				continue;
			}
	
			// [0] -> P || R || X
			// [1] -> "001-HTT-MTH"
			// [2] -> Uppercase HTTP Methods
			// [3] -> 8
			final String[] _fla = fileInput[i].split(":");
	
			// Check that there are four fields separated by :
			if (_fla.length != 4) {
				continue;
			}
	
			final char inputTypeChar = _fla[0].charAt(0);
	
			// Check [0] -> Fuzzer Type 'Z' or 'P', etc..
			if(!Prototype.isValidFuzzerType(inputTypeChar)) {
				continue;
			}
	
			// The Id: 009-SQL-INJ cannot be empty
			if (_fla[1].isEmpty()) {
				continue;
			}
	
			// The name: "SQL Injection" cannot be empty
			if (_fla[2].isEmpty()) {
				continue;
			}
	
			// Check the prototype name length
			if (_fla[2].length() > MAX_PROTO_NAME_LENGTH) {
				continue;
			}
			
			int noPayloads = 0;
			try {
				noPayloads = Integer.parseInt(_fla[3]);
	
			} catch (final NumberFormatException e) {
				continue;
			}
	
			// Check how many payloads this prototype has
			if (noPayloads > MAX_NO_OF_PAYLOADS) {
				continue;
			}
	
			// Allow only zero fuzzers to have no payloads
			if (noPayloads == 0) {
				continue;
			}
	
			// Check we have that many payloads left in file
			if (i + noPayloads > fileInput.length) {
				continue;
			}
	
			try {
				if (!fileInput[i + 1].startsWith(">")) {
					continue;
				}
				if (!fileInput[i + 2].startsWith(">>")) {
					continue;
				}
			} catch (IndexOutOfBoundsException e) {
				continue;
			}
	
			String line2 = "";
			try {
				line2 = fileInput[i + 1].substring(1);
			} catch (IndexOutOfBoundsException e) {
				continue;
			}
			
			String comment = "";
			try {
				comment = fileInput[i + 2].substring(2);
			} catch (IndexOutOfBoundsException e) {
				continue;
			}
	
			// [0] -> HTTP Methods
			// [1] -> Replacive Fuzzers
			// [2] -> Uppercase Fuzzers
			final String[] _sla = line2.split("\\|");
			if (_sla.length > MAX_NO_OF_CATEGORIES) {
				continue;
			}
	
			// Alas! Finally create a prototype
			final Prototype proto = 
				new Prototype(inputTypeChar, _fla[1], _fla[2]);
			
			// If categories do exist in the second line
			if (_sla.length > 0) {
	
				for (String categ_ry : _sla) {
					// add the category to the prototype
					categ_ry = StringUtils.stripEnd(categ_ry, " ");
					categ_ry = StringUtils.stripStart(categ_ry, " ");
	
					if (!categ_ry.isEmpty()) {
						proto.addCategory(categ_ry);
					}
	
				}
			}
			// If no categories have been identified,
			// add a default category
			else {
	
				proto.addCategory("JBroFuzz");
	
			}
	
			// Add the comment
			proto.addComment(comment);
			
			// Add the values of each payload
			for (int j = 1; j <= noPayloads; j++) {
				try {
	
					proto.addPayload(fileInput[i + 2 + j]);
	
				} catch (IndexOutOfBoundsException e) {
					continue;
				}
			}
	
			// Finally add the prototype to the database
			map.put(_fla[1], proto);
	
		}
	
	}

}
