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
package it.cnr.iit.usagecontrolframework.test.pdp;

import java.io.File;
import java.util.Scanner;

import it.cnr.iit.usagecontrolframework.pdp.PolicyDecisionPoint;
import it.cnr.iit.xacmlutilities.policy.PolicyHelper;
import it.cnr.iit.xacmlutilities.policy.utility.JAXBUtility;
import oasis.names.tc.xacml.core.schema.wd_17.PolicyType;

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
		
		String request = "";
		try {
			Scanner scanner = new Scanner(
			    new File("/home/antonio/projects/Policies/RequestNew.xml"));
			while (scanner.hasNext()) {
				request += scanner.nextLine();
			}
			scanner.close();
		} catch (Exception e) {
			
		}
		PolicyType policyType = JAXBUtility.unmarshalToObject(PolicyType.class,
		    string);
		String p = JAXBUtility.marshalToString(PolicyType.class, policyType,
		    "Policy", JAXBUtility.SCHEMA);
		System.out.println(p);
		PolicyHelper ph = PolicyHelper.buildPolicyHelper(string);
		System.out.println(ph.getAttributesForCondition("pre"));
		// String policy = ph.getConditionForEvaluation("post");
		// System.out.println(policy);
		PolicyDecisionPoint policyDecisionPoint = new PolicyDecisionPoint(null);
		policyDecisionPoint.evaluate(request, p);
	}
	
}
