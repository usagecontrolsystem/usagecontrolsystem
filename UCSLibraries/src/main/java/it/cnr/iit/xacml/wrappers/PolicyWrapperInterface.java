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
package it.cnr.iit.xacml.wrappers;

import java.util.List;

import it.cnr.iit.ucs.exceptions.PolicyException;
import it.cnr.iit.xacml.Attribute;

/**
 * This is the interface provided by a PolicyWapper.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public interface PolicyWrapperInterface {
    /**
     * Retrieves the list of attributes to be used to evaluate a certain condition.
     *
     * @param conditionName
     *          the name of the condition
     * @return the list of attributes
     */
    public List<Attribute> getAttributesForCondition( String conditionName );

    /**
     * Retrieves the list of obligations
     */
    public String retrieveObligations();

    /**
     * Retrieves the ruleCombiningAlgorithmID.
     * The rule combining algorithm is necessary whenever we need to couple
     * together a set of rules to provide a single decision.
     *
     * @return the id of the rule combining algorithm
     */
    public String getRuleCombiningAlgorithmId();

    /**
     * Converts the actual object into a String that can be used by the PDP for
     * evaluation. Our PDP in fact can evaluate Policies only in the string
     * format.
     *
     * @param condition
     *          states which is the condition we will use for the evaluation
     * @return a String representing the policy to be used for the evaluation
     * @throws PolicyException
     */
    public PolicyWrapper getPolicyForCondition( String condition ) throws PolicyException;

}
