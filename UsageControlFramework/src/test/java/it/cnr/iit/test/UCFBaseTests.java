package it.cnr.iit.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import it.cnr.iit.test.properties.TestProperties;
import it.cnr.iit.ucs.constants.OperationNames;
import it.cnr.iit.ucs.constants.STATUS;
import it.cnr.iit.ucs.properties.UCSProperties;
import it.cnr.iit.ucs.properties.components.PipProperties;
import it.cnr.iit.ucsinterface.contexthandler.ContextHandlerConstants;
import it.cnr.iit.ucsinterface.contexthandler.ContextHandlerInterface;
import it.cnr.iit.ucsinterface.forwardingqueue.ForwardingQueueToCHInterface;
import it.cnr.iit.ucsinterface.forwardingqueue.ForwardingQueueToRMInterface;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucsinterface.message.pipch.PipChMessage;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationMessage;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucsinterface.obligationmanager.ObligationManagerInterface;
import it.cnr.iit.ucsinterface.pap.PAPInterface;
import it.cnr.iit.ucsinterface.pdp.PDPEvaluation;
import it.cnr.iit.ucsinterface.pdp.PDPInterface;
import it.cnr.iit.ucsinterface.pep.PEPInterface;
import it.cnr.iit.ucsinterface.pip.PIPCHInterface;
import it.cnr.iit.ucsinterface.requestmanager.RequestManagerToCHInterface;
import it.cnr.iit.ucsinterface.sessionmanager.SessionInterface;
import it.cnr.iit.ucsinterface.sessionmanager.SessionManagerInterface;
import it.cnr.iit.usagecontrolframework.contexthandler.ContextHandlerLC;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPAP;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPDP;
import it.cnr.iit.usagecontrolframework.proxies.ProxySessionManager;
import it.cnr.iit.usagecontrolframework.requestmanager.RequestManagerLC;
import it.cnr.iit.usagecontrolframework.rest.UsageControlFramework;
import it.cnr.iit.utility.JAXBUtility;
import it.cnr.iit.xacmlutilities.Attribute;
import it.cnr.iit.xacmlutilities.Category;
import it.cnr.iit.xacmlutilities.DataType;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;
import oasis.names.tc.xacml.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

public class UCFBaseTests {
    protected Logger log = Logger.getLogger( UCFBaseTests.class.getName() );

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
        contextHandler.setPdpInterface( getMockedPDP( getMockedPDPEvaluation( DecisionType.PERMIT ) ) );
        contextHandler.setPapInterface( getMockedPAP( null ) );
        contextHandler.setRequestManagerToChInterface( getMockedRequestManagerToChInterface() );
        contextHandler.setSessionManagerInterface( getSessionManagerForStatus( "", "", "", ContextHandlerConstants.TRY_STATUS ) );
        contextHandler.setForwardingQueue( getMockedForwardingQueueToCHInterface() );
        contextHandler.setObligationManager( getMockedObligationManager() );
    }

    protected ContextHandlerLC getContextHandlerCorrectlyInitialized( UCSProperties prop,
            String policy,
            String request ) {
        ContextHandlerLC contextHandler = getContextHandler( prop );
        initContextHandler( contextHandler );
        contextHandler.setSessionManagerInterface(
            getSessionManagerForStatus( "", policy, request, ContextHandlerConstants.TRY_STATUS ) );

        contextHandler.verify();
        /* must be called after initialisation */
        addMockedPips( prop, contextHandler );
        assertTrue( contextHandler.startMonitoringThread() );

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
        Mockito.when( sessionManagerInterface.createEntry( ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString(), ArgumentMatchers.<List<String>>any(), ArgumentMatchers.<List<String>>any(),
            ArgumentMatchers.<List<String>>any(), ArgumentMatchers.<List<String>>any(), ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(), ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString() ) ).thenReturn( true );

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

        return sessionInterface;
    }

    /* Mocked ContextHandlerInterface */

    protected ContextHandlerInterface getMockedContextHandlerInterface() {
        ContextHandlerInterface contextHandler = Mockito.mock( ContextHandlerInterface.class );

        return contextHandler;
    }

    /* Mocked RequestManager */

    protected RequestManagerToCHInterface getMockedRequestManagerToChInterface() {
        RequestManagerToCHInterface requestManagerToChInterface = Mockito.mock( RequestManagerToCHInterface.class );
        return requestManagerToChInterface;
    }

    protected ForwardingQueueToCHInterface getMockedForwardingQueueToCHInterface() {
        ForwardingQueueToCHInterface forwardingQueueToCHInterface = Mockito.mock( ForwardingQueueToCHInterface.class );
        return forwardingQueueToCHInterface;
    }

    protected OperationNames getMockedNodeInterface() {
        OperationNames nodeInterface = Mockito
            .mock( OperationNames.class );

        return nodeInterface;
    }

    protected ForwardingQueueToRMInterface getMockedForwardingQueueToRMInterface( Message message ) {
        ForwardingQueueToRMInterface forwardingQueue = Mockito
            .mock( ForwardingQueueToRMInterface.class );
        Mockito.when( forwardingQueue.getOriginalSource( ArgumentMatchers.anyString() ) ).thenReturn( message );

        return forwardingQueue;
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
        Mockito.when( obligationManager.translateObligations( ArgumentMatchers.<PDPEvaluation>any(), ArgumentMatchers.anyString(),
            ArgumentMatchers.anyString() ) ).thenReturn( null );
        return obligationManager;
    }

    /* Mocked PDP */

    protected PDPInterface getMockedPDP( PDPEvaluation pdpEval ) {
        PDPInterface pdp = Mockito.mock( PDPInterface.class );
        Mockito.when( pdp.evaluate( ArgumentMatchers.anyString(), ArgumentMatchers.<StringBuilder>any(), ArgumentMatchers.<STATUS>any() ) )
            .thenReturn( pdpEval );
        Mockito.when( pdp.evaluate( ArgumentMatchers.anyString(), ArgumentMatchers.anyString() ) ).thenReturn( pdpEval );
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
        Mockito.when( pip.setContextHandlerInterface( ArgumentMatchers.<ContextHandlerLC>any() ) ).thenReturn( true );

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
        attr.createAttributeId( id );
        attr.createAttributeValues( type, val );
        attr.setAttributeDataType( type );
        attr.setCategory( category );
        return attr;
    }

    protected ArrayList<PIPCHInterface> getPIPS( UCSProperties prop ) {
        ArrayList<PIPCHInterface> pips = new ArrayList<>();

        for( PipProperties pipProp : prop.getPipList() ) {
            log.info( "Loading pip" );
            PIPCHInterface pip = (PIPCHInterface) UsageControlFramework.buildComponent( pipProp ).get();
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

    protected PipChMessage buildPipChMessage( String sessionId, String src, String dest ) {
        PipChMessage message = new PipChMessage( src, dest );
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
        String data = readResourceFileAsString( fileName );
        return JAXBUtility.unmarshalToObject( className, data );
    }

    protected String readResourceFileAsString( String resource ) throws URISyntaxException, IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();

        log.info( "Loading resource file : " + resource );
        Path path = Paths.get( classLoader.getResource( resource ).toURI() );
        byte[] data = Files.readAllBytes( path );
        return new String( data );
    }
}
