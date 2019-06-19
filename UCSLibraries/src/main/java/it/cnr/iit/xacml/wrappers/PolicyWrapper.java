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

import it.cnr.iit.ucs.exceptions.PolicyException;
import it.cnr.iit.ucs.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucs.pap.PAPInterface;
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
     * @throws PolicyException
     */
    public static PolicyWrapper build( String policy ) throws PolicyException {
        PolicyWrapper policyWrapper = new PolicyWrapper();
        try {
            PolicyType policyType = unmarshalPolicyType( policy );
            policyWrapper.setPolicyType( policyType );
        } catch( JAXBException e ) {
            throw new PolicyException( "Error unmarshalling policy : {0}" + e.getMessage() );
        }
        policyWrapper.setPolicy( policy );
        return policyWrapper;
    }

    public static PolicyWrapper build( PolicyType policyType ) throws PolicyException {
        PolicyWrapper policyWrapper = new PolicyWrapper();
        try {
            String policy = marshalPolicyType( policyType );
            policyWrapper.setPolicy( policy );
        } catch( JAXBException e ) {
            throw new PolicyException( "Error marshalling policy : {0}" + e.getMessage() );
        }
        policyWrapper.setPolicyType( policyType );
        return policyWrapper;
    }

    public static PolicyWrapper build( PAPInterface pap, TryAccessMessage message ) throws PolicyException {
        String policy = message.getPolicy();
        if( policy == null && message.getPolicyId() != null ) {
            policy = pap.retrievePolicy( message.getPolicyId() );
        }
        return PolicyWrapper.build( policy );
    }

    @Override
    public List<Attribute> getAttributesForCondition( String conditionName ) {
        Reject.ifBlank( conditionName );
        for( RuleType ruleType : policyType.getRuleTypeList() ) {
            List<ConditionType> conditions = ruleType.getCondition();
            if( conditions != null ) {
                for( ConditionType conditionType : conditions ) {
                    List<Attribute> attributeList = getAttributesFromCondition( conditionType, conditionName );
                    if( !attributeList.isEmpty() ) {
                        return attributeList;
                    }
                }
            }
        }
        log.warning( String.format( MSG_WARN_COND_NOT_FOUND, conditionName ) );
        return new ArrayList<>();
    }

    private List<Attribute> getAttributesFromCondition( ConditionType conditionType, String conditionName ) {
        if( conditionType.getDecisionTime() == null ) {
            if( conditionName.equals( PolicyTags.CONDITION_PRE ) ) {
                return extractAttributes( conditionType );
            }
        } else if( conditionType.getDecisionTime().equals( conditionName ) ) {
            return extractAttributes( conditionType );
        }
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
     * In UXACML we may have 3 types of conditions: pre, ongoing and post.
     * This function retrieves the policy with the required condition.
     *
     * @param conditionName
     *          the required condition
     * @return a copy of the policyType containing only the required condition
     * @throws PolicyException
     */
    @Override
    public PolicyWrapper getPolicyForCondition( String conditionName ) throws PolicyException {
        PolicyType clonedPolicyType = partialClonePolicyType();
        List<Object> objectList = policyType.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();
        List<Object> clonedObjectList = clonedPolicyType.getCombinerParametersOrRuleCombinerParametersOrVariableDefinition();

        for( Object obj : objectList ) {
            RuleType ruleType = (RuleType) obj;
            /* If this list of objects contains a ruleType with a condition list it must be analysed.
              In any other case the object will be copied inside cloned list. */
            if( !( obj instanceof RuleType ) ||
                    ( ruleType.getCondition() == null || ruleType.getCondition().isEmpty() ) ) {
                clonedObjectList.add( obj );
                continue;
            }

            for( ConditionType conditionType : ruleType.getCondition() ) {
                RuleType clonedRuleType;
                if( conditionType.getDecisionTime() == null ) {
                    if( conditionName.equals( PolicyTags.CONDITION_PRE ) ) {
                        clonedRuleType = cloneRuleType( ruleType, conditionType );
                    } else {
                        clonedRuleType = getDefaultRuleType( "def-permit", EffectType.PERMIT );
                        clonedRuleType.setObligationExpressions( ruleType.getObligationExpressions() );
                    }
                    clonedObjectList.add( clonedRuleType );
                } else if( conditionType.getDecisionTime().equals( conditionName ) ) {
                    clonedRuleType = cloneRuleType( ruleType, conditionType );
                    clonedObjectList.add( clonedRuleType );
                    break;
                }
            }
        }

        return PolicyWrapper.build( clonedPolicyType );
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy( String policy ) {
        this.policy = policy;
    }

    public PolicyType getPolicyType() {
        return policyType;
    }

    public void setPolicyType( PolicyType policyType ) {
        this.policyType = policyType;
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
    private RuleType cloneRuleType( RuleType ruleType, ConditionType conditionType ) {
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
     * Performs a partial copy of the policyType object.
     *
     * @return the PolicyType object that is the copy of the one stored in this
     *         object
     */
    private PolicyType partialClonePolicyType() {
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

    private static PolicyType unmarshalPolicyType( String policy ) throws JAXBException {
        return JAXBUtility.unmarshalToObject( PolicyType.class, policy );
    }

    private static String marshalPolicyType( PolicyType policy ) throws JAXBException {
        return JAXBUtility.marshalToString( PolicyType.class, policy, PolicyTags.POLICY, JAXBUtility.SCHEMA );
    }

}
