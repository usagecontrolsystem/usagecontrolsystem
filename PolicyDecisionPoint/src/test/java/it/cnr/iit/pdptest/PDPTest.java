package it.cnr.iit.pdptest;

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
import it.cnr.iit.ucs.properties.components.PdpProperties;
import it.cnr.iit.ucsinterface.pdp.PDPEvaluation;
import it.cnr.iit.usagecontrolframework.pdp.PolicyDecisionPoint;
import it.cnr.iit.xacmlutilities.wrappers.PolicyWrapper;

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

    @Value( "${ucf.policy-decision-point.class-name}" )
    private String className;

    @Value( "${ucf.policy-decision-point.communication-type}" )
    private String communication;

    @Value( "${ucf.policy-decision-point.journal-dir}" )
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
            public String getCommunicationType() {
                return communication;
            }

            @Override
            public String getClassName() {
                return className;
            }
        };

        policyDecisionpoint = new PolicyDecisionPoint( pdpProperties );
    }

    @Test
    public void testPDP() {
        log.info( "HELLO\n" + requestDeny + "\n" + policy );
        PolicyWrapper policyHelper = PolicyWrapper.buildPolicyWrapper( policy );
        assertThat( testEvaluation( requestDeny, policy ) ).contains( "deny" );
        assertThat( testEvaluation( requestIndeterminate, policy ) ).contains( "indeterminate" );
        assertThat( testEvaluation( requestPermit, policy ) ).contains( "permit" );
        assertThat( testEvaluation( requestNotApplicable, policyNotApplicable ) ).contains( "notapplicable" );
        assertThat( testEvaluation( requestNotApplicable, "" ) ).descriptionText().isEmpty();
        assertThat( testEvaluation( requestDeny, policyHelper, STATUS.TRYACCESS ) ).contains( "deny" );
        assertThat( testEvaluation( requestPermit, policyHelper, STATUS.TRYACCESS ) ).contains( "permit" );
        assertThat( testEvaluation( requestIndeterminate, policyHelper, STATUS.TRYACCESS ) ).contains( "indeterminate" );
        assertThat( testEvaluation( requestDeny, policyHelper, STATUS.STARTACCESS ) ).contains( "deny" );
        assertThat( testEvaluation( requestPermit, policyHelper, STATUS.STARTACCESS ) ).contains( "permit" );
        assertThat( testEvaluation( requestIndeterminate, policyHelper, STATUS.STARTACCESS ) ).contains( "indeterminate" );
        assertThat( testEvaluation( requestDeny, policyHelper, STATUS.ENDACCESS ) ).contains( "deny" );
        assertThat( testEvaluation( requestPermit, policyHelper, STATUS.ENDACCESS ) ).contains( "permit" );
        assertThat( testEvaluation( requestIndeterminate, policyHelper, STATUS.ENDACCESS ) ).contains( "indeterminate" );
        assertTrue( testEvaluation( requestIndeterminate, null, STATUS.ENDACCESS ) == null );
        assertTrue( testEvaluation( null, policyHelper, STATUS.ENDACCESS ) == null );
        PolicyWrapper policyHelperDup = PolicyWrapper.buildPolicyWrapper( policyDup );
        assertThat( testEvaluation( requestPermit, policyHelperDup, STATUS.TRYACCESS ) ).contains( "permit" );
    }

    private String testEvaluation( String request, String policy ) {
        PDPEvaluation response = policyDecisionpoint.evaluate( request, policy );
        if( response != null ) {
            String result = response.getResult().toLowerCase();
            log.info( result );
            return result;
        }
        return null;
    }

    private String testEvaluation( String request, PolicyWrapper policyHelper, STATUS status ) {
        try {
            PDPEvaluation response = policyDecisionpoint.evaluate( request, policyHelper, status );
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
