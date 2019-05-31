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

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import it.cnr.iit.utility.JAXBUtility;
import it.cnr.iit.utility.errorhandling.Reject;
import it.cnr.iit.xacml.Attribute;
import it.cnr.iit.xacml.Category;
import it.cnr.iit.xacml.DataType;
import it.cnr.iit.xacml.PolicyTags;

import oasis.names.tc.xacml.core.schema.wd_17.ApplyType;
import oasis.names.tc.xacml.core.schema.wd_17.AttributeDesignatorType;
import oasis.names.tc.xacml.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml.core.schema.wd_17.ConditionType;
import oasis.names.tc.xacml.core.schema.wd_17.EffectType;
import oasis.names.tc.xacml.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml.core.schema.wd_17.RuleType;

/**
 * This is a wrapper for the policy class.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public class PolicyWrapper implements PolicyWrapperInterface {

    private static final Logger log = Logger.getLogger( PolicyWrapper.class.getName() );

    private static final String MSG_ERR_UNMASHAL = "Error unmarshalling policy : {0}";
    private static final String MSG_ERR_MARSHAL = "Error marshalling policy : {0}";
    private static final String MSG_WARN_COND_NOT_FOUND = "Condition not found : {0}";

    private PolicyType policyType;
    private String policy;

    private PolicyWrapper() {}

    /**
     * Builds a PolicyHelper object starting from the raw policy string
     *
     * @param policy
     *          the string that describes the policy
     * @return a PolicyWrapper object if everything goes fine, null otherwise
     */
    public static PolicyWrapper build( String policy ) {
        Reject.ifBlank( policy );

        PolicyWrapper policyWrapper = new PolicyWrapper();
        policyWrapper.setPolicy( policy );

        return policyWrapper.policyType != null ? policyWrapper : null;
    }

    @Override
    public List<Attribute> getAttributesForCondition( String conditionName ) {
        Reject.ifBlank( conditionName );

        List<Object> list = policyType.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();

        for( Object obj : list ) {
            if( !( obj instanceof RuleType ) ) {
                continue;
            }

            RuleType ruleType = (RuleType) obj;
            List<ConditionType> conditions = ruleType.getCondition();
            if( conditions == null ) {
                continue;
            }

            for( ConditionType conditionType : conditions ) {
                String decisionTime = conditionType.getDecisionTime();
                if( decisionTime == null ) {
                    if( conditionName.equals( PolicyTags.CONDITION_PRE ) ) {
                        return extractAttributes( conditionType );
                    }
                    return new ArrayList<>();
                } else if( decisionTime.equals( conditionName ) ) {
                    return extractAttributes( conditionType );
                }
            }
        }

        log.warning( String.format( MSG_WARN_COND_NOT_FOUND, conditionName ) );
        return new ArrayList<>();
    }

    /**
     * Function that effectively extracts the attributes from the condition.
     * The attribute object we have built up, embeds two different complex types
     * in the xsd: one is the AttributeDesignator, the other is the attribute
     * value.
     *
     * @param conditionType
     *          the condition we are analysing
     * @return the list of attributes types interested by this condition.
     */
    private List<Attribute> extractAttributes( ConditionType conditionType ) {
        ArrayList<JAXBElement<?>> elementList = new ArrayList<>();
        elementList.add( conditionType.getExpression() );
        ArrayList<Attribute> attributeList = new ArrayList<>();
        Attribute newAttribute = new Attribute();
        int lastIndex = 0;
        for( int i = 0; i < elementList.size(); i++ ) {
            JAXBElement<?> element = elementList.get( i );
            Object objValue = element.getValue();
            if( objValue instanceof ApplyType ) {
                elementList.addAll( ( (ApplyType) objValue ).getExpression() );
            } else if( objValue instanceof AttributeDesignatorType ) {
                AttributeDesignatorType attrDesignatorType = (AttributeDesignatorType) objValue;
                Attribute attribute = attributeList.get( lastIndex );
                attribute.setAttributeId( attrDesignatorType.getAttributeId() );
                attribute.setCategory( Category.toCATEGORY( attrDesignatorType.getCategory() ) );
                attribute.setDataType( DataType.toDATATYPE( attrDesignatorType.getDataType() ) );
                lastIndex += 1;
            } else if( objValue instanceof AttributeValueType ) {
                AttributeValueType attributeValueType = (AttributeValueType) element.getValue();
                for( Object obj : attributeValueType.getContent() ) {
                    newAttribute.setAttributeValues( attributeValueType.getDataType(), obj.toString() );
                }
                attributeList.add( newAttribute );
                newAttribute = new Attribute();
            }
        }
        return attributeList;
    }

    @Override
    public String retrieveObligations() {
        return null;
    }

    @Override
    public String getRuleCombiningAlgorithmId() {
        return policyType.getRuleCombiningAlgId();
    }

    /**
     * Retrieves the particular condition for the evaluation.
     *
     * In an UXACML policy we may have 3 types of conditions:
     * pre, ongoing and post.
     * In this function we basically want to extract only the condition
     * in which we're interested in.
     *
     * @param conditionName
     *          the required condition
     * @return a copy of the policyType containing only the required condition
     */
    @Override
    public PolicyWrapper getPolicy( String conditionName ) {
        PolicyType clonedPolicyType = clonePolicyType();
        /**
         * This is the most delicate part of this function.
         * If this list of objects contains a ruleType it must be analyzed
         * in order to retrieve only the required condition.
         * In any other case the object will be copied inside cloned list.
         */
        List<Object> objectList = policyType.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
        List<Object> clonedObjectList = clonedPolicyType.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
        for( Object obj : objectList ) {
            RuleType ruleType = (RuleType) obj;
            if( !( obj instanceof RuleType ) ||
                    ( ruleType.getCondition() == null || ruleType.getCondition().isEmpty() ) ) {
                clonedObjectList.add( obj );
                continue;
            }

            for( ConditionType conditionType : ruleType.getCondition() ) {
                String decisionTime = conditionType.getDecisionTime();
                RuleType clonedRuleType;
                if( decisionTime == null ) {
                    if( conditionName.equals( PolicyTags.CONDITION_PRE ) ) {
                        clonedRuleType = copyRuleType( ruleType, conditionType );
                    } else {
                        clonedRuleType = getDefaultRuleType( "def-permit", EffectType.PERMIT );
                        clonedRuleType.setObligationExpressions( ruleType.getObligationExpressions() );
                    }
                    clonedObjectList.add( clonedRuleType );
                } else if( decisionTime.equals( conditionName ) ) {
                    clonedRuleType = copyRuleType( ruleType, conditionType );
                    clonedObjectList.add( clonedRuleType );
                    break;
                }
            }
        }

        return PolicyWrapper.build( marshalPolicyType( clonedPolicyType ) );
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy( String policy ) {
        this.policy = policy;
        this.policyType = unmarshalPolicyType( policy );
    }

    public PolicyType getPolicyType() {
        return policyType;
    }

    private RuleType getDefaultRuleType( String id, EffectType effectType ) {
        RuleType ruleType = new RuleType();
        ruleType.setEffect( effectType );
        ruleType.setRuleId( id );
        return ruleType;
    }

    /**
     * Performs a copy of the ruleType object.
     *
     * @param ruleType
     *          the ruleType object we want to copy
     * @param conditionType
     *          the condition to be put inside the new ruleType object
     * @return the ruleType object built in this way
     */
    private RuleType copyRuleType( RuleType ruleType,
            ConditionType conditionType ) {
        RuleType newRuleType = new RuleType();
        newRuleType.getCondition().add( conditionType );
        newRuleType.setAdviceExpressions( ruleType.getAdviceExpressions() );
        newRuleType.setDescription( ruleType.getDescription() );
        newRuleType.setObligationExpressions( ruleType.getObligationExpressions() );
        newRuleType.setEffect( ruleType.getEffect() );
        newRuleType.setRuleId( ruleType.getRuleId() );
        newRuleType.setTarget( ruleType.getTarget() );

        return newRuleType;
    }

    /**
     * Performs a copy of the policyType object.
     *
     * @return the PolicyType object that is the copy of the one stored in this
     *         object
     */
    private PolicyType clonePolicyType() {
        PolicyType newPolicyType = new PolicyType();
        newPolicyType.setDescription( policyType.getDescription() );
        newPolicyType.setPolicyId( policyType.getPolicyId() );
        newPolicyType.setPolicyIssuer( policyType.getPolicyIssuer() );
        newPolicyType.setAdviceExpressions( policyType.getAdviceExpressions() );
        newPolicyType.setMaxDelegationDepth( policyType.getMaxDelegationDepth() );
        newPolicyType.setPolicyDefaults( policyType.getPolicyDefaults() );
        newPolicyType.setRuleCombiningAlgId( policyType.getRuleCombiningAlgId() );
        newPolicyType.setTarget( policyType.getTarget() );
        newPolicyType.setVersion( policyType.getVersion() );
        newPolicyType.setObligationExpressions( policyType.getObligationExpressions() );
        return newPolicyType;
    }

    private static PolicyType unmarshalPolicyType( String policy ) {
        try {
            return JAXBUtility.unmarshalToObject( PolicyType.class, policy );
        } catch( Exception e ) {
            log.severe( String.format( MSG_ERR_UNMASHAL, e.getMessage() ) );
        }
        return null;
    }

    private static String marshalPolicyType( PolicyType policy ) {
        try {
            return JAXBUtility.marshalToString( PolicyType.class, policy, PolicyTags.POLICY,
                JAXBUtility.SCHEMA );
        } catch( JAXBException e ) {
            log.severe( String.format( MSG_ERR_MARSHAL, e.getMessage() ) );
        }
        return null;
    }

}
