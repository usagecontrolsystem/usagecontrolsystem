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
package iit.cnr.it.ucsinterface.message.attributeretrieval;

import java.util.ArrayList;

import it.cnr.iit.xacmlutilities.Attribute;

import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.PURPOSE;

/**
 * This is the response to the ATTRIBUTE_RETRIEVAL message.
 * <p>
 * The response has a structure that is equal to the one of the request message,
 * the only difference is that here, if something has gone wrong, the list of
 * attributes is null.
 * </p>
 *
 * @author antonio
 *
 */
public class AttributeRetrievalResponse extends Message {

    private static final long serialVersionUID = 1L;
    // list of attributes with their values
    private ArrayList<Attribute> attributes = new ArrayList<>();

    /**
     * Constructor for the attribute retrieval response
     *
     * @param source
     *          the source of the message
     * @param destination
     *          the destination of the message
     * @param id
     *          the id of the message taken from the request
     */
    public AttributeRetrievalResponse( String source, String destination,
            String id ) {
        super( source, destination, id );
        purpose = PURPOSE.ATTRIBUTE_RETRIEVAL_RESPONSE;
    }

    // ---------------------------------------------------------------------------
    // GETTERS
    // ---------------------------------------------------------------------------
    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

}
