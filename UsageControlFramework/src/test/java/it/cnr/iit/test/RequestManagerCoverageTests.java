package it.cnr.iit.test;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.Configuration;
import it.cnr.iit.usagecontrolframework.requestmanager.RequestManagerLC;

@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringRunner.class)
public class RequestManagerCoverageTests extends UCFAbstractTest {
	private Configuration ucsConfiguration;

	
	@PostConstruct
	private void init() throws JAXBException, URISyntaxException, IOException {
		LOGGER.info("Init tests");
		ucsConfiguration = getUCSConfiguration(conf.getUcsConfigFile());
	}

	@Before
	public void setUp() {
		LOGGER.info("setUp >>>>>>>>>>>>>>>>>>");
		// nothing to do for now
	}
	
	@Test
	public void requestManagerTest() throws JAXBException, URISyntaxException, IOException {
		RequestManagerLC requestManager = getRequestManager(ucsConfiguration);
		
		requestManager.sendMessageToCH(buildPipChMessage(conf.getSessionId(), "", ""));
	}
	
}
