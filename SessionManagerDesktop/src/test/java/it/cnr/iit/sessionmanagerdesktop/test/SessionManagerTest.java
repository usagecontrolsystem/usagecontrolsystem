package it.cnr.iit.sessionmanagerdesktop.test;

import java.sql.Connection;
import java.sql.Statement;

import javax.xml.bind.JAXBException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import iit.cnr.it.ucsinterface.sessionmanager.SessionInterface;
import it.cnr.iit.sessionmanagerdesktop.SessionManagerDesktop;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLSessionManager;
import it.cnr.iit.xacmlutilities.policy.utility.JAXBUtility;

@EnableConfigurationProperties
@TestPropertySource(properties = "application-test.properties")
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest
@SpringBootConfiguration
public class SessionManagerTest {

	@Value("${conf}")
	private String conf;
	
	@Value("${session.sessionid}")
	private String sessionId;
	
	@Value("${session.policy}")
	private String policy;
	
	@Value("${session.request}")
	private String request;
	
	@Value("${session.status.try}")
	private String tryStatus;
	
	@Value("${session.status.start}")
	private String startStatus;
	
	@Value("${session.status.revoke}")
	private String revokeStatus;
	
	@Value("${session.status.end}")
	private String endStatus;
	
	@Value("${session.pepuri}")
	private String pepuri;
	
	@Value("${session.myip}")
	private String myip;
	
	@Value("${session.subject}")
	private String subject;
	
	@Value("${session.resource}")
	private String resource;
	
	@Value("${session.action}")
	private String action;
	
	

	@InjectMocks 
	private SessionManagerDesktop sessionManagerDesktop;
	@Mock
	private Connection mockConnection;
	@Mock
	private Statement mockStatement;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		
	}
	
	public SessionManagerTest() {
		/*System.out.println(conf);
		try {
			//sessionManagerDesktop = new SessionManagerDesktop(JAXBUtility.unmarshalToObject(XMLSessionManager.class, conf));
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	@Test
	public void testMockDBConnection() throws Exception {
		System.out.println(conf);
		try {
			XMLSessionManager xml = JAXBUtility.unmarshalToObject(XMLSessionManager.class, conf);
			System.out.println(xml.getDriver());
			sessionManagerDesktop = new SessionManagerDesktop(xml);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		sessionManagerDesktop.start();
		Mockito.when(mockConnection.createStatement()).thenReturn(mockStatement);
		Mockito.when(mockConnection.createStatement().executeUpdate(Mockito.any())).thenReturn(1);
		boolean status = sessionManagerDesktop.createEntry(sessionId, policy, request, null, null, null, null, tryStatus, pepuri, myip, subject, action, resource);
		Assert.assertEquals(status, true);
		Mockito.verify(mockConnection.createStatement(), Mockito.times(1));
		SessionInterface sessionInterface = sessionManagerDesktop.getSessionForId(sessionId);
		System.out.println(sessionInterface.toString());
	}

}
