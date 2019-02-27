package it.cnr.iit.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import iit.cnr.it.ucsinterface.message.endaccess.EndAccessMessage;
import iit.cnr.it.ucsinterface.message.reevaluation.ReevaluationMessage;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessMessage;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessage;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.Configuration;
import it.cnr.iit.usagecontrolframework.contexthandler.ContextHandlerLC;
import it.cnr.iit.usagecontrolframework.contexthandler.exceptions.RevokeException;
import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

@EnableConfigurationProperties
@TestPropertySource(properties = "application-test.properties")
@SpringBootConfiguration
@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
public class ContextHandlerTests extends UCFAbstractTest {

	@Value("${ucs-config-file}")
	private String ucsConfigFile;

	@Value("${policy-file}")
	private String policyFile;

	@Value("${request-file}")
	private String requestFile;

	@Value("${pep-id}")
	private String pepId;

	@Value("${session-id}")
	private String sessionId;

	@Value("${ucs-uri}")
	private String ucsUri;

	@PostConstruct
	private void setUp() throws URISyntaxException, IOException {
		log.info("Init tests");
		policy = readResourceFileAsString(policyFile);
		request = readResourceFileAsString(requestFile);
	}

	private String policy;
	private String request;

	@Test
	public void contextHandlerConfigurationShouldFail() throws JAXBException, URISyntaxException, IOException {
		Configuration ucsConfiguration = getUCSConfiguration(ucsConfigFile);

		/* incorrect CH initialisation */
		ContextHandlerLC contextHandler = getContextHandler(ucsConfiguration);
		contextHandler.verify();
		assertFalse(contextHandler.startThread());
	}

	@Test
	public void contextHandlerTryAccessShouldFail() throws JAXBException, URISyntaxException, IOException {
		Configuration ucsConfiguration = getUCSConfiguration(ucsConfigFile);

		/* CH initialisation with deny */
		ContextHandlerLC contextHandler = getContextHandler(ucsConfiguration);
		initContextHandler(contextHandler);
		// set the pdp response to return deny
		contextHandler.setPdpInterface(getMockedPDP(getMockedPDPEvaluation(DecisionType.DENY)));
		contextHandler.verify();
		assertTrue(contextHandler.startThread());

		/* tryAccess */
		contextHandler.tryAccess(null);
	}

	@Test(expected = RevokeException.class)
	public void contextHandlerStartAccessShouldFail() throws JAXBException, URISyntaxException, IOException, Exception {
		Configuration ucsConfiguration = getUCSConfiguration(ucsConfigFile);

		/* CH initialisation */
		ContextHandlerLC contextHandler = getContextHandlerCorrectlyInitialized(ucsConfiguration, policy, request);

		/* tryAccess */
		TryAccessMessage tryAccessMessage = buildTryAccessMessage(pepId, ucsUri, policy, request);
		contextHandler.tryAccess(tryAccessMessage);

		/* startAccess */
		contextHandler.setSessionManagerInterface(
				getSessionManagerForStatus(sessionId, policy, request, ContextHandlerLC.TRY_STATUS));
		// this line makes the start access to take the deny path
		contextHandler.setPdpInterface(getMockedPDP(getMockedPDPEvaluation(DecisionType.DENY)));
		StartAccessMessage startAccessMessage = buildStartAccessMessage(sessionId, "", "");
		contextHandler.startAccess(startAccessMessage);
	}

	@Test
	public void contextHandlerEndAccessShouldFail() throws JAXBException, URISyntaxException, IOException, Exception {
		Configuration ucsConfiguration = getUCSConfiguration(ucsConfigFile);

		/* CH initialisation */
		ContextHandlerLC contextHandler = getContextHandlerCorrectlyInitialized(ucsConfiguration, policy, request);

		/* tryAccess */
		TryAccessMessage tryAccessMessage = buildTryAccessMessage(pepId, ucsUri, policy, request);
		contextHandler.tryAccess(tryAccessMessage);

		/* startAccess */
		contextHandler.setSessionManagerInterface(
				getSessionManagerForStatus(sessionId, policy, request, ContextHandlerLC.TRY_STATUS));
		StartAccessMessage startAccessMessage = buildStartAccessMessage(sessionId, "", "");
		contextHandler.startAccess(startAccessMessage);

		/* endAccess */
		contextHandler.setSessionManagerInterface(
				getSessionManagerForStatus(sessionId, policy, request, ContextHandlerLC.START_STATUS));
		contextHandler.setPdpInterface(getMockedPDP(getMockedPDPEvaluation(DecisionType.DENY)));
		EndAccessMessage endAccessMessage = buildEndAccessMessage(sessionId, "", "");
		contextHandler.endAccess(endAccessMessage);
	}

	@Test
	public void contextHandlerFullFlow() throws JAXBException, URISyntaxException, IOException, Exception {
		Configuration ucsConfiguration = getUCSConfiguration(ucsConfigFile);

		/* CH initialisation */
		ContextHandlerLC contextHandler = getContextHandlerCorrectlyInitialized(ucsConfiguration, policy, request);

		/* tryAccess */
		TryAccessMessage tryAccessMessage = buildTryAccessMessage(pepId, ucsUri, policy, request);
		contextHandler.tryAccess(tryAccessMessage);

		/* startAccess */
		contextHandler.setSessionManagerInterface(
				getSessionManagerForStatus(sessionId, policy, request, ContextHandlerLC.TRY_STATUS));
		StartAccessMessage startAccessMessage = buildStartAccessMessage(sessionId, "", "");
		contextHandler.startAccess(startAccessMessage);

		/* reevaluate */
		ReevaluationMessage reevaluationMessage = buildReevaluationMessage(sessionId, "", "");
		reevaluationMessage.setSession(getMockedSessionInterface(policy, request, ContextHandlerLC.START_STATUS));
		contextHandler.reevaluate(reevaluationMessage);

		/* endAccess */
		contextHandler.setSessionManagerInterface(
				getSessionManagerForStatus(sessionId, policy, request, ContextHandlerLC.START_STATUS));
		EndAccessMessage endAccessMessage = buildEndAccessMessage(sessionId, "", "");
		contextHandler.endAccess(endAccessMessage);
	}

}
