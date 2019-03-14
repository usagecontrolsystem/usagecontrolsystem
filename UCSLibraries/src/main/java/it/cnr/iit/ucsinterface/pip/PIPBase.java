/*
 * CNR - IIT (2015-2016)
 *
 * @authors Fabio Bindi and Filippo Lauria
 */
package it.cnr.iit.ucsinterface.pip;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

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

    protected ContextHandlerPIPInterface contextHandlerInterface;

    // Properties file used for managing the properties of the PIP
    private String properties;

    public HashMap<String, Attribute> attributes = new HashMap<>();

    protected String configuration;

    private volatile boolean initialized = false;

    /**
     * Basic constructor for a PIP
     *
     * @param xmlPip
     *          the configuration of the PIP
     */
    public PIPBase( String xmlPip ) {
        // BEGIN parameter checking
        if( xmlPip == null || xmlPip.equals( "" ) ) {
            LOGGER.severe( "xml is null or empty " );
            // TODO throw exception
            return;
        }
        // END parameter checking
        properties = xmlPip;
        initialized = true;
    }

    @Override
    final public ArrayList<String> getAttributeIds() {
        if( initialized ) {
            ArrayList<String> arrayList = new ArrayList<>();
            arrayList.addAll( attributes.keySet() );
            return arrayList;
        }
        return null;
    }

    @Override
    final public ArrayList<Attribute> getAttributes() {
        if( initialized ) {
            ArrayList<Attribute> arrayList = new ArrayList<>();
            arrayList.addAll( attributes.values() );
            return arrayList;
        }
        return null;
    }

    @Override
    final public HashMap<String, Attribute> getAttributesCharacteristics() {
        if( initialized ) {
            return attributes;
        }
        return null;
    }

    final protected String getProperties() {
        if( initialized ) {
            return properties;
        }
        return null;
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
