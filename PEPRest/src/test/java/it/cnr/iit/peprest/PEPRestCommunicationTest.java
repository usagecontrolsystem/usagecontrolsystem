package it.cnr.iit.peprest;

import static it.cnr.iit.ucs.constants.RestOperation.FINISH;
import static it.cnr.iit.ucs.constants.RestOperation.FLOW_STATUS;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.HttpClientErrorException;

import it.cnr.iit.peprest.messagetrack.MessageStorage;

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
    public void finishRequestPerformsEndAccessSuccessfully() throws Exception {
        doCallRealMethod().when( pepRest ).end( SESSION_ID_01 );
        when( pepRest.endAccess( SESSION_ID_01 ) ).thenReturn( SESSION_ID_01 );

        MockHttpServletResponse mvcResponse = postStringResponseToPEPRest( SESSION_ID_01, FINISH.getOperationUri() );

        assertEquals( SC_OK, mvcResponse.getStatus() );
        assertNotNull( mvcResponse.getContentAsString() );
    }

    @Test
    public void finishRequestWithoutSessionIdResultsInBadRequestResponse() throws Exception {
        assertEquals( SC_BAD_REQUEST, postStringResponseToPEPRest( "", FINISH.getOperationUri() ).getStatus() );
    }

    @Test( expected = HttpClientErrorException.class )
    public void flowStatusRequestWithoutMessageIdResultsInBadRequestResponse() throws Throwable {
        try {
            postGetRequestToPEPRest( "", FLOW_STATUS.getOperationUri() );
            fail( "Should have thrown HttpClientErrorException: 204 NO_CONTENT" );
        } catch( Exception e ) {
            throw e.getCause();
        }
    }

    @Test( expected = IllegalArgumentException.class )
    public void flowStatusRequestWithWrongMessageIdResultsInBadRequestResponse() throws Throwable {
        when( pepRest.getMessageHistory() ).thenReturn( new MessageStorage() );
        try {
            postGetRequestToPEPRest( " ", FLOW_STATUS.getOperationUri() ).getStatus();
            fail( "Should have thrown IllegalArgumentException" );
        } catch( Exception e ) {
            throw e.getCause();
        }
    }
}
