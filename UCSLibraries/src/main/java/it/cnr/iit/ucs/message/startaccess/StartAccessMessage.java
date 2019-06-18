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
package it.cnr.iit.ucs.message.startaccess;

import it.cnr.iit.ucs.constants.ENTITIES;
import it.cnr.iit.ucs.constants.PURPOSE;
import it.cnr.iit.ucs.message.IdentifiedMessage;
import it.cnr.iit.ucs.message.Message;

/**
 * Structure of the startaccess message.
 * This is the structure of the startaccess message.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 */
public final class StartAccessMessage extends Message implements IdentifiedMessage {

    private String sessionId;

    public StartAccessMessage( String source, String destination ) {
        super( source, destination );
        purpose = PURPOSE.START;
    }

    public StartAccessMessage() {
        super( ENTITIES.PEP.toString(), ENTITIES.CH.toString() );
        purpose = PURPOSE.START;
    }

    @Override
    public void setSessionId( String sessionId ) {
        this.sessionId = sessionId;
    }

    @Override
    public String getSessionId() {
        return sessionId;
    }

}
