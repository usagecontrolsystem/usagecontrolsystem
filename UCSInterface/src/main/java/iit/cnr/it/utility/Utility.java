/*******************************************************************************
 * Copyright 2018 IIT-CNR
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package iit.cnr.it.utility;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class contains all the utility function we need throughout this project.
 * 
 * @author antonio
 *
 */
final public class Utility {
	
	private static final Logger LOGGER = Logger
	    .getLogger(Utility.class.getName());
	
	private Utility() {
		
	}
	
	/**
	 * Reads a file using the passed parameter as absolute path. Returns a String
	 * representing the content of the file.
	 * 
	 * @param string
	 *          a string that represents the absolute path to the file
	 * @return the String that represents the content of the file
	 */
	public static String readFileAbsPath(String filePath) {
		if (!isValidPath(filePath)){
			return null;
		}
		String absFilePath = findFileAbsPathUsingClassLoader(filePath);
		if (absFilePath != null){
			filePath = absFilePath;
		}else{
			LOGGER.log(Level.SEVERE, "Attempting to read file using provided filePath.");
		}
		try {
			Scanner scanner = new Scanner(new File(filePath));
			StringBuilder stringB = new StringBuilder();
			while (scanner.hasNext()) {
				stringB.append(scanner.nextLine());
			}
			scanner.close();
			return stringB.toString();
		} catch (IOException ioexception) {
			LOGGER.log(Level.SEVERE, "Unable to read file due to error: "+ioexception.getLocalizedMessage());
			return null;
		}
	}

	private static boolean isValidPath(String filePath) {
		// BEGIN parameter checking
		if (filePath == null || filePath.isEmpty()) {
			LOGGER.log(Level.SEVERE, "String for filePath can not be empty.");
			return false;
		}
		return true;
		// END parameter checking
	}
	
	/**
	 * Return the absolute location of the file for the reader
	 * @param relPath
	 * @return
	 */
	public static String findFileAbsPathUsingClassLoader(String relPath) {
		if (!isValidPath(relPath)){
			return null;
		}
		try {
			ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
			URL input = classLoader.getResource(relPath);
			return input.getPath();
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Unable to find absolute path due to error: "+ e.getMessage());
			return null;
		}
	}
	
	/**
	 * Secfure reading of the file from the absolute path TODO
	 */
	public static String secureReadFileAbsPath(String string) {
		return null;
	}
	
}
