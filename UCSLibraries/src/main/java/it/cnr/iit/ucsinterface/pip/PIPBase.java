/*
 * CNR - IIT (2015-2016)
 *
 * @authors Fabio Bindi and Filippo Lauria
 */
package it.cnr.iit.ucsinterface.pip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import it.cnr.iit.ucs.configuration.pip.PipProperties;
import it.cnr.iit.ucsinterface.contexthandler.ContextHandlerPIPInterface;
import it.cnr.iit.xacmlutilities.Attribute;

/**
 * General PIP abstract class
 * <p>
 * In provides basics implementations of getters function
 * </p>
 *
 * @author Fabio Bindi and Filippo Lauria and Antonio La Marra
 */
public abstract class PIPBase implements PIPCHInterface, PIPOMInterface {
    private static final Logger LOGGER = Logger.getLogger( PIPBase.class.getName() );

    /**
     * Whenever a PIP has to retrieve some informations related to an attribute
     * that is stored inside the request, the only information it requires in
     * order to retrieve the value from the request is the category of the
     * attribute
     */
    public static final String EXPECTED_CATEGORY = "EXPECTED_CATEGORY";

    /**
     * Since we have designed a general PIPReader which is able to read basically
     * all the files, we have to tell this PIP all the characteristics of the
     * attribute it is in charge of reading
     */
    public static final String ATTRIBUTE_ID = "ATTRIBUTE_ID";
    public static final String CATEGORY = "CATEGORY";
    public static final String DATA_TYPE = "DATA_TYPE";
    // path to the file that has to be read
    public static final String FILE_PATH = "FILE_PATH";

    protected ContextHandlerPIPInterface contextHandlerInterface;
    public HashMap<String, Attribute> attributes = new HashMap<>();
    private PipProperties properties;

    private volatile boolean initialized = false;

    public enum TAGS {
        ENVIRONMENT( "environment" ),
        SUBJECT( "subject" ),
        RESOURCE( "resource" ),
        ACTION( "action" );

        private String tag;

        TAGS( String t ) {
            tag = t;
        }

        String getValue() {
            return tag;
        }

        @Override
        public String toString() {
            return tag;
        }
    }

    /**
     * Basic constructor for a PIP
     *
     * @param properties
     *          the configuration of the PIP
     */
    public PIPBase( PipProperties properties ) {
        // BEGIN parameter checking
        if( properties == null ) {
            LOGGER.severe( "properties are null" );
            // TODO throw exception
            return;
        }
        // END parameter checking
        this.properties = properties;
        initialized = true;
    }

    @Override
    public final ArrayList<String> getAttributeIds() {
        if( initialized ) {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.addAll( attributes.keySet() );
            return arrayList;
        }
        return null;
    }

    @Override
    public final ArrayList<Attribute> getAttributes() {
        if( initialized ) {
            ArrayList<Attribute> arrayList = new ArrayList<>();
            arrayList.addAll( attributes.values() );
            return arrayList;
        }
        LOGGER.severe( "Cannot read attributes, PIP not initialised" );
        return null;
    }

    @Override
    public final HashMap<String, Attribute> getAttributesCharacteristics() {
        return attributes;
    }

    final protected PipProperties getProperties() {
        return properties;
    }

    final protected boolean isInitialized() {
        return initialized;
    }

    @Override
    public boolean setContextHandlerInterface(
            ContextHandlerPIPInterface contextHandlerInterface ) {
        // BEGIN parameter checking
        if( initialized == false || contextHandlerInterface == null ) {
            return false;
        }
        // END parameter checking
        this.contextHandlerInterface = contextHandlerInterface;
        return true;
    }

    final protected boolean addAttribute( Attribute attribute ) {
        // BEGIN parameter checking
        if( attribute == null
                || attributes.containsKey( attribute.getAttributeId() ) ) {
            return false;
        }
        // END parameter checking
        attributes.put( attribute.getAttributeId(), attribute );
        return true;
    }
}
