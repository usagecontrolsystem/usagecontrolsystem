package it.cnr.iit.usagecontrolframework.rest.jgiven.stages;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.BeforeScenario;
import com.tngtech.jgiven.annotation.ProvidedScenarioState;
import com.tngtech.jgiven.integration.spring.JGivenStage;

import it.cnr.iit.ucs.configuration.UCSConfiguration;
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
        request = readResourceFileAsString( conf.getPolicyFile() );
    }

    public GivenMessage a_TryAccess_request() {
        try {
            message = buildTryAccessMessage( conf.getPepId(), conf.getUcsUri(), policy, request );
        } catch( URISyntaxException | IOException e ) {
            fail( e.getLocalizedMessage() );
        }
        return self();
    }

    protected TryAccessMessage buildTryAccessMessage( String pepId, String ucsUri, String policy, String request )
            throws URISyntaxException, IOException {
        TryAccessMessageBuilder builder = new TryAccessMessageBuilder( pepId, ucsUri );
        builder.setPolicy( policy );
        builder.setRequest( request );

        TryAccessMessage message = builder.build();

        return message;
    }

    protected UCSConfiguration getUCSConfiguration( String ucsConfigFile )
            throws JAXBException, URISyntaxException, IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();
        File file = new File( classLoader.getResource( ucsConfigFile ).getFile() );

        return JsonUtility.loadObjectFromJsonFile( file, UCSConfiguration.class ).get();
    }

    protected String readResourceFileAsString( String resource ) throws URISyntaxException, IOException {
        ClassLoader classLoader = this.getClass().getClassLoader();

        Path path = Paths.get( classLoader.getResource( resource ).toURI() );
        byte[] data = Files.readAllBytes( path );
        return new String( data );
    }
}
