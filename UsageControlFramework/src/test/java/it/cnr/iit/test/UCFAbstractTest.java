package it.cnr.iit.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import iit.cnr.it.ucsinterface.pap.PAPInterface;
import iit.cnr.it.ucsinterface.pdp.PDPInterface;
import iit.cnr.it.ucsinterface.pip.PIPCHInterface;
import iit.cnr.it.ucsinterface.sessionmanager.SessionManagerInterface;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.Configuration;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLPip;
import it.cnr.iit.usagecontrolframework.contexthandler.ContextHandlerLC;
import it.cnr.iit.usagecontrolframework.proxies.PIPBuilder;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPAP;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPDP;
import it.cnr.iit.usagecontrolframework.proxies.ProxySessionManager;
import it.cnr.iit.xacmlutilities.policy.utility.JAXBUtility;
import oasis.names.tc.xacml.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

public abstract class UCFAbstractTest {
    protected Logger log = (Logger) LoggerFactory.getLogger(UCFAbstractTest.class);
	
	protected SessionManagerInterface getSessionManager(Configuration ucsConfiguration) {
		SessionManagerInterface sessionManager = new ProxySessionManager(
			    ucsConfiguration.getSessionManager());
		assertTrue(sessionManager.isInitialized());
		return sessionManager;
	}

	protected ContextHandlerLC getContextHandler(Configuration ucsConfiguration) {
		ContextHandlerLC contextHandler = new ContextHandlerLC(ucsConfiguration.getCh());
		assertTrue(contextHandler.startThread());
		contextHandler.stopThread();
		return contextHandler;
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
	
	protected RequestType getRequestType(String fileName) throws JAXBException, URISyntaxException, IOException {
		return (RequestType) loadXMLFromFile(fileName, RequestType.class);
	}
	
	protected PolicyType getPolicyType(String fileName) throws JAXBException, URISyntaxException, IOException {
		return (PolicyType) loadXMLFromFile(fileName, PolicyType.class);
	}
	
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
