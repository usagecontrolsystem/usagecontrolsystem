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
package it.cnr.iit.ucs.message.attributechange;

import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.ucs.constants.ATTRIBUTE_ACTION;
import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.xacml.Attribute;

/**
 * This is the message a PIP sends to a CH and vice versa.
 * We have defined a single class for both local and remote messages, in this
 * way it is a lot easier to pass messages from one source to the other.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public final class AttributeChangeMessage extends Message {

    List<Attribute> attributes = new ArrayList<>();

    private ATTRIBUTE_ACTION attributeAction;

    public AttributeChangeMessage( String source, String destination ) {
        super( source, destination );
    }

    public AttributeChangeMessage( String source, String destination, List<Attribute> attributes ) {
        super( source, destination );
        setAttributes( attributes );
    }

    public void setAttributes( List<Attribute> attributes ) {
        this.attributes = attributes;
    }

    public boolean addAttribute( Attribute attribute ) {
        return attributes.add( attribute );
    }

    public List<Attribute> getAttributes() {
        return new ArrayList<>( attributes );
    }

    public void setAttributeAction( ATTRIBUTE_ACTION action ) {
        this.attributeAction = action;
    }

    public ATTRIBUTE_ACTION getAttributeAction() {
        return attributeAction;
    }

}
