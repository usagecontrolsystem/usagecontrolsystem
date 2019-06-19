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

/**
 * This is the Session interface.
 *
 * A session interface is a set of common API the SessionManager and the Context
 * Handler agree on using when communicating between each other informations
 * about a session.
 * The provided API are the ones to retrieve the various fields of a Session.
 * All the returned parameters are in String format.
 *
 * @author antonio
 *
 */
public interface SessionInterface {

    /**
     * Retrieves the Id of the Session
     *
     * @return the Id of the Session
     */
    public String getId();

    /**
     * Retrieves the policy set
     *
     * @return the policy set
     */
    public String getPolicySet();

    /**
     * Retrieves the status of the session
     *
     * @return the status of the session
     */
    public String getStatus();

    public boolean isStatus( String status );

    /**
     * Retrieves the OriginalRequest
     *
     * @return the original request
     */
    public String getOriginalRequest();

    /**
     * Retrieves the Id of the PEP
     *
     * @return the id of the PEP
     */
    public String getPepId();

    /**
     * Retrieves the IP of the ContextHandler that has evaluated the session
     *
     * @return the IP of the ContextHandler that has evaluated the session
     */
    public String getIP();

    /**
     * Retrieves the list of On going attributes related to the session
     *
     * @return
     */
    public List<OnGoingAttributesInterface> getOnGoingAttributes();

    /**
     * Retrieves the list of mutable attributes of the session
     *
     * @return list of mutable attributes
     */
    public void setRequest( String request );

}
