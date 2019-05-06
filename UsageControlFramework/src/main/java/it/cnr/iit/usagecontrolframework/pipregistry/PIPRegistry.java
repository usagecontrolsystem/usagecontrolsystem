package it.cnr.iit.usagecontrolframework.pipregistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import it.cnr.iit.ucsinterface.pip.PIPCHInterface;
import it.cnr.iit.xacmlutilities.Attribute;

import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

public class PIPRegistry {

    private static final Logger log = Logger.getLogger( PIPRegistry.class.getName() );

    private List<PIPCHInterface> pipList;

    public PIPRegistry() {
        super();
        pipList = new ArrayList<>();
    }

    public boolean addPIP( PIPCHInterface pip ) {
        return pipList.add( pip );
    }

    public void unsubscribe( List<Attribute> attributes ) {
        for( PIPCHInterface pip : pipList ) {
            try {
                pip.unsubscribe( attributes );
            } catch( Exception e ) {
                log.severe( "Error unsubscribe : " + e.getMessage() );
            }
        }
    }

    public void subscribe( RequestType requestType ) {
        try {
            for( PIPCHInterface pip : pipList ) {
                pip.subscribe( requestType );
            }
        } catch( Exception e ) {
            log.severe( "Error subscribe : " + e.getMessage() );
        }
    }

    public void retrieve( RequestType requestType ) {
        try {
            for( PIPCHInterface pip : pipList ) {
                pip.retrieve( requestType );
            }
        } catch( Exception e ) {
            log.severe( "Error retrieve : " + e.getMessage() );
        }
    }

    public Optional<PIPCHInterface> getPIPByAttributeId( String attributeId ) {
        Attribute attribute = new Attribute();
        attribute.createAttributeId( attributeId );
        return getPIPByAttribute( attribute );
    }

    public Optional<PIPCHInterface> getPIPByAttribute( Attribute attribute ) {
        for( PIPCHInterface pip : pipList ) {
            if( pip.getAttributeIds().contains( attribute.getAttributeId() ) ) {
                return Optional.of( pip );
            }
        }
        return Optional.empty();
    }

    public boolean hasAttribute( Attribute attribute ) {
        return getPIPByAttribute( attribute ) != null;
    }

}
