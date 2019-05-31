package it.cnr.iit.ucs.contexthandler.pipregistry;

import java.util.List;
import java.util.Optional;

import it.cnr.iit.ucs.pip.PIPCHInterface;
import it.cnr.iit.xacml.Attribute;

import oasis.names.tc.xacml.core.schema.wd_17.RequestType;

public interface PIPRegistryInterface {

    public boolean add( PIPCHInterface pip );

    public boolean remove( PIPCHInterface pip );

    public void removeAll();

    public void unsubscribeAll( List<Attribute> attributes );

    public void subscribeAll( RequestType requestType );

    public void retrieveAll( RequestType requestType );

    public Optional<PIPCHInterface> getByAttributeId( String attributeId );

    public Optional<PIPCHInterface> getByAttribute( Attribute attribute );

    public boolean hasAttribute( Attribute attribute );

}