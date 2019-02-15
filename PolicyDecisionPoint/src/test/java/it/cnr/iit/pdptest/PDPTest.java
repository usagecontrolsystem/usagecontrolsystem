package it.cnr.iit.pdptest;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import iit.cnr.it.ucsinterface.contexthandler.STATUS;
import iit.cnr.it.ucsinterface.pdp.PDPEvaluation;
import it.cnr.iit.usagecontrolframework.pdp.PolicyDecisionPoint;

@EnableConfigurationProperties
@TestPropertySource(properties = "application-test.properties")
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@SpringBootConfiguration
public class PDPTest {
	
	private PolicyDecisionPoint policyDecisionpoint = new PolicyDecisionPoint(null);
	
	@Value("${request.deny}")
	private String requestDeny;
	
	@Value("${request.indeterminate}")
	private String requestIndeterminate;
	
	@Value("${request.permit}")
	private String requestPermit;
	
	@Value("${request.notapplicable}")
	private String requestNotApplicable;
	
	@Value("${policy}")
	private String policy;
	
	@Value("${policy.notapplicable}")
	private String policyNotApplicable;
	
	
	@Test
	public void testPDP() {
		System.out.println("HELLO\n" + requestDeny + "\n" + policy);
		assertThat(testEvaluation(requestDeny, policy)).contains("deny");
		assertThat(testEvaluation(requestIndeterminate, policy)).contains("indeterminate");
		assertThat(testEvaluation(requestPermit, policy)).contains("permit");
		assertThat(testEvaluation(requestNotApplicable, policyNotApplicable)).contains("indeterminate");
		assertThat(testEvaluation(requestNotApplicable, "")).descriptionText().isEmpty();
		assertThat(testEvaluation(requestDeny, policy, STATUS.TRYACCESS)).contains("deny");
		assertThat(testEvaluation(requestPermit, policy, STATUS.TRYACCESS)).contains("permit");
		assertThat(testEvaluation(requestIndeterminate, policy, STATUS.TRYACCESS)).contains("indeterminate");
		assertThat(testEvaluation(requestDeny, policy, STATUS.STARTACCESS)).contains("deny");
		assertThat(testEvaluation(requestPermit, policy, STATUS.STARTACCESS)).contains("permit");
		assertThat(testEvaluation(requestIndeterminate, policy, STATUS.STARTACCESS)).contains("indeterminate");
		assertThat(testEvaluation(requestDeny, policy, STATUS.ENDACCESS)).contains("deny");
		assertThat(testEvaluation(requestPermit, policy, STATUS.ENDACCESS)).contains("permit");
		assertThat(testEvaluation(requestIndeterminate, policy, STATUS.ENDACCESS)).contains("indeterminate");
	}
	
	private String testEvaluation(String request, String policy) {
		PDPEvaluation response = policyDecisionpoint.evaluate(request, policy);
		if(response != null) {
		String result = response.getResponse().toLowerCase();
		System.out.println(result);
		return result;
		}
		return null;
	}
	
	private String testEvaluation(String request, String policy, STATUS status) {
		PDPEvaluation response = policyDecisionpoint.evaluate(request, new StringBuilder(policy), status);
		if(response != null) {
		String result = response.getResponse().toLowerCase();
		System.out.println(result);
		return result;
		}
		else {
			return null;
		}
	}
	
	
}
