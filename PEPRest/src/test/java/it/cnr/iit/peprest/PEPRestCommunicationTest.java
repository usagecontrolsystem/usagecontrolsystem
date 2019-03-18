package it.cnr.iit.peprest;

import static it.cnr.iit.peprest.PEPRestOperation.FINISH;
import static it.cnr.iit.peprest.PEPRestOperation.SEND_SYNCHRONOUS;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import it.cnr.iit.peprest.PEPRestCommunication;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessResponse;

@RunWith( SpringJUnit4ClassRunner.class )
@WebAppConfiguration
@WebMvcTest( value = PEPRestCommunication.class, secure = false )
public class PEPRestCommunicationTest extends PEPRestAbstractTest {

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void sendRequestForTryAccessWithDenyResponsePerformsRun() throws Exception {
        Mockito.when( proxyRequestManager.sendMessageToCH( any() ) ).thenReturn( new Message() );

        doCallRealMethod().when( pepRest ).run();

        when( pepRest.tryAccess() ).thenReturn( SESSION_ID_01 );

        TryAccessResponse tryAccessResponse = buildTryAccessResponseDeny();
        when( pepRest.waitForResponse( SESSION_ID_01 ) ).thenReturn( tryAccessResponse );

        when( pepRest.startAccess( anyString() ) ).thenReturn( SESSION_ID_01 );

        MockHttpServletResponse mvcResponse = postStringResponseToPEPRest( "", SEND_SYNCHRONOUS.getOperationUri() );

        assertEquals( SC_OK, mvcResponse.getStatus() );
    }

    @Test
    public void finishRequestPerformsEndAccessSuccessfully() throws Exception {
        doCallRealMethod().when( pepRest ).end( SESSION_ID_01 );
        when( pepRest.endAccess( SESSION_ID_01 ) ).thenReturn( SESSION_ID_01 );
        when( pepRest.waitForResponse( SESSION_ID_01 ) ).thenReturn( new EndAccessResponse( SESSION_ID_01 ) );

        MockHttpServletResponse mvcResponse = postStringResponseToPEPRest( SESSION_ID_01, FINISH.getOperationUri() );

        assertEquals( SC_OK, mvcResponse.getStatus() );
        assertNotNull( mvcResponse.getContentAsString() );
    }

    @Test
    public void finishRequestWithoutSessionIdResultsInBadRequestResponse() throws Exception {
        assertEquals( SC_BAD_REQUEST, postStringResponseToPEPRest( "", FINISH.getOperationUri() ).getStatus() );
    }
}
