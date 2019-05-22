package it.cnr.iit.peprest.jgiven.stages;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static it.cnr.iit.ucs.constants.RestOperation.TRY_ACCESS_RESPONSE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;

import it.cnr.iit.peprest.PEPRest;
import it.cnr.iit.peprest.messagetrack.CallerResponse;
import it.cnr.iit.peprest.messagetrack.STATUS;
import it.cnr.iit.ucs.constants.RestOperation;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessage;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

@JGivenStage
public class ThenMessage extends Stage<ThenMessage> {

    @Autowired
    PEPRest pepRest;

    @ExpectedScenarioState
    String messageId;

    @ExpectedScenarioState
    String messageBody;

    @ExpectedScenarioState
    WireMock wireMockContextHandler;

    @ExpectedScenarioState
    Message message;

    @ExpectedScenarioState
    String sessionId;

    @ExpectedScenarioState
    Exception expectedException;

    @ExpectedScenarioState
    List<String> messageIds;

    public ThenMessage the_$_message_is_put_in_the_unanswered_queue( RestOperation restOperation ) {
        assertNotNull( pepRest.getUnanswered() );
        assertTrue( pepRest.getUnanswered().size() > 0 );
        Message messageType = pepRest.getUnanswered().entrySet().stream().findFirst().get().getValue();
        switch( restOperation ) {
            case TRY_ACCESS:
                assertTrue( messageType instanceof TryAccessMessage );
                break;
            case START_ACCESS:
                assertTrue( messageType instanceof StartAccessMessage );
                break;
            case END_ACCESS:
                assertTrue( messageType instanceof EndAccessMessage );
                break;
            default:
                fail( "Unknown message type in unanswered map" );
                break;
        }
        return self();
    }

    public ThenMessage the_message_id_in_the_unanswered_queue_matches_the_one_sent() {
        assertNotNull( pepRest.getUnanswered() );
        assertNotNull( pepRest.getUnanswered().get( messageId ) );
        return self();
    }

    public ThenMessage the_asynch_post_request_for_$_was_received_by_context_handler( @Quoted String operation ) {
        wireMockContextHandler.verifyThat( postRequestedFor( urlEqualTo( operation ) )
            .withHeader( "Content-Type", equalTo( "application/json" ) ) );
        return self();
    }

    public ThenMessage the_asynch_post_request_for_$_is_NOT_received_by_context_handler( @Quoted String operation ) {
        wireMockContextHandler.verifyThat( exactly( 0 ), postRequestedFor( urlEqualTo( operation ) )
            .withHeader( "Content-Type", equalTo( "application/json" ) ) );
        return self();
    }

    public ThenMessage the_Message_is_not_placed_into_the_unanswered_queue() {
        assertNotNull( pepRest.getUnanswered() );
        assertTrue( pepRest.getUnanswered().size() < 1 );
        return self();
    }

    public ThenMessage an_illegal_access_exception_is_thrown() {
        assertNotNull( expectedException );
        assertTrue( expectedException.getMessage().contains( "IllegalAccessException" ) );
        return self();
    }

    public ThenMessage the_Message_motivation_is_NOT_OK() {
        assertNotNull( message );
        assertNull( message.getMotivation() );
        return self();
    }

    public ThenMessage the_Message_motivation_is_OK() {
        assertNotNull( message );
        try {
            assertEquals( "OK", new ObjectMapper().readValue( message.getMotivation(), String.class ) );
        } catch( Exception e ) {
            fail( e.getLocalizedMessage() );
        }
        return self();
    }

    public ThenMessage the_message_is_put_in_the_responses_queue() {
        assertNotNull( pepRest.getResponses() );
        assertTrue( pepRest.getResponses().size() > 0 );
        return self();
    }

    public ThenMessage the_session_id_is_not_null( RestOperation restOperation ) {
        if( restOperation == TRY_ACCESS_RESPONSE ) {
            assertNotNull( pepRest.getSessionIdInTryAccess( message.getMessageId() ).get() );
            assertTrue( pepRest.getSessionIdInTryAccess( messageId ).get().length() > 0 );
        }
        return self();
    }

    public void the_evaluation_result_decision_is_$( DecisionType decision ) {
        assertNotNull( pepRest.getEvaluationResult( messageId ).get() );
        assertTrue( pepRest.getEvaluationResult( messageId ).get().length() > 0 );
        assertTrue( pepRest.getEvaluationResult( messageId ).get().equals( decision.value() ) );
    }

    public ThenMessage a_message_id_is_returned() {
        assertNotNull( messageId );
        return self();
    }

    public ThenMessage a_message_body_is_returned() {
        assertNotNull( messageBody );
        return self();
    }

    public ThenMessage the_message_body_contains_$_decision( @Quoted DecisionType decisionType ) {
        assertNotNull( messageBody );
        assertEquals( messageBody, decisionType.value() );
        return self();
    }

    public ThenMessage the_message_is_in_$_status( STATUS messageSendStatus ) {
        try {
            assertNotNull( messageId );
            CallerResponse callerResponse = pepRest.getMessageHistory().getMessageStatus( messageId ).get();
            assertNotNull( callerResponse.getStatus() );
            assertEquals( messageSendStatus, callerResponse.getStatus() );
        } catch( Exception e ) {
            fail( e.getMessage() );
        }
        return self();
    }

    @SuppressWarnings( "unchecked" )
    public ThenMessage the_message_body_has_the_$_sent_message_Ids( int numberOfMessages ) {
        try {
            assertNotNull( messageBody );
            messageIds = mapFromJson( messageBody, List.class );
            assertNotNull( messageIds );
            assertTrue( messageIds.size() == numberOfMessages );
        } catch( Exception e ) {
            fail( e.getMessage() );
        }
        return self();
    }

    protected <T> T mapFromJson( String json, Class<T> clazz )
            throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue( json, clazz );
    }

    public ThenMessage a_$_message_is_sent_to_context_handler( RestOperation restOperation ) {
        assertTrue( pepRest.getUnanswered().size() > 0 );
        switch( restOperation ) {
            case TRY_ACCESS:
                Message messageType = pepRest.getUnanswered().get( messageId );
                assertTrue( messageType instanceof TryAccessMessage );
                break;
            case START_ACCESS:
                messageIds = pepRest.getMessagesPerSession().getMessagesPerSession( sessionId );
                messageType = pepRest.getUnanswered().get( messageIds.get( 1 ) );
                assertTrue( messageType instanceof StartAccessMessage );
                break;
            case END_ACCESS:
                messageIds = pepRest.getMessagesPerSession().getMessagesPerSession( sessionId );
                messageType = pepRest.getUnanswered().get( messageIds.get( 3 ) );
                assertTrue( messageType instanceof EndAccessMessage );
                break;
            default:
                fail( "Unknown message type in unanswered map" );
                break;
        }
        return self();
    }

    public ThenMessage a_$_message_is_NOT_sent_to_context_handler( RestOperation restOperation ) {
        switch( restOperation ) {
            case START_ACCESS:
                try {
                    pepRest.getMessagesPerSession().getMessagesPerSession( sessionId );
                } catch( Exception e ) {
                    expectedException = e;
                }
                break;
            case END_ACCESS:
//                messageIds = pepRest.getMessagesPerSession().getMessagesPerSession( sessionId );
//                messageType = pepRest.getUnanswered().get( messageIds.get( 3 ) );
//                assertTrue( messageType instanceof EndAccessMessage );
                break;
            default:
                fail( "Unknown message type in unanswered map" );
                break;
        }
        return self();
    }
}
