/*******************************************************************************
 * Copyright 2018 IIT-CNR
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package iit.cnr.it.ucsinterface.message.reevaluation;

import java.util.ArrayList;

import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.PURPOSE;
import iit.cnr.it.ucsinterface.sessionmanager.Session;
import iit.cnr.it.ucsinterface.sessionmanager.SessionInterface;

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
 * @author antonio
 *
 */
public class ReevaluationMessage extends Message {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  // the session to be reevaluated
  private ArrayList<String> sessionId;
  private SessionInterface session;

  /**
   * Constructor for the reevaluation message
   * 
   * @param source
   *          is the ip of the source of the message
   * @param destination
   *          is the destination of the message
   */
  public ReevaluationMessage(String source, String destination) {
    super(source, destination);
    purpose = PURPOSE.REEVALUATION;
    sessionId = new ArrayList<>();
  }

  // ---------------------------------------------------------------------------
  // GETTERS and SETTERS
  // ---------------------------------------------------------------------------
  public ArrayList<String> getSessionId() {
    return sessionId;
  }

  public void setSessionId(ArrayList<String> sessionId) {
    this.sessionId = sessionId;
  }

  public void setSession(SessionInterface session) {
    this.session = session;
  }

  public SessionInterface getSession() {
    return session;
  }
}
