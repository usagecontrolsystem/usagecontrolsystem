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
package it.cnr.iit.ucsinterface.message.reevaluation;

import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.PURPOSE;
import it.cnr.iit.ucsinterface.sessionmanager.SessionInterface;

/**
 * This is the message the CH sends to ask a reevaluation.
 * <p>
 * A reevaluation occurs whenever the value of one of the ongoing attributes
 * changes. For this reason in the reevaluation message it is sufficient to put
 * the String representing the ID of the session under consideration. In this
 * way we save bandwidth and memory.
 *
 * <br>
 *
 * Since it may happens that a node asks to another node to perform a
 * reevaluation task, it is reasonable to allow to have an entire session as
 * possible field inside this message. In fact, in this latter case, the session
 * was already retrieved from the database, thus it makes to sense to repeat the
 * same operation once again. Of course we're considering here reasonable
 * policies.
 * </p>
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public class ReevaluationMessage extends Message {

    private static final long serialVersionUID = 1L;

    private List<String> sessionIds;
    private SessionInterface session;

    /**
     * Constructor for the reevaluation message
     *
     * @param source      is the source of the message
     * @param destination is the destination of the message
     */
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
