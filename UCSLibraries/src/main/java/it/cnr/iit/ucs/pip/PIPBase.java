/*
 * CNR - IIT (2015-2016)
 *
 * @authors Fabio Bindi and Filippo Lauria
 */
package it.cnr.iit.ucs.pip;

import java.util.ArrayList;
import java.util.HashMap;

import it.cnr.iit.ucs.contexthandler.ContextHandlerPIPInterface;
import it.cnr.iit.ucs.properties.components.PipProperties;
import it.cnr.iit.utility.errorhandling.Reject;
import it.cnr.iit.xacmlutilities.Attribute;

/**
 * General PIP abstract class
 *
 * @author Fabio Bindi and Filippo Lauria and Antonio La Marra and Alessandro Rosetti
 */
public abstract class PIPBase implements PIPCHInterface, PIPOMInterface {

    private ContextHandlerPIPInterface contextHandler;
    private HashMap<String, Attribute> attributesMap = new HashMap<>();

    private PipProperties properties;

    /**
     * Basic constructor for a PIP
     *
     * @param properties
     *          the configuration of the PIP
     */
    public PIPBase( PipProperties properties ) {
        Reject.ifNull( properties );
        this.properties = properties;
    }

    @Override
    public final ArrayList<String> getAttributeIds() {
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.addAll( attributesMap.keySet() );
        return arrayList;
    }

    @Override
    public final ArrayList<Attribute> getAttributes() {
        ArrayList<Attribute> arrayList = new ArrayList<>();
        arrayList.addAll( attributesMap.values() );
        return arrayList;
    }

    @Override
    public final HashMap<String, Attribute> getAttributesCharacteristics() {
        return attributesMap;
    }

    @Override
    public ContextHandlerPIPInterface getContextHandler() {
        Reject.ifNull( contextHandler );
        return contextHandler;
    }

    @Override
    public void setContextHandler( ContextHandlerPIPInterface contextHandler ) {
        Reject.ifNull( contextHandler );
        this.contextHandler = contextHandler;
    }

    protected final boolean addAttribute( Attribute attribute ) {
        Reject.ifNull( attribute );
        if( attributesMap.containsKey( attribute.getAttributeId() ) ) {
            return false;
        }
        attributesMap.put( attribute.getAttributeId(), attribute );
        return true;
    }

}
