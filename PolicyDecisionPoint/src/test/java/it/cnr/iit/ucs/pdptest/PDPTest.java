package it.cnr.iit.ucs.pdptest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
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
import it.cnr.iit.ucs.pdp.PDPEvaluation;
import it.cnr.iit.ucs.pdp.PolicyDecisionPoint;
import it.cnr.iit.ucs.properties.components.PdpProperties;
import it.cnr.iit.xacml.wrappers.PolicyWrapper;
import it.cnr.iit.xacml.wrappers.RequestWrapper;

@EnableConfigurationProperties
@TestPropertySource( properties = "application-test.properties" )
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
            public String getJournalDir() {
                return journalDir;
            }

            @Override
            public String getName() {
                return className;
            }
        };

        policyDecisionpoint = new PolicyDecisionPoint( pdpProperties );
    }

    @Test
    public void testPDP() {
        PolicyWrapper policy = PolicyWrapper.build( this.policy );
        assertThat( testEvaluation( requestDeny, policy ) ).contains( "deny" );
        assertThat( testEvaluation( requestIndeterminate, policy ) ).contains( "indeterminate" );
        assertThat( testEvaluation( requestPermit, policy ) ).contains( "permit" );
        assertThat( testEvaluation( requestNotApplicable, PolicyWrapper.build( policyNotApplicable ) ) ).contains( "notapplicable" );
        assertThat( testEvaluation( requestDeny, policy, STATUS.TRY ) ).contains( "deny" );
        assertThat( testEvaluation( requestPermit, policy, STATUS.TRY ) ).contains( "permit" );
        assertThat( testEvaluation( requestIndeterminate, policy, STATUS.TRY ) ).contains( "indeterminate" );
        assertThat( testEvaluation( requestDeny, policy, STATUS.START ) ).contains( "deny" );
        assertThat( testEvaluation( requestPermit, policy, STATUS.START ) ).contains( "permit" );
        assertThat( testEvaluation( requestIndeterminate, policy, STATUS.START ) ).contains( "indeterminate" );
        assertThat( testEvaluation( requestDeny, policy, STATUS.END ) ).contains( "deny" );
        assertThat( testEvaluation( requestPermit, policy, STATUS.END ) ).contains( "permit" );
        assertThat( testEvaluation( requestIndeterminate, policy, STATUS.END ) ).contains( "indeterminate" );
        assertTrue( testEvaluation( requestIndeterminate, null, STATUS.END ) == null );
        assertTrue( testEvaluation( null, policy, STATUS.END ) == null );
        PolicyWrapper policyHelperDup = PolicyWrapper.build( policyDup );
        assertThat( testEvaluation( requestPermit, policyHelperDup, STATUS.TRY ) ).contains( "permit" );
    }

    private String testEvaluation( String request, PolicyWrapper policy ) {
        RequestWrapper requestWrapper = RequestWrapper.build( request );
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
            RequestWrapper requestWrapper = RequestWrapper.build( request );
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

}
