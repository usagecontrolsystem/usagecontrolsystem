package iit.cnr.it.peprest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doCallRealMethod;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.google.gson.Gson;

import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.PURPOSE;

@WebMvcTest(value = PEPUCSCommunication.class, secure = false)
public class PEPUCSCommunicationTest extends PEPRestAbstractTest {

    @Override
    @Before
    public void setUp() {
       super.setUp();
    }

	@Test
	public void tryAccessResponseWithDeny() throws Exception {
		pepRest.responses = new ConcurrentHashMap<>();
		pepRest.mutex= new Object();
		doCallRealMethod().when(pepRest).receiveResponse(Mockito.any(Message.class));
		
		Message message = new Message("source", "destination", SESSION_ID_01);
		message.setPurpose(PURPOSE.TRYACCESS);
		String jsonMessage = new Gson().toJson(message);

	    String uri = "/tryAccessResponse";
	    MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
			   .contentType(MediaType.TEXT_PLAIN_VALUE).content(jsonMessage)).andReturn();
	   
	    int status = mvcResult.getResponse().getStatus();
	    assertEquals(200, status);
	    String content = mvcResult.getResponse().getContentAsString();
	    assertNotNull(content);
	}

}
