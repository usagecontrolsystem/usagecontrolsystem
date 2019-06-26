package it.cnr.iit.ucs.pdptest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import it.cnr.iit.ucs.constants.STATUS;
import it.cnr.iit.ucs.exceptions.PolicyException;
import it.cnr.iit.ucs.exceptions.RequestException;
import it.cnr.iit.ucs.pdp.PDPEvaluation;
import it.cnr.iit.ucs.pdp.PolicyDecisionPoint;
import it.cnr.iit.ucs.properties.components.PdpProperties;
import it.cnr.iit.xacml.wrappers.PolicyWrapper;
import it.cnr.iit.xacml.wrappers.RequestWrapper;

@EnableConfigurationProperties
@TestPropertySource( properties = "application.properties" )
@ActiveProfiles( "test" )
@RunWith( SpringRunner.class )
@SpringBootTest
@SpringBootConfiguration
public class PDPTest {

    private static final Logger log = Logger.getLogger( PDPTest.class.getName() );

    @Value( "${request.deny}" )
    private String requestDeny;

    @Value( "${request.indeterminate}" )
    private String requestIndeterminate;

    @Value( "${request.permit}" )
    private String requestPermit;

    @Value( "${request.notapplicable}" )
    private String requestNotApplicable;

    @Value( "${policy}" )
    private String policy;

    @Value( "${policy.notapplicable}" )
    private String policyNotApplicable;

    @Value( "${policy.dup}" )
    private String policyDup;

    @Value( "${ucs.policy-decision-point.name}" )
    private String className;

    @Value( "${ucs.policy-decision-point.communication}" )
    private String communication;

    @Value( "${ucs.policy-decision-point.journal-dir}" )
    private String journalDir;

    private PolicyDecisionPoint policyDecisionpoint;

    @Before
    public void init() throws JsonParseException, JsonMappingException, IOException {

        PdpProperties pdpProperties = new PdpProperties() {

            @Override
            public String getName() {
                return className;
            }

            @Override
            public Map<String, String> getAdditionalProperties() {
                return null;
            }

            @Override
            public String getId() {
                return null;
            }

            @Override
            public String getJournalPath() {
                return journalDir;
            }

            @Override
            public String getJournalProtocol() {
                return "file";
            }

            @Override
            public Map<String, String> getJournalAdditionalProperties() {
                return null;
            }
        };

        policyDecisionpoint = new PolicyDecisionPoint( pdpProperties );
    }

    @Test
    public void testPDP() {
        PolicyWrapper policyWrapper = getPolicyWrapper( this.policy );
        assertNotNull( policyWrapper );
        assertThat( testEvaluation( requestDeny, policyWrapper ) ).contains( "deny" );
        assertThat( testEvaluation( requestIndeterminate, policyWrapper ) ).contains( "indeterminate" );
        assertThat( testEvaluation( requestPermit, policyWrapper ) ).contains( "permit" );
        PolicyWrapper policyWrapperNotApplicable = getPolicyWrapper( policyNotApplicable );
        assertNotNull( policyWrapperNotApplicable );
        assertThat( testEvaluation( requestNotApplicable, policyWrapperNotApplicable ) ).contains( "notapplicable" );
        assertThat( testEvaluation( requestDeny, policyWrapper, STATUS.TRY ) ).contains( "deny" );
        assertThat( testEvaluation( requestPermit, policyWrapper, STATUS.TRY ) ).contains( "permit" );
        assertThat( testEvaluation( requestIndeterminate, policyWrapper, STATUS.TRY ) ).contains( "indeterminate" );
        assertThat( testEvaluation( requestDeny, policyWrapper, STATUS.START ) ).contains( "deny" );
        assertThat( testEvaluation( requestPermit, policyWrapper, STATUS.START ) ).contains( "permit" );
        assertThat( testEvaluation( requestIndeterminate, policyWrapper, STATUS.START ) ).contains( "indeterminate" );
        assertThat( testEvaluation( requestDeny, policyWrapper, STATUS.END ) ).contains( "deny" );
        assertThat( testEvaluation( requestPermit, policyWrapper, STATUS.END ) ).contains( "permit" );
        assertThat( testEvaluation( requestIndeterminate, policyWrapper, STATUS.END ) ).contains( "indeterminate" );
        assertTrue( testEvaluation( requestIndeterminate, null, STATUS.END ) == null );
        assertTrue( testEvaluation( null, policyWrapper, STATUS.END ) == null );
        PolicyWrapper policyWrapperDup = getPolicyWrapper( policyDup );
        assertNotNull( policyWrapperDup );
        assertThat( testEvaluation( requestPermit, policyWrapperDup, STATUS.TRY ) ).contains( "permit" );
    }

    private String testEvaluation( String request, PolicyWrapper policy ) {
        RequestWrapper requestWrapper = null;
        try {
            requestWrapper = RequestWrapper.build( request, null );
        } catch( RequestException e ) {
            fail( "error parsing request" );

        }

        PDPEvaluation response = policyDecisionpoint.evaluate( requestWrapper, policy );
        if( response != null ) {
            String result = response.getResult().toLowerCase();
            log.info( result );
            return result;
        }
        return null;
    }

    private String testEvaluation( String request, PolicyWrapper policy, STATUS status ) {
        try {
            RequestWrapper requestWrapper = getRequestWrapper( request );
            assertNotNull( requestWrapper );
            PDPEvaluation response = policyDecisionpoint.evaluate( requestWrapper, policy, status );
            if( response != null ) {
                String result = response.getResult().toLowerCase();
                log.info( result );
                return result;
            } else {
                return null;
            }
        } catch( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }

    private RequestWrapper getRequestWrapper( String request ) {
        RequestWrapper requestWrapper = null;
        try {
            requestWrapper = RequestWrapper.build( request, null );
        } catch( RequestException e ) {}
        return requestWrapper;
    }

    private PolicyWrapper getPolicyWrapper( String policy ) {
        PolicyWrapper policyWrapper = null;
        try {
            policyWrapper = PolicyWrapper.build( policy );
        } catch( PolicyException e ) {}
        return policyWrapper;
    }

}
