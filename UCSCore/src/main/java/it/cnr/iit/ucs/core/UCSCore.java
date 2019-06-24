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
package it.cnr.iit.ucs.core;

import java.util.HashMap;
import java.util.List;

import it.cnr.iit.ucs.contexthandler.AbstractContextHandler;
import it.cnr.iit.ucs.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucs.obligationmanager.ObligationManagerInterface;
import it.cnr.iit.ucs.pap.PAPInterface;
import it.cnr.iit.ucs.pdp.PDPInterface;
import it.cnr.iit.ucs.pep.PEPInterface;
import it.cnr.iit.ucs.pip.PIPBase;
import it.cnr.iit.ucs.requestmanager.AbstractRequestManager;
import it.cnr.iit.ucs.sessionmanager.SessionManagerInterface;
import it.cnr.iit.ucs.ucs.UCSInterface;

/**
 * This class contains all the components
 *
 * @author Antonio La Marra, Alessandro Rosetti
 */
public class UCSCore implements UCSInterface {

    AbstractContextHandler contextHandler;
    AbstractRequestManager requestManager;
    ObligationManagerInterface obligationManager;
    SessionManagerInterface sessionManager;
    PDPInterface pdp;
    PAPInterface pap;
    List<PIPBase> pipList;
    HashMap<String, PEPInterface> pepMap;

    public UCSCore() {}

    @Override
    public Boolean tryAccess( TryAccessMessage tryAccessMessage ) {
        return requestManager.sendMessageToCH( tryAccessMessage );
    }

    @Override
    public Boolean startAccess( StartAccessMessage startAccessMessage ) {
        return requestManager.sendMessageToCH( startAccessMessage );
    }

    @Override
    public Boolean endAccess( EndAccessMessage endAccessMessage ) {
        return requestManager.sendMessageToCH( endAccessMessage );
    }

}
