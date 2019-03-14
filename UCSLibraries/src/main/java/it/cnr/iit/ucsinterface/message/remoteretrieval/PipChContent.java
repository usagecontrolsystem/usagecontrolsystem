/*******************************************************************************
 * Copyright 2018 IIT-CNR
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package it.cnr.iit.ucsinterface.message.remoteretrieval;

import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.xacmlutilities.Attribute;

/**
 * This is the structure of the message sent by the ContextHandler to a remote
 * PIP.
 * <p>
 * The context handler sends a message containing the list of attributes it is
 * interested into, it is up to each remote pip to fill that list with its own
 * values. For this reason this message is basically formed by the list of
 * Attributes in which the context handler is interested.
 * </p>
 * 
 * @author antonio
 *
 */
public final class PipChContent {
    List<Attribute> attributes = new ArrayList<>();

    /**
     * Adds a new attribute to the list
     * 
     * @param attribute
     *          the attribute to be added
     * @return true if everything goes fine, false otherwise
     */
    public boolean addAttribute( Attribute attribute ) {
        return attributes.add( attribute );
    }

    /**
     * Retrieves the list of attributes stored in the content
     * 
     * @return the list of attributes
     */
    public List<Attribute> getAttributes() {
        return attributes;
    }

}
