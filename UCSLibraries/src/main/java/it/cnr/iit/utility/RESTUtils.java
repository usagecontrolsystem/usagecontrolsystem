/*******************************************************************************
 * Copyright 2018 IIT-CNR
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package it.cnr.iit.utility;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import it.cnr.iit.utility.errorhandling.Reject;

import reactor.core.publisher.Mono;

/**
 * This class provides some static methods to perform synchronous or asynchronous
 * rest operations.
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */
public final class RESTUtils {

    private static final Logger log = Logger.getLogger( RESTUtils.class.getName() );

    private static final String MSG_ERR_POST = "Error posting to : {0}";

    private RESTUtils() {

    }

    public static Optional<ResponseEntity<Void>> post( String baseUri, String api, Object obj ) {
        return post( baseUri, api, obj, Void.class );
    }

    public static Optional<URI> parseUri( String str ) {
        try {
            URI uri = new URI( str );
            return Optional.of( uri );
        } catch( Exception e ) {
            log.severe( e.getLocalizedMessage() );
        }
        return Optional.empty();
    }

    public static Optional<String> joinUrl( String base, String api ) {
        try {
            return Optional.of( new URL( new URL( base ), api ).toString() );
        } catch( MalformedURLException e ) {
            return Optional.empty();
        }
    }

    public static <T, E> Optional<ResponseEntity<T>> post( String baseUri, String api, E obj, Class<T> responseClass ) {
        Optional<String> url = joinUrl( baseUri, api );
        Reject.ifAbsent( url );

        Optional<String> jsonString = JsonUtility.getJsonStringFromObject( obj, false );
        if( !jsonString.isPresent() ) {
            return Optional.empty();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_JSON );
        HttpEntity<String> entity = new HttpEntity<>( jsonString.get(), headers );
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<T> responseEntity = restTemplate.postForEntity( url.get(), entity, responseClass ); // NOSONAR

        checkResponesEntity( responseEntity );

        return Optional.of( responseEntity );
    }

    public static <E> CompletableFuture<ResponseEntity<Void>> asyncPost( String baseUri, String api, E obj ) {
        return asyncPost( baseUri, api, obj, Void.class );
    }

    public static <T, E> CompletableFuture<ResponseEntity<T>> asyncPost( String baseUri, String api, E obj, Class<T> clazz ) {
        Mono<ResponseEntity<T>> response = WebClient
            .create( baseUri )
            .post()
            .uri( api )
            .contentType( MediaType.APPLICATION_JSON )
            .body( BodyInserters.fromValue( obj ) )
            .exchange()
            .flatMap( r -> r.toEntity( clazz ) );

        return CompletableFuture.supplyAsync( getResponseEntity( response ) );
    }

    private static <T> Supplier<ResponseEntity<T>> getResponseEntity( Mono<ResponseEntity<T>> response ) {
        return () -> {
            ResponseEntity<T> responseEntity = response.block();
            checkResponesEntity( responseEntity );
            return responseEntity;
        };
    }

    private static <T> void checkResponesEntity( ResponseEntity<T> responseEntity ) {
        if( !responseEntity.getStatusCode().is2xxSuccessful() ) {
            String uri = responseEntity.getHeaders().getLocation().toString();
            log.log( Level.SEVERE, MSG_ERR_POST, uri );
        }
    }
}
