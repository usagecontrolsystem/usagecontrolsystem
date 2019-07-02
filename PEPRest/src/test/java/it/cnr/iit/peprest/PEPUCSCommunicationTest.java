package it.cnr.iit.peprest;

import static it.cnr.iit.ucs.constants.OperationName.TRYACCESSRESPONSE_REST;
import static oasis.names.tc.xacml.core.schema.wd_17.DecisionType.PERMIT;
import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import it.cnr.iit.peprest.PEPUCSController;
import it.cnr.iit.ucs.message.Message;

@RunWith( SpringJUnit4ClassRunner.class )
@WebAppConfiguration
@WebMvcTest( value = PEPUCSController.class, secure = false )
public class PEPUCSCommunicationTest extends PEPRestAbstractTest {

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void tryAccessResponseRequestTriggersReceiveResponse() throws Exception {
        Message tryAccessResponse = buildTryAccessResponseDeny();

        when( pepRest.receiveResponse( tryAccessResponse ) ).thenReturn( PERMIT.value() );

        MockHttpServletResponse mvcResponse = postResponseToPEPRest( tryAccessResponse, TRYACCESSRESPONSE_REST );

        assertEquals( SC_OK, mvcResponse.getStatus() );
        assertNotNull( mvcResponse.getContentAsString() );
    }

    @Test
    public void tryAccessResponseRequestWithEmptyMessageResultsInBadRequestResponse() throws Exception {
        assertEquals( HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE, postStringResponseToPEPRest( "", TRYACCESSRESPONSE_REST ).getStatus() );
    }
}
