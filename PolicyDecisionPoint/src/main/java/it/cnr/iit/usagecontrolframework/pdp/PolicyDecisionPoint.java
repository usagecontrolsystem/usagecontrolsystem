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
package it.cnr.iit.usagecontrolframework.pdp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.Balana;
import org.wso2.balana.PDPConfig;
import org.wso2.balana.ParsingException;
import org.wso2.balana.Policy;
import org.wso2.balana.PolicyReference;
import org.wso2.balana.PolicySet;
import org.wso2.balana.PolicyTreeElement;
import org.wso2.balana.XACMLConstants;
import org.wso2.balana.combine.CombinerElement;
import org.wso2.balana.ctx.AbstractRequestCtx;
import org.wso2.balana.ctx.AbstractResult;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.EvaluationCtxFactory;
import org.wso2.balana.ctx.RequestCtxFactory;
import org.wso2.balana.ctx.ResponseCtx;
import org.wso2.balana.ctx.ResultFactory;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.ctx.xacml3.RequestCtx;
import org.wso2.balana.ctx.xacml3.Result;
import org.wso2.balana.ctx.xacml3.XACML3EvaluationCtx;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.PolicyFinderResult;
import org.wso2.balana.xacml3.MultipleCtxResult;

import it.cnr.iit.ucs.configuration.PdpProperties;
import it.cnr.iit.ucs.constants.STATUS;
import it.cnr.iit.ucsinterface.pdp.AbstractPDP;
import it.cnr.iit.ucsinterface.pdp.PDPEvaluation;
import it.cnr.iit.ucsinterface.pdp.PDPResponse;
import it.cnr.iit.utility.JAXBUtility;
import it.cnr.iit.xacmlutilities.policy.PolicyHelper;

import journal.io.api.Journal;
import journal.io.api.Journal.WriteType;
import journal.io.api.JournalBuilder;
import oasis.names.tc.xacml.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml.core.schema.wd_17.RuleType;

/**
 * Implementation of the PDP.
 * <p>
 * The pdp is basically a wrapper around the one offered by BALANA. <br>
 * In our implementation we are able to evaluate single condition policies only,
 * hence we need the context handler to pass to the PDP only the condition it
 * effectively wants to be evaluated. This because BALANA is designed for XACML
 * that is slightly different than UXACML. In particular, in the former, it is
 * allowed to have only one condition per rule.
 * </p>
 * <p>
 * NOTE: MOST of this code has been implemented by Fabio and Filippo
 * </p>
 *
 * @author Antonio La Marra, Fabio Bindi, Filippo Lauria
 *
 */
public final class PolicyDecisionPoint extends AbstractPDP {
    private static Logger LOGGER = Logger.getLogger( PolicyDecisionPoint.class.getName() );

    // Configuration of the PDP
    private PDPConfig pdpConfig;
    private Journal journal = null;

    public PolicyDecisionPoint( PdpProperties configuration ) {
        super( configuration );
        configure( configuration.getJournalingDir() );

    }

    private void configure( String journalFolder ) {
        try {
            File file = new File( journalFolder );
            if( !file.exists() ) {
                file.mkdir();
            }
            journal = JournalBuilder.of( file ).open();
        } catch( Exception e ) {
            throw new RuntimeException( e.getMessage() );
        }
    }

    private String extractFromStatus( STATUS status ) {
        switch( status ) {
            case TRYACCESS:
                return "pre";
            case STARTACCESS:
            case REEVALUATION:
            case REVOKE:
                return "ongoing";
            case ENDACCESS:
                return "post";
            default:
                return null;
        }
    }

    /**
     * This is the effective evaluation function.
     */
    @Override
    public PDPEvaluation evaluate( String request, StringBuilder stringPolicy,
            STATUS status ) {
        try {
            String conditionName = extractFromStatus( status );
            PolicyHelper policyHelper = PolicyHelper
                .buildPolicyHelper( stringPolicy.toString() );
            String policyToEvaluate = policyHelper
                .getConditionForEvaluation( conditionName );

            ArrayList<ResponseCtx> responses = new ArrayList<>();

            PolicyFinder policyFinder = new PolicyFinder();
            Set<PolicyFinderModule> policyFinderModules = new HashSet<>();
            InputStreamBasedPolicyFinderModule dataUCONPolicyFinderModule = new InputStreamBasedPolicyFinderModule(
                policyToEvaluate );
            policyFinderModules.add( dataUCONPolicyFinderModule );
            policyFinder.setModules( policyFinderModules );
            policyFinder.init();
            ResponseCtx response = evaluate( request, policyFinder );
            LOGGER.info( response.encode() );
            journal.write( policyToEvaluate.getBytes(), WriteType.ASYNC );
            journal.write( request.getBytes(), WriteType.ASYNC );
            journal.write( response.encode().getBytes(), WriteType.ASYNC );
            journal.sync();
            responses.add( response );
            ArrayList<Integer> firingRules = new ArrayList<>();
            PDPResponse pdpResponse = new PDPResponse( response.encode() );
            if( response.getResults().iterator().next()
                .getDecision() == AbstractResult.DECISION_PERMIT ) {
                // stringPolicy.delete(0, stringPolicy.length());
                mergeFiringRules( firingRules, stringPolicy );
            }
            return pdpResponse;
        } catch( Exception e ) {
            LOGGER.severe( e.getMessage() );
        }
        return null;
    }

    /**
     * This is the effective evaluation function.
     */
    @Deprecated
    public PDPEvaluation evaluateSingleRules( String request, StringBuilder stringPolicy,
            STATUS status ) {
        try {
            String conditionName = extractFromStatus( status );
            PolicyHelper policyHelper = PolicyHelper
                .buildPolicyHelper( stringPolicy.toString() );
            String policyToEvaluate = policyHelper
                .getConditionForEvaluation( conditionName );
            PolicyType policyType = JAXBUtility.unmarshalToObject( PolicyType.class,
                policyToEvaluate );
            ArrayList<PolicyType> policies = splitSingleRule( policyType,
                policyToEvaluate );

            ArrayList<ResponseCtx> responses = new ArrayList<>();

            for( PolicyType policyTmp : policies ) {
                String policyAsString = JAXBUtility.marshalToString( PolicyType.class,
                    policyTmp, "Policy", JAXBUtility.SCHEMA );
                PolicyFinder policyFinder = new PolicyFinder();
                Set<PolicyFinderModule> policyFinderModules = new HashSet<>();
                InputStreamBasedPolicyFinderModule dataUCONPolicyFinderModule = new InputStreamBasedPolicyFinderModule(
                    policyAsString );
                policyFinderModules.add( dataUCONPolicyFinderModule );
                policyFinder.setModules( policyFinderModules );
                policyFinder.init();
                ResponseCtx response = evaluate( request, policyFinder );
                LOGGER.info( response.encode() );
                responses.add( response );
            }
            ArrayList<Integer> firingRules = new ArrayList<>();
            ResponseCtx firingRule = checkFiringRule( responses,
                policyType.getRuleCombiningAlgId(), firingRules );
            PDPResponse pdpResponse = new PDPResponse( firingRule.encode() );
            if( firingRule.getResults().iterator().next()
                .getDecision() == AbstractResult.DECISION_PERMIT ) {
                // stringPolicy.delete(0, stringPolicy.length());
                mergeFiringRules( firingRules, stringPolicy );
            }
            return pdpResponse;
        } catch( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    private void mergeFiringRules( ArrayList<Integer> firingRules,
            StringBuilder stringPolicy ) throws JAXBException {
        PolicyType policyType = new PolicyType();
        PolicyType oldPolicy = JAXBUtility.unmarshalToObject( PolicyType.class,
            stringPolicy.toString() );
        ArrayList<PolicyType> policies = splitSingleRule( oldPolicy,
            stringPolicy.toString() );

        for( Integer i : firingRules ) {
            PolicyType policy = policies.get( i );
            policyType.setAdviceExpressions( policy.getAdviceExpressions() );
            policyType.setPolicyId( policy.getPolicyId() );
            policyType.setDescription( policy.getDescription() );
            policyType.setMaxDelegationDepth( policy.getMaxDelegationDepth() );
            policyType.setPolicyDefaults( policy.getPolicyDefaults() );
            policyType.setObligationExpressions( policy.getObligationExpressions() );
            policyType.setRuleCombiningAlgId( policy.getRuleCombiningAlgId() );
            policyType.setTarget( policy.getTarget() );
            policyType.setVersion( policy.getVersion() );
            policyType
                .getCombinerParametersOrRuleCombinerParametersOrVariableDefinition()
                .addAll( policy
                    .getCombinerParametersOrRuleCombinerParametersOrVariableDefinition() );
        }
        if( stringPolicy.length() > 0 ) {
            stringPolicy.delete( 0, stringPolicy.length() );
        }
        try {
            stringPolicy.append( JAXBUtility.marshalToString( PolicyType.class,
                policyType, "PolicyType", JAXBUtility.SCHEMA ) );
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    /**
     * Once retrieved all the set of responses based on the rules we have decided
     * to apply, check which one is the firing rule
     *
     * @param responses
     *          the list of responses to each single rule
     * @param ruleCombining
     *          the rule combining algorithm to be used
     * @return the response
     */
    private ResponseCtx checkFiringRule( ArrayList<ResponseCtx> responses,
            String ruleCombining, ArrayList<Integer> firingRules ) {
        ResponseCtx firing = null;
        if( ruleCombining.equals( RULE_COMBINING.DENY_UNLESS_PERMIT.getValue() ) ) {
            int i = 0;
            for( ResponseCtx response : responses ) {
                if( response.getResults().iterator().next()
                    .getDecision() == AbstractResult.DECISION_DENY ) {
                    firing = response;
                    break;
                }
                if( response.getResults().iterator().next()
                    .getDecision() == AbstractResult.DECISION_PERMIT ) {
                    firing = response;
                    firingRules.add( i );
                }
                i += 1;
            }
        }
        if( ruleCombining.equals( RULE_COMBINING.FIRST_APPLICABLE.getValue() ) ) {
            int i = 0;
            for( ResponseCtx response : responses ) {
                if( response.getResults().iterator().next()
                    .getDecision() == AbstractResult.DECISION_DENY ) {
                    firing = response;
                    break;
                }
                if( response.getResults().iterator().next()
                    .getDecision() == AbstractResult.DECISION_PERMIT ) {
                    firing = response;
                    firingRules.add( i );
                    break;
                }
                i += 1;
            }
        }
        if( firing == null ) {
            firing = new ResponseCtx( new Result( AbstractResult.DECISION_INDETERMINATE,
                Status.getOkInstance() ) );
        }
        return firing;
    }

    /**
     * Parses the actual policy passed as parameter in order to split it in many
     * sub-policies all formed by a single rule, the eventual default deny rule is
     * appended to all the other rules and is not considered alone.
     *
     * @param policyType
     *          the policytype we are considering
     * @param stringPolicy
     *          the policy in string format
     * @return the list of all the policies formed by the single rule plus the
     *         eventual default-deny rule
     * @throws JAXBException
     */
    private ArrayList<PolicyType> splitSingleRule( PolicyType policyType,
            String stringPolicy ) throws JAXBException {
        ArrayList<PolicyType> policies = new ArrayList<>();
        ArrayList<Integer> rulesIndexes = new ArrayList<>();
        for( int i = 0; i < policyType
            .getCombinerParametersOrRuleCombinerParametersOrVariableDefinition()
            .size(); i++ ) {
            Object object = policyType
                .getCombinerParametersOrRuleCombinerParametersOrVariableDefinition()
                .get( i );
            if( object instanceof RuleType ) {
                if( !( (RuleType) object ).getRuleId()
                    .equals( "urn:oasis:names:tc:xacml:3.0:defdeny" ) ) {
                    rulesIndexes.add( i );
                    policies.add(
                        JAXBUtility.unmarshalToObject( PolicyType.class, stringPolicy ) );
                }
            }
        }
        int i = 0;
        for( int p = 0; p < policies.size(); p++ ) {
            PolicyType policyTmp = policies.get( p );
            for( int j = rulesIndexes.size() - 1; j >= 0; ) {
                if( j != i ) {
                    int t = rulesIndexes.get( j );
                    policyTmp
                        .getCombinerParametersOrRuleCombinerParametersOrVariableDefinition()
                        .remove( t );
                    j -= 1;
                } else {
                    j -= 1;
                }
            }
            i += 1;
            if( i == rulesIndexes.size() ) {
                break;
            }
        }
        return policies;
    }

    /**
     * Attempts to evaluate the request against the policies known to this PDP.
     * This is really the core method of the entire XACML specification, and for
     * most people will provide what you want. If you need any special handling,
     * you should look at the version of this method that takes an
     * <code>EvaluationCtx</code>.
     * <p>
     * Note that if the request is somehow invalid (it was missing a required
     * attribute, it was using an unsupported scope, etc), then the result will be
     * a decision of INDETERMINATE.
     *
     * @param request
     *          the request to evaluate
     *
     * @return a response paired to the request
     */
    private ResponseCtx evaluate( String request, PolicyFinder policyFinder ) {
        AbstractRequestCtx requestCtx;
        ResponseCtx responseCtx;

        try {
            requestCtx = RequestCtxFactory.getFactory()
                .getRequestCtx( request.replaceAll( ">\\s+<", "><" ) );
            responseCtx = evaluate( requestCtx, policyFinder );
            /*
             * Set<AbstractResult> aa = responseCtx.getResults();
             *
             * AbstractResult a = aa.iterator().next();
             * a.getDecision()==AbstractResult.DECISION_DENY ; List<ObligationResult >
             * l = a.getObligations(); l.get(0).
             */
        } catch( ParsingException e ) {
            String error = "Invalid request  : " + e.getMessage();
            // there was something wrong with the request, so we return
            // Indeterminate with a status of syntax error...though this
            // may change if a more appropriate status type exists
            ArrayList<String> code = new ArrayList<>();
            code.add( Status.STATUS_SYNTAX_ERROR );
            Status status = new Status( code, error );
            // As invalid request, by default XACML 3.0 response is created.
            responseCtx = new ResponseCtx(
                new Result( AbstractResult.DECISION_INDETERMINATE, status ) );
        }

        return responseCtx;
    }

    /**
     * Attempts to evaluate the request against the policies known to this PDP.
     * This is really the core method of the entire XACML specification, and for
     * most people will provide what you want. If you need any special handling,
     * you should look at the version of this method that takes an
     * <code>EvaluationCtx</code>.
     * <p>
     * Note that if the request is somehow invalid (it was missing a required
     * attribute, it was using an unsupported scope, etc), then the result will be
     * a decision of INDETERMINATE.
     *
     * @param request
     *          the request to evaluate
     *
     * @return a response paired to the request
     */
    private ResponseCtx evaluate( AbstractRequestCtx request,
            PolicyFinder policyFinder ) {

        EvaluationCtx evalContext = null;
        try {
            Balana balana = Balana.getInstance();
            pdpConfig = balana.getPdpConfig();
            evalContext = EvaluationCtxFactory.getFactory().getEvaluationCtx( request,
                pdpConfig );
            return evaluate( evalContext, policyFinder );
        } catch( ParsingException e ) {
            // there was something wrong with the request, so we return
            // Indeterminate with a status of syntax error...though this
            // may change if a more appropriate status type exists
            ArrayList<String> code = new ArrayList<>();
            code.add( Status.STATUS_SYNTAX_ERROR );
            Status status = new Status( code, e.getMessage() );
            return new ResponseCtx( ResultFactory.getFactory().getResult(
                AbstractResult.DECISION_INDETERMINATE, status,
                request.getXacmlVersion() ) );

        }
    }

    /**
     * Uses the given <code>EvaluationCtx</code> against the available policies to
     * determine a response. If you are starting with a standard XACML Request,
     * then you should use the version of this method that takes a
     * <code>RequestCtx</code>. This method should be used only if you have a real
     * need to directly construct an evaluation context (or if you need to use an
     * <code>EvaluationCtx</code> implementation other than
     * <code>XACML3EvaluationCtx</code> and <code>XACML2EvaluationCtx</code>).
     *
     * @param context
     *          representation of the request and the context used for evaluation
     *
     * @return a response based on the contents of the context
     */
    private ResponseCtx evaluate( EvaluationCtx context,
            PolicyFinder policyFinder ) {

        // check whether this PDP configure to support multiple decision profile
        if( pdpConfig.isMultipleRequestHandle() ) {

            Set<EvaluationCtx> evaluationCtxSet;
            MultipleCtxResult multipleCtxResult = context.getMultipleEvaluationCtx();
            if( multipleCtxResult.isIndeterminate() ) {
                return new ResponseCtx( ResultFactory.getFactory().getResult(
                    AbstractResult.DECISION_INDETERMINATE,
                    multipleCtxResult.getStatus(), context ) );
            } else {
                evaluationCtxSet = multipleCtxResult.getEvaluationCtxSet();
                HashSet<AbstractResult> results = new HashSet<>();
                for( EvaluationCtx ctx : evaluationCtxSet ) {
                    // do the evaluation, for all evaluate context
                    AbstractResult result = evaluateContext( ctx, policyFinder );
                    // add the result
                    results.add( result );
                }
                // XACML 3.0.version
                return new ResponseCtx( results, XACMLConstants.XACML_VERSION_3_0 );
            }
        } else {
            // this is special case that specific to XACML3 request
            if( context instanceof XACML3EvaluationCtx
                    && ( (XACML3EvaluationCtx) context ).isMultipleAttributes() ) {
                ArrayList<String> code = new ArrayList<>();
                code.add( Status.STATUS_SYNTAX_ERROR );
                Status status = new Status( code,
                    "PDP does not supports multiple decision profile. "
                            + "Multiple AttributesType elements with the same Category can be existed" );
                return new ResponseCtx( ResultFactory.getFactory()
                    .getResult( AbstractResult.DECISION_INDETERMINATE, status, context ) );
            } else if( context instanceof XACML3EvaluationCtx
                    && ( (RequestCtx) context.getRequestCtx() ).isCombinedDecision() ) {
                List<String> code = new ArrayList<>();
                code.add( Status.STATUS_PROCESSING_ERROR );
                Status status = new Status( code,
                    "PDP does not supports multiple decision profile. "
                            + "Multiple decision is not existed to combine them" );
                return new ResponseCtx( ResultFactory.getFactory()
                    .getResult( AbstractResult.DECISION_INDETERMINATE, status, context ) );
            } else {
                return new ResponseCtx( evaluateContext( context, policyFinder ) );
            }
        }
    }

    /**
     * A private helper routine that resolves a policy for the given context, and
     * then tries to evaluate based on the policy
     *
     * @param context
     *          context
     * @return a response
     */
    private AbstractResult evaluateContext( EvaluationCtx context,
            PolicyFinder policyFinder ) {
        // first off, try to find a policy
        PolicyFinderResult finderResult = policyFinder.findPolicy( context );

        // see if there weren't any applicable policies
        if( finderResult.notApplicable() ) {
            return ResultFactory.getFactory()
                .getResult( AbstractResult.DECISION_NOT_APPLICABLE, context );
        }
        // see if there were any errors in trying to get a policy
        if( finderResult.indeterminate() ) {
            return ResultFactory.getFactory().getResult(
                AbstractResult.DECISION_INDETERMINATE, finderResult.getStatus(),
                context );
        }

        // we found a valid policy,

        // list all found policies if XACML 3.0
        if( context instanceof XACML3EvaluationCtx
                && ( (RequestCtx) context.getRequestCtx() ).isReturnPolicyIdList() ) {
            Set<PolicyReference> references = new HashSet<>();
            processPolicyReferences( finderResult.getPolicy(), references );
            ( (XACML3EvaluationCtx) context ).setPolicyReferences( references );
        }

        // so we can do the evaluation
        return finderResult.getPolicy().evaluate( context );
    }

    /**
     *
     * @param policy
     * @param references
     */
    private void processPolicyReferences( AbstractPolicy policy,
            Set<PolicyReference> references ) {
        if( policy instanceof Policy ) {
            references.add( new PolicyReference( policy.getId(),
                PolicyReference.POLICY_REFERENCE, null, null ) );
        } else if( policy instanceof PolicySet ) {
            List<CombinerElement> elements = policy.getChildElements();
            if( elements != null && elements.size() > 0 ) {
                for( CombinerElement element : elements ) {
                    PolicyTreeElement treeElement = element.getElement();
                    if( treeElement instanceof AbstractPolicy ) {
                        processPolicyReferences( (AbstractPolicy) treeElement, references );
                    } else {
                        references.add( new PolicyReference( policy.getId(),
                            PolicyReference.POLICYSET_REFERENCE, null, null ) );
                    }
                }
            }
        }
    }

    @Override
    public PDPEvaluation evaluate( String request ) {
        LOGGER.info( "evaluate( String request ) not implemented" );
        return null;
    }

    @Override
    public PDPEvaluation evaluate( String request, String policy ) {
        try {
            PolicyFinder policyFinder = new PolicyFinder();
            Set<PolicyFinderModule> policyFinderModules = new HashSet<>();
            InputStreamBasedPolicyFinderModule dataUCONPolicyFinderModule = new InputStreamBasedPolicyFinderModule(
                policy );
            policyFinderModules.add( dataUCONPolicyFinderModule );
            policyFinder.setModules( policyFinderModules );
            policyFinder.init();
            ResponseCtx responseCtx = evaluate( request, policyFinder );
            journal.write( policy.getBytes(), WriteType.ASYNC );
            journal.write( request.getBytes(), WriteType.ASYNC );
            journal.write( responseCtx.encode().getBytes(), WriteType.ASYNC );
            journal.sync();
            PDPResponse pdpResponse = new PDPResponse( responseCtx.encode() );
            return pdpResponse;
        } catch(

        Exception e ) {
            e.printStackTrace();
        }

        return null;
    }

    @Deprecated
    public PDPEvaluation evaluateSingleRule( String request, String policy ) {
        try {
            PolicyType policyType = JAXBUtility.unmarshalToObject( PolicyType.class,
                policy );
            ArrayList<PolicyType> policies = splitSingleRule( policyType, policy );

            ArrayList<ResponseCtx> responses = new ArrayList<>();

            for( PolicyType policyTmp : policies ) {
                String policyAsString = JAXBUtility.marshalToString( PolicyType.class,
                    policyTmp, "Policy", JAXBUtility.SCHEMA );
                PolicyFinder policyFinder = new PolicyFinder();
                Set<PolicyFinderModule> policyFinderModules = new HashSet<>();
                InputStreamBasedPolicyFinderModule dataUCONPolicyFinderModule = new InputStreamBasedPolicyFinderModule(
                    policyAsString );
                policyFinderModules.add( dataUCONPolicyFinderModule );
                policyFinder.setModules( policyFinderModules );
                policyFinder.init();
                responses.add( evaluate( request, policyFinder ) );
            }
            ArrayList<Integer> firingRules = new ArrayList<>();
            ResponseCtx firingRule = checkFiringRule( responses,
                policyType.getRuleCombiningAlgId(), firingRules );
            PDPResponse pdpResponse = new PDPResponse( firingRule.encode() );

            return pdpResponse;
        } catch( Exception e ) {
            LOGGER.severe( e.getMessage() );
        }

        return null;
    }
}
