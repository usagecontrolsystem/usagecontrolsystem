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

import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import it.cnr.iit.utility.JsonUtility;
import it.cnr.iit.utility.errorhandling.Reject;

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
public class Message {

    private static final long serialVersionUID = 1L;

    // source of the message
    protected String source;
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

    private String messageId;

    private boolean ucsDestination = false;

    // this field states if the message has already passed a scheduler or not
    private boolean scheduled = false;

    private volatile boolean initialized = false;

    private boolean delivered = false;

    public Message( String source, String destination ) {
        Reject.ifBlank( source );
        Reject.ifBlank( destination );
        this.source = source;
        this.destination = destination;
        setRandomMessageID();
        initialized = true;
    }

    public Message( String source, String destination, String messageId ) {
        this( source, destination );
        Reject.ifBlank( messageId );
        this.messageId = messageId;
    }

    public Message() {
        setRandomMessageID();
        initialized = true;
    }

    public final String getSource() {
        return source;
    }

    public void setSource( String source ) {
        this.source = source;
    }

    public final String getDestination() {
        return destination;
    }

    public void setDestination( String destination ) {
        this.destination = destination;
    }

    public <T> boolean setMotivation( T motivation ) {
        // Reject.ifNull( motivation );
        Optional<String> optObj = JsonUtility.getJsonStringFromObject( motivation, false );
        if( optObj.isPresent() ) {
            this.motivation = optObj.get();
            return true;
        }

        return false;
    }

    public String getMotivation() {
        if( !initialized ) {
            return null;
        }
        return motivation;
    }

    public PURPOSE getPurpose() {
        return purpose;
    }

    public void setPurpose( PURPOSE purpose2 ) {
        this.purpose = purpose2;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId( String id ) {
        this.messageId = id;
    }

    private void setRandomMessageID() {
        setMessageId( "ID:" + UUID.randomUUID() );
    }

    public void setCallback( Object callback, MEAN mean ) {
        this.callback = callback;
        this.mean = mean;
    }

    public Object getCallback() {
        return callback;
    }

    public MEAN getMean() {
        return mean;
    }

    public void setScheduled() {
        scheduled = true;
    }

    public boolean isScheduled() {
        return scheduled;
    }

    public void setUCSDestination() {
        ucsDestination = true;
    }

    public boolean getUCSDestination() {
        return ucsDestination;
    }

    public boolean isDelivered() {
        return delivered;
    }

    public void setDelivered( boolean delivered ) {
        this.delivered = delivered;
    }

    final protected boolean isInitialized() {
        return initialized;
    }

}
