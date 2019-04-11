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

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

/**
 * This class provides some static methods to perform a rest request
 *
 * @author Antonio La Marra, Alessandro Rosetti
 *
 */

public final class RESTUtils {
    private static final Logger log = Logger.getLogger( RESTUtils.class.getName() );

    private RESTUtils() {

    }

    public static Optional<ResponseEntity> post( String baseUri, String api, Object obj ) throws RestClientException {
        return post( baseUri, api, obj, Void.class );
    }

    public static <T, E> Optional<ResponseEntity> post( String baseUri, String api, E obj, Class<T> responseClass ) {
        // TODO fix this mess, use URI instead of string
        String url = baseUri + ( ( !api.endsWith( "/" ) && !baseUri.endsWith( "/" ) ) ? "/" : "" ) + api;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType( MediaType.APPLICATION_JSON );

        Optional<String> jsonString = JsonUtility.getJsonStringFromObject( obj, false );
        if( !jsonString.isPresent() ) {
            return Optional.empty();
        }

        HttpEntity<String> entity = new HttpEntity<>( jsonString.get(), headers );
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity responseEntity = restTemplate.postForEntity( url, entity, Void.class );

        return Optional.of( responseEntity );
    }

    public static <E> CompletableFuture<ResponseEntity> asyncPost( String baseUri, String api, E obj ) {
        Mono<ResponseEntity> response = WebClient
            .create( baseUri )
            .post()
            .uri( api )
            .contentType( MediaType.APPLICATION_JSON )
            .body( BodyInserters.fromObject( obj ) )
            .exchange()
            .flatMap( r -> r.toEntity( String.class ) );

        // TODO maybe log errors here?
        return CompletableFuture.supplyAsync( () -> response.block() );
    }

}
