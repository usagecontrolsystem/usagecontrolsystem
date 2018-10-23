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
package iit.cnr.it.ucsinterface.message;

/**
 * Enumerate to handle in quick and easy way the source and the destination
 * string
 * 
 * @author antonio
 *
 */
public enum PART {
	PIP("PIP"), CH("CH"), PIPRetrieval("PIPRetrieval"), PEP("PEP"), ADVERTISING(
	    "ADVERTISING");
	private String string;
	
	PART(String string) {
		this.string = string;
	}
	
	@Override
	public String toString() {
		return string;
	}
	
	public static PART fromString(String string) {
		if (string == null || string.equals("")) {
			return null;
		}
		if (string.equals(PIP.toString())) {
			return PIP;
		}
		if (string.equals(PIPRetrieval.toString())) {
			return PIPRetrieval;
		}
		if (string.equals(CH.toString())) {
			return CH;
		}
		if (string.equals(PEP.toString())) {
			return PEP;
		}
		return null;
	}
}
