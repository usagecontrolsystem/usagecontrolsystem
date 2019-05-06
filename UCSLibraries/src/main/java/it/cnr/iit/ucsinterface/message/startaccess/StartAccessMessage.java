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
package it.cnr.iit.ucsinterface.message.startaccess;

import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.PART;
import it.cnr.iit.ucsinterface.message.PURPOSE;

/**
 * Structure of the startaccess message.
 * <p> This is the structure of the startaccess message.</p>
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public final class StartAccessMessage extends Message {

    private static final long serialVersionUID = 1L;

    private String sessionId;

    public StartAccessMessage( String source, String destination ) {
        super( source, destination );
        purpose = PURPOSE.STARTACCESS;
    }

    public StartAccessMessage() {
        super( PART.PEP.toString(), PART.CH.toString() );
        purpose = PURPOSE.STARTACCESS;
    }

    public void setSessionId( String sessionId ) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

}
