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
package it.cnr.iit.ucs.properties;

import java.util.List;

import it.cnr.iit.ucs.properties.components.ContextHandlerProperties;
import it.cnr.iit.ucs.properties.components.CoreProperties;
import it.cnr.iit.ucs.properties.components.ObligationManagerProperties;
import it.cnr.iit.ucs.properties.components.PapProperties;
import it.cnr.iit.ucs.properties.components.PdpProperties;
import it.cnr.iit.ucs.properties.components.PepProperties;
import it.cnr.iit.ucs.properties.components.PipProperties;
import it.cnr.iit.ucs.properties.components.RequestManagerProperties;
import it.cnr.iit.ucs.properties.components.SessionManagerProperties;

public interface UCSProperties {
    public CoreProperties getCore();

    public ContextHandlerProperties getContextHandler();

    public RequestManagerProperties getRequestManager();

    public SessionManagerProperties getSessionManager();

    public PdpProperties getPolicyDecisionPoint();

    public PapProperties getPolicyAdministrationPoint();

    public ObligationManagerProperties getObligationManager();

    public List<PipProperties> getPipList();

    public List<PepProperties> getPepList();
}
