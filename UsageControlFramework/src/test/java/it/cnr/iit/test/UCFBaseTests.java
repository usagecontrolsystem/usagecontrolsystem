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

import it.cnr.iit.ucsinterface.contexthandler.ContextHandlerInterface;
import it.cnr.iit.ucsinterface.contexthandler.STATUS;
import it.cnr.iit.ucsinterface.forwardingqueue.ForwardingQueueToCHInterface;
import it.cnr.iit.ucsinterface.forwardingqueue.ForwardingQueueToRMInterface;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessMessage;
import it.cnr.iit.ucsinterface.message.endaccess.EndAccessResponse;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationMessage;
import it.cnr.iit.ucsinterface.message.reevaluation.ReevaluationResponse;
import it.cnr.iit.ucsinterface.message.remoteretrieval.MessagePipCh;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessMessage;
import it.cnr.iit.ucsinterface.message.startaccess.StartAccessResponse;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessageBuilder;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessResponse;
import it.cnr.iit.ucsinterface.node.NodeInterface;
import it.cnr.iit.ucsinterface.obligationmanager.ObligationManagerInterface;
import it.cnr.iit.ucsinterface.pap.PAPInterface;
import it.cnr.iit.ucsinterface.pdp.PDPEvaluation;
import it.cnr.iit.ucsinterface.pdp.PDPInterface;
import it.cnr.iit.ucsinterface.pep.PEPInterface;
import it.cnr.iit.ucsinterface.pip.PIPCHInterface;
import it.cnr.iit.ucsinterface.pip.PIPRetrieval;
import it.cnr.iit.ucsinterface.requestmanager.RequestManagerToCHInterface;
import it.cnr.iit.ucsinterface.sessionmanager.SessionInterface;
import it.cnr.iit.ucsinterface.sessionmanager.SessionManagerInterface;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.Configuration;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLPip;
import it.cnr.iit.usagecontrolframework.configuration.xmlclasses.XMLRequestManager;
import it.cnr.iit.usagecontrolframework.contexthandler.ContextHandlerLC;
import it.cnr.iit.usagecontrolframework.proxies.PIPBuilder;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPAP;
import it.cnr.iit.usagecontrolframework.proxies.ProxyPDP;
import it.cnr.iit.usagecontrolframework.proxies.ProxySessionManager;
import it.cnr.iit.usagecontrolframework.requestmanager.RequestManagerLC;
import it.cnr.iit.xacmlutilities.Attribute;
import it.cnr.iit.xacmlutilities.Category;
import it.cnr.iit.xacmlutilities.DataType;
import it.cnr.iit.xacmlutilities.policy.utility.JAXBUtility;

import oasis.names.tc.xacml.core.schema.wd_17.DecisionType;
import oasis.names.tc.xacml.core.schema.wd_17.PolicyType;
import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

public class UCFBaseTests {
    protected Logger LOGGER = Logger.getLogger( UCFBaseTests.class.getName() );

    @Autowired
    TestConfiguration conf;

    /* Request manager functions */

    protected RequestManagerLC getRequestManager( XMLRequestManager xml ) {
        RequestManagerLC requestManager = new RequestManagerLC( xml );
        return requestManager;
    }

    /* Context Hanlder functions */

    protected ContextHandlerLC getContextHandler( Configuration ucsConfiguration ) {
        ContextHandlerLC contextHandler = new ContextHandlerLC( ucsConfiguration.getCh() );
        return contextHandler;
    }

    protected void initContextHandler( ContextHandlerLC contextHandler ) {
        contextHandler.setPdpInterface( getMockedPDP( getMockedPDPEvaluation( DecisionType.PERMIT ) ) );
        contextHandler.setPapInterface( getMockedPAP( null ) );
        contextHandler.setRequestManagerToChInterface( getMockedRequestManagerToChInterface() );
        contextHandler.setSessionManagerInterface( getSessionManagerForStatus( "", "", "", ContextHandlerInterface.TRY_STATUS ) );
        contextHandler.setForwardingQueue( getMockedForwardingQueueToCHInterface() );
        contextHandler.setObligationManager( getMockedObligationManager() );
        contextHandler.setPIPRetrieval( getMockedPipRetrieval() );
    }

    protected ContextHandlerLC getContextHandlerCorrectlyInitialized( Configuration ucsConfiguration, String policy,
            String request ) {
        ContextHandlerLC contextHandler = getContextHandler( ucsConfiguration );
        initContextHandler( contextHandler );
        contextHandler.setSessionManagerInterface(
            getSessionManagerForStatus( "", policy, request, ContextHandlerInterface.TRY_STATUS ) );

        contextHandler.verify();
        /* must be called after initialisation */
        addMockedPips( ucsConfiguration, contextHandler );
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

    protected NodeInterface getMockedNodeInterface() {
        NodeInterface nodeInterface = Mockito
            .mock( NodeInterface.class );

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

    /* mocked PipRetrieval */

    protected PIPRetrieval getMockedPipRetrieval() {
        PIPRetrieval pipRetrieval = Mockito.mock( PIPRetrieval.class );
        Mockito.doAnswer( a -> {
            RequestType requestType = a.getArgument( 0 );
            // List<Attribute> attributeRetrievals = a.getArgument(1);
            LOGGER.info( "pip retrieve!" );
            requestType.addAttribute( Category.ENVIRONMENT.toString(), DataType.INTEGER.toString(), "virus", "1" );
            requestType.addAttribute( Category.ENVIRONMENT.toString(), DataType.INTEGER.toString(), "telephone", "1" );
            requestType.addAttribute( Category.ENVIRONMENT.toString(), DataType.STRING.toString(), "position", "Pisa" );

            return null;
        } ).when( pipRetrieval ).retrieve( ArgumentMatchers.<RequestType>any(), ArgumentMatchers.any() );
        return pipRetrieval;
    }

    /* Mocked PIPs */

    protected PIPCHInterface getMockedPIPCHInterface( String attrId, Category category, DataType dataType, String attrReturn ) {
        PIPCHInterface pip = Mockito.mock( PIPCHInterface.class );

        Attribute attr = getNewAttribute( attrId, category, dataType, attrReturn );
        ArrayList<Attribute> attributeList = new ArrayList<>( Arrays.asList( new Attribute[] { attr } ) );
        ArrayList<String> attributeIdList = new ArrayList<>( Arrays.asList( new String[] { attrId } ) );

        Mockito.when( pip.getAttributes() ).thenReturn( attributeList );
        Mockito.when( pip.getAttributeIds() ).thenReturn( attributeIdList );
        Mockito.when( pip.setContextHandlerInterface( ArgumentMatchers.<ContextHandlerLC>any() ) ).thenReturn( true );

        return pip;
    }

    protected void addPips( Configuration ucsConfiguration, ContextHandlerLC contextHandler ) {
        for( PIPCHInterface pip : getPIPS( ucsConfiguration ) ) {
            contextHandler.addPip( pip );
        }
    }

    protected void addMockedPips( Configuration ucsConfiguration, ContextHandlerLC contextHandler ) {
        // TODO FIX THIS HACK
        String[] pips = { "virus", "telephone", "position", "role", "telephone", "time" };
        String[] pipVal = { "0", "0", "Pisa", "IIT", "0", "12:00" };
        Category[] pipCat = { Category.ENVIRONMENT, Category.ENVIRONMENT, Category.SUBJECT, Category.SUBJECT, Category.ENVIRONMENT,
            Category.ENVIRONMENT };
        DataType[] pipDT = { DataType.INTEGER, DataType.INTEGER, DataType.STRING, DataType.STRING, DataType.INTEGER, DataType.STRING };

        for( int i = 0; i < pips.length; i++ ) {
            contextHandler.addPip( getMockedPIPCHInterface( pips[i], pipCat[i], pipDT[i], pipVal[i] ) );
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

    protected ArrayList<PIPCHInterface> getPIPS( Configuration ucsConfiguration ) {
        ArrayList<PIPCHInterface> pips = new ArrayList<>();

        for( XMLPip xmlPIP : ucsConfiguration.getPipList() ) {
            LOGGER.info( "Loading pip" );
            PIPCHInterface pip = PIPBuilder.build( xmlPIP );
            assertNotNull( pip );
            pips.add( pip );
        }

        return pips;
    }

    protected SessionManagerInterface getSessionManager( Configuration ucsConfiguration ) {
        SessionManagerInterface sessionManager = new ProxySessionManager( ucsConfiguration.getSessionManager() );
        assertTrue( sessionManager.isInitialized() );
        return sessionManager;
    }

    protected PDPInterface getPDP( Configuration ucsConfiguration ) {
        PDPInterface pdp = new ProxyPDP( ucsConfiguration.getPdp() );
        assertNotNull( pdp );
        return pdp;
    }

    protected PAPInterface getPAP( Configuration ucsConfiguration ) {
        PAPInterface pap = new ProxyPAP( ucsConfiguration.getPap() );
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
        TryAccessMessageBuilder builder = new TryAccessMessageBuilder( pepId, ucsUri );
        builder.setPolicy( policy );
        builder.setRequest( request );

        TryAccessMessage message = builder.build();

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

    protected MessagePipCh buildPipChMessage( String sessionId, String src, String dest ) {
        MessagePipCh message = new MessagePipCh( src, dest );
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

    protected Configuration getUCSConfiguration( String ucsConfigFile )
            throws JAXBException, URISyntaxException, IOException {
        return (Configuration) loadXMLFromFile( ucsConfigFile, Configuration.class );
    }

    private Object loadXMLFromFile( String fileName, Class<?> className )
            throws JAXBException, URISyntaxException, IOException {
        String data = readResourceFileAsString( fileName );
        return JAXBUtility.unmarshalToObject( className, data );
    }

    protected String readResourceFileAsString( String resource ) throws URISyntaxException, IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();

        LOGGER.info( "Loading resource file : " + resource );
        Path path = Paths.get( classLoader.getResource( resource ).toURI() );
        byte[] data = Files.readAllBytes( path );
        return new String( data );
    }
}
