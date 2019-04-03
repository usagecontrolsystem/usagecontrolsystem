package it.cnr.iit.ucs.builders;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.logging.Logger;

import it.cnr.iit.ucs.configuration.PapProperties;
import it.cnr.iit.ucs.configuration.pip.PipProperties;
import it.cnr.iit.ucsinterface.pap.PAPInterface;
import it.cnr.iit.utility.JsonUtility;

public class PAPBuilder {
    private static Logger LOG = Logger.getLogger( PAPBuilder.class.getName() );

    private static final String MSG_ERR_BUILD_PROP = "Error building PAP from properties : {0}";
    private static final String MSG_ERR_BUILD_STR = "Error building PAP from properties string";

    private PAPBuilder() {}

    public static Optional<PapProperties> getPropertiesFromString( String properties ) {
        Optional<PapProperties> obj = JsonUtility.loadObjectFromJsonString( properties, PapProperties.class );
        return obj;
    }

    public static Optional<PAPInterface> buildFromString( String strProperties ) {
        Optional<PapProperties> properties = getPropertiesFromString( strProperties );
        if( properties.isPresent() ) {
            return buildFromProperties( properties.get() );
        }
        LOG.severe( MSG_ERR_BUILD_STR );
        return Optional.empty();
    }

    public static Optional<PAPInterface> buildFromProperties( PapProperties properties ) {
        try {
            Class<?> clazz = Class.forName( properties.getClassName() );
            Constructor<?> constructor = clazz.getConstructor( PipProperties.class );
            PAPInterface pap = (PAPInterface) constructor.newInstance( properties );
            return Optional.of( pap );
        } catch( Exception e ) {
            LOG.severe( String.format( MSG_ERR_BUILD_PROP, e.getMessage() ) );
        }
        return Optional.empty();
    }
}
