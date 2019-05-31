package it.cnr.iit.ucsrest.coverage;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;

import it.cnr.iit.ucs.constants.OperationNames;
import it.cnr.iit.ucs.constants.STATUS;
import it.cnr.iit.ucs.contexthandler.ContextHandlerConstants;
import it.cnr.iit.ucs.contexthandler.ContextHandlerInterface;
import it.cnr.iit.ucs.message.Message;
import it.cnr.iit.ucs.message.attributechange.AttributeChangeMessage;
import it.cnr.iit.ucs.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucs.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucs.message.reevaluation.ReevaluationMessage;
import it.cnr.iit.ucs.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucs.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucs.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucs.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucs.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucs.obligationmanager.ObligationManagerInterface;
import it.cnr.iit.ucs.pap.PAPInterface;
import it.cnr.iit.ucs.pdp.PDPEvaluation;
import it.cnr.iit.ucs.pdp.PDPInterface;
import it.cnr.iit.ucs.pep.PEPInterface;
import it.cnr.iit.ucs.pip.PIPCHInterface;
import it.cnr.iit.ucs.properties.UCSProperties;
import it.cnr.iit.ucs.properties.components.PipProperties;
import it.cnr.iit.ucs.requestmanager.RequestManagerToCHInterface;
import it.cnr.iit.ucs.sessionmanager.SessionInterface;
import it.cnr.iit.ucs.sessionmanager.SessionManagerInterface;
import it.cnr.iit.ucsrest.contexthandler.ContextHandlerLC;
import it.cnr.iit.ucsrest.coverage.properties.TestProperties;
import it.cnr.iit.ucsrest.proxies.ProxyPAP;
import it.cnr.iit.ucsrest.proxies.ProxyPDP;
import it.cnr.iit.ucsrest.proxies.ProxySessionManager;
import it.cnr.iit.ucsrest.requestmanager.RequestManagerLC;
import it.cnr.iit.ucsrest.rest.UCSRest;
import it.cnr.iit.utility.JAXBUtility;
import it.cnr.iit.utility.FileUtility;
import it.cnr.iit.xacmlutilities.Attribute;
import it.cnr.iit.xacmlutilities.Category;
import it.cnr.iit.xacmlutilities.DataType;
import it.cnr.iit.xacmlutilities.wrappers.PolicyWrapper;
import it.cnr.iit.xacmlutilities.wrappers.RequestWrapper;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;
import oasis.names.tc.xacml.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

public class UCSRestBaseTests {
    protected Logger log = Logger.getLogger( UCSRestBaseTests.class.getName() );

    @Autowired
    TestProperties testProperties;

    /* Request Manager functions */

    protected RequestManagerLC getRequestManager( UCSProperties prop ) {
        RequestManagerLC requestManager = new RequestManagerLC( prop.getRequestManager() );
        return requestManager;
    }

    /* Context Handler functions */

    protected ContextHandlerLC getContextHandler( UCSProperties prop ) {
        ContextHandlerLC contextHandler = new ContextHandlerLC( prop.getContextHandler() );
        return contextHandler;
    }

    protected void initContextHandler( ContextHandlerLC contextHandler ) {
        contextHandler.setPdp( getMockedPDP( getMockedPDPEvaluation( DecisionType.PERMIT ) ) );
        contextHandler.setPap( getMockedPAP( null ) );
        contextHandler.setRequestManager( getMockedRequestManagerToChInterface() );
        contextHandler.setSessionManager( getSessionManagerForStatus( "", "", "", ContextHandlerConstants.TRY_STATUS ) );
        contextHandler.setObligationManager( getMockedObligationManager() );
    }

    protected ContextHandlerLC getContextHandlerCorrectlyInitialized( UCSProperties prop,
            String policy,
            String request ) throws Exception {
        ContextHandlerLC contextHandler = getContextHandler( prop );
        initContextHandler( contextHandler );
        contextHandler.setSessionManager(
            getSessionManagerForStatus( "a", policy, request, ContextHandlerConstants.TRY_STATUS ) );

        contextHandler.verify();
        /* must be called after initialisation */
        addMockedPips( prop, contextHandler );
        contextHandler.startMonitoringThread();

        return contextHandler;
    }

    /* mocked SessionManager */

    protected SessionManagerInterface getMockedSessionManager( SessionInterface sessionInterface ) {
        SessionManagerInterface sessionManagerInterface = Mockito.mock( SessionManagerInterface.class );
        Mockito.when( sessionManagerInterface.getSessionForId( ArgumentMatchers.anyString() ) )
            .thenReturn( Optional.ofNullable( sessionInterface ) );
        // TODO add ongoing attributes
        Mockito.when( sessionManagerInterface.getOnGoingAttributes( ArgumentMatchers.anyString() ) ).thenReturn( null );
        Mockito.when( sessionManagerInterface.deleteEntry( ArgumentMatchers.anyString() ) ).thenReturn( true );
        Mockito.when( sessionManagerInterface.createEntry( ArgumentMatchers.any() ) ).thenReturn( true );

        List<SessionInterface> sessionInterfaceList = new ArrayList<>( Arrays.asList( new SessionInterface[] { sessionInterface } ) );
        Mockito.when( sessionManagerInterface.getSessionsForSubjectAttributes(
            ArgumentMatchers.anyString(), ArgumentMatchers.anyString() ) ).thenReturn( sessionInterfaceList );
        Mockito.when( sessionManagerInterface.getSessionsForEnvironmentAttributes(
            ArgumentMatchers.anyString() ) ).thenReturn( sessionInterfaceList );

        return sessionManagerInterface;
    }

    protected SessionManagerInterface getSessionManagerForStatus( String sessionId, String policy, String request,
            String status ) {
        SessionInterface sessionInterface = getMockedSessionInterface( policy, request, status );
        return getMockedSessionManager( sessionInterface );
    }

    protected SessionInterface getMockedSessionInterface( String policy, String request, String status ) {
        SessionInterface sessionInterface = Mockito.mock( SessionInterface.class );
        Mockito.when( sessionInterface.getPolicySet() ).thenReturn( policy );
        Mockito.when( sessionInterface.getOriginalRequest() ).thenReturn( request );
        Mockito.when( sessionInterface.getStatus() ).thenReturn( status );
        Mockito.when( sessionInterface.getPEPUri() ).thenReturn( "localhost" + ContextHandlerLC.PEP_ID_SEPARATOR + "1" );

        Mockito.when( sessionInterface.isStatus( ArgumentMatchers.anyString() ) ).thenAnswer(
            new Answer<Boolean>() {
                @Override
                public Boolean answer( final InvocationOnMock invocation ) throws Throwable {
                    return invocation.getArguments()[0].equals( status );
                }
            } );

        return sessionInterface;
    }

    /* Mocked ContextHandlerInterface */

    protected ContextHandlerInterface getMockedContextHandlerInterface() {
        ContextHandlerInterface contextHandler = Mockito
            .mock( ContextHandlerInterface.class );

        return contextHandler;
    }

    /* Mocked RequestManager */

    protected RequestManagerToCHInterface getMockedRequestManagerToChInterface() {
        RequestManagerToCHInterface requestManagerToChInterface = Mockito.mock( RequestManagerToCHInterface.class );
        return requestManagerToChInterface;
    }

    protected OperationNames getMockedNodeInterface() {
        OperationNames nodeInterface = Mockito
            .mock( OperationNames.class );

        return nodeInterface;
    }

    protected HashMap<String, PEPInterface> getMockedPEPMap( String key, String response ) {
        HashMap<String, PEPInterface> map = new HashMap<>();
        map.put( key, getMockedPEPInterface( response ) );
        return map;
    }

    /* Mocked PEPInterface */

    protected PEPInterface getMockedPEPInterface( String response ) {
        PEPInterface pep = Mockito.mock( PEPInterface.class );
        Mockito.when( pep.receiveResponse( ArgumentMatchers.<Message>any() ) ).thenReturn( response );
        return pep;
    }

    /* Mocked ObligationManager */

    protected ObligationManagerInterface getMockedObligationManager() {
        ObligationManagerInterface obligationManager = Mockito
            .mock( ObligationManagerInterface.class );
        Mockito.when( obligationManager.translateObligations( ArgumentMatchers.<PDPEvaluation>any(), ArgumentMatchers.anyString() ) )
            .thenReturn( null );
        return obligationManager;
    }

    /* Mocked PDP */

    protected PDPInterface getMockedPDP( PDPEvaluation pdpEval ) {
        PDPInterface pdp = Mockito.mock( PDPInterface.class );
        Mockito
            .when( pdp.evaluate( ArgumentMatchers.<RequestWrapper>any(), ArgumentMatchers.<PolicyWrapper>any(),
                ArgumentMatchers.<STATUS>any() ) )
            .thenReturn( pdpEval );
        Mockito.when( pdp.evaluate( ArgumentMatchers.<RequestWrapper>any(), ArgumentMatchers.<PolicyWrapper>any() ) ).thenReturn( pdpEval );
        assertNotNull( pdp );
        return pdp;
    }

    protected PDPEvaluation getMockedPDPEvaluation( DecisionType decisionType ) {
        PDPEvaluation pdpEvaluation = Mockito.mock( PDPEvaluation.class );
        Mockito.when( pdpEvaluation.getResult() ).thenReturn( decisionType.value() );
        return pdpEvaluation;
    }

    /* Mocked PAP */

    protected PAPInterface getMockedPAP( String policy ) {
        PAPInterface pap = Mockito.mock( PAPInterface.class );
        Mockito.when( pap.retrievePolicy( ArgumentMatchers.anyString() ) ).thenReturn( policy );
        return pap;
    }

    /* Mocked PIPs */

    protected PIPCHInterface getMockedPIPCHInterface( String attrId, Category category, DataType dataType, String attrReturn ) {
        PIPCHInterface pip = Mockito.mock( PIPCHInterface.class );

        Attribute attr = getNewAttribute( attrId, category, dataType, attrReturn );
        List<Attribute> attributeList = Arrays.asList( new Attribute[] { attr } );
        List<String> attributeIdList = Arrays.asList( new String[] { attrId } );

        Mockito.when( pip.getAttributes() ).thenReturn( attributeList );
        Mockito.when( pip.getAttributeIds() ).thenReturn( attributeIdList );

        return pip;
    }

    protected void addMockedPips( UCSProperties prop, ContextHandlerLC contextHandler ) {
        // TODO FIX THIS HACK
        String[] pips = { "virus", "telephone", "position", "role", "telephone", "time" };
        String[] pipVal = { "0", "0", "Pisa", "IIT", "0", "12:00" };
        Category[] pipCat = { Category.ENVIRONMENT, Category.ENVIRONMENT, Category.SUBJECT, Category.SUBJECT, Category.ENVIRONMENT,
            Category.ENVIRONMENT };
        DataType[] pipDT = { DataType.INTEGER, DataType.INTEGER, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.STRING };

        for( int i = 0; i < pips.length; i++ ) {
            contextHandler.getPipRegistry().add( getMockedPIPCHInterface( pips[i], pipCat[i], pipDT[i], pipVal[i] ) );
        }
    }

    /* Non mocked components created from configuration */

    protected Attribute getNewAttribute( String id, Category category, DataType type, String val ) {
        Attribute attr = new Attribute();
        attr.setAttributeId( id );
        attr.setAttributeValues( type, val );
        attr.setDataType( type );
        attr.setCategory( category );
        return attr;
    }

    protected ArrayList<PIPCHInterface> getPIPS( UCSProperties prop ) {
        ArrayList<PIPCHInterface> pips = new ArrayList<>();

        for( PipProperties pipProp : prop.getPipList() ) {
            log.info( "Loading pip" );
            PIPCHInterface pip = (PIPCHInterface) UCSRest.buildComponent( pipProp ).get();
            assertNotNull( pip );
            pips.add( pip );
        }

        return pips;
    }

    protected SessionManagerInterface getSessionManager( UCSProperties prop ) {
        SessionManagerInterface sessionManager = new ProxySessionManager( prop.getSessionManager() );
        assertTrue( sessionManager.isInitialized() );
        return sessionManager;
    }

    protected PDPInterface getPDP( UCSProperties prop ) {
        PDPInterface pdp = new ProxyPDP( prop.getPolicyDecisionPoint() );
        assertNotNull( pdp );
        return pdp;
    }

    protected PAPInterface getPAP( UCSProperties prop ) {
        PAPInterface pap = new ProxyPAP( prop.getPolicyAdministrationPoint() );
        assertNotNull( pap );
        return pap;
    }

    /* Messages functions */

    protected Message buildMessage( Class<?> clazz, String src, String dest ) throws NoSuchMethodException, SecurityException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Constructor<?> constructor = clazz.getConstructor( String.class, String.class );
        Message message = (Message) constructor.newInstance( new Object[] { src, dest } );
        return message;
    }

    protected TryAccessMessage buildTryAccessMessage( String pepId, String ucsUri, String policy, String request )
            throws URISyntaxException, IOException {
        TryAccessMessage message = new TryAccessMessage( pepId, ucsUri );
        message.setPolicy( policy );
        message.setRequest( request );

        return message;
    }

    protected StartAccessMessage buildStartAccessMessage( String sessionId, String src, String dest ) {
        StartAccessMessage message = new StartAccessMessage( src, dest );
        message.setSessionId( sessionId );
        return message;
    }

    protected EndAccessMessage buildEndAccessMessage( String sessionId, String src, String dest ) {
        EndAccessMessage message = new EndAccessMessage( src, dest );
        message.setSessionId( sessionId );
        return message;
    }

    protected ReevaluationMessage buildReevaluationMessage( String sessionId, String src, String dest ) {
        ReevaluationMessage message = new ReevaluationMessage( src, dest );
        return message;
    }

    protected AttributeChangeMessage buildPipChMessage( String sessionId, String src, String dest ) {
        AttributeChangeMessage message = new AttributeChangeMessage( src, dest );
        return message;
    }

    protected TryAccessResponse buildTryAccessResponse( String src, String dest, String id ) {
        TryAccessResponse message = new TryAccessResponse( src, dest, id );

        return message;
    }

    protected StartAccessResponse buildStartAccessResponse( String src, String dest, String id ) {
        StartAccessResponse message = new StartAccessResponse( src, dest, id );

        return message;
    }

    protected EndAccessResponse buildEndAccessResponse( String src, String dest, String id ) {
        EndAccessResponse message = new EndAccessResponse( src, dest, id );

        return message;
    }

    protected ReevaluationResponse buildReevaluationResponse( String src, String dest, String id ) {
        ReevaluationResponse message = new ReevaluationResponse( src, dest, id );

        return message;
    }

    /* Policy/Request functions */

    protected RequestType getRequestType( String fileName ) throws JAXBException, URISyntaxException, IOException {
        return (RequestType) loadXMLFromFile( fileName, RequestType.class );
    }

    protected PolicyType getPolicyType( String fileName ) throws JAXBException, URISyntaxException, IOException {
        return (PolicyType) loadXMLFromFile( fileName, PolicyType.class );
    }

    /* Utility functions */

    private Object loadXMLFromFile( String fileName, Class<?> className )
            throws JAXBException, URISyntaxException, IOException {
        String data = FileUtility.readFileAsString( fileName );
        return JAXBUtility.unmarshalToObject( className, data );
    }
}
