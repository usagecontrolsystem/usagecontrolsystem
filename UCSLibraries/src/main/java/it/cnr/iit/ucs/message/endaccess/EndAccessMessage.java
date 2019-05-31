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
package it.cnr.iit.ucs.message.endaccess;

import it.cnr.iit.ucs.constants.ENTITIES;
import it.cnr.iit.ucs.constants.PURPOSE;
import it.cnr.iit.ucs.message.Message;

/**
 * Structure of the endaccess message
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public class EndAccessMessage extends Message {

    private static final long serialVersionUID = 1L;

    private String sessionId;

    public EndAccessMessage( String source, String destination ) {
        super( source, destination );
        purpose = PURPOSE.END;
    }

    public EndAccessMessage() {
        super( ENTITIES.PEP.toString(), ENTITIES.CH.toString() );
        purpose = PURPOSE.END;
    }

    public void setSessionId( String sessionId ) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

}
