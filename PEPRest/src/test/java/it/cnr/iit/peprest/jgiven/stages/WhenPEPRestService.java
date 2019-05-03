package it.cnr.iit.peprest.jgiven.stages;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Optional;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeStage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;

import it.cnr.iit.peprest.PEPRest;
import it.cnr.iit.peprest.messagetrack.CallerResponse;
import it.cnr.iit.peprest.messagetrack.MessageStorage;
import it.cnr.iit.peprest.messagetrack.STATUS;
import it.cnr.iit.ucs.constants.RestOperation;
import it.cnr.iit.ucsinterface.message.Message;

@JGivenStage
public class WhenPEPRestService extends Stage<WhenPEPRestService> {

    @Autowired
    PEPRest pepRest;

    @ProvidedScenarioState
    String messageId;

    @ProvidedScenarioState
    Exception expectedException;

    @ExpectedScenarioState
    String sessionId;

    @ExpectedScenarioState
    Message message;

    @BeforeStage
    public void init() {
        MessageStorage messageStorage = Mockito.mock( MessageStorage.class );
        pepRest.setMessageStorage( messageStorage );
    }

    public WhenPEPRestService() {}

    public WhenPEPRestService PEPRest_service_$_is_executed( RestOperation restOperation ) {
        assertNotNull( pepRest );
        performOperation( restOperation );
        return self();
    }

    public WhenPEPRestService PEPRest_service_$_execution_fails( RestOperation restOperation ) {
        assertNotNull( pepRest );
        try {
            performOperation( restOperation );
        } catch( Exception e ) {
            expectedException = e;
        }
        return self();
    }

    private void performOperation( RestOperation restOperation ) {
        switch( restOperation ) {
            case TRY_ACCESS:
                messageId = pepRest.tryAccess();
                break;
            case START_ACCESS:
                assertNotNull( sessionId );
                messageId = pepRest.startAccess( sessionId );
                break;
            case END_ACCESS:
                assertNotNull( sessionId );
                messageId = pepRest.endAccess( sessionId );
                break;
            case ON_GOING_RESPONSE:
                assertNotNull( message );
                message = pepRest.onGoingEvaluation( message );
                messageId = message.getID();
                break;
            default:
                fail( "Unknown restOperation" );
                break;
        }
    }

    public WhenPEPRestService PEPRest_service_receive_response_is_executed( RestOperation operation ) {
        assertNotNull( message );
        CallerResponse callerResponse = new CallerResponse();
        callerResponse.setSessionId( sessionId );
        switch( operation ) {
            case TRY_ACCESS_RESPONSE:
                Mockito.when( pepRest.getMessageHistory().addMessage( message ) ).thenReturn( true );
                callerResponse.setStatus( STATUS.TRYACCESS_PERMIT );
                Mockito.when( pepRest.getMessageHistory().getMessageStatus( messageId ) ).thenReturn( Optional.of( callerResponse ) );
                break;
            case START_ACCESS_RESPONSE:
                Mockito.when( pepRest.getMessageHistory().addMessage( message ) ).thenReturn( true );
                callerResponse.setStatus( STATUS.STARTACCESS_PERMIT );
                Mockito.when( pepRest.getMessageHistory().getMessageStatus( messageId ) ).thenReturn( Optional.of( callerResponse ) );
                break;
            case END_ACCESS_RESPONSE:
                Mockito.when( pepRest.getMessageHistory().addMessage( message ) ).thenReturn( true );
                callerResponse.setStatus( STATUS.ENDACCESS_PERMIT );
                Mockito.when( pepRest.getMessageHistory().getMessageStatus( messageId ) ).thenReturn( Optional.of( callerResponse ) );

            default:
                break;
        }
        pepRest.receiveResponse( message );
        return self();
    }

}
