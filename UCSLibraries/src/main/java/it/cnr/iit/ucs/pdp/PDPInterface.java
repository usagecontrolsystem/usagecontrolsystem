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

import it.cnr.iit.ucs.constants.STATUS;
import it.cnr.iit.xacmlutilities.wrappers.PolicyWrapper;
import it.cnr.iit.xacmlutilities.wrappers.RequestWrapper;

/**
 * This is the interface offered by the PDP. The only task the PDP has to
 * accomplish is that of evaluating a request versus a policy. The PDP can work
 * in two different ways: the policy can be provided by the ContextHandler or it
 * can retrieve the policy from the PAP. <br>
 * Once the request has been analysed, the PDP has to ask the ObligationManager
 * to convert eventual obligation in a PEP-friendly and PIP-friendly format.
 * Only after this step the response with the obligations can be send to the
 * ContextHandler.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public interface PDPInterface {

    /**
     * Ask the evaluation to the PDP providing to it the request in string format
     * and the policy in string format. This choice has been performed because,
     * even if now we're using balana, nothing would prevent us to use a different
     * PDP in the future
     *
     * @param request
     *          the request in string format
     * @param policy
     *          the policy in string format
     * @param status
     * @return the PDPEvaluation provided as response
     */
    public PDPEvaluation evaluate( RequestWrapper request, PolicyWrapper policy, STATUS status );

    /**
     * Ask the evaluation to the PDP providing to it the request in string format
     * and the policy in string format. This choice has been performed because,
     * even if now we're using balana, nothing would prevent us to use a different
     * PDP in the future
     *
     * @param request
     *          the request in string format
     * @param policy
     *          the policy in string format
     * @return the PDPEvaluation provided as response
     */
    public PDPEvaluation evaluate( RequestWrapper request, PolicyWrapper policy );

    /**
     * Evaluate the request
     *
     * @param request
     * @return
     */
    public PDPEvaluation evaluate( RequestWrapper request );

}
