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
package it.cnr.iit.ucsinterface.message.pipch;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.PART;
import it.cnr.iit.utility.JsonUtility;
import it.cnr.iit.xacmlutilities.Attribute;

/**
 * This is the message a PIP sends to a CH and vice versa.
 * <p>
 * We have defined a single class for both local and remote messages, in this
 * way it is a lot easier to pass messages from one source to the other. <br>
 *
 * </p>
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public final class PipChMessage extends Message {
    private static final Logger log = Logger.getLogger( PipChMessage.class.getName() );

    private static final long serialVersionUID = 1L;

    List<Attribute> attributes = new ArrayList<>();

    private ACTION action;

    /**
     * Message exchanged between PIP and CH
     *
     * @param source
     *          source of the message
     * @param destination
     *          destination of the message
     * @param motivation
     *          motivation of the message (most likely it is a json describing the
     *          motivation)
     */
    public PipChMessage( String source, String destination ) {
        super( source, destination );
    }

    /**
     * Message exchanged between PIP and CH
     *
     * @param source
     *          source of the message
     * @param destination
     *          destination of the message
     * @param content
     *          content of the message (most likely it is a json describing the
     *          motivation)
     */
    public PipChMessage( PART source, PART destination, List<Attribute> attributes ) {
        super( source.toString(), destination.toString() );
        if( super.isInitialized() ) {
            setAttributes( attributes );
        }
    }

    public void setAttributes( List<Attribute> attributes ) {
        this.attributes = attributes;
    }

    @Override
    public String getMotivation() {
        Optional<String> optObj = JsonUtility.getJsonStringFromObject( attributes, false );
        return optObj.isPresent() ? optObj.get() : "";
    }

    /**
     * Adds a new attribute to the motivation of the message.
     *
     * @param attribute
     *          the attribute to be added
     * @return true if everything goes ok, false otherwise
     */
    public boolean addAttribute( Attribute attribute ) {
        return attributes.add( attribute );
    }

    public List<Attribute> getAttributes() {
        return new ArrayList<>( attributes );
    }

    public void setAction( ACTION retrieve ) {
        this.action = retrieve;
    }

    public ACTION getAction() {
        return action;
    }

}
