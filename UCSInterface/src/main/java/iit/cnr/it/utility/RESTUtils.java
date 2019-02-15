/*******************************************************************************
 * Copyright 2018 IIT-CNR
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package iit.cnr.it.utility;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

/**
 * This class provides some static methods to perform a rest request
 *
 * @author antonio
 *
 */
final public class RESTUtils {

	private static final Logger LOGGER = Logger
	    .getLogger(RESTUtils.class.getName());

	private RESTUtils() {

	}

	/**
	 * Post an object to the host specified by the url parameter, expecting no
	 * returning object.
	 *
	 * @param url
	 *          the complete url of the host
	 * @param obj
	 *          the object to send using the post http method
	 */
	public static void post(String url, Object obj) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<Void> response = restTemplate.postForEntity(url, obj,
		    Void.class);
		if (response == null || !response.getStatusCode().is2xxSuccessful()) {
			LOGGER.log(Level.INFO, "FAIL: " + url);
		}
	}

	/**
	 * Post an object to the host specified by the url parameter, expecting a
	 * returning object of the class specified by the parameter responseType.
	 *
	 * @param url
	 *          url the complete url of the host
	 * @param obj
	 *          obj the object to send using the post http method
	 * @param responseType
	 *          the type of the object expected to retrieve.
	 * @return an instance of the class specified by the parameter responseType if
	 *         successful, null otherwise.
	 */
	public static <T, E> T post(String url, E obj, Class<T> responseType) {
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<T> response = restTemplate.postForEntity(url, obj,
		    responseType);
		if (response == null || !response.getStatusCode().is2xxSuccessful()) {
			LOGGER.log(Level.INFO, "FAIL: " + url);
			return null;
		}
		return response.getBody();
	}

	/**
	 * Post an object to the host specified by the url parameter, expecting a
	 * returning object of the class specified by the parameter responseType.
	 *
	 * @param url
	 *          url the complete url of the host
	 * @param obj
	 *          obj the object to send using the post http method
	 * @param responseType
	 *          the type of the object expected to retrieve.
	 * @return an instance of the class specified by the parameter responseType if
	 *         successful, null otherwise.
	 */
	public static <T, E> T postAsString(String url, E obj,
	    Class<T> responseType) {
		RestTemplate restTemplate = new RestTemplate();
		String string = new Gson().toJson(obj);
		ResponseEntity<T> response = restTemplate.postForEntity(url, string,
		    responseType);
		if (response == null || !response.getStatusCode().is2xxSuccessful()) {
			LOGGER.log(Level.INFO, "FAIL: " + url);
			return null;
		}
		return response.getBody();
	}

	/**
	 * Post an object to the host specified by the url parameter, expecting no
	 * returning object.
	 *
	 * @param url
	 *          the complete url of the host
	 * @param obj
	 *          the object to send using the post http method
	 */
	public static <E> RESTAsynchPostStatus asyncPost(String url, E obj) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<E> entity = new HttpEntity<>(obj, headers);
		AsyncRestTemplate restTemplate = new AsyncRestTemplate();
		restTemplate.postForEntity(url, entity, Void.class);

		ListenableFuture<ResponseEntity<Void>> future = restTemplate.postForEntity(url, entity, Void.class);
        try {
        	if (future == null || !future.get().getStatusCode().is2xxSuccessful()) {
				return RESTAsynchPostStatus.FAILURE;
        	}
            return RESTAsynchPostStatus.SUCCESS;
        } catch (Exception e) {
            if (e.getCause() instanceof HttpServerErrorException) {
                return RESTAsynchPostStatus.SERVER_ERROR;
            }
            return RESTAsynchPostStatus.FAILURE;
        }
	}

	/**
	 * Post an object to the host specified by the url parameter, expecting a
	 * returning object of the class specified by the parameter responseType.
	 *
	 * @param url
	 *          url the complete url of the host
	 * @param obj
	 *          obj the object to send using the post http method
	 * @param responseType
	 *          the type of the object expected to retrieve.
	 * @return an instance of the class specified by the parameter responseType if
	 *         successful, null otherwise.
	 */
	public static <T, E> T asyncPost(String url, E obj, Class<T> responseType) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<E> entity = new HttpEntity<>(obj, headers);
		AsyncRestTemplate restTemplate = new AsyncRestTemplate();
		try {
			ListenableFuture<ResponseEntity<T>> response = restTemplate
			    .postForEntity(url, entity, responseType);
			if (response == null
			    || !response.get().getStatusCode().is2xxSuccessful()) {
				LOGGER.log(Level.INFO, "FAIL: " + url);
				return null;
			}
			return response.get().getBody();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Post an object to the host specified by the url parameter, expecting a
	 * returning object of the class specified by the parameter responseType.
	 *
	 * @param url
	 *          url the complete url of the host
	 * @param obj
	 *          obj the object to send using the post http method
	 * @param responseType
	 *          the type of the object expected to retrieve.
	 * @return an instance of the class specified by the parameter responseType if
	 *         successful, null otherwise.
	 */
	public static <T, E> T asyncPostAsString(String url, E obj,
	    Class<T> responseType) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		AsyncRestTemplate restTemplate = new AsyncRestTemplate();
		String string = new Gson().toJson(obj);
		HttpEntity<String> entity = new HttpEntity<>(string, headers);
		try {
			ListenableFuture<ResponseEntity<T>> response = restTemplate
			    .postForEntity(url, entity, responseType);
			if (response == null
			    || !response.get().getStatusCode().is2xxSuccessful()) {
				LOGGER.log(Level.INFO, "FAIL: " + url + "\t" + string);
				return null;
			}
			return response.get().getBody();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Post an object to the host specified by the url parameter, expecting a
	 * returning object of the class specified by the parameter responseType.
	 *
	 * @param url
	 *          url the complete url of the host
	 * @param obj
	 *          obj the object to send using the post http method
	 * @param responseType
	 *          the type of the object expected to retrieve.
	 * @return an instance of the class specified by the parameter responseType if
	 *         successful, null otherwise.
	 */
	public static <E> void asyncPostAsString(String url, E obj) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.TEXT_PLAIN);
		AsyncRestTemplate restTemplate = new AsyncRestTemplate();
		String string = new Gson().toJson(obj);
		HttpEntity<String> entity = new HttpEntity<>(string, headers);
		restTemplate.postForEntity(url, entity, Void.class);
	}

}
