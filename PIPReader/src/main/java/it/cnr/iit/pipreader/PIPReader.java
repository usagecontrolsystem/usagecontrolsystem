/*******************************************************************************
 * Copyright 2018 IIT-CNR
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package it.cnr.iit.pipreader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.ucs.properties.components.PipProperties;
import it.cnr.iit.ucsinterface.message.PART;
import it.cnr.iit.ucsinterface.message.pipch.PipChMessage;
import it.cnr.iit.ucsinterface.obligationmanager.ObligationInterface;
import it.cnr.iit.ucsinterface.pip.PIPBase;
import it.cnr.iit.ucsinterface.pip.exception.PIPException;
import it.cnr.iit.utility.Utility;
import it.cnr.iit.utility.errorhandling.Reject;
import it.cnr.iit.xacmlutilities.Attribute;
import it.cnr.iit.xacmlutilities.Category;
import it.cnr.iit.xacmlutilities.DataType;

import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

/**
 * This is the PIPReader.
 * <p>
 * The only task this PIP will perform is to read data from a file.
 * The Path to reach the file is passed as parameter to the pip.
 * </p>
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public final class PIPReader extends PIPBase {

    private static Logger log = Logger.getLogger( PIPReader.class.getName() );
    private PIPJournalHelper journal;

    // list that stores the attributes on which a subscribe has been performed
    protected final BlockingQueue<Attribute> subscriptions = new LinkedBlockingQueue<>();

    // the subscriber timer in charge of performing the polling of the values
    private PIPReaderSubscriberTimer subscriberTimer;

    /**
     * Whenever a PIP has to retrieve some informations related to an attribute
     * that is stored inside the request, it has to know in advance all the
     * informations to retrieve that attribute. E.g. if this PIP has to retrieve
     * the informations about the subject, it has to know in advance which is the
     * attribute id qualifying the subject, its category and the data-type used,
     * otherwise it is not able to retrieve the value of that attribute, hence it
     * would not be able to communicate with the AM properly
     */
    private Category expectedCategory;

    private static final String ATTRIBUTE_SEPARATOR = "\t";

    public static final String FILE_PATH = "FILE_PATH";
    private String filePath;

    public PIPReader( PipProperties properties ) {
        super( properties );
        Reject.ifFalse( init( properties ),
            "Error initialising pip : " + properties.getId() );
        subscriberTimer = new PIPReaderSubscriberTimer( this );
        subscriberTimer.start();
        log.info( "PIPReader " + properties.getId() + " initialised" );
    }

    // TODO use reject here
    private boolean init( PipProperties properties ) {
        try {
            Map<String, String> attributeMap = properties.getAttributes().get( 0 );
            Attribute attribute = new Attribute();

            String attributeId = attributeMap.get( ATTRIBUTE_ID );
            if( attributeId == null || attributeId.isEmpty() ) {
                log.severe( "Wrong attributeId : " + attributeMap.get( ATTRIBUTE_ID ) );
                return false;
            }
            attribute.setAttributeId( attributeId );

            Category category = Category.toCATEGORY( attributeMap.get( CATEGORY ) );
            if( category == null ) {
                log.severe( "Wrong category : " + attributeMap.get( CATEGORY ) );
                return false;
            }
            attribute.setCategory( category );

            DataType dataType = DataType.toDATATYPE( attributeMap.get( DATA_TYPE ) );
            if( dataType == null ) {
                log.severe( "Wrong datatype : " + attributeMap.get( DATA_TYPE ) );
                return false;
            }
            attribute.setDataType( dataType );

            if( attribute.getCategory() != Category.ENVIRONMENT ) {
                expectedCategory = Category.toCATEGORY( attributeMap.get( EXPECTED_CATEGORY ) );
                if( expectedCategory == null ) {
                    return false;
                }
            }

            if( attributeMap.containsKey( FILE_PATH ) ) {
                setFilePath( attributeMap.get( FILE_PATH ) );
            } else {
                log.severe( "Missing PIPReader file path" );
                return false;
            }

            addAttribute( attribute );
            journal = new PIPJournalHelper( properties.getJournalDir() );
            return true;
        } catch( Exception e ) {
            log.severe( "Error in PIP initialization : " + e.getMessage() );
            return false;
        }
    }

    /**
     * Performs the retrieve operation.
     * <p>
     * The retrieve operation is a very basic operation in which the PIP simply
     * asks to the AttributeManager the value in which it is interested into. Once
     * that value has been retrieved, the PIP will fatten the request.
     * </p>
     *
     * @param request
     *          this is an in/out parameter
     */
    @Override
    public void retrieve( RequestType request ) throws PIPException {
        Reject.ifNull( request );

        Attribute attribute = getAttributes().get( 0 );
        addAdditionalInformation( request, attribute );
        String value = retrieve( attribute );

        request.addAttribute( attribute, value );
    }

    /**
     * This is the function called by the context handler whenever we have a
     * remote retrieve request
     */
    @Override
    public String retrieve( Attribute attribute ) throws PIPException {
        if( isEnvironmentCategory( attribute ) ) {
            return read();
        } else {
            return read( attribute.getAdditionalInformations() );
        }
    }

    /**
     * Performs the subscribe operation. This operation is very similar to the
     * retrieve operation. The only difference is that in this case we have to
     * signal to the thread in charge of performing the polling that it has to
     * poll a new attribute
     *
     * @param request
     *          IN/OUT parameter
     */
    @Override
    public void subscribe( RequestType request ) throws PIPException {
        Reject.ifNull( request );
        Reject.ifNull( contextHandler );

        Attribute attribute = getAttributes().get( 0 );
        addAdditionalInformation( request, attribute );

        String value = subscribe( attribute );

        request.addAttribute( attribute, value );
    }

    /**
     * This is the function called by the context handler whenever we have a
     * remote retrieve request
     */
    @Override
    public String subscribe( Attribute attribute ) throws PIPException {
        Reject.ifNull( attribute );
        Reject.ifNull( contextHandler );

        String value = retrieve( attribute );
        DataType dataType = attribute.getDataType();
        attribute.setValue( dataType, value );
        addSubscription( attribute );

        return value;

    }

    /**
     * Checks if it has to remove an attribute (the one passed in the list) from
     * the list of subscribed attributes
     *
     * @param attributes
     *          the list of attributes that must be unsubscribed
     */
    @Override
    public boolean unsubscribe( List<Attribute> attributes ) throws PIPException {
        Reject.ifEmpty( attributes );
        for( Attribute attribute : attributes ) {
            if( attribute.getAttributeId().equals( getAttributeIds().get( 0 ) ) ) {
                for( Attribute subscribedAttribute : subscriptions ) {
                    if( subscribedAttribute.getAdditionalInformations()
                        .equals( attribute.getAdditionalInformations() ) ) {
                        if( !subscriptions.remove( subscribedAttribute ) ) {
                            throw new IllegalStateException( "Unable to remove attribute from list" );
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void addAdditionalInformation( RequestType request, Attribute attribute ) {
        String filter = request.extractValue( expectedCategory );
        attribute.setAdditionalInformations( filter );
    }

    public boolean isEnvironmentCategory( Attribute attribute ) {
        return attribute.getCategory() == Category.ENVIRONMENT;
    }

    /**
     * Effective retrieval of the monitored value.
     *
     * @return the requested value
     * @throws PIPException
     */
    private String read() throws PIPException {
        try {
            Path path = Paths.get( filePath );
            // TODO UCS-33 NOSONAR
            String value = new String( Files.readAllBytes( path ) );
            journal.logReadOperation( value );
            return value;
        } catch( IOException e ) {
            throw new PIPException( "Attribute Manager error : " + e.getMessage() );
        }
    }

    /**
     * Effective retrieval of the monitored value looking for the line containing a filter.
     *
     * <br>
     * NOTE we suppose that in the file each line has the following structure:
     * filter\tattribute.
     *
     * @param filter
     *          the string to be used to search for the item we're interested into
     * @return the requested value
     * @throws PIPException
    */
    private String read( String filter ) throws PIPException {
        // TODO UCS-33 NOSONAR
        try (BufferedReader br = new BufferedReader( new FileReader( filePath ) )) {
            for( String line; ( line = br.readLine() ) != null; ) {
                if( line.contains( filter ) ) {
                    String value = line.split( ATTRIBUTE_SEPARATOR )[1];
                    journal.logReadOperation( value, filter );
                    return value;
                }
            }
        } catch( Exception e ) {
            throw new PIPException( "Attribute Manager error : " + e.getMessage() );
        }
        throw new PIPException( "Attribute Manager error : no value for this filter : " + filter );
    }

    private final void setFilePath( String filePath ) {
        Reject.ifBlank( filePath );
        String absFilePath = Utility.findFileAbsPathUsingClassLoader( filePath );
        if( absFilePath != null ) {
            this.filePath = absFilePath;
        } else {
            this.filePath = filePath;
        }
    }

    @Override
    public void update( String data ) throws PIPException {
        try {
            Path path = Paths.get( filePath );
            Files.write( path, data.getBytes() );
        } catch( IOException e ) {
            log.severe( "Error updating attribute : " + e.getMessage() );
        }
    }

    @Override
    public void retrieve( RequestType request,
            List<Attribute> attributeRetrievals ) {
        log.severe( "Multiple retrieve is unimplemented" );
    }

    @Override
    public void subscribe( RequestType request,
            List<Attribute> attributeRetrieval ) {
        log.severe( "Multiple subscribe is unimplemented" );
    }

    @Override
    public void performObligation( ObligationInterface obligation ) {
        log.severe( "Perform obligation is unimplemented" );
    }

    public void addSubscription( Attribute attribute ) {
        if( !subscriptions.contains( attribute ) ) {
            subscriptions.add( attribute );
        }
    }

    // TODO interface for timer?
    public void checkSubscriptions() {
        for( Attribute attribute : subscriptions ) {
            String value = "";
            log.log( Level.INFO, "Polling on value of the attribute " + attribute.getAttributeId() + "for change." );

            try {
                value = retrieve( attribute );
            } catch( PIPException e ) {
                log.log( Level.WARNING, "Error reading attribute " + attribute.getAttributeId() );
                return;
            }

            String oldValue = attribute.getAttributeValues( attribute.getDataType() ).get( 0 );
            if( !oldValue.equals( value ) ) { // if the attribute has changed
                log.log( Level.INFO,
                    "Attribute {0}={1}:{2} changed at {1}",
                    new Object[] { attribute.getAttributeId(), value,
                        attribute.getAdditionalInformations(),
                        System.currentTimeMillis() } );
                attribute.setValue( attribute.getDataType(), value );
                notifyContextHandler( attribute );
            }
        }
    }

    public void notifyContextHandler( Attribute attribute ) {
        PipChMessage pipchMessage = new PipChMessage( PART.PIP.toString(), PART.CH.toString() );
        ArrayList<Attribute> attrList = new ArrayList<>( Arrays.asList( attribute ) );
        pipchMessage.setAttributes( attrList );
        contextHandler.attributeChanged( pipchMessage );
    }
}
