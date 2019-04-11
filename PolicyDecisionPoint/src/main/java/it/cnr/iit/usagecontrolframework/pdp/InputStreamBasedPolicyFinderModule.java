/*
 * THIS IS POLICY FINDER MODULE implemented by CNR for supporting DATA USAGE
 * CONTROL CHANGED by CNR-IIT : THIS SHOULD BE MOVED TO PDP (CORE)
 */

package it.cnr.iit.usagecontrolframework.pdp;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wso2.balana.AbstractPolicy;
import org.wso2.balana.MatchResult;
import org.wso2.balana.Policy;
import org.wso2.balana.PolicyMetaData;
import org.wso2.balana.PolicyReference;
import org.wso2.balana.PolicySet;
import org.wso2.balana.VersionConstraints;
import org.wso2.balana.combine.PolicyCombiningAlgorithm;
import org.wso2.balana.combine.xacml2.DenyOverridesPolicyAlg;
import org.wso2.balana.ctx.EvaluationCtx;
import org.wso2.balana.ctx.Status;
import org.wso2.balana.finder.PolicyFinder;
import org.wso2.balana.finder.PolicyFinderModule;
import org.wso2.balana.finder.PolicyFinderResult;

/**
 * This is file based policy repository. Policies can be inside the directory in
 * a file system. Then you can set directory location using
 * "org.wso2.balana.PolicyDirectory" JAVA property
 *
 * @author Fabio Bindi Filippo Lauria
 *
 */
class InputStreamBasedPolicyFinderModule extends PolicyFinderModule {

    private static final Logger log = Logger.getLogger( InputStreamBasedPolicyFinderModule.class.getName() );

    private PolicyFinder finder = null;

    private Map<URI, AbstractPolicy> policies = new HashMap<>();

    // the policy is stored here
    private String dataUsagePolicy = "";

    private PolicyCombiningAlgorithm combiningAlg;

    private DocumentBuilderFactory documentBuilderFactory;

    public InputStreamBasedPolicyFinderModule( String policy ) {
        dataUsagePolicy = new String( policy );

        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setIgnoringComments( true );
        documentBuilderFactory.setNamespaceAware( true );
        documentBuilderFactory.setValidating( false );
    }

    @Override
    public void init( PolicyFinder finder ) {
        this.finder = finder;

        loadPolicies();
        combiningAlg = new DenyOverridesPolicyAlg();
    }

    @Override
    public PolicyFinderResult findPolicy( EvaluationCtx context ) {
        ArrayList<AbstractPolicy> selectedPolicies = new ArrayList<>();
        Set<Map.Entry<URI, AbstractPolicy>> entrySet = policies.entrySet();

        // iterate through all the policies we currently have loaded
        for( Map.Entry<URI, AbstractPolicy> entry : entrySet ) {
            AbstractPolicy policy = entry.getValue();
            MatchResult match = policy.match( context );
            int result = match.getResult();

            // if target matching was indeterminate, then return the error
            if( result == MatchResult.INDETERMINATE ) {
                return new PolicyFinderResult( match.getStatus() );
            }
            // see if the target matched
            if( result == MatchResult.MATCH ) {
                if( ( combiningAlg == null ) && ( selectedPolicies.size() > 0 ) ) {
                    // we found a match before, so this is an error
                    ArrayList<String> code = new ArrayList<>();
                    code.add( Status.STATUS_PROCESSING_ERROR );
                    Status status = new Status( code, "too many applicable " + "top-level policies" );
                    return new PolicyFinderResult( status );
                }
                // this is the first match we've found, so remember it
                selectedPolicies.add( policy );
            }
        }

        // no errors happened during the search, so now take the right
        // action based on how many policies we found
        switch( selectedPolicies.size() ) {
            case 0:
                return new PolicyFinderResult();
            case 1:
                return new PolicyFinderResult( ( selectedPolicies.get( 0 ) ) );
            default:
                return new PolicyFinderResult( new PolicySet( null, combiningAlg, null, selectedPolicies ) );
        }
    }

    @Override
    public PolicyFinderResult findPolicy( URI idReference, int type, VersionConstraints constraints,
            PolicyMetaData parentMetaData ) {

        AbstractPolicy policy = policies.get( idReference );
        if( policy != null ) {
            if( type == PolicyReference.POLICY_REFERENCE ) {
                if( policy instanceof Policy ) {
                    return new PolicyFinderResult( policy );
                }
            } else {
                if( policy instanceof PolicySet ) {
                    return new PolicyFinderResult( policy );
                }
            }
        }

        // if there was an error loading the policy, return the error
        ArrayList<String> code = new ArrayList<>();
        code.add( Status.STATUS_PROCESSING_ERROR );
        Status status = new Status( code, "couldn't load referenced policy" );
        return new PolicyFinderResult( status );
    }

    @Override
    public boolean isIdReferenceSupported() {
        return true;
    }

    @Override
    public boolean isRequestSupported() {
        return true;
    }

    public void loadPolicies() {
        policies.clear();
        loadPolicy( finder );
    }

    /**
     * Private helper that tries to load the given file-based policy, and returns
     * null if any error occurs.
     */

    private AbstractPolicy loadPolicy( PolicyFinder finder ) {
        AbstractPolicy policy = null;
        InputStream stream = null;

        try {
            // create a builder based on the factory & try to load the policy
            DocumentBuilder db = documentBuilderFactory.newDocumentBuilder();
            // convert UXACML policy to input stream
            stream = new ByteArrayInputStream( dataUsagePolicy.getBytes() );
            Document doc = db.parse( stream );

            // handle the policy, if it's a known type
            Element root = doc.getDocumentElement();
            String name = root.getLocalName();

            if( name.equals( "Policy" ) ) {
                policy = Policy.getInstance( root );
            } else if( name.equals( "PolicySet" ) ) {
                policy = PolicySet.getInstance( root, finder );
            }
        } catch( Exception e ) {
            log.warning( "fail to load UXACML policy : " + e.getLocalizedMessage() );
        } finally {
            if( stream != null ) {
                try {
                    stream.close();
                } catch( IOException e ) {
                    log.warning( "error while closing input stream (UXACML policy)" );
                }
            }
        }

        if( policy != null ) {
            policies.put( policy.getId(), policy );
        }

        return policy;
    }
}
