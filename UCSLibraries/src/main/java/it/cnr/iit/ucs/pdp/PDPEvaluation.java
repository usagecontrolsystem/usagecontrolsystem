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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

/**
 * This is the interface offered by the object returned by a PDP after evaluation.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 */
@JsonIgnoreProperties( ignoreUnknown = true )
@JsonTypeInfo( use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "evaluationType" )
@JsonSubTypes( {
    @JsonSubTypes.Type( value = PDPResponse.class, name = "PDPResponse" )
} )
public interface PDPEvaluation {

    /**
     * Retrieves the response to the evaluation performed by the PDP
     *
     * @return a String representing the response of the PDP
     */
    public String getResult();

    public String getResponse();

    /**
     * Retrieves the obligations to be performed by the PIPs in list format
     *
     * @return a list representing the obligations to be performed by the PIPs
     */
    public List<PDPObligationInterface> getPIPObligations();

    /**
     * Retrieves the obligations to be performed by the PEP in list format
     *
     * @return a list representing the obligations to be performed by the PEP
     */
    public List<PDPObligationInterface> getPEPObligations();

    /**
     * Retrieves the obligations in string format
     *
     * @return the obligations in String format
     */
    public List<String> getObligations();

    public List<Integer> getFiringRules();

    public boolean isDecision( DecisionType decisionType );
}
