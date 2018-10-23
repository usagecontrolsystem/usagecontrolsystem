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
package it.cnr.iit.xacmlutilities.policy;

import java.io.File;
import java.util.Scanner;

public class Main {
	
	public static void main(String[] args) throws Exception {
		
		// policy parser
		String string = "";
		try {
			Scanner scanner = new Scanner(
			    new File("/home/antonio/projects/Policies/Policy5.xml"));
			while (scanner.hasNext()) {
				string += scanner.nextLine();
			}
			scanner.close();
		} catch (Exception e) {
			
		}
		PolicyHelper ph = PolicyHelper.buildPolicyHelper(string);
		// System.out.println(ph.getAttributesForCondition("pre"));
		
		System.out.println(ph.getConditionForEvaluation("post"));
	}
	
}
