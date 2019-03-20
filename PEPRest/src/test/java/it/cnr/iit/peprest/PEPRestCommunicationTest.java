package it.cnr.iit.peprest;

import static it.cnr.iit.peprest.PEPRestOperation.FINISH;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
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
}
