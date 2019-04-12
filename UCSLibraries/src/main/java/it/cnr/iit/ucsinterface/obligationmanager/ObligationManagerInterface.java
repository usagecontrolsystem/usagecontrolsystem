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
package it.cnr.iit.ucsinterface.obligationmanager;

import java.util.List;

import it.cnr.iit.ucsinterface.pdp.PDPEvaluation;
import it.cnr.iit.ucsinterface.pip.PIPOMInterface;

/**
 * This is the Interface provided by the ObligationManager.
 *
 * <p>
 * Also the obligation manager provides a single function, the
 * translateObligations one. It is called by the PDP when there are some
 * obligations in the policy that have to be managed by the ObligationManager.
 * </p>
 *
 * @author antonio
 *
 */
public interface ObligationManagerInterface {

    /**
     * This is the function offered by the ObligationManager. This function is
     * called by the PDP whenever an obligation has to be translated into a format
     * that can be readable for both PEP and PIPs
     *
     * @param pdpEvaluation
     *          the evaluation provided by the PDP
     * @param sessionId
     *          the sessionId
     * @param status
     *          the status in which the obligation has to be performed
     *
     * @return a message that represents the
     */
    public PDPEvaluation translateObligations( PDPEvaluation pdpEvaluation,
            String sessionId, String status );

    /**
     * Set the pips to be used by this obligation manager, the obligation manager
     * can also employ a PIPRetrieval
     *
     * @param pips
     *          the list of pips
     * @param pipRetrieval
     *          the eventual pip retrieval to be used
     * @return true if everything goes alright, false otherwise
     */
    public boolean setPIPs( List<PIPOMInterface> pips,
            PIPOMInterface pipRetrieval );

}
