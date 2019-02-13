package it.cnr.iit.test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import iit.cnr.it.ucsinterface.pap.PAPInterface;
import iit.cnr.it.ucsinterface.pdp.PDPInterface;
import iit.cnr.it.ucsinterface.pip.PIPCHInterface;
import iit.cnr.it.ucsinterface.sessionmanager.SessionManagerInterface;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.Configuration;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLPip;
import it.cnr.iit.usagecontrolframework.contexthandler.ContextHandlerLC;
import it.cnr.iit.usagecontrolframework.contexthandler.exceptions.MalformedObjectException;
import it.cnr.iit.usagecontrolframework.proxies.PIPBuilder;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPAP;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPDP;
import it.cnr.iit.usagecontrolframework.proxies.ProxySessionManager;
import it.cnr.iit.xacmlutilities.policy.utility.JAXBUtility;

@EnableConfigurationProperties
@TestPropertySource(properties = "application-test.properties")
@ActiveProfiles("test")
@SpringBootTest
@SpringBootConfiguration
class CoverageTest {
    private Logger log = (Logger) LoggerFactory.getLogger(CoverageTest.class);

	@Value("${ucsConfigFile}")
	private String ucsConfigFile;
	
	private Configuration ucsConfiguration;
	private ContextHandlerLC contextHandler;
	private SessionManagerInterface sessionManager;
	private PIPCHInterface pip;
	private PDPInterface pdp;
	private PAPInterface pap;
	private ArrayList<PIPCHInterface> pips;
	
	@PostConstruct
	private void init() {
		log.info("Init coverage tests");			
	}
	
	private String readResourceFileAsString(String resource) throws URISyntaxException, IOException {
		ClassLoader classLoader = this.getClass().getClassLoader(); 
		Path path = Paths.get(classLoader.getResource(resource).toURI());
		byte[] data = Files.readAllBytes(path);
		return new String(data);
	} 
	
	private void initUCSConfig() {
		loadUCSConfiguration();
		loadContextHandler();
		loadSessionManager();
		loadPIPS();
		
		PDPInterface pdp = new ProxyPDP(ucsConfiguration.getPdp());
		assertNotNull(pdp);

		PAPInterface pap = new ProxyPAP(ucsConfiguration.getPap());
		assertNotNull(pap);
	}
	
	private void loadUCSConfiguration() {
		try {
			String configXml = readResourceFileAsString(ucsConfigFile);
			ucsConfiguration = JAXBUtility
				    .unmarshalToObject(Configuration.class, configXml);
		} catch (Exception e) {
			fail("cannot initialize ucs configuration xml");
			e.printStackTrace();
		}
	}
	
	private void loadSessionManager() {
		sessionManager = new ProxySessionManager(
			    ucsConfiguration.getSessionManager());
			assertNotNull(sessionManager);
	}

	private void loadContextHandler() {
		try {
			contextHandler = new ContextHandlerLC(ucsConfiguration.getCh());
			log.info("Context handler status : " + contextHandler.isOk());
		} catch (MalformedObjectException e) {
			fail("context hanler error");
		}
	}
	
	private void loadPIPS() {
		pips = new ArrayList<>();
		for (XMLPip xmlPIP : ucsConfiguration.getPipList()) {
			PIPCHInterface pip = PIPBuilder.build(xmlPIP);
			assertNotNull(pip);
			pips.add(pip);
		}
	}

	@Test
	@DisplayName("Test of a test")
	void test() {
		initUCSConfig();
		fail("Not yet implemented");
	}

}
