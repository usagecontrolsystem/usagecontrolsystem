package it.cnr.iit.peprest.jgiven.rules;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import java.util.List;

import org.junit.rules.ExternalResource;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.verification.NearMiss;

/**
 * JUnit test rule that sets up a mocked HTTP service using WireMock.
 * To make sure the service works both locally and on a Jenkins instance,
 * use it in combination with a {@link PortForwardingTestRule}.
 */
public class MockedHttpServiceTestRule extends ExternalResource {

    private boolean failOnUnmatchedStubs;
    private WireMockServer wireMockServer;

    public MockedHttpServiceTestRule( Options options ) {
        this( options, true );
    }

    public MockedHttpServiceTestRule( Options options, boolean failOnUnmatchedStubs ) {
        wireMockServer = new WireMockServer( options );
        this.failOnUnmatchedStubs = failOnUnmatchedStubs;
    }

    public MockedHttpServiceTestRule( int port ) {
        this( wireMockConfig().port( port ) );
    }

    public MockedHttpServiceTestRule() {}

    public MockedHttpServiceTestRule( int httpsPort, String keystorePath, String keystorePassword ) {
        this( wireMockConfig()
            .httpsPort( httpsPort )
            .trustStorePath( keystorePath )
            .trustStorePassword( keystorePassword )
            .keystorePath( keystorePath )
            .keystorePassword( keystorePassword )
            .trustStoreType( "JKS" ) );
    }

    private void checkForUnmatchedRequests() {
        if( failOnUnmatchedStubs ) {
            List<NearMiss> nearMisses = wireMockServer.findNearMissesForAllUnmatchedRequests();
            if( !nearMisses.isEmpty() ) {
                throw VerificationException.forUnmatchedNearMisses( nearMisses );
            }
        }
    }

    public void start( int port ) {
        wireMockConfig().port( port );
        wireMockServer = new WireMockServer( options().port( port ) );
        wireMockServer.start();
    }

    @Override
    protected void after() {
        try {
            checkForUnmatchedRequests();
        } finally {
            wireMockServer.stop();
        }
    }
}
