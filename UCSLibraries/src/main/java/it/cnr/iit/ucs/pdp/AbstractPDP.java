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
package it.cnr.iit.ucs.pdp;

import it.cnr.iit.ucs.obligationmanager.ObligationManagerInterface;
import it.cnr.iit.ucs.pap.PAPInterface;
import it.cnr.iit.ucs.properties.components.PdpProperties;
import it.cnr.iit.utility.errorhandling.Reject;

/**
 * This is the abstract class providing a schema for the PDP.
 *
 * <p>
 * The PDP requires an interface to deal with the obligation manager and another
 * to deal with the PAP. <br>
 * Also in this case, following the CERT guidelines, we will implement this
 * class using a volatile variable in charge of saying if the object has been
 * correctly created or not.
 * </p>
 *
 * @author Antonio La Marra
 *
 */
public abstract class AbstractPDP implements PDPInterface {
    private ObligationManagerInterface obligationManager;
    private PAPInterface pap;
    private volatile boolean initialized = false;
    private PdpProperties properties;

    /**
     * The constructor for the abstract class is empty
     */
    public AbstractPDP( PdpProperties properties ) {
        Reject.ifNull( properties );
        this.properties = properties;
        initialized = true;
    }

    protected final ObligationManagerInterface getObligationManager() {
        return obligationManager;
    }

    public final void setObligationManager( ObligationManagerInterface obligationManager ) {
        Reject.ifNull( obligationManager );
        this.obligationManager = obligationManager;
    }

    protected final PAPInterface getPAP() {
        return pap;
    }

    public final void setPAP( PAPInterface pap ) {
        Reject.ifNull( pap );

        this.pap = pap;
    }

    public final boolean isInitialized() {
        return initialized;
    }

}
