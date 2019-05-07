package it.cnr.iit.usagecontrolframework.pipregistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import it.cnr.iit.ucsinterface.contexthandler.pipregistry.PIPRegistryInterface;
import it.cnr.iit.ucsinterface.pip.PIPCHInterface;
import it.cnr.iit.xacmlutilities.Attribute;

import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

public class PIPRegistry implements PIPRegistryInterface {

    private static final Logger log = Logger.getLogger( PIPRegistry.class.getName() );

    private List<PIPCHInterface> pipList;

    public PIPRegistry() {
        super();
        pipList = new ArrayList<>();
    }

    @Override
    public boolean add( PIPCHInterface pip ) {
        return pipList.add( pip );
    }

    @Override
    public boolean remove( PIPCHInterface pip ) {
        return pipList.remove( pip );
    }

    @Override
    public void removeAll() {
        pipList.clear();
    }

    @Override
    public void unsubscribeAll( List<Attribute> attributes ) {
        for( PIPCHInterface pip : pipList ) {
            try {
                pip.unsubscribe( attributes );
            } catch( Exception e ) {
                log.severe( "Error unsubscribe : " + e.getMessage() );
            }
        }
    }

    @Override
    public void subscribeAll( RequestType requestType ) {
        try {
            for( PIPCHInterface pip : pipList ) {
                pip.subscribe( requestType );
            }
        } catch( Exception e ) {
            log.severe( "Error subscribe : " + e.getMessage() );
        }
    }

    @Override
    public void retrieveAll( RequestType requestType ) {
        try {
            for( PIPCHInterface pip : pipList ) {
                pip.retrieve( requestType );
            }
        } catch( Exception e ) {
            log.severe( "Error retrieve : " + e.getMessage() );
        }
    }

    @Override
    public Optional<PIPCHInterface> getByAttributeId( String attributeId ) {
        Attribute attribute = new Attribute();
        attribute.createAttributeId( attributeId );
        return getByAttribute( attribute );
    }

    @Override
    public Optional<PIPCHInterface> getByAttribute( Attribute attribute ) {
        for( PIPCHInterface pip : pipList ) {
            if( pip.getAttributeIds().contains( attribute.getAttributeId() ) ) {
                return Optional.of( pip );
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean hasAttribute( Attribute attribute ) {
        return getByAttribute( attribute ) != null;
    }

}
