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

import java.util.ArrayList;
import java.util.List;

import org.wso2.balana.ctx.ResponseCtx;

import com.google.common.annotations.VisibleForTesting;

import iit.cnr.it.ucsinterface.pdp.PDPEvaluation;
import iit.cnr.it.ucsinterface.pdp.PDPObligationInterface;
import it.cnr.iit.xacmlutilities.policy.utility.JAXBUtility;
import oasis.names.tc.xacml.core.schema.wd_17.ObligationType;
import oasis.names.tc.xacml.core.schema.wd_17.ResponseType;

/**
 * This is the result sent by the PDP
 * <p>
 * The result of an evaluation is composed by a ResponseType that is one of
 * those classes generated by the XJC tool starting from the xsd of the XACML
 * definition. Furthermore we have an attribute that signals the id of the
 * session to which the evaluation is referred to and a boolean that states if
 * the object is in a correct state
 * </p>
 * 
 * @author antonio
 *
 */
public final class PDPResponse implements PDPEvaluation {
	
	// the response provided by the PDP object
	private ResponseType				responseType;
	// states if the object has been correctly inistialized
	private volatile boolean		initialized	= false;
	// the id of the session the which the evaluation was referred
	private String							sessionId;
	// list of firing rules
	private ArrayList<Integer>	firingRules	= new ArrayList<>();
	// private String responseCtx;
	
	public PDPResponse() {
		
	}
	
	/**
	 * Constructor for the PDP response
	 * 
	 * @param string
	 *          the ResponseType in string format
	 */
	public PDPResponse(String string) {
		if (setResponse(string)) {
			initialized = true;
		} else {
			initialized = false;
		}
	}
	
	public PDPResponse(ResponseCtx response) {
		// BEGIN parameter checking
		if (response == null) {
			initialized = false;
			return;
		}
		// END parameter checking
		// this.responseCtx = response.encode();
		if (!setResponse(response.encode())) {
			initialized = false;
			return;
		}
		initialized = true;
	}
	
	/**
	 * Sets the response provided by the PDP. This function also checks if the
	 * response provided by the PDP is of a valid Response type.
	 * 
	 * @param string
	 *          the response in string format
	 * @return true if everything goes ok, false otherwise
	 */
	private boolean setResponse(String string) {
		try {
			responseType = JAXBUtility.unmarshalToObject(ResponseType.class, string);
			return true;
		} catch (Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}
	
	public void setFiringRules(ArrayList<Integer> firingRules) {
		this.firingRules = new ArrayList<>(firingRules);
	}
	
	// ---------------------------------------------------------------------------
	// GETTERS
	// ---------------------------------------------------------------------------
	@Override
	public String getResponse() {
		// BEGIN parameter checking
		if (!initialized) {
			return null;
		}
		// END parameter checking
		return responseType.getResult().get(0).getDecision().value();
	}
	
	@Override
	public List<PDPObligationInterface> getPIPObligations() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<PDPObligationInterface> getPEPObligations() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setSessionId(String id) {
		// BEGIN parameter checking
		if (id == null || id.isEmpty()) {
			return;
		}
		// END parameter checking
		sessionId = id;
	}
	
	@Override
	public String getSessionId() {
		return sessionId;
	}
	
	@Override
	public ArrayList<String> getObligations() {
		ArrayList<String> obligations = new ArrayList<>();
		if (responseType.getResult().get(0).getObligations() == null) {
			return null;
		}
		for (ObligationType obligation : responseType.getResult().get(0)
		    .getObligations().getObligation()) {
			obligations.add(obligation.getObligationId());
		}
		return obligations;
	}
	
	public void setResponseType(ResponseType responseType) {
		this.responseType = responseType;
	}
	
	@Override
	public Object getResponseAsObject() {
		return responseType;
	}
	
	@Override
	public ArrayList<Integer> getFiringRulesIndex() {
		return firingRules;
	}
	
	@VisibleForTesting
	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}
}
