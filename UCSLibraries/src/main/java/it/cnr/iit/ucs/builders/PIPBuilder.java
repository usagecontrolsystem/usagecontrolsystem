package it.cnr.iit.ucs.builders;

import java.lang.reflect.Constructor;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import it.cnr.iit.ucs.properties.components.PipProperties;
import it.cnr.iit.ucsinterface.pip.PIPBase;

public class PIPBuilder {

    private static Logger log = Logger.getLogger( PIPBuilder.class.getName() );

    private static final String MSG_ERR_BUILD_PROP = "Error building PIPBase from properties : {0}";

    private PIPBuilder() {}

    public static Optional<PIPBase> build( PipProperties properties ) {
        try {
            // TODO UCS-32 NOSONAR
            Class<?> clazz = Class.forName( properties.getClassName() );
            Constructor<?> constructor = clazz.getConstructor( PipProperties.class );
            PIPBase pip = (PIPBase) constructor.newInstance( properties );
            return Optional.of( pip );
        } catch( Exception e ) {
            log.log( Level.SEVERE, MSG_ERR_BUILD_PROP, e.getMessage() );
        }
        return Optional.empty();
    }
}
