package it.cnr.iit.peprest.messagetrack;

import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucs.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucs.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucs.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucs.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessResponse;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;

public class MessageStorageTest {
    @Mock
    private MessageStorageInterface messageStorageMock;

    private MessageStorage storage = new MessageStorage();

    private String sessionId = "sid:01234567890987654321";

    private TryAccessMessage tryAccessMessage;
    private TryAccessResponse tryAccessResponsePermit;
    private TryAccessResponse tryAccessResponseDeny;
    private StartAccessMessage startAccessMessage;
    private StartAccessMessage startAccessMessageDeny;
    private StartAccessResponse startAccessResponsePermit;
    private StartAccessResponse startAccessResponseDeny;
    private EndAccessMessage endAccessMessage;
    private EndAccessMessage endAccessMessageDeny;
    private EndAccessResponse endAccessResponsePermit;
    private EndAccessResponse endAccessResponseDeny;
    private ReevaluationResponse reevaluationResponseDenyMessage;
    private ReevaluationResponse reevaluationResponsePermitMessage;

    @Before
    public void init() {
        tryAccessMessage = Utility.buildTryAccessMessage();
        tryAccessResponsePermit = Utility.buildTryAccessResponse( tryAccessMessage, DecisionType.PERMIT, sessionId );
        assertTrue( tryAccessMessage.getMessageId().equals( tryAccessResponsePermit.getMessageId() ) );
        tryAccessResponseDeny = Utility.buildTryAccessResponse( tryAccessMessage, DecisionType.DENY, sessionId );
        startAccessMessage = Utility.buildStartAccessMessage( sessionId );
        startAccessResponsePermit = Utility.buildStartAccessResponse( startAccessMessage, DecisionType.PERMIT,
            sessionId );
        startAccessResponseDeny = Utility.buildStartAccessResponse( startAccessMessage, DecisionType.DENY, sessionId );
        endAccessMessage = Utility.buildEndAccessMessage( sessionId );
        endAccessResponsePermit = Utility.buildEndAccessResponse( endAccessMessage, DecisionType.PERMIT, sessionId );
        endAccessResponseDeny = Utility.buildEndAccessResponse( endAccessMessage, DecisionType.DENY, sessionId );
        reevaluationResponseDenyMessage = Utility.buildReevaluationResponse( sessionId, DecisionType.DENY );
        reevaluationResponsePermitMessage = Utility.buildReevaluationResponse( sessionId, DecisionType.PERMIT );
    }

    private void prepareMockedMessageStorage() {
        messageStorageMock = Mockito.mock( MessageStorageInterface.class );
        Mockito.when( messageStorageMock.addMessage( new Message() ) ).thenReturn( true );
        Mockito.when( messageStorageMock.addMessage( null ) ).thenReturn( false );
        Mockito.when( messageStorageMock.getMessageStatus( "" ) ).thenReturn( Optional.of( new CallerResponse() ) );
        Mockito.when( messageStorageMock.getMessageStatus( null ) ).thenReturn( Optional.empty() );
    }

    @Test
    public void testMessageMockStorage() {
        prepareMockedMessageStorage();
        assertTrue( messageStorageMock.getMessageStatus( "" ).isPresent() );
        assertTrue( !messageStorageMock.getMessageStatus( null ).isPresent() );
    }

    @Test
    public void testMessageStorage() {
        assertTrue( storage.addMessage( tryAccessMessage ) );
        assertTrue( storage.getMessageStatus( tryAccessMessage.getMessageId() ).isPresent() );
        assertTrue( storage.getMessageStatus( tryAccessMessage.getMessageId() ).get().getStatus() == STATUS.TRYACCESS_SENT );
        assertTrue( storage.addMessage( tryAccessResponsePermit ) );
        assertTrue( storage.getMessageStatus( tryAccessMessage.getMessageId() ).isPresent() );
        assertTrue( storage.getMessageStatus( tryAccessMessage.getMessageId() ).get().getStatus() == STATUS.TRYACCESS_PERMIT );
        assertTrue( storage.addMessage( startAccessMessage ) );
        assertTrue( storage.getMessageStatus( startAccessMessage.getMessageId() ).isPresent() );
        assertTrue( storage.getMessageStatus( startAccessMessage.getMessageId() ).get().getStatus() == STATUS.STARTACCESS_SENT );
        assertTrue( storage.getMessageStatus( tryAccessMessage.getMessageId() ).get().getStatus() == STATUS.STARTACCESS_SENT );
        assertTrue( storage.addMessage( startAccessResponsePermit ) );
        assertTrue( storage.getMessageStatus( startAccessMessage.getMessageId() ).isPresent() );
        assertTrue( storage.getMessageStatus( startAccessMessage.getMessageId() ).get().getStatus() == STATUS.STARTACCESS_PERMIT );
        assertTrue( storage.getMessageStatus( tryAccessMessage.getMessageId() ).get().getStatus() == STATUS.STARTACCESS_PERMIT );
        assertTrue( storage.addMessage( reevaluationResponseDenyMessage ) );
        assertTrue( storage.getMessageStatus( reevaluationResponseDenyMessage.getMessageId() ).isPresent() );
        assertTrue( storage.getMessageStatus( reevaluationResponseDenyMessage.getMessageId() ).get().getStatus() == STATUS.REVOKED );
        assertTrue( storage.getMessageStatus( tryAccessMessage.getMessageId() ).get().getStatus() == STATUS.REVOKED );
        assertTrue( storage.getMessageStatus( tryAccessMessage.getMessageId() ).get().getStatus() == STATUS.REVOKED );
        assertTrue( storage.addMessage( reevaluationResponsePermitMessage ) );
        assertTrue( storage.getMessageStatus( reevaluationResponsePermitMessage.getMessageId() ).isPresent() );
        assertTrue( storage.getMessageStatus( reevaluationResponsePermitMessage.getMessageId() ).get().getStatus() == STATUS.SESSION_RESUMED );
        assertTrue( storage.getMessageStatus( tryAccessMessage.getMessageId() ).get().getStatus() == STATUS.SESSION_RESUMED );
        assertTrue( storage.addMessage( endAccessMessage ) );
        assertTrue( storage.getMessageStatus( endAccessMessage.getMessageId() ).isPresent() );
        assertTrue( storage.getMessageStatus( endAccessMessage.getMessageId() ).get().getStatus() == STATUS.ENDACCESS_SENT );
        assertTrue( storage.getMessageStatus( tryAccessMessage.getMessageId() ).get().getStatus() == STATUS.ENDACCESS_SENT );
        assertTrue( storage.addMessage( endAccessResponsePermit ) );
        assertTrue( storage.getMessageStatus( endAccessMessage.getMessageId() ).isPresent() );
        assertTrue( storage.getMessageStatus( endAccessMessage.getMessageId() ).get().getStatus() == STATUS.ENDACCESS_PERMIT );
        assertTrue( storage.getMessageStatus( tryAccessMessage.getMessageId() ).get().getStatus() == STATUS.ENDACCESS_PERMIT );
        assertTrue( storage.getMessagesPerSession( sessionId ).get( 0 ).equals( tryAccessMessage.getMessageId() ) );
        assertTrue( storage.getMessagesPerSession( sessionId ).get( 1 ).equals( startAccessMessage.getMessageId() ) );
    }

}
