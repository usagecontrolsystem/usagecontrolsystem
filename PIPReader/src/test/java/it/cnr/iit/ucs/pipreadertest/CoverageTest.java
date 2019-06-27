package it.cnr.iit.ucs.pipreadertest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import com.tngtech.jgiven.annotation.AfterScenario;

import it.cnr.iit.ucs.exceptions.PIPException;
import it.cnr.iit.ucs.pipreader.PIPReader;
import it.cnr.iit.ucs.properties.UCFPipProperties;
import it.cnr.iit.ucs.requestmanager.RequestManagerInterface;
import it.cnr.iit.utility.JAXBUtility;
import it.cnr.iit.utility.JsonUtility;
import it.cnr.iit.utility.errorhandling.exception.PreconditionException;
import it.cnr.iit.xacml.Attribute;
import it.cnr.iit.xacml.Category;
import it.cnr.iit.xacml.DataType;

import oasis.names.tc.xacml.core.schema.wd_17.AttributeType;
import oasis.names.tc.xacml.core.schema.wd_17.AttributeValueType;
import oasis.names.tc.xacml.core.schema.wd_17.AttributesType;
import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

@EnableConfigurationProperties
@TestPropertySource( properties = "application.properties" )
@ActiveProfiles( "test" )
@RunWith( SpringRunner.class )
@SpringBootTest
@SpringBootConfiguration
public class CoverageTest {

    private static Logger log = Logger.getLogger( CoverageTest.class.getName() );

    @Value( "${environment.filepath}" )
    private String environmentFilePath;

    @Value( "${requestType}" )
    private String requestS;

    @Value( "${subjectPip}" )
    private String subjectPip;

    @Value( "${resourcePip}" )
    private String resourcePip;

    @Value( "${actionPip}" )
    private String actionPip;

    @Value( "${environmentPip}" )
    private String environmentPip;

    @Value( "${missingCategory}" )
    private String missingCategory;

    @Value( "${missingAttributeId}" )
    private String missingAttributeId;

    @Value( "${missingDataType}" )
    private String missingDataType;

    @Value( "${missingFilePath}" )
    private String missingFilePath;

    @Value( "${missingExpectedCategory}" )
    private String missingExpectedCategory;

    @Value( "${malformedInput}" )
    private String malformedInput;

    private RequestType requestType = new RequestType();
    private PIPReader subjectAttributePip;
    private PIPReader resourceAttributePip;
    private PIPReader actionAttributePip;
    private PIPReader environmentAttributePip;
    private PIPReader fault;
    private Attribute subjectAttribute = new Attribute();
    private Attribute resourceAttribute = new Attribute();
    private Attribute actionAttribute = new Attribute();
    private Attribute environmentAttribute = new Attribute();

    private RequestManagerInterface requestManager;

    public void init() {
        try {
            resetRequest();
            requestManager = Mockito.mock( RequestManagerInterface.class );
            subjectAttributePip = new PIPReader( getPropertiesFromString( subjectPip ) );
            resourceAttributePip = new PIPReader( getPropertiesFromString( resourcePip ) );
            actionAttributePip = new PIPReader( getPropertiesFromString( actionPip ) );
            environmentAttributePip = new PIPReader( getPropertiesFromString( environmentPip ) );
            initAttributes();
            subjectAttributePip.setRequestManager( requestManager );
            resourceAttributePip.setRequestManager( requestManager );
            actionAttributePip.setRequestManager( requestManager );
            environmentAttributePip.setRequestManager( requestManager );
        } catch( Exception e ) {
            e.printStackTrace();
        }
    }

    @AfterScenario
    private void cleanUp() {
        try {
            Files.delete( Paths.get( environmentFilePath ) );
        } catch( IOException e ) {}
    }

    private void resetRequest() {
        try {
            requestType = JAXBUtility.unmarshalToObject( RequestType.class, requestS );
        } catch( JAXBException e ) {
            e.printStackTrace();
        }
    }

    private void initAttributes() {
        subjectAttribute.setAttributeId( "urn:oasis:names:tc:xacml:1.0:subject:role" );
        subjectAttribute.setDataType( DataType.STRING );
        subjectAttribute.setCategory( Category.SUBJECT );

        resourceAttribute.setAttributeId( "urn:oasis:names:tc:xacml:1.0:resource:class" );
        resourceAttribute.setDataType( DataType.STRING );
        resourceAttribute.setCategory( Category.RESOURCE );

        actionAttribute.setAttributeId( "urn:oasis:names:tc:xacml:1.0:resource:type" );
        actionAttribute.setDataType( DataType.STRING );
        actionAttribute.setCategory( Category.ACTION );

        environmentAttribute.setAttributeId( "urn:oasis:names:tc:xacml:3.0:environment:temperature" );
        environmentAttribute.setDataType( DataType.STRING );
        environmentAttribute.setCategory( Category.ENVIRONMENT );
    }

    @Test
    public void test() {
        init();
        testRetrieve();
        testSubscribe();
        try {
            Thread.sleep( 2000 ); // NOSONAR
            changeAttributeValue();
            Thread.sleep( 2000 ); // NOSONAR
            resetAttributeValue();
            testUnsubscribe();
        } catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    public static UCFPipProperties getPropertiesFromString( String properties ) {
        return JsonUtility.loadObjectFromJsonString( properties, UCFPipProperties.class ).get();
    }

    @Test( expected = PreconditionException.class )
    public void testMissingCategory() throws PIPException {
        PIPReader missingCatPIP = new PIPReader( getPropertiesFromString( missingCategory ) );
    }

    @Test( expected = PreconditionException.class )
    public void testMissingAttributeId() throws PIPException {
        PIPReader missingAttIdPIP = new PIPReader( getPropertiesFromString( missingAttributeId ) );
    }

    @Test( expected = PreconditionException.class )
    public void testMissingExpCat() throws PIPException {
        PIPReader missingExpCatPIP = new PIPReader( getPropertiesFromString( missingExpectedCategory ) );
    }

    @Test( expected = PreconditionException.class )
    public void testMissingDataType() throws PIPException {
        PIPReader missingDataTypePIP = new PIPReader( getPropertiesFromString( missingDataType ) );
    }

    @Test( expected = PreconditionException.class )
    public void testMissingFilePath() throws PIPException {
        PIPReader missingDataTypePIP = new PIPReader( getPropertiesFromString( missingFilePath ) );
    }

    @Test( expected = PreconditionException.class )
    public void testNull() throws PIPException {
        PIPReader nullPIP = new PIPReader( null );
    }

    public void testRetrieve() {
        try {
            log.info( "*****************BEGIN RETIREVE TEST*******************" );
            remoteRetrievalTest();
            localTest();
            log.info( "*****************END RETRIEVE TEST*******************" );
        } catch( Exception e ) {
            log.severe( e.getMessage() );
        }
    }

    public void remoteRetrievalTest() {
        String value = testRetrieveAttribute( subjectAttribute, subjectAttributePip );
        assertTrue( value.equals( "IIT" ) );
        value = testRetrieveAttribute( resourceAttribute, resourceAttributePip );
        assertTrue( value.equals( "SECRET" ) );
        log.info( JsonUtility.getJsonStringFromObject( environmentAttributePip, true ).get() );
        log.info( JsonUtility.getJsonStringFromObject( actionAttributePip, true ).get() );
        value = testRetrieveAttribute( actionAttribute, actionAttributePip );
        assertTrue( value.equals( "ANALYZE" ) );
        value = testRetrieveAttribute( environmentAttribute, environmentAttributePip );
        assertTrue( value.equals( "30.0" ) );
    }

    private String testRetrieveAttribute( Attribute attribute, PIPReader pipReader ) {
        try {
            String value = pipReader.retrieve( attribute );
            return value;
        } catch( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    public void localTest() throws Exception {
        log.info( "-------BEGIN RETRIEVE TEST-------" );
        testRetrieveAndEnrichment( requestType, subjectAttributePip );
        assertTrue( verifyRequest( requestType, subjectAttribute ).equals( "[IIT]" ) );
        testRetrieveAndEnrichment( requestType, resourceAttributePip );
        assertTrue( verifyRequest( requestType, resourceAttribute ).equals( "[SECRET]" ) );
        testRetrieveAndEnrichment( requestType, actionAttributePip );
        assertTrue( verifyRequest( requestType, actionAttribute ).equals( "[ANALYZE]" ) );
        testRetrieveAndEnrichment( requestType, environmentAttributePip );
        assertTrue( verifyRequest( requestType, environmentAttribute ).equals( "[30.0]" ) );
        resetRequest();

        Attribute dummySubjectAttribute = new Attribute();
        dummySubjectAttribute.setAttributeId( "subjectId" );
        testRetrieveAndEnrichment( requestType, fault );
        testRetrieveAndEnrichment( null, fault );
        assertEquals( null, verifyRequest( requestType, dummySubjectAttribute ) );
        assertFalse( verifyRequest( requestType, environmentAttribute ).equals( "40.0" ) );
        log.info( "-------END RETRIEVE TEST-------" );
    }

    private void testRetrieveAndEnrichment( RequestType requestType, PIPReader pipReader ) {
        try {
            pipReader.retrieve( requestType );
        } catch( Exception e ) {}
    }

    public void testSubscribe() {
        try {
            log.info( "*****************BEGIN SUBSCRIBE TEST*******************" );
            remoteSubscribeTest();
            localSubscribeTest();
            log.info( "*****************END SUBSCRIBE TEST*******************" );
        } catch( Exception e ) {
            log.severe( e.getMessage() );
        }
    }

    public void localSubscribeTest() throws Exception {
        log.info( "-------BEGIN SUBSCRIBE TEST-------" );
        testSubscribeAndEnrichment( requestType, subjectAttributePip );
        assertTrue( verifyRequest( requestType, subjectAttribute ).equals( "[IIT]" ) );
        testSubscribeAndEnrichment( requestType, resourceAttributePip );
        assertTrue( verifyRequest( requestType, resourceAttribute ).equals( "[SECRET]" ) );
        testSubscribeAndEnrichment( requestType, actionAttributePip );
        assertTrue( verifyRequest( requestType, actionAttribute ).equals( "[ANALYZE]" ) );
        testSubscribeAndEnrichment( requestType, environmentAttributePip );
        assertTrue( verifyRequest( requestType, environmentAttribute ).equals( "[30.0]" ) );
        resetRequest();

        Attribute dummySubjectAttribute = new Attribute();
        dummySubjectAttribute.setAttributeId( "subjectId" );
        testSubscribeAndEnrichment( requestType, fault );
        testSubscribeAndEnrichment( null, fault );
        assertEquals( null, verifyRequest( requestType, dummySubjectAttribute ) );
        assertFalse( verifyRequest( requestType, environmentAttribute ).equals( "40.0" ) );
        log.info( "-------END SUBSCRIBE TEST-------" );
    }

    private void testSubscribeAndEnrichment( RequestType requestType, PIPReader pipReader ) {
        try {
            pipReader.subscribe( requestType );
        } catch( Exception e ) {
            log.severe( e.getMessage() );
        }
    }

    public void remoteSubscribeTest() {
        String value = testSubscribeAttribute( subjectAttribute, subjectAttributePip );
        assertTrue( value.equals( "IIT" ) );
        value = testSubscribeAttribute( resourceAttribute, resourceAttributePip );
        assertTrue( value.equals( "SECRET" ) );
        value = testSubscribeAttribute( actionAttribute, actionAttributePip );
        assertTrue( value.equals( "ANALYZE" ) );
        value = testSubscribeAttribute( environmentAttribute, environmentAttributePip );
        assertTrue( value.equals( "30.0" ) );
    }

    private String testSubscribeAttribute( Attribute attribute, PIPReader pipReader ) {
        try {
            return pipReader.subscribe( attribute );
        } catch( Exception e ) {
            log.severe( e.getMessage() );
        }
        return null;
    }

    private void changeAttributeValue() {
        write( "60.0" );
    }

    private void resetAttributeValue() {
        write( "30.0" );
    }

    private void write( String string ) {
        try {
            Path path = Paths.get( environmentFilePath );
            Files.write( path, string.getBytes() );
        } catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public void testUnsubscribe() {
        try {
            log.info( "*****************BEGIN UNSUBSCRIBE TEST*******************" );
            boolean value = testUnsubscribeAttribute( subjectAttribute, subjectAttributePip );
            assertTrue( value );
            value = testUnsubscribeAttribute( resourceAttribute, resourceAttributePip );
            assertTrue( value );
            value = testUnsubscribeAttribute( actionAttribute, actionAttributePip );
            assertTrue( value );
            value = testUnsubscribeAttribute( environmentAttribute, environmentAttributePip );
            assertTrue( value );
            value = testUnsubscribeAttribute( subjectAttribute, fault );
            assertFalse( value );
            value = testUnsubscribeAttribute( subjectAttribute, resourceAttributePip );
            value = testUnsubscribeAttribute( subjectAttribute, fault );
            assertFalse( value );
            value = testUnsubscribeAttribute( subjectAttribute, resourceAttributePip );
            assertFalse( value );
            log.info( "*****************END UNSUBSCRIBE TEST*******************" );
        } catch( Exception e ) {
            log.severe( e.getMessage() );
        }
    }

    private boolean testUnsubscribeAttribute( Attribute attribute, PIPReader pipReader ) {
        ArrayList<Attribute> list = new ArrayList<>();
        try {
            list.add( attribute );
            boolean value = pipReader.unsubscribe( list );
            return value;
        } catch( Exception e ) {
            log.severe( e.getMessage() );
        }
        return false;
    }

    private String verifyRequest( RequestType requestType, Attribute attribute ) {
        for( AttributesType attributeTypes : requestType.getAttributes() ) {
            for( AttributeType attributeType : attributeTypes.getAttribute() ) {
                if( attributeType.getAttributeId().equals( attribute.getAttributeId().toString() ) ) {
                    for( AttributeValueType attributeValue : attributeType.getAttributeValue() ) {
                        log.info( attributeValue.getContent().toString() );
                        return attributeValue.getContent().toString();
                    }
                }
            }
        }
        return null;
    }
}
