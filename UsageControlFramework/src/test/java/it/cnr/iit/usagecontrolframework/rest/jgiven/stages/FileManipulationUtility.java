package it.cnr.iit.usagecontrolframework.rest.jgiven.stages;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import com.tngtech.jgiven.annotation.Quoted;

public class FileManipulationUtility {

    private FileManipulationUtility() {}

    public static String readResourceFileAsString( String resource ) {
        try {
            byte[] data = Files.readAllBytes( Paths.get( FileManipulationUtility.class.getClassLoader().getResource( resource ).toURI() ) );
            return new String( data );
        } catch( URISyntaxException | IOException e ) {
            fail( "Unable to read resource due to " + e.getLocalizedMessage() );
            return null;
        }
    }

    public static void updateResourceFileContent( @Quoted String resource, String value ) {
        try {
            Files.write( Paths.get( FileManipulationUtility.class.getClassLoader().getResource( resource ).toURI() ),
                Arrays.asList( value ), StandardCharsets.UTF_8 );
        } catch( IOException | URISyntaxException e ) {
            fail( "Unable to update monitored attribute value due to error: " + e.getLocalizedMessage() );
        }
    }
}
