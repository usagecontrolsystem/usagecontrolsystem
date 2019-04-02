package it.cnr.iit.ucs.builders;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.logging.Logger;

import it.cnr.iit.ucs.configuration.PapProperties;
import it.cnr.iit.ucs.configuration.pip.PipProperties;
import it.cnr.iit.ucsinterface.pap.PAPInterface;
import it.cnr.iit.utility.JsonUtility;

public class PAPBuilder {
    private static Logger LOGGER = Logger.getLogger( PAPBuilder.class.getName() );

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
        LOGGER.severe( "Cannot build from string : use valid properties json" );
        return Optional.empty();
    }

    public static Optional<PAPInterface> buildFromProperties( PapProperties properties ) {
        Optional<PAPInterface> obj;

        try {
            Class<?> clazz = Class.forName( properties.getClassName() );
            Constructor<?> constructor = clazz.getConstructor( PipProperties.class );
            PAPInterface pap = (PAPInterface) constructor.newInstance( properties );
            return Optional.ofNullable( pap );
        } catch( Exception e ) {
            LOGGER.severe( "Cannot build PAP from properties : " + e.getMessage() );
        }
        return Optional.empty();
    }
}
