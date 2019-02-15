package it.cnr.iit.test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

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

import iit.cnr.it.ucsinterface.pap.PAPInterface;
import iit.cnr.it.ucsinterface.pdp.PDPInterface;
import iit.cnr.it.ucsinterface.pip.PIPCHInterface;
import iit.cnr.it.ucsinterface.sessionmanager.SessionManagerInterface;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.Configuration;
import it.cnr.iit.usagecontrolframework.contexthandler.ContextHandlerLC;
import it.cnr.iit.usagecontrolframework.contexthandler.exceptions.MalformedObjectException;


@EnableConfigurationProperties
@TestPropertySource(properties = "application-test.properties")
@ActiveProfiles("test")
@SpringBootTest
@SpringBootConfiguration
@RunWith(SpringRunner.class)
public class UCFCoverageTest extends UCFAbstractTest {

	@Value("${ucs-config-file}")
	private String ucsConfigFile;

	@Value("${policy-file}")
	private String policyFile;

	@Value("${request-file}")
	private String requestFile;
	
	private Configuration ucsConfiguration;
	
	@PostConstruct
	private void setUp() throws JAXBException, URISyntaxException, IOException {
		log.info("Init coverage tests");
		ucsConfiguration  = getUCSConfiguration(ucsConfigFile);
		
	}
	
	@Test
	public void validateSessionManagerConfiguration() {
		log.info("======================================");
		SessionManagerInterface sessionManagerInterface = getSessionManager(ucsConfiguration);
	}

	@Test
	public void validatePIPSConfiguration() {
		log.info("======================================");
		ArrayList<PIPCHInterface> pipArray = getPIPS(ucsConfiguration);
	}

	@Test
	public void validateContextHandlerConfiguration() {
		log.info("======================================");
		ContextHandlerLC contextHandler = getContextHandler(ucsConfiguration);
	}
	
	@Test
	public void validatePAPConfiguration() {
		log.info("======================================");
		PAPInterface pap = getPAP(ucsConfiguration);
	}

	@Test
	public void validatePDPConfiguration() {
		log.info("======================================");
		PDPInterface pdp = getPDP(ucsConfiguration);
	}

}
