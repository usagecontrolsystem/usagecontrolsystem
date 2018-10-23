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
package iit.cnr.it.ucsinterface.message.attributeretrieval;

import java.util.ArrayList;

import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.PURPOSE;
import it.cnr.iit.xacmlutilities.Attribute;

/**
 * This class is used to ask for the retrieval of an attribute.
 * <p>
 * Basically in this class we put all the informations required to insert a
 * remote attribute. Since it can be possible that we need more than a single
 * attribute from a certain ip, then we will have the list of Attributes
 * representing we need. We will use the Attribute class instead of using only
 * the ID of the attribute because the other party may need additional
 * informations to correctly retrieve the value of the attribute in which we're
 * interested into.
 * </p>
 * 
 * @author antonio
 *
 */
public class AttributeRetrievalMessage extends Message {
	
	/**
	 * 
	 */
	private static final long			serialVersionUID	= 1L;
	// list dof atrtbiutes in which we're interested into
	private ArrayList<Attribute>	attributes				= new ArrayList<>();
	
	/**
	 * Constructor for the AttributeRetrievalMessage
	 * 
	 * @param source
	 *          the source of the message
	 * @param destination
	 *          the destination of the message
	 */
	public AttributeRetrievalMessage(String source, String destination) {
		super(source, destination);
		purpose = PURPOSE.ATTRIBUTE_RETRIEVAL;
	}
	
	// ---------------------------------------------------------------------------
	// GETTERS and SETTERS
	// ---------------------------------------------------------------------------
	public ArrayList<Attribute> getAttributes() {
		return attributes;
	}
	
}
