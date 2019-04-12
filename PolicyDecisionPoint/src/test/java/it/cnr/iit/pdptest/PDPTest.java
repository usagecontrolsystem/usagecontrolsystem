package it.cnr.iit.pdptest;

import static org.assertj.core.api.Assertions.assertThat;

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
import com.fasterxml.jackson.databind.ObjectMapper;

import it.cnr.iit.ucs.configuration.PdpProperties;
import it.cnr.iit.ucs.constants.STATUS;
import it.cnr.iit.ucsinterface.pdp.PDPEvaluation;
import it.cnr.iit.usagecontrolframework.pdp.PolicyDecisionPoint;

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

    @Value( "${pdp.conf}" )
    private String configuration;

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

    private PolicyDecisionPoint policyDecisionpoint;

    @Before
    public void init() throws JsonParseException, JsonMappingException, IOException {
        policyDecisionpoint = new PolicyDecisionPoint( new ObjectMapper().readValue( configuration, PdpProperties.class ) );
    }

    @Test
    public void testPDP() {
        log.info( "HELLO\n" + requestDeny + "\n" + policy );
        assertThat( testEvaluation( requestDeny, policy ) ).contains( "deny" );
        assertThat( testEvaluation( requestIndeterminate, policy ) ).contains( "indeterminate" );
        assertThat( testEvaluation( requestPermit, policy ) ).contains( "permit" );
        assertThat( testEvaluation( requestNotApplicable, policyNotApplicable ) ).contains( "notapplicable" );
        assertThat( testEvaluation( requestNotApplicable, "" ) ).descriptionText().isEmpty();
        assertThat( testEvaluation( requestDeny, policy, STATUS.TRYACCESS ) ).contains( "deny" );
        assertThat( testEvaluation( requestPermit, policy, STATUS.TRYACCESS ) ).contains( "permit" );
        assertThat( testEvaluation( requestIndeterminate, policy, STATUS.TRYACCESS ) ).contains( "indeterminate" );
        assertThat( testEvaluation( requestDeny, policy, STATUS.STARTACCESS ) ).contains( "deny" );
        assertThat( testEvaluation( requestPermit, policy, STATUS.STARTACCESS ) ).contains( "permit" );
        assertThat( testEvaluation( requestIndeterminate, policy, STATUS.STARTACCESS ) ).contains( "indeterminate" );
        assertThat( testEvaluation( requestDeny, policy, STATUS.ENDACCESS ) ).contains( "deny" );
        assertThat( testEvaluation( requestPermit, policy, STATUS.ENDACCESS ) ).contains( "permit" );
        assertThat( testEvaluation( requestIndeterminate, policy, STATUS.ENDACCESS ) ).contains( "indeterminate" );
        assertThat( testEvaluation( requestIndeterminate, null, STATUS.ENDACCESS ) == null );
        assertThat( testEvaluation( null, policy, STATUS.ENDACCESS ) == null );
        assertThat( testEvaluation( requestPermit, policyDup, STATUS.TRYACCESS ) ).contains( "permit" );
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

    private String testEvaluation( String request, String policy, STATUS status ) {
        try {
            PDPEvaluation response = policyDecisionpoint.evaluate( request, new StringBuilder( policy ), status );
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
