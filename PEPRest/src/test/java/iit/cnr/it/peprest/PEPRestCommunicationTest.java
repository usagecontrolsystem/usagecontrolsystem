package iit.cnr.it.peprest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.endaccess.EndAccessResponse;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessResponse;

@WebMvcTest(value = PEPRestCommunication.class, secure = false)
public class PEPRestCommunicationTest extends PEPRestAbstractTest {
	
    @Override
    @Before
    public void setUp() {
       super.setUp();
    }

	@Test
	public void performSendWithDenyResponse() throws Exception {
    	Mockito.when(proxyRequestManager.sendMessageToCH(any())).thenReturn(new Message());    	

		doCallRealMethod().when(pepRest).run();

		when(pepRest.tryAccess()).thenReturn(SESSION_ID_01);
		
		TryAccessResponse tryAccessResponse = buildTryAccessResponseDeny();		
		when(pepRest.waitForResponse(SESSION_ID_01)).thenReturn(tryAccessResponse);		
		
		when(pepRest.startAccess(anyString())).thenReturn(SESSION_ID_01);

	    String uri = "/send";
	    MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)).andReturn();
	   
	    int status = mvcResult.getResponse().getStatus();
	    assertEquals(200, status);
	    String content = mvcResult.getResponse().getContentAsString();
	    assertNotNull(content);
	}

	@Test
	public void finish() throws Exception {
		doCallRealMethod().when(pepRest).end(SESSION_ID_01);
		when(pepRest.endAccess(SESSION_ID_01)).thenReturn(SESSION_ID_01);
		when(pepRest.waitForResponse(SESSION_ID_01)).thenReturn(new EndAccessResponse(SESSION_ID_01));

	    String uri = "/finish";
	    MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
			   .contentType(MediaType.TEXT_PLAIN_VALUE).content(SESSION_ID_01)).andReturn();
	   
	    int status = mvcResult.getResponse().getStatus();
	    assertEquals(200, status);
	    String content = mvcResult.getResponse().getContentAsString();
	    assertNotNull(content);
	}

}
