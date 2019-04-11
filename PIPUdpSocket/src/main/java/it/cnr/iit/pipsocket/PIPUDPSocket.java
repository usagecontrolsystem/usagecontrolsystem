package it.cnr.iit.pipsocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;

import it.cnr.iit.ucs.configuration.pip.PipProperties;
import it.cnr.iit.ucsinterface.obligationmanager.ObligationInterface;
import it.cnr.iit.ucsinterface.pip.PIPBase;
import it.cnr.iit.ucsinterface.pip.exception.PIPException;
import it.cnr.iit.xacmlutilities.Attribute;
import it.cnr.iit.xacmlutilities.Category;
import it.cnr.iit.xacmlutilities.DataType;

import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

/**
 * The PIP acts as the Client of the communication
 *
 * @author antonio
 *
 */
public class PIPUDPSocket extends PIPBase {

    private static Logger log = Logger.getLogger( PIPUDPSocket.class.getName() );

    /**
     * Whenever a PIP has to retrieve some informations related to an attribute
     * that is stored inside the request, it has to know in advance all the
     * informations to retrieve that atrtribute. E.g. if this PIP has to retrieve
     * the informations about the subject, it has to know in advance which is the
     * attribute id qualifying the subject, its category and the datatype used,
     * otherwise it is not able to retrieve the value of that attribute, hence it
     * would not be able to communicate with the AM properly
     */
    private Category expectedCategory;

    // this is the attribute manager of this pip
    private Integer port;

    private DatagramSocket clientSocket;
    private String destinationIPAddress;
    private String destinationPort;
    private final int length = 1024;

    // states if the pip has been correctly initialized
    private volatile boolean initialized = false;

    // list that stores the attributes on which a subscribe has been performed
    protected final BlockingQueue<Attribute> subscriptions = new LinkedBlockingQueue<>();

    // the subscriber timer in charge of performing the polling of the values
    private PRSubscriberTimer subscriberTimer;
    // timer to be used to instantiate the subscriber timer
    private Timer timer = new Timer();

    public PIPUDPSocket( PipProperties properties ) {
        super( properties );
        if( !isInitialized() ) {
            return;
        }
        if( initialize( properties ) ) {
            initialized = true;
            subscriberTimer = new PRSubscriberTimer( contextHandlerInterface,
                subscriptions, clientSocket, destinationIPAddress, destinationPort );
            timer.scheduleAtFixedRate( subscriberTimer, 0, 3L * 1000 );
        } else {
            return;
        }
    }

    /**
     * Performs the effective initialization of the PIP.
     *
     * @param xmlPip
     *          the xml of the pip in string format
     * @return true if everything goes ok, false otherwise
     */
    private boolean initialize( PipProperties properties ) {
        try {
            Map<String, String> arguments = properties.getAttributes().get( 0 ).getArgs();
            Attribute attribute = new Attribute();
            if( !attribute.createAttributeId( arguments.get( ATTRIBUTE_ID ) ) ) {
                log.severe( "[PIPReader] wrong set Attribute" );
                return false;
            }
            if( !attribute
                .setCategory( Category.toCATEGORY( arguments.get( CATEGORY ) ) ) ) {
                log.severe( "[PIPReader] wrong set category " + arguments.get( CATEGORY ) );
                return false;
            }
            if( !attribute.setAttributeDataType(
                DataType.toDATATYPE( arguments.get( DATA_TYPE ) ) ) ) {
                log.severe( "[PIPReader] wrong set datatype" );
                return false;
            }
            if( attribute.getCategory() != Category.ENVIRONMENT ) {
                if( !setExpectedCategory( arguments.get( EXPECTED_CATEGORY ) ) ) {
                    return false;
                }
            }
            addAttribute( attribute );
            destinationPort = arguments.get( "AM_PORT" );
            destinationIPAddress = arguments.get( "AM_IP" );
            clientSocket = new DatagramSocket();
            return true;
        } catch( SocketException e ) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void subscribe( RequestType accessRequest ) throws PIPException {
        // BEGIN parameter checking
        if( accessRequest == null || !initialized || !isInitialized() ) {
            log.severe( "[PIPREader] wrong initialization" + initialized
                    + "\t" + isInitialized() );
            return;
        }
        // END parameter checking

        subscriberTimer.setContextHandlerInterface( contextHandlerInterface );

        if( subscriberTimer.getContextHandler() == null
                || contextHandlerInterface == null ) {
            log.severe( "Context handler not set" );
            return;
        }

        // create the new attribute
        Attribute attribute = getAttributes().get( 0 );

        String value;
        String filter;
        // read the value of the attribute, if necessary extract the additional info
        if( attribute.getCategory() == Category.ENVIRONMENT ) {
            value = read();
        } else {
            filter = accessRequest.extractValue( expectedCategory );
            attribute.setAdditionalInformations( filter );
            value = read( filter );
        }
        attribute.setValue( attribute.getAttributeDataType(), value );

        // add the attribute to the access request
        accessRequest.addAttribute( attribute.getCategory().toString(),
            attribute.getAttributeDataType().toString(), attribute.getAttributeId(),
            value );

        // add the attribute to the subscription list
        if( !subscriptions.contains( attribute ) ) {
            subscriptions.add( attribute );
        }
    }

    @Override
    public void retrieve( RequestType accessRequest ) throws PIPException {
        // BEGIN parameter checking
        if( accessRequest == null || !initialized || !isInitialized() ) {
            log.severe( "[PIPREader] wrong initialization" + initialized
                    + "\t" + isInitialized() );
            return;
        }
        // END parameter checking

        String value;
        Attribute attribute = getAttributes().get( 0 );

        if( attribute.getCategory() == Category.ENVIRONMENT ) {
            value = read();
        } else {
            String filter = accessRequest.extractValue( expectedCategory );
            value = read( filter );
        }

        accessRequest.addAttribute( attribute.getCategory().toString(),
            attribute.getAttributeDataType().toString(), attribute.getAttributeId(),
            value );
    }

    @Override
    public boolean unsubscribe( List<Attribute> attributes ) throws PIPException {
        // BEGIN parameter checking
        if( attributes == null || !initialized || !isInitialized() ) {
            log.severe( "[PIPREader] wrong initialization" + initialized
                    + "\t" + isInitialized() );
            return false;
        }
        // END parameter checking

        for( Attribute attribute : attributes ) {
            if( attribute.getAttributeId().equals( getAttributeIds().get( 0 ) ) ) {
                for( Attribute attributeS : subscriptions ) {
                    if( attributeS.getAdditionalInformations()
                        .equals( attribute.getAdditionalInformations() ) ) {
                        subscriptions.remove( attributeS );
                        log.info( "UNSUB " + subscriptions.size() );
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public String retrieve( Attribute attributeRetrievals ) throws PIPException {
        String value;

        if( getAttributes().get( 0 ).getCategory() == Category.ENVIRONMENT ) {
            value = read();
        } else {
            String filter = attributeRetrievals.getAdditionalInformations();
            value = read( filter );
        }
        return value;
    }

    @Override
    public String subscribe( Attribute attributeRetrieval ) throws PIPException {
        subscriberTimer.setContextHandlerInterface( contextHandlerInterface );

        if( subscriberTimer.getContextHandler() == null
                || contextHandlerInterface == null ) {
            log.severe( "Context handler not set" );
            return null;
        }

        String value;
        if( getAttributes().get( 0 ).getCategory() == Category.ENVIRONMENT ) {
            value = read();
        } else {
            String filter = attributeRetrieval.getAdditionalInformations();
            value = read( filter );
        }
        attributeRetrieval.setValue( getAttributes().get( 0 ).getAttributeDataType(),
            value );
        if( !subscriptions.contains( attributeRetrieval ) ) {
            subscriptions.add( attributeRetrieval );
        }
        return value;
    }

    @Override
    public void retrieve( RequestType request,
            List<Attribute> attributeRetrievals ) {
        // TODO Auto-generated method stub

    }

    @Override
    public void subscribe( RequestType request,
            List<Attribute> attributeRetrieval ) {
        // TODO Auto-generated method stub

    }

    @Override
    public void updateAttribute( String json ) throws PIPException {
        // TODO Auto-generated method stub

    }

    @Override
    public void performObligation( ObligationInterface obligation ) {
        // TODO Auto-generated method stub

    }

    /**
     * Sets up a client socket with the AttributeManager that will be in charge of
     * providing the various informations
     *
     * @param port
     *          the port on which we want to start the socket
     */
    private boolean setSocket( String amPort ) {
        try {
            clientSocket = new DatagramSocket( Integer.parseInt( amPort ) );
            return true;
        } catch( NumberFormatException | IOException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    private final boolean setExpectedCategory( String category ) {
        // BEGIN parameter checking
        if( !isInitialized() || category == null || category.isEmpty() ) {
            initialized = false;
            return false;
        }
        // END parameter checking
        Category categoryObj = Category.toCATEGORY( category );
        if( categoryObj == null ) {
            initialized = false;
            return false;
        }
        expectedCategory = categoryObj;
        return true;
    }

    private String read() {
        try {
            byte receive[] = new byte[1024];
            String read = "READ";
            DatagramPacket sendPacket = new DatagramPacket( read.getBytes(),
                read.getBytes().length, InetAddress.getByName( destinationIPAddress ),
                Integer.parseInt( destinationPort ) );
            clientSocket.send( sendPacket );
            DatagramPacket receivePacket = new DatagramPacket( receive,
                receive.length );
            clientSocket.receive( receivePacket );
            String line = new String( receivePacket.getData() );
            line = line.trim();
            log.info( "RESULT10: " + line );
            receive = null;
            return line;
        } catch( Exception e ) {
            e.printStackTrace();
            return null;
        }
    }

    private String read( String filter ) {
        try {
            byte receive[] = new byte[1024];
            String read = filter;
            DatagramPacket sendPacket = new DatagramPacket( read.getBytes(),
                read.getBytes().length, InetAddress.getByName( destinationIPAddress ),
                Integer.parseInt( destinationPort ) );
            clientSocket.send( sendPacket );
            DatagramPacket receivePacket = new DatagramPacket( receive,
                receive.length );
            clientSocket.receive( receivePacket );
            String line = new String( receivePacket.getData() );
            line = line.trim();
            log.info( line );
            receive = null;
            return line;
        } catch( Exception e ) {
            e.printStackTrace();
            return null;
        }

    }

}
