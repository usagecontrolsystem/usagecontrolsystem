package it.cnr.iit.peprest;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

@RunWith( SpringJUnit4ClassRunner.class )
@WebAppConfiguration
public class PEPWireMockExampleTest extends PEPRestAbstractTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(
        options().port( Integer.parseInt( PORT ) )

    // Set the root of the filesystem WireMock will look under for files and mappings
    // .usingFilesUnderDirectory("D:\\git")

    // Set a path within the classpath as the filesystem root
    // .usingFilesUnderClasspath("src/test/resources")
    );

    @Override
    @Before
    public void setUp() {
        super.setUp();
    }

    @Test
    public void wireMockGetExampleTest() throws ClientProtocolException, IOException {
        stubFor( get( urlPathMatching( "/ucsinterface/.*" ) )
            .willReturn( aResponse()
                .withStatus( 200 )
                .withHeader( "Content-Type", "application/json" )
                .withBody( "\"testing-library\": \"WireMock\"" ) ) );

        HttpResponse httpResponse = sendGetRequestAndReceiveResponse();
        String stringResponse = convertHttpResponseToString( httpResponse );

        verify( getRequestedFor( urlEqualTo( "/ucsinterface/wiremock" ) ) );
        assertEquals( 200, httpResponse.getStatusLine().getStatusCode() );
        assertEquals( "application/json", httpResponse.getFirstHeader( "Content-Type" ).getValue() );
        assertEquals( "\"testing-library\": \"WireMock\"", stringResponse );
    }

    @Test
    public void wireMockPostExampleTest() throws ClientProtocolException, IOException {
        stubFor( post( urlPathMatching( "/ucsinterface/.*" ) )
            .willReturn( aResponse()
                .withStatus( 200 )
                .withHeader( "Content-Type", "application/json" )
                .withBody( "\"testing-library\": \"WireMock\"" ) ) );

        HttpResponse httpResponse = sendPostRequestAndReceiveResponse();
        String stringResponse = convertHttpResponseToString( httpResponse );

        verify( postRequestedFor( urlEqualTo( "/ucsinterface/wiremock" ) ) );
        assertEquals( 200, httpResponse.getStatusLine().getStatusCode() );
        assertEquals( "application/json", httpResponse.getFirstHeader( "Content-Type" ).getValue() );
        assertEquals( "\"testing-library\": \"WireMock\"", stringResponse );
    }

    @Test
    @Ignore
    public void wireMockPostExampleWithBodyFileTest() throws ClientProtocolException, IOException {
        stubFor( post( urlPathMatching( "/ucsinterface/.*" ) )
            .willReturn( aResponse()
                .withStatus( 200 )
                .withHeader( "Content-Type", "application/json" )
                .withBodyFile( "/wiremock_response.json" ) ) );

        HttpResponse httpResponse = sendPostRequestAndReceiveResponse();
        String stringResponse = convertHttpResponseToString( httpResponse );

        verify( postRequestedFor( urlPathMatching( "/ucsinterface/wiremock" ) )
            .withHeader( "Content-Type", equalTo( "application/json" ) ) );
        assertEquals( 200, httpResponse.getStatusLine().getStatusCode() );
        String expectedResponse = "{\r\n" +
                "    \"testing-library\": \"WireMock\"\r\n" +
                "}";
        assertEquals( expectedResponse,
            convertResponseToString( new Scanner( stringResponse ) ) );
    }

    private HttpResponse sendGetRequestAndReceiveResponse() throws IOException, ClientProtocolException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet( "http://" + HOST + PORT + "/ucsinterface/wiremock" );
        request.addHeader( "Content-Type", "application/json" );
        HttpResponse httpResponse = httpClient.execute( request );
        return httpResponse;
    }

    private HttpResponse sendPostRequestAndReceiveResponse() throws IOException, ClientProtocolException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost( "http://" + HOST + PORT + "/ucsinterface/wiremock" );
        request.addHeader( "Content-Type", "application/json" );
        HttpResponse httpResponse = httpClient.execute( request );
        return httpResponse;
    }

    private String convertHttpResponseToString( HttpResponse httpResponse ) throws UnsupportedOperationException, IOException {
        InputStream inputStream = httpResponse.getEntity().getContent();
        return convertResponseToString( new Scanner( inputStream, "UTF-8" ) );
    }

    private String convertResponseToString( Scanner scanner ) {
        String string = scanner.useDelimiter( "\\Z" ).next();
        scanner.close();
        return string;
    }

}
