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
	public static String readFileAbsPath(String string) {
		// BEGIN parameter checking
		if (string == null || string.isEmpty()) {
			LOGGER.log(Level.SEVERE, "String for filePath is not valid");
		}
		// END parameter checking
		try {
			Scanner scanner = new Scanner(new File(string));
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
	
	/**
	 * Secfure reading of the file from the absolute path TODO
	 */
	public static String secureReadFileAbsPath(String string) {
		return null;
	}
	
}
