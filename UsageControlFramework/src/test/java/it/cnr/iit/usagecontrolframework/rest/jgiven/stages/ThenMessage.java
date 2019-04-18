package it.cnr.iit.usagecontrolframework.rest.jgiven.stages;

import static org.assertj.core.api.Assertions.fail;

//import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
//import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
//import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;

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
        try {
            Thread.sleep( 100000 );
//        wireMockContextHandler.verifyThat( postRequestedFor( urlEqualTo( operation ) )
//            .withHeader( "Content-Type", equalTo( "application/json" ) ) );
        } catch( InterruptedException e ) {
            fail( e.getLocalizedMessage() );
        }
        return self();
    }
}
