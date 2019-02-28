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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.PART;
import iit.cnr.it.ucsinterface.message.PURPOSE;
import iit.cnr.it.ucsinterface.pdp.PDPEvaluation;

/**
 * This is the tryAccess response message
 * <p>
 * This message will be sent from the ContextHandler to the PEP to tell to it
 * which is the exit of the tryaccess it has performed
 * </p>
 * 
 * @author antonio
 *
 */

public final class TryAccessResponse extends Message {
	private static final long	serialVersionUID					= 1L;
	
	@JsonProperty
	private TryAccessResponseContent	tryAccessResponseContent;
	
	// states if the message has been correctly created
	private volatile boolean	responseInitialized				= false;
	
	public TryAccessResponse() {
		super(PART.CH.toString(), PART.PEP.toString());
		responseInitialized = true;
		purpose = PURPOSE.TRYACCESS_RESPONSE;
	}
	
	/**
	 * Constructor for a TryAccessResponse
	 * 
	 * @param id
	 *          the id of the request
	 */
	public TryAccessResponse(String id) {
		super(PART.CH.toString(), PART.PEP.toString(), id);
		if (isInitialized()) {
			responseInitialized = true;
			purpose = PURPOSE.TRYACCESS_RESPONSE;
		}
	}
	
	/**
	 * other faschion of the TryAccessResponse message
	 * 
	 * @param source
	 *          the source of the message
	 * @param dest
	 *          the destination of the message
	 * @param id
	 *          the id of the request
	 */
	public TryAccessResponse(String source, String dest, String id) {
		super(source, dest, id);
		if (isInitialized()) {
			responseInitialized = true;
		}
	}
	
	// ---------------------------------------------------------------------------
	// GETTERS and SETTERS
	// ---------------------------------------------------------------------------
	
	/**
	 * Sets the content of the tryAccessResponse message
	 * 
	 * @param content
	 *          the content of the response
	 * @return true if everything goes ok, false otherwise
	 */
	public boolean setContent(TryAccessResponseContent content) {
		// BEGIN parameter checking
		if (content == null || !content.isInitialized() || !isInitialized()
		    || !responseInitialized) {
			responseInitialized = false;
			return false;
		}
		// END parameter checking
		tryAccessResponseContent = content;
		return true;
	}
	
	@Override
	public int compareTo(Message o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	/*
	 * public TryAccessResponseContent getContent() { // BEGIN parameter checking
	 * if (!initialized) { return null; } // END parameter checking return
	 * tryAccessResponseContent; }
	 */
	
	public PDPEvaluation getPDPEvaluation() {
		return tryAccessResponseContent.getPDPEvaluation();
	}
	
	public String getStatus() {
		return tryAccessResponseContent.getStatus();
	}
	
	public String getSessionId() {
		return tryAccessResponseContent.getSessionId();
	}
	
	@Override
	public void setId(String id) {
		super.setId(id);
	}

	public TryAccessResponseContent getTryAccessResponseContent() {
		return tryAccessResponseContent;
	}

	public static TryAccessResponse buildFromString(String string) {
		try {
			return new ObjectMapper().readValue(string, TryAccessResponse.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String toString() {
		// System.out.println("TO STRING");
		// System.out.println(new Gson().toJson(this));
		//return new Gson().toJson(this);
		try {
			return new ObjectMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
