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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

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

import it.cnr.iit.ucs.constants.STATUS;
import it.cnr.iit.ucs.properties.components.PdpProperties;
import it.cnr.iit.utility.FileUtility;
import it.cnr.iit.xacml.PolicyTags;
import it.cnr.iit.xacml.wrappers.PolicyWrapper;
import it.cnr.iit.xacml.wrappers.RequestWrapper;

import journal.io.api.Journal;
import journal.io.api.Journal.WriteType;
import journal.io.api.JournalBuilder;

/**
 * This PDP is a wrapper around the one offered by BALANA.
 * In our implementation we are able to evaluate single condition policies only,
 * hence we need the context handler to pass to the PDP only the condition it
 * effectively wants to be evaluated. This because BALANA is designed for XACML
 * that is slightly different than UXACML. In particular, in the former, it is
 * allowed to have only one condition per rule.
 *
 * @author Antonio La Marra, Fabio Bindi, Filippo Lauria, Alessandro Rosetti
 *
 */
public final class PolicyDecisionPoint extends AbstractPDP {

    private static Logger log = Logger.getLogger( PolicyDecisionPoint.class.getName() );

    private PDPConfig pdpConfig;
    private Journal journal = null;

    public PolicyDecisionPoint( PdpProperties properties ) {
        super( properties );
        buildJournal( properties.getJournalDir() );
    }

    @Override
    public PDPEvaluation evaluate( RequestWrapper request, PolicyWrapper policy, STATUS status ) {
        String conditionName = PolicyTags.getCondition( status );
        PolicyWrapper policyToEvaluate = policy.getPolicyForCondition( conditionName );
        return evaluate( request, policyToEvaluate );
    }

    @Override
    public PDPEvaluation evaluate( RequestWrapper request, PolicyWrapper policy ) {
        try {
            PolicyFinder policyFinder = getPolicyFinder( policy );
            ResponseCtx responseCtx = evaluate( request.getRequest(), policyFinder );
            journal.write( policy.getPolicy().getBytes(), WriteType.ASYNC );
            journal.write( request.getRequest().getBytes(), WriteType.ASYNC );
            journal.write( responseCtx.encode().getBytes(), WriteType.ASYNC );
            journal.sync();
            return new PDPResponse( responseCtx.encode() );
        } catch( Exception e ) {
            log.severe( "Error in evaluation : " + e.getMessage() );
        }

        return null;
    }

    @Override
    public PDPEvaluation evaluate( RequestWrapper request ) {
        log.info( "evaluate( request ) not implemented" );
        return null;
    }

    private PolicyFinder getPolicyFinder( PolicyWrapper policy ) {
        PolicyFinder policyFinder = new PolicyFinder();
        Set<PolicyFinderModule> policyFinderModulesSet = new HashSet<>();
        InputStreamBasedPolicyFinderModule finderModule = new InputStreamBasedPolicyFinderModule(
            policy.getPolicy() );
        policyFinderModulesSet.add( finderModule );
        policyFinder.setModules( policyFinderModulesSet );
        policyFinder.init();
        return policyFinder;
    }

    /**
     * Attempts to evaluate the request against the policies known to this PDP.
     * This is really the core method of the entire XACML specification, and for
     * most people will provide what you want. If you need any special handling,
     * you should look at the version of this method that takes an
     * <code>EvaluationCtx</code>.
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
            // TODO UCS-36 NOSONAR
            requestCtx = RequestCtxFactory.getFactory().getRequestCtx( request.replaceAll( ">\\s+<", "><" ) );
            responseCtx = evaluate( requestCtx, policyFinder );
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
         * Note that if the request is somehow invalid (it was missing a required
     * attribute, it was using an unsupported scope, etc), then the result will be
     * a decision of INDETERMINATE.
     *
     * @param request
     *          the request to evaluate
     *
     * @return a response paired to the request
     */
    private ResponseCtx evaluate( AbstractRequestCtx request, PolicyFinder policyFinder ) {
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
    private ResponseCtx evaluate( EvaluationCtx context, PolicyFinder policyFinder ) {
        // check whether this PDP is configured to support multiple decision profiles
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
                    AbstractResult result = evaluateContext( ctx, policyFinder );
                    results.add( result );
                }
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
    private AbstractResult evaluateContext( EvaluationCtx context, PolicyFinder policyFinder ) {
        PolicyFinderResult finderResult = policyFinder.findPolicy( context );

        if( finderResult.notApplicable() ) {
            return ResultFactory.getFactory()
                .getResult( AbstractResult.DECISION_NOT_APPLICABLE, context );
        }
        if( finderResult.indeterminate() ) {
            return ResultFactory.getFactory().getResult(
                AbstractResult.DECISION_INDETERMINATE, finderResult.getStatus(),
                context );
        }
        // we found a valid policy, list all found policies if XACML 3.0
        if( context instanceof XACML3EvaluationCtx
                && ( (RequestCtx) context.getRequestCtx() ).isReturnPolicyIdList() ) {
            Set<PolicyReference> references = new HashSet<>();
            processPolicyReferences( finderResult.getPolicy(), references );
            ( (XACML3EvaluationCtx) context ).setPolicyReferences( references );
        }

        // we can perform the evaluation
        return finderResult.getPolicy().evaluate( context );
    }

    private void processPolicyReferences( AbstractPolicy policy,
            Set<PolicyReference> references ) {
        if( policy instanceof Policy ) {
            references.add( new PolicyReference( policy.getId(),
                PolicyReference.POLICY_REFERENCE, null, null ) );
        } else if( policy instanceof PolicySet ) {
            List<CombinerElement> elements = policy.getChildElements();
            if( elements != null && elements.isEmpty() ) {
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

    private void buildJournal( String journalDir ) {
        try {
            File file = new File( journalDir ); // TODO UCS-33 NOSONAR
            FileUtility.createPathIfNotExists( file );
            journal = JournalBuilder.of( file ).open();
        } catch( Exception e ) {
            throw new IllegalArgumentException( e.getMessage() );
        }
    }

}
