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
package iit.cnr.it.ucsinterface.message.endaccess;

import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.PART;
import iit.cnr.it.ucsinterface.message.PURPOSE;

/**
 * Structure of the endaccess message
 * 
 * @author antonio
 *
 */
public class EndAccessMessage extends Message {
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	// the session id
	private String						sessionId;
	// flag that states if this class has been correctly initialized
	private volatile boolean	initialized2				= false;
	
	/**
	 * Constructor for an EndAccessMessage
	 * 
	 * @param source
	 *          source of the message
	 * @param destination
	 *          destination of the message
	 */
	public EndAccessMessage(String source, String destination) {
		super(source, destination);
		if (isInitialized()) {
			purpose = PURPOSE.ENDACCESS;
			initialized2 = true;
		}
	}
	
	/**
	 * Constructor for an EndAccessMessage
	 */
	public EndAccessMessage() {
		super(PART.PEP.toString(), PART.CH.toString());
		if (isInitialized()) {
			purpose = PURPOSE.ENDACCESS;
			initialized2 = true;
		}
	}
	
	// ---------------------------------------------------------------------------
	// GETTERS and SETTERS
	// ---------------------------------------------------------------------------
	public boolean setSessionId(String sessionId) {
		// BEGIN parameter checking
		if (!initialized2 || sessionId == null || sessionId.isEmpty()) {
			initialized2 = false;
			return false;
		}
		// END parameter checking
		this.sessionId = sessionId;
		return true;
	}
	
	public String getSessionId() {
		// BEGIN parameter checking
		if (!initialized2) {
			return null;
		}
		// END parameter checking
		return sessionId;
	}
	
	@Override
	public int compareTo(Message o) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
