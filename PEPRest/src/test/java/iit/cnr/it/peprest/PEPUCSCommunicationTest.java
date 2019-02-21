package iit.cnr.it.peprest;

import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doCallRealMethod;

import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletResponse;

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
	public void tryAccessResponseRequestTriggersReceiveResponse() throws Exception {
		pepRest.responses = new ConcurrentHashMap<>();
		pepRest.mutex= new Object();
		doCallRealMethod().when(pepRest).receiveResponse(Mockito.any(Message.class));

		Message message = new Message("source", "destination", SESSION_ID_01);
		message.setPurpose(PURPOSE.TRYACCESS);
		String jsonMessage = new Gson().toJson(message);

	    MockHttpServletResponse mvcResponse = postResponseToPEPRest(jsonMessage, TRY_ACCESS_RESPONSE_URI);

	    assertEquals(SC_OK, mvcResponse.getStatus());
	    assertNotNull(mvcResponse.getContentAsString());
	}

	@Test
	public void tryAccessResponseRequestWithEmptyMessageResultsInBadRequestResponse() throws Exception {
	     assertEquals(SC_BAD_REQUEST, postResponseToPEPRest("", TRY_ACCESS_RESPONSE_URI).getStatus());
	}

	//@Test
	public void onGoingEvaluationRequestWithPermit() throws Exception {
		pepRest.responses = new ConcurrentHashMap<>();
		pepRest.mutex= new Object();
		doCallRealMethod().when(pepRest).receiveResponse(Mockito.any(Message.class));

		Message message = new Message("source", "destination", SESSION_ID_01);
		message.setPurpose(PURPOSE.TRYACCESS);
		String jsonMessage = new Gson().toJson(message);

	    MockHttpServletResponse mvcResponse = postResponseToPEPRest(jsonMessage, TRY_ACCESS_RESPONSE_URI);

	    assertEquals(SC_OK, mvcResponse.getStatus());
	    assertNotNull(mvcResponse.getContentAsString());
	}
}
