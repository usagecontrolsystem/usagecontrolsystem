package it.cnr.iit.usagecontrolframework.rest.jgiven.stages;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;

import it.cnr.iit.ucs.configuration.PepProperties;
import it.cnr.iit.ucs.configuration.UCSConfiguration;
import it.cnr.iit.ucsinterface.message.MEAN;
import it.cnr.iit.ucsinterface.message.Message;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessage;
import it.cnr.iit.ucsinterface.message.tryaccess.TryAccessMessageBuilder;
import it.cnr.iit.usagecontrolframework.rest.UCFTestContext;
import it.cnr.iit.utility.JsonUtility;

@JGivenStage
public class GivenMessage extends Stage<GivenMessage> {

    private String policy;
    private String request;

    @ProvidedScenarioState
    UCSConfiguration ucsConfiguration;

    @Autowired
    UCFTestContext conf;

    @ProvidedScenarioState
    Message message;

    @BeforeScenario
    public void init() throws URISyntaxException, IOException, JAXBException {
        ucsConfiguration = getUCSConfiguration( conf.getUcsConfigFile() );
        policy = readResourceFileAsString( conf.getPolicyFile() );
        request = readResourceFileAsString( conf.getRequestFile() );
    }

    public GivenMessage a_TryAccess_request() {
        try {
            message = buildTryAccessMessage();
        } catch( Exception e ) {
            fail( e.getLocalizedMessage() );
        }
        return self();
    }

    protected TryAccessMessage buildTryAccessMessage() {
        assertNotNull( ucsConfiguration );
        PepProperties pepProps = ucsConfiguration.getPepList().get( Integer.parseInt( conf.getPepId() ) );

        TryAccessMessageBuilder tryAccessBuilder = new TryAccessMessageBuilder( pepProps.getId(), pepProps.getBaseUri() );
        tryAccessBuilder.setPepUri( buildOnGoingEvaluationInterface( pepProps ) )
            .setPolicy( policy ).setRequest( request ).setPolicyId( "1" );

        TryAccessMessage tryAccessMessage = tryAccessBuilder.build();
        tryAccessMessage.setCallback( buildResponseInterface( pepProps, "tryAccessResponse" ), MEAN.REST );

        return tryAccessMessage;
    }

    private final String buildResponseInterface( PepProperties pepProps, String name ) {
        StringBuilder response = new StringBuilder();
        response.append( pepProps.getBaseUri() );
        if( !( pepProps.getBaseUri().endsWith( "/" ) || name.startsWith( "/" ) ) ) {
            response.append( "/" );
        }
        response.append( name );
        return response.toString();
    }

    private String buildOnGoingEvaluationInterface( PepProperties pepProps ) {
        return buildResponseInterface( pepProps, "onGoingEvaluation" );
    }

    protected UCSConfiguration getUCSConfiguration( String ucsConfigFile ) {
        ClassLoader classLoader = this.getClass().getClassLoader();
        File file = new File( classLoader.getResource( ucsConfigFile ).getFile() );

        Optional<UCSConfiguration> loadObjectFromJsonFile = JsonUtility.loadObjectFromJsonFile( file, UCSConfiguration.class );
        if( loadObjectFromJsonFile.isPresent() ) {
            return loadObjectFromJsonFile.get();
        } else {
            return null;
        }
    }

    protected String readResourceFileAsString( String resource ) throws URISyntaxException, IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();

        Path path = Paths.get( classLoader.getResource( resource ).toURI() );
        byte[] data = Files.readAllBytes( path );
        return new String( data );
    }
}
