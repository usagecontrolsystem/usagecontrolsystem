package it.cnr.iit.ucs.builders;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.logging.Logger;

import it.cnr.iit.ucs.configuration.pip.PipProperties;
import it.cnr.iit.ucsinterface.pip.PIPBase;
import it.cnr.iit.utility.JsonUtility;

public class PIPBuilder {
    private static Logger LOGGER = Logger.getLogger( PIPBuilder.class.getName() );

    private PIPBuilder() {}

    public static Optional<PipProperties> getPipPropertiesFromString( String properties ) {
        Optional<PipProperties> obj = JsonUtility.loadObjectFromJsonString( properties, PipProperties.class );
        return obj;
    }

    public static Optional<PIPBase> buildPIPBaseFromString( String strProperties ) {
        Optional<PipProperties> properties = getPipPropertiesFromString( strProperties );
        if( properties.isPresent() ) {
            return buildPIPBaseFromPipProperties( properties.get() );
        }
        LOGGER.severe( "Cannot build PIPBase from string : use a valid PipProperties json" );
        return Optional.empty();
    }

    public static Optional<PIPBase> buildPIPBaseFromPipProperties( PipProperties properties ) {
        Optional<PIPBase> obj;

        try {
            Class<?> clazz = Class.forName( properties.getClassName() );
            Constructor<?> constructor = clazz.getConstructor( PipProperties.class );
            PIPBase pip = (PIPBase) constructor.newInstance( properties );
            return Optional.ofNullable( pip );
        } catch( Exception e ) {
            LOGGER.severe( "Cannot build PIPBase from properties : " + e.getMessage() );
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
