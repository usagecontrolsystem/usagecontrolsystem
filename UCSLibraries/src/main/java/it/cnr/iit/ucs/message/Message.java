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
package it.cnr.iit.ucs.message;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import it.cnr.iit.ucs.constants.CONNECTION;
import it.cnr.iit.ucs.constants.PURPOSE;
import it.cnr.iit.utility.errorhandling.Reject;

/**
 * A message contains basic informations needed by the actors that are communicating.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 */
@JsonIgnoreProperties( ignoreUnknown = true )
public class Message {

    private String messageId;
    protected String source;
    protected String destination;
    private CONNECTION connection;
    protected PURPOSE purpose;
    protected Object callback;

    public static final String MESSAGE_ID_PREFIX = "ID:";

    public Message( String source, String destination ) {
        Reject.ifBlank( source );
        Reject.ifBlank( destination );
        this.source = source;
        this.destination = destination;
        setRandomMessageId();
    }

    public Message( String source, String destination, String messageId ) {
        this( source, destination );
        Reject.ifBlank( messageId );
        this.messageId = messageId;
    }

    public Message() {
        setRandomMessageId();
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

    public void setConnection( CONNECTION connection ) {
        this.connection = connection;
    }

    public CONNECTION getConnection() {
        return connection;
    }

    public PURPOSE getPurpose() {
        return purpose;
    }

    public void setPurpose( PURPOSE purpose ) {
        this.purpose = purpose;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId( String id ) {
        this.messageId = id;
    }

    private void setRandomMessageId() {
        setMessageId( MESSAGE_ID_PREFIX + UUID.randomUUID() );
    }

    public void setCallback( Object callback, CONNECTION connection ) {
        this.callback = callback;
        this.connection = connection;
    }

    public Object getCallback() {
        return callback;
    }

}
