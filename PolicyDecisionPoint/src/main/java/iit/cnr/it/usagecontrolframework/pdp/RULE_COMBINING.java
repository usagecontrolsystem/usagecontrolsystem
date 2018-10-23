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
package iit.cnr.it.usagecontrolframework.pdp;

public enum RULE_COMBINING {
	DENY_UNLESS_PERMIT(
	    "urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:deny-unless-permit"), FIRST_APPLICABLE(
	        "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:first-applicable");
	
	String value;
	
	private RULE_COMBINING(String string) {
		value = string;
	}
	
	public String getValue() {
		return value;
	}
}
