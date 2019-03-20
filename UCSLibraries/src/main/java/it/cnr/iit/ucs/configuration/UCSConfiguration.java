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
package it.cnr.iit.ucs.configuration;

import java.util.ArrayList;
import java.util.List;

import it.cnr.iit.ucs.configuration.fields.ContextHandlerProperties;
import it.cnr.iit.ucs.configuration.fields.GeneralProperties;
import it.cnr.iit.ucs.configuration.fields.ObligationManagerProperties;
import it.cnr.iit.ucs.configuration.fields.PapProperties;
import it.cnr.iit.ucs.configuration.fields.PdpProperties;
import it.cnr.iit.ucs.configuration.fields.PepProperties;
import it.cnr.iit.ucs.configuration.fields.ProxiesProperties;
import it.cnr.iit.ucs.configuration.fields.RequestManagerProperties;
import it.cnr.iit.ucs.configuration.fields.distributed.DistributedProperties;
import it.cnr.iit.ucs.configuration.fields.pip.PipProperties;
import it.cnr.iit.ucs.configuration.fields.sessionManager.SessionManagerProperties;

/**
 * These are the classes required to perform marshalling/unmarshalling of the
 * xml file describing the properties of the UCS implementation.
 *
 * <p>
 * We want our framework to be highly modular and configurable. The latter
 * characteristic has to be provided by the XML file through which we configure
 * the framework.
 *
 * <br>
 *
 * <b>NOTE</b>: It can be considered the opportunity of plugging only the
 * modules provided in the configuration, hence let this part of code to
 * dynamically modify the pom.xml file or to do something similar with equinox
 * or felix.
 * </p>
 *
 * @author antonio
 *
 */

public final class UCSConfiguration {

    private GeneralProperties general;
    private ContextHandlerProperties contextHandler;
    private SessionManagerProperties sessionManager;
    private RequestManagerProperties requestManager;
    private ObligationManagerProperties obligationManager;
    private PdpProperties policyDecisionPoint;
    private PapProperties policyAdministrationPoint;
    private ProxiesProperties proxies;
    private DistributedProperties distributed;
    private List<PipProperties> pipList = new ArrayList<>();
    private List<PepProperties> pepList = new ArrayList<>();

    public List<PipProperties> getPipList() {
        return pipList;
    }

    public void setPipList( List<PipProperties> pipList ) {
        this.pipList = pipList;
    }

    public List<PepProperties> getPepList() {
        return pepList;
    }

    public ProxiesProperties getProxies() {
        return proxies;
    }

    public SessionManagerProperties getSessionManager() {
        return sessionManager;
    }

    public ContextHandlerProperties getContextHandler() {
        return contextHandler;
    }

    public PdpProperties getPolicyDecisionPoint() {
        return policyDecisionPoint;
    }

    public PapProperties getPolicyAdministrationPoint() {
        return policyAdministrationPoint;
    }

    public RequestManagerProperties getRequestManager() {
        return requestManager;
    }

    public ObligationManagerProperties getObligationManager() {
        return obligationManager;
    }

    public GeneralProperties getGeneral() {
        return general;
    }

    public DistributedProperties getDistributed() {
        return distributed;
    }

}
