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
package it.cnr.iit.ucs.sessionmanager;

import it.cnr.iit.xacml.Attribute;

/**
 * This is the interface used to deal with the reevaluation interface.
 * The ReevaluationTable is used to keep a track of the sessions under
 * evaluation in order to avoid sessions reevaluated at the same time by
 * different nodes. The ReevaluationTable must provide, to sum up, some methods
 * to insert a session, if that session is not already under evaluation. and
 * methods to retrieve the sessions that are already under evaluation.
 * 
 * @author antonio
 *
 */
public interface ReevaluationTableInterface {

    public enum STATUS {
        NOT_IN_REEVALUATION, IN_REEVALUATION_DIFFERENT_ATTRIBUTE, IN_REEVALUATION_SAME_ATTRIBUTE, IN_REEVALUATION
    }

    /**
     * Checks if the session id is already present and if the attribute id that
     * has triggered the evaluation is the same
     * 
     * @param sessionId
     *          the session id
     * @param attribute
     *          the attribute that has triggered the evaluation. If the attribute
     *          is null, this means that we are checking only if the session is
     *          already under evaluation
     * @return the status of the session.
     */
    public STATUS checkSession( String sessionId, Attribute attribute );

    /**
     * Tries to insert the session along with the Attribute that has triggered the
     * evaluation
     * 
     * @param session
     *          the session under evaluation
     * @param attribute
     *          the attribute that has triggered the evaluation
     * @return true if the insertion goes ok, false otherwise
     */
    public boolean insertSession( SessionInterface session, Attribute attribute );

    /**
     * Stores in the table that the session has been reevaluated
     * 
     * @param session
     *          the session that was under reevaluation
     * @return true if everything goes ok, false otherwise
     */
    public boolean stopSession( SessionInterface session );

}
