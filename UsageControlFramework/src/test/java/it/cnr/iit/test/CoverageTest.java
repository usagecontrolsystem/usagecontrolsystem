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
public class CoverageTest extends BaseTests {

	@Value("${ucs-config-file}")
	private String ucsConfigFile;

	@Value("${policy-file}")
	private String policyFile;

	@Value("${request-file}")
	private String requestFile;
	
	@PostConstruct
	private void init() {
		log.info("Init coverage tests");			
	}
	
	@Test
	public void testInitConfiguration() throws JAXBException, URISyntaxException, IOException, MalformedObjectException {
		Configuration ucsConfiguration  = getUCSConfiguration(ucsConfigFile);
		SessionManagerInterface sessionManagerInterface = getSessionManager(ucsConfiguration);
		ArrayList<PIPCHInterface> pipArray = getPIPS(ucsConfiguration);
		PAPInterface pap = getPAP(ucsConfiguration);
		PDPInterface pdp = getPDP(ucsConfiguration);
		ContextHandlerLC contextHandler = getContextHandler(ucsConfiguration);
		//usageControlFramework = new UsageControlFramework();
	}

}
