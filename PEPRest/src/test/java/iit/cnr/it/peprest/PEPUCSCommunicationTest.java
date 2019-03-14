package iit.cnr.it.peprest;

import static iit.cnr.it.ucsinterface.node.NodeInterface.TRYACCESSRESPONSE_REST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doCallRealMethod;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletResponse;

import iit.cnr.it.peprest.messagetrack.MessageStorage;
import iit.cnr.it.peprest.messagetrack.Utility;
import iit.cnr.it.ucsinterface.message.Message;
import iit.cnr.it.ucsinterface.message.PURPOSE;
import iit.cnr.it.ucsinterface.message.tryaccess.TryAccessMessage;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

@WebMvcTest( value = PEPUCSCommunication.class, secure = false )
public class PEPUCSCommunicationTest extends PEPRestAbstractTest {

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void tryAccessResponseRequestTriggersReceiveResponse() throws Exception {
        pepRest.responses = new ConcurrentHashMap<>();
        pepRest.unanswered = new ConcurrentHashMap<>();
        pepRest.messageHistory = new MessageStorage();

        TryAccessMessage message = Utility.buildTryAccessMessage();
        pepRest.messageHistory.addMessage( message );
        Message tryAccessResponse = Utility.buildTryAccessResponse( message, DecisionType.DENY, "123456" );
        doCallRealMethod().when( pepRest ).receiveResponse( Matchers.any( Message.class ) );

        MockHttpServletResponse mvcResponse = postResponseToPEPRest( tryAccessResponse, TRYACCESSRESPONSE_REST );

        assertEquals( SC_OK, mvcResponse.getStatus() );
        assertNotNull( mvcResponse.getContentAsString() );
    }

    @Test
    public void tryAccessResponseRequestWithEmptyMessageResultsInBadRequestResponse() throws Exception {
        assertEquals( HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, postStringResponseToPEPRest( "", TRYACCESSRESPONSE_REST ).getStatus() );
    }

    // @Test
    public void onGoingEvaluationRequestWithPermit() throws Exception {
        pepRest.responses = new ConcurrentHashMap<>();
        pepRest.unanswered = new ConcurrentHashMap<>();
        doCallRealMethod().when( pepRest ).receiveResponse( Matchers.any( Message.class ) );

        Message message = new Message( "source", "destination", SESSION_ID_01 );
        message.setPurpose( PURPOSE.TRYACCESS );

        MockHttpServletResponse mvcResponse = postResponseToPEPRest( message, TRYACCESSRESPONSE_REST );

        assertEquals( SC_OK, mvcResponse.getStatus() );
        assertNotNull( mvcResponse.getContentAsString() );
    }
}
