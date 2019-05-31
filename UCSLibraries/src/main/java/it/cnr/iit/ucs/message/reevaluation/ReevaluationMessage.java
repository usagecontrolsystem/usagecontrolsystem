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
package it.cnr.iit.ucs.message.reevaluation;

import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.ucs.constants.PURPOSE;
import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.sessionmanager.SessionInterface;

/**
 * This is the message the CH sends to ask a reevaluation.
 * A reevaluation occurs whenever the value of one of the ongoing attributes
 * changes.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 */
public class ReevaluationMessage extends Message {

    private static final long serialVersionUID = 1L;

    private List<String> sessionIds;
    private SessionInterface session;

    public ReevaluationMessage( String source, String destination ) {
        super( source, destination );
        purpose = PURPOSE.REEVALUATION;
        sessionIds = new ArrayList<>();
    }

    public List<String> getSessionIds() {
        return sessionIds;
    }

    public void setSessionIds( List<String> sessionId ) {
        this.sessionIds = sessionId;
    }

    public void setSession( SessionInterface session ) {
        this.session = session;
    }

    public SessionInterface getSession() {
        return session;
    }

}
