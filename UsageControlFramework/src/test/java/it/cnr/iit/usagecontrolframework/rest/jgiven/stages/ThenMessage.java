package it.cnr.iit.usagecontrolframework.rest.jgiven.stages;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.awaitility.Awaitility.await;
import static org.awaitility.Duration.TWO_SECONDS;
import static org.junit.Assert.fail;

import java.util.concurrent.Callable;

import com.github.tomakehurst.wiremock.client.VerificationException;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.annotation.ExpectedScenarioState;
import com.tngtech.jgiven.annotation.Quoted;
import com.tngtech.jgiven.integration.spring.JGivenStage;

@JGivenStage
public class ThenMessage extends Stage<ThenMessage> {

    @ExpectedScenarioState
    WireMock wireMockContextHandler;

    public ThenMessage the_asynch_post_request_for_$_was_received_by_PEPRest( @Quoted String operation ) {
        await()
            .pollDelay( TWO_SECONDS )
                .until( postRequestWasVerified( operation ) );
        return self();
    }

    private Callable<Boolean> postRequestWasVerified( String operation ) {
        return new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                try {
                    wireMockContextHandler.verifyThat( postRequestedFor( urlEqualTo( operation ) )
                        .withHeader( "Content-Type", equalTo( "application/json" ) ) );
                } catch( VerificationException e ) {
                    return false;
                } catch( Exception e ) {
                    fail( e.getLocalizedMessage() );
                }
                return true;
            }
        };
    }
}
