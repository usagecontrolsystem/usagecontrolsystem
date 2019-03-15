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
package it.cnr.iit.ucsinterface.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import it.cnr.iit.utility.JsonUtility;

/**
 * This is the class message.
 *
 * <p>
 * A message has to contain various informations independently on which are the
 * actors that are communicating. All the other functionalities will be
 * expressed by the classes that extend this one. All messages, in order to be
 * put into a PriorityQueue must implement the Comparable interface.
 * Informations that all messages <i>MUST</i> are:
 * <ol>
 * <li>Source: sender of the message</li>
 * <li>Destination: destination of the message</li>
 * <li>Motivation: why this message has been sent</li>
 * </ol>
 * Obviously depending on the situation the message may have other fields.
 * </p>
 *
 * <p>
 * In the framework we want that all parties can communicate by passing JSONs,
 * because it is very simple to convert an object to a JSON and viceversa in
 * JAVA. Moreover JSONs are lighter than XML.
 * </p>
 *
 * <p>
 * TODO ASAP switch to this schema to builders for message!!
 * </p>
 *
 * @author antonio
 *
 */

@JsonIgnoreProperties( ignoreUnknown = true )
public class Message implements Comparable<Message>, Serializable {
    private static final Logger LOGGER = Logger.getLogger( Message.class.getName() );

    private static final long serialVersionUID = 1L;

    // source of the message
    protected String sourceAddress;
    // port on which the source expects the reply
    protected String sourcePort;
    // destination of the message
    protected String destination;
    // motivation of the message
    protected String motivation = null;
    // purpose (tryaccess)
    protected PURPOSE purpose;
    // callback object if needed
    protected Object callback;
    // mean on which we expect to receive the response to the message
    private MEAN mean;
    // id of the message
    private String id;
    private boolean ucsDestination = false;

    // this field states if the message has already passed a scheduler or not
    private boolean scheduled = false;

    private volatile boolean initialized = false;

    private boolean deliveredToDestination = false;

    /**
     * Constructor for the message class.
     *
     * @param source
     *          the source of the message
     * @param destination
     *          the destination of the message
     * @param motivation
     *          the motivation of the message
     */
    public Message( String source, String destination ) {
        id = "ID:" + UUID.randomUUID();
        // BEGIN parameter checking
        if( ( source == null ) || ( destination == null ) ) {
            LOGGER.info( "[Message]" + source + "\t" + destination );
            return;
        }
        // END parameter checking
        this.sourceAddress = source;
        this.destination = destination;
        initialized = true;
    }

    /**
     * Constructor for the message class.
     *
     * @param source
     *          the source of the message
     * @param destination
     *          the destination of the message
     * @param motivation
     *          the motivation of the message
     */
    public Message( String source, String destination, String id ) {
        this.id = id;
        // BEGIN parameter checking
        if( ( source == null ) || ( destination == null ) ) {
            LOGGER.severe( "[Message]" + source + "\t" + destination );
            // TODO handle error
            return;
        }
        // END parameter checking
        this.sourceAddress = source;
        this.destination = destination;
        initialized = true;
    }

    public Message() {
        id = "ID:" + UUID.randomUUID();
        initialized = true;
    }

    /**
     * Constructor for a message class
     *
     * @param source
     *          source of the message
     * @param destination
     *          destination of the message
     * @param content
     *          content of the message
     * @param purpose
     *          purpose of the message
     */
    public <T> Message( String source, String destination, T content, PURPOSE purpose ) {
        id = "ID:" + UUID.randomUUID();
        // BEGIN parameter checking
        if( ( source == null ) || ( destination == null ) ) {
            LOGGER.severe( "[Message]" + source + "\t" + destination );
            return;
        }
        // END parameter checking
        this.sourceAddress = source;
        this.destination = destination;

        Optional<String> optObj = JsonUtility.getJsonStringFromObject( content, false );
        this.motivation = optObj.isPresent() ? optObj.get() : "";

        initialized = true;
    }

    /* Getters */

    final public String getSource() {
        if( !initialized ) {
            return null;
        }
        return sourceAddress;
    }

    final public String getDestination() {
        if( !initialized ) {
            return null;
        }
        return destination;
    }

    public <T> boolean setMotivation( T motivation ) {
        // BEGIN parameter checking
        if( !initialized || motivation == null ) {
            return false;
        }
        // END parameter checking
        Optional<String> optObj = JsonUtility.getJsonStringFromObject( motivation, false );
        this.motivation = optObj.isPresent() ? optObj.get() : "";

        return true;
    }

    public String getMotivation() {
        if( !initialized ) {
            return null;
        }
        return motivation;
    }

    final protected boolean isInitialized() {
        return initialized;
    }

    @Override
    public int compareTo( Message o ) {
        return 0;
    }

    public PURPOSE getPurpose() {
        return purpose;
    }

    /*
     * public boolean setMotivation(String string) { // BEGIN parameter checking if
     * (!initialized) { return false; } // END parameter checking motivation =
     * string; return true; }
     */

    public String getID() {
        if( !initialized ) {
            return null;
        }
        return id;
    }

    public void setCallback( Object callback, MEAN mean ) {
        this.callback = callback;
        this.mean = mean;
    }

    public MEAN getMean() {
        return mean;
    }

    public Object getCallback() {
        return callback;
    }

    public void setId( String id ) {
        this.id = id;
    }

    public void setPurpose( PURPOSE purpose2 ) {
        this.purpose = purpose2;
    }

    public void setHops( ArrayList<String> buildRoute ) {
        // TODO Auto-generated method stub

    }

    public void setScheduled() {
        scheduled = true;
    }

    public boolean getScheduled() {
        return scheduled;
    }

    public void setSource( String source ) {
        this.sourceAddress = source;
    }

    public void setDestination( String destination ) {
        this.destination = destination;
    }

    public String getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort( String sourcePort ) {
        this.sourcePort = sourcePort;
    }

    public void setDestinationType() {
        ucsDestination = true;
    }

    public boolean getDestinationType() {
        return ucsDestination;
    }

    public boolean isDeliveredToDestination() {
        return deliveredToDestination;
    }

    public void setDeliveredToDestination( boolean deliveredToDestination ) {
        this.deliveredToDestination = deliveredToDestination;
    }

}
