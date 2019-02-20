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

import iit.cnr.it.ucsinterface.contexthandler.ContextHandlerInterface;
import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessMessage;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessage;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessageBuilder;
import iit.cnr.it.ucsinterface.sessionmanager.SessionInterface;
import iit.cnr.it.ucsinterface.sessionmanager.SessionManagerInterface;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.Configuration;
import it.cnr.iit.usagecontrolframework.contexthandler.ContextHandlerLC;

@EnableConfigurationProperties
@TestPropertySource(properties = "application-test.properties")
@ActiveProfiles("test")
@SpringBootTest
@SpringBootConfiguration
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

	@Value("${ucs-uri}")
	private String ucsUri;
	
	@PostConstruct
	private void setUp() {
		log.info("Init coverage tests");
	}

	@Test
	public void validateContextHandlerConfigurationShouldFail() throws JAXBException, URISyntaxException, IOException {
		Configuration ucsConfiguration = getUCSConfiguration(ucsConfigFile);
		ContextHandlerLC contextHandler = getContextHandler(ucsConfiguration);

		contextHandler.verify();
		assertFalse(contextHandler.startThread());
	}
	
	@Test
	public void validateContextHandlerTryAccessShouldFail() throws JAXBException, URISyntaxException, IOException {
		Configuration ucsConfiguration = getUCSConfiguration(ucsConfigFile);
		ContextHandlerLC contextHandler = getContextHandler(ucsConfiguration);
		initContextHandler(contextHandler);
		// set Deny pdp response
		contextHandler.setPdpInterface(getMockedPDP(getMockedPDPEvaluationDeny()));
		
		contextHandler.verify();
		assertTrue(contextHandler.startThread());

		contextHandler.tryAccess(null);
	}
	
	@Test
	public void validateContextHandlerTryAccess() throws JAXBException, URISyntaxException, IOException, Exception {
		String sessionId ="session-111-222-333";
		String policy = readResourceFileAsString(policyFile);
		String request = readResourceFileAsString(requestFile);
		SessionInterface sessionInterface = 
				getMockedSessionInterface(policy, request,  ContextHandlerInterface.TRY_STATUS); 
		SessionManagerInterface sessionManagerInterface = 
				getMockedSessionManager(sessionInterface);
		
		/* CH initialization */
		Configuration ucsConfiguration = getUCSConfiguration(ucsConfigFile);
		ContextHandlerLC contextHandler = getContextHandler(ucsConfiguration);
		initContextHandler(contextHandler);
		contextHandler.setSessionManagerInterface(sessionManagerInterface);

		contextHandler.verify();
		assertTrue(contextHandler.startThread());
		
		/* tryAccess */
		TryAccessMessage tryAccessMessage = buildTryAccessMessage(pepId, ucsUri, policy, request);
		contextHandler.tryAccess(tryAccessMessage);
		String id = tryAccessMessage.getID();
		log.info(String.format("TRYACCESS { id=" + id + " }"));

		/* startAccess */
		StartAccessMessage startAccessMessage = buildStartAccessMessage(sessionId);
		sessionInterface = 
				getMockedSessionInterface(policy, request,  ContextHandlerInterface.TRY_STATUS); 
		sessionManagerInterface = 
				getMockedSessionManager(sessionInterface);
		// set the session interface to return the policy and correct status
		contextHandler.setSessionManagerInterface(sessionManagerInterface);

		contextHandler.startAccess(startAccessMessage);
	}
	
}
