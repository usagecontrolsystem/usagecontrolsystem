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

import java.util.List;
import java.util.Optional;

/**
 * This is the interface the Context Handler uses to deal with the Session
 * Manager
 *
 * The session manager has to provide to the context handler all the functions
 * to add, retrieve, delete records from/to the database table.
 * <br>
 * The id suffix is used only in the case in which the string under
 * consideration is an unique identifier, hence it can be used in the case of
 * the identifier of a session or that of an attribute. Since some attributes,
 * like the one related to the action, the subject and the object require the
 * additional information of the name of the thing to which they're related, we
 * will use the suffix name in thses cases. <b>Note</b> in the following the
 * terms object and resource are used with the same meaning <br>
 *
 * @author antonio
 *
 */
public interface SessionManagerInterface extends ReevaluationTableInterface {

    /**
     * Starts the sessionmanager
     *
     * @return true if everything goes fine, false otherwise
     */
    public Boolean start();

    /**
     * Stops the sessionmanager
     *
     * @return true if everything goes fine, false otherwise
     */
    public Boolean stop();

    public Boolean createEntry( SessionAttributes parameterObject );

    /**
     * Updates the status of a session identified by its session id with the
     * status provided
     *
     * @param sessionId
     *          the id of the session
     * @param status
     *          the new status of the session
     * @return true if everything goes fine, false otherwise
     */
    public Boolean updateEntry( String sessionId, String status );

    /**
     * Removes the entry identified by that sessionid
     *
     * @param sessionId
     *          the id of the session to be removed
     * @return true if everything goes ok, false otherwise
     */
    public Boolean deleteEntry( String sessionId );

    /**
     * Retrieves the list of sessions interested by that attribute id
     *
     * @param attributeId
     *          the attribute id in which sessions are interested
     * @return the list of sessions interested in that attribute id
     */
    public List<SessionInterface> getSessionsForAttribute( String attributeId );

    /**
     * Retrieves the list of sessions that have that attributeid and that subject
     * specified as on going attributes. This is done to avoid the retrieval of
     * sessions that are interested in a certain attributeid related to different
     * subject
     *
     * @param subjectName
     *          the name of the subject
     * @param attributeId
     *          the attribute id
     * @return the list of sessions interested by the couple
     */
    public List<SessionInterface> getSessionsForSubjectAttributes(
            String subjectName, String attributeId );

    /**
     * Retrieves the list of sessions that have that attributeid and that object
     * specified as on going attributes. This is done to avoid the retrieval of
     * sessions that are interested in a certain attribute id related the a
     * different object
     *
     * @param resourceName
     *          the name of the object
     * @param attributeId
     *          the attribute id
     * @return the list of sessions interested by the couple
     */
    public List<SessionInterface> getSessionsForResourceAttributes(
            String resourceName, String attributeId );

    /**
     * Retrieves the list of sessions that have that attributeid and that action
     * specified as on going attributes. This is done to avoid the retrieval of
     * sessions that are interested in a certain attribute related to different
     * actions.
     *
     * @param actionName
     *          the name of the action
     * @param attributeId
     *          the id of the attribute
     * @return the list of sessions interested by the couple
     */
    public List<SessionInterface> getSessionsForActionAttributes(
            String actionName, String attributeId );

    /**
     * Retrieve the list of sessions related to that attributeName
     *
     * @param the
     *          attribute name related to the environment in which we're
     *          interested into
     * @return the list of sessions
     */
    public List<SessionInterface> getSessionsForEnvironmentAttributes(
            String attributeName );

    /**
     * Retrieve the session that is identified by the specified session id
     *
     * @param sessionId
     *          the id of the session in which we're interested
     * @return the object implementing the Session interface
     */
    public Optional<SessionInterface> getSessionForId( String sessionId );

    /**
     * Retrieves the list of sessions that are sharing the same status
     *
     * @param status
     *          the status in which we're interested in
     * @return the list of session interface that have that status
     */
    public List<SessionInterface> getSessionsForStatus( String status );

    /**
     * Retrieves the list of ongoing attributes related to the session id passed
     * as parameter.
     *
     * @param sessionId
     *          the session id in which we're interested into
     * @return the list of ongoingattributes related to that sessio id
     */
    public List<OnGoingAttributesInterface> getOnGoingAttributes( String sessionId );

    /**
     * Tells if the session manager has been initialized
     * */
    public boolean isInitialized();
}
