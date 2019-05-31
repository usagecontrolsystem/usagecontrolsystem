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
package oasis.names.tc.xacml.core.schema.wd_17;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType( XmlAccessType.FIELD )
@XmlType( name = "PolicyType",
    propOrder = { "description", "policyIssuer", "policyDefaults", "target",
        "combinerParametersOrRuleCombinerParametersOrVariableDefinition",
        "obligationExpressions", "adviceExpressions" } )
public class PolicyType {

    @XmlElement( name = "Description" )
    protected String description;
    @XmlElement( name = "PolicyIssuer" )
    protected PolicyIssuerType policyIssuer;
    @XmlElement( name = "PolicyDefaults" )
    protected DefaultsType policyDefaults;
    @XmlElement( name = "Target", required = true )
    protected TargetType target;
    @XmlElements( {
        @XmlElement( name = "CombinerParameters",
            type = CombinerParametersType.class ),
        @XmlElement( name = "RuleCombinerParameters",
            type = RuleCombinerParametersType.class ),
        @XmlElement( name = "VariableDefinition",
            type = VariableDefinitionType.class ),
        @XmlElement( name = "Rule", type = RuleType.class ) } )
    protected List<Object> combinerParametersOrRuleCombinerParametersOrVariableDefinition;
    @XmlElement( name = "ObligationExpressions" )
    protected ObligationExpressionsType obligationExpressions;
    @XmlElement( name = "AdviceExpressions" )
    protected AdviceExpressionsType adviceExpressions;
    @XmlAttribute( name = "PolicyId", required = true )
    @XmlSchemaType( name = "anyURI" )
    protected String policyId;
    @XmlAttribute( name = "Version", required = true )
    protected String version;
    @XmlAttribute( name = "RuleCombiningAlgId", required = true )
    @XmlSchemaType( name = "anyURI" )
    protected String ruleCombiningAlgId;
    @XmlAttribute( name = "MaxDelegationDepth" )
    protected BigInteger maxDelegationDepth;

    public String getDescription() {
        return description;
    }

    public void setDescription( String value ) {
        this.description = value;
    }

    public PolicyIssuerType getPolicyIssuer() {
        return policyIssuer;
    }

    public void setPolicyIssuer( PolicyIssuerType value ) {
        this.policyIssuer = value;
    }

    public DefaultsType getPolicyDefaults() {
        return policyDefaults;
    }

    public void setPolicyDefaults( DefaultsType value ) {
        this.policyDefaults = value;
    }

    public TargetType getTarget() {
        return target;
    }

    public void setTarget( TargetType value ) {
        this.target = value;
    }

    /**
     * This accessory method returns a reference to the live list, not a snapshot.
     * Therefore any modification you make to the returned list will be present
     * inside the JAXB object.
     */
    public List<Object> getCombinerParametersOrRuleCombinerParametersOrVariableDefinition() {
        if( combinerParametersOrRuleCombinerParametersOrVariableDefinition == null ) {
            combinerParametersOrRuleCombinerParametersOrVariableDefinition = new ArrayList<>();
        }
        return this.combinerParametersOrRuleCombinerParametersOrVariableDefinition;
    }

    public ObligationExpressionsType getObligationExpressions() {
        return obligationExpressions;
    }

    public void setObligationExpressions( ObligationExpressionsType value ) {
        this.obligationExpressions = value;
    }

    public AdviceExpressionsType getAdviceExpressions() {
        return adviceExpressions;
    }

    public void setAdviceExpressions( AdviceExpressionsType value ) {
        this.adviceExpressions = value;
    }

    public String getPolicyId() {
        return policyId;
    }

    public void setPolicyId( String value ) {
        this.policyId = value;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion( String value ) {
        this.version = value;
    }

    public String getRuleCombiningAlgId() {
        return ruleCombiningAlgId;
    }

    public void setRuleCombiningAlgId( String value ) {
        this.ruleCombiningAlgId = value;
    }

    public BigInteger getMaxDelegationDepth() {
        return maxDelegationDepth;
    }

    public void setMaxDelegationDepth( BigInteger value ) {
        this.maxDelegationDepth = value;
    }

    public void removeOtherRules( String ruleId ) {
        for( int i = 0; i < combinerParametersOrRuleCombinerParametersOrVariableDefinition
            .size(); i++ ) {
            Object obj = combinerParametersOrRuleCombinerParametersOrVariableDefinition
                .get( i );
            if( obj instanceof RuleType ) {
                RuleType rule = (RuleType) obj;
                if( !rule.getRuleId().equals( ruleId ) ) {
                    combinerParametersOrRuleCombinerParametersOrVariableDefinition
                        .remove( i );
                }
            }
        }
    }

}
