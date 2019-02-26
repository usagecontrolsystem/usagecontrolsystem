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
package iit.cnr.it.ucsinterface.message.tryaccess;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import iit.cnr.it.ucsinterface.pdp.PDPEvaluation;

/**
 * This is the content of the response to a tryAccess.
 * <p>
 * The response to a tryAccess has to contain the evaluation provided by the PDP
 * that, on its turn, contains the obligations the PEP has to perform and the
 * response of the PDP; the session id if the tryAccess was successful and the
 * status of the tryAccess.
 * 
 * @author antonio
 *
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class TryAccessResponseContent {
	// the evaluation provided by the PDP
	@JsonProperty
	private PDPEvaluation				pdpEvaluation;
	// the id of the session
	private String						newSessionId;
	// the status of the tryAccess
	private String						status;
	// states if the response is a correct object or not
	@JsonProperty
	private volatile boolean	contentInitialized	= false;
	
	/**
	 * Basic constructor for a TryAccessResponseContent object
	 */
	public TryAccessResponseContent() {
		contentInitialized = true;
	}
	
	// ---------------------------------------------------------------------------
	// GETTERS and SETTERS
	// ---------------------------------------------------------------------------
	public boolean setPDPEvaluation(PDPEvaluation pdpEvaluation) {
		// BEGIN parameter checking
		if (pdpEvaluation == null) {
			contentInitialized = false;
			return false;
		}
		// END parameter checking
		this.pdpEvaluation = pdpEvaluation;
		return true;
	}
	
	public boolean setSessionId(String sessionId) {
		// BEGIN parameter checking
		if (sessionId == null || sessionId.isEmpty()) {
			contentInitialized = false;
			return false;
		}
		// END parameter checking
		this.newSessionId = sessionId;
		return true;
	}
	
	public boolean setStatus(String status) {
		// BEGIN parameter checking
		if (status == null || status.isEmpty()) {
			contentInitialized = false;
			return false;
		}
		// END parameter checking
		this.status = status;
		return true;
	}
	
	public String getStatus() {
		// BEGIN parameter checking
		if (!contentInitialized) {
			return null;
		}
		// END parameter checking
		return status;
	}
	
	public String getSessionId() {
		// BEGIN parameter checking
		if (!contentInitialized) {
			return null;
		}
		// END parameter checking
		return newSessionId;
	}
	
	public boolean isInitialized() {
		return contentInitialized;
	}
	
	public void setContentInitialized(boolean contentInitialized) {
		this.contentInitialized = contentInitialized;
	}
	
	public PDPEvaluation getPDPEvaluation() {
		// BEGIN parameter checking
		//if (!contentInitialized) {
			//return null;
		//}
		// END parameter checking
		return pdpEvaluation;
	}
}
