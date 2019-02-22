package it.cnr.iit.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import iit.cnr.it.ucsinterface.contexthandler.STATUS;
import iit.cnr.it.ucsinterface.forwardingqueue.ForwardingQueueToCHInterface;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessMessage;
import iit.cnr.it.ucsinterface.message.startaccess.StartAccessMessage;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessage;
import iit.cnr.it.ucsinterface.message.reevaluation.ReevaluationMessage;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessageBuilder;
import iit.cnr.it.ucsinterface.obligationmanager.ObligationManagerInterface;
import iit.cnr.it.ucsinterface.pap.PAPInterface;
import iit.cnr.it.ucsinterface.pdp.PDPEvaluation;
import iit.cnr.it.ucsinterface.pdp.PDPInterface;
import iit.cnr.it.ucsinterface.pip.PIPCHInterface;
import iit.cnr.it.ucsinterface.pip.PIPRetrieval;
import iit.cnr.it.ucsinterface.requestmanager.RequestManagerToCHInterface;
import iit.cnr.it.ucsinterface.sessionmanager.SessionInterface;
import iit.cnr.it.ucsinterface.sessionmanager.SessionManagerInterface;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.Configuration;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLPip;
import it.cnr.iit.usagecontrolframework.contexthandler.ContextHandlerLC;
import it.cnr.iit.usagecontrolframework.proxies.PIPBuilder;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPAP;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPDP;
import it.cnr.iit.usagecontrolframework.proxies.ProxySessionManager;
import it.cnr.iit.xacmlutilities.Attribute;
import it.cnr.iit.xacmlutilities.policy.utility.JAXBUtility;
import oasis.names.tc.xacml.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

public abstract class UCFAbstractTest {
    protected Logger log = (Logger) LoggerFactory.getLogger(UCFAbstractTest.class);

    /* mocked PipRetrieval */

	protected PIPRetrieval getMockedPipRetrieval() {
		PIPRetrieval pipRetrieval = Mockito.mock(PIPRetrieval.class);
		
		return pipRetrieval;
	}

    /* mocked SessionManager */
	
	protected SessionManagerInterface getMockedSessionManager(SessionInterface sessionInterface) {
		SessionManagerInterface sessionManagerInterface = Mockito.mock(SessionManagerInterface.class);
		Mockito.when(
				sessionManagerInterface.getSessionForId(
						Matchers.<String>any())
				).thenReturn(sessionInterface);
		// TODO add ongoing attributes
		Mockito.when(
				sessionManagerInterface.getOnGoingAttributes(
						Matchers.<String>any())
				).thenReturn(null);
		Mockito.when(
				sessionManagerInterface.deleteEntry(
						Matchers.<String>any())
				).thenReturn(true);
		Mockito.when(sessionManagerInterface.createEntry(
				Matchers.<String>any(),
				Matchers.<String>any(),
				Matchers.<String>any(),
				Matchers.<List<String>>any(),
				Matchers.<List<String>>any(),
				Matchers.<List<String>>any(),
				Matchers.<List<String>>any(),
				Matchers.<String>any(),
				Matchers.<String>any(),
				Matchers.<String>any(),
				Matchers.<String>any(),
				Matchers.<String>any(),
				Matchers.<String>any())).thenReturn(true);
		return sessionManagerInterface;
	}
	
    protected SessionManagerInterface getSessionManagerForStatus(String sessionId, String policy, String request, String status) {
		SessionInterface sessionInterface = 
				getMockedSessionInterface(policy, request, status); 
		return getMockedSessionManager(sessionInterface);
    }

	protected SessionInterface getMockedSessionInterface(String policy, String request, String status) {
		SessionInterface sessionInterface = Mockito.mock(SessionInterface.class);
		Mockito.when(
				sessionInterface.getPolicySet()
				).thenReturn(policy);
		Mockito.when(
				sessionInterface.getOriginalRequest()
				).thenReturn(request);
		Mockito.when(
				sessionInterface.getStatus()
				).thenReturn(status);
		Mockito.when(
				sessionInterface.getPEPUri()
				).thenReturn("localhost#1");
		
		return sessionInterface;
	}

    /* mocked RequestManager */

	protected RequestManagerToCHInterface getMockedRequestManagerToChInterface() {
		RequestManagerToCHInterface requestManagerToChInterface = Mockito.mock(RequestManagerToCHInterface.class);
		return requestManagerToChInterface;
	}

	protected ForwardingQueueToCHInterface getMockedForwardingQueueToCHInterface() {
		ForwardingQueueToCHInterface forwardingQueueToCHInterface = Mockito.mock(ForwardingQueueToCHInterface.class);
		return forwardingQueueToCHInterface;
	}

	/* Mocked ObligationManager */
	
	protected ObligationManagerInterface getMockedObligationManager() {
		ObligationManagerInterface obligationManager = (ObligationManagerInterface) Mockito.mock(ObligationManagerInterface.class);
		Mockito.when(
				obligationManager.translateObligations(
						Matchers.<PDPEvaluation>any(),
						Matchers.<String>any(),
						Matchers.<String>any())
			).thenReturn(null);
		return obligationManager;
	}

	/* Mocked PDP */
	
	protected PDPInterface getMockedPDP() {
		return getMockedPDP(getMockedPDPEvaluationPermit());
	}
	
	protected PDPInterface getMockedPDP(PDPEvaluation pdpEval) {
		PDPInterface pdp = (PDPInterface) Mockito.mock(PDPInterface.class);
		Mockito.when(
					pdp.evaluate(
						Matchers.<String>any(),
						Matchers.<StringBuilder>any(),
						Matchers.<STATUS>any())
				)
				.thenReturn(pdpEval);
		Mockito.when(
				pdp.evaluate(
					Matchers.<String>any(),
					Matchers.<String>any())
			)
			.thenReturn(pdpEval);
		assertNotNull(pdp);
		return pdp;
	}

	protected PDPEvaluation getMockedPDPEvaluationPermit() {
		return getMockedPDPEvaluation("Permit");
	}
	
	protected PDPEvaluation getMockedPDPEvaluationDeny() {
		return getMockedPDPEvaluation("Deny");
	}

	protected PDPEvaluation getMockedPDPEvaluationIndeterminate() {
		return getMockedPDPEvaluation("Indeterminate");
	}
	
	protected PDPEvaluation getMockedPDPEvaluationNotApplicable() {
		return getMockedPDPEvaluation("Not Applicable");
	}

	private PDPEvaluation getMockedPDPEvaluation(String message) {
		PDPEvaluation pdpEvaluation = Mockito.mock(PDPEvaluation.class);
		Mockito.when(
				pdpEvaluation.getResponse()
			)
			.thenReturn(message);
		assertNotNull(pdpEvaluation);
			return pdpEvaluation;
	}

	/* Mocked PAP */
	
	protected PAPInterface getMockedPAP() {
		PAPInterface pap = Mockito.mock(PAPInterface.class);
		return pap;
	}

	/* Mocked PIPs */
	
	protected PIPCHInterface getMockedPIPCHInterface(String name) {
		PIPCHInterface pip = Mockito.mock(PIPCHInterface.class);
		Mockito.when(
				pip.getAttributes()
				).thenReturn(new ArrayList<Attribute>());
		Mockito.when(
				pip.setContextHandlerInterface(
						Matchers.<ContextHandlerLC>any())
				).thenReturn(true);
		return pip;
	}
	
	protected void addPips(ContextHandlerLC contextHandler) {
		String[] pips = {"virus", "telephone", "position", "role", "telephone", "time"};
		for (String pipName : pips) {
			// TODO use the pip var to parametrize the pip somehow
			// maybe using a template xml to unmarshal a real instance.
			// for now I'll try to mock one
			contextHandler.addPip(getMockedPIPCHInterface(pipName));
		}
	}
	
	/* non mocked components */
	
	protected ContextHandlerLC getContextHandler(Configuration ucsConfiguration) {
		ContextHandlerLC contextHandler = new ContextHandlerLC(ucsConfiguration.getCh());
		return contextHandler;
	}
	
	protected void initContextHandler(ContextHandlerLC contextHandler) {
		contextHandler.setPdpInterface(getMockedPDP());
		contextHandler.setPapInterface(getMockedPAP());
		contextHandler.setRequestManagerToChInterface(getMockedRequestManagerToChInterface());
		contextHandler.setSessionManagerInterface(getSessionManagerForStatus("","", "", ContextHandlerLC.TRY_STATUS));
		contextHandler.setForwardingQueue(getMockedForwardingQueueToCHInterface());
		contextHandler.setObligationManager(getMockedObligationManager());
		contextHandler.setPIPRetrieval(getMockedPipRetrieval());
		addPips(contextHandler);
	}
	
    protected ContextHandlerLC getContextHandlerCorrectlyInitialized(Configuration ucsConfiguration, String policy, String request) {
		ContextHandlerLC contextHandler = getContextHandler(ucsConfiguration);
		initContextHandler(contextHandler);
		contextHandler.setSessionManagerInterface(
				getSessionManagerForStatus("",policy, request, ContextHandlerLC.TRY_STATUS));

		contextHandler.verify();
		assertTrue(contextHandler.startThread());
		
		return contextHandler;
    }
    
	protected SessionManagerInterface getSessionManager(Configuration ucsConfiguration) {
		SessionManagerInterface sessionManager = new ProxySessionManager(
			    ucsConfiguration.getSessionManager());
		assertTrue(sessionManager.isInitialized());
		return sessionManager;
	}

	protected PDPInterface getPDP(Configuration ucsConfiguration) {
		PDPInterface pdp = new ProxyPDP(ucsConfiguration.getPdp());
		assertNotNull(pdp);
		return pdp;
	}

	protected PAPInterface getPAP(Configuration ucsConfiguration) {
		PAPInterface pap = new ProxyPAP(ucsConfiguration.getPap());
		assertNotNull(pap);
		return pap;
	}

	protected ArrayList<PIPCHInterface> getPIPS(Configuration ucsConfiguration) {
		ArrayList<PIPCHInterface>  pips = new ArrayList<>();

		for (XMLPip xmlPIP : ucsConfiguration.getPipList()) {
			log.info("Loading pip");
			PIPCHInterface pip = PIPBuilder.build(xmlPIP);
			assertNotNull(pip);
			pips.add(pip);
		}
		
		return pips;
	}

	/* Messages functions */
	
	protected TryAccessMessage buildTryAccessMessage(String pepId, String ucsUri,
			String policy, String request) throws URISyntaxException, IOException {
		TryAccessMessageBuilder builder = new TryAccessMessageBuilder(pepId, ucsUri);
		builder.setPolicy(policy);
		builder.setRequest(request);
		
		TryAccessMessage message = builder.build();
			
		return message;
	}
	
	// TODO set source dest
	protected StartAccessMessage buildStartAccessMessage(String sessionId, String src, String dest) {
		StartAccessMessage message = new StartAccessMessage(src, dest);
		return message;
	}

	// TODO set source dest
	protected EndAccessMessage buildEndAccessMessage(String sessionId, String src, String dest) {
		EndAccessMessage message = new EndAccessMessage("","");
		return message;
	}
	
	// TODO set source dest
	protected ReevaluationMessage buildReevaluationMessage(String sessionId, String src, String dest) {
		ReevaluationMessage message = new ReevaluationMessage("","");
		return message;
	}

	/* Policy/Request functions */
	
	protected RequestType getRequestType(String fileName) throws JAXBException, URISyntaxException, IOException {
		return (RequestType) loadXMLFromFile(fileName, RequestType.class);
	}

	protected PolicyType getPolicyType(String fileName) throws JAXBException, URISyntaxException, IOException {
		return (PolicyType) loadXMLFromFile(fileName, PolicyType.class);
	}

	/* Utility functions */
	
	protected Configuration getUCSConfiguration(String ucsConfigFile) throws JAXBException, URISyntaxException, IOException {
		return (Configuration) loadXMLFromFile(ucsConfigFile, Configuration.class);
	}

	private Object loadXMLFromFile(String fileName, Class<?> className) throws JAXBException, URISyntaxException, IOException {
		String data = readResourceFileAsString(fileName);
		return JAXBUtility.unmarshalToObject(className, data);
	}

	protected String readResourceFileAsString(String resource) throws URISyntaxException, IOException {
		ClassLoader classLoader = this.getClass().getClassLoader();
		
		log.info("Loading resource file : " + resource);
		Path path = Paths.get(classLoader.getResource(resource).toURI());
		byte[] data = Files.readAllBytes(path);
		return new String(data);
	}
}
