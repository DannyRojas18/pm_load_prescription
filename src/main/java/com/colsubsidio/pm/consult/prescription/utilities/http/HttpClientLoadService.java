package com.colsubsidio.pm.consult.prescription.utilities.http;

import java.net.URI;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

public interface HttpClientLoadService {

	/**
	 * Metodo para peticiones GET
	 * @param uri
	 * @param object
	 * @param mediaType
     * @param auth
	 * @return
     * @throws java.lang.Exception
	 */
	ResponseEntity<?> get(URI uri, Class<?> object, MediaType mediaType, Boolean auth) throws Exception;
	/**
	 * Metodo para peticiones DELETE
	 * @param uri
	 * @param object
	 * @param mediaType
     * @param auth
	 * @return
     * @throws java.lang.Exception
	 */
	ResponseEntity<?> delete(URI uri, Class<?> object, MediaType mediaType, Boolean auth) throws Exception;
	/**
	 * Metodo para peticiones POST
	 * @param uri
	 * @param data
	 * @param objectClass
	 * @param mediaType
	 * @param auth
	 * @return
     * @throws java.lang.Exception
	 */
	ResponseEntity<?> post(URI uri, Object data, Class<?> objectClass, MediaType mediaType, Boolean auth) throws Exception;
	/**
	 * Metodo para peticiones PUT
	 * @param uri
	 * @param data
	 * @param objectClass
	 * @param mediaType
	 * @param auth
	 * @return
     * @throws java.lang.Exception
	 */
	ResponseEntity<?> put(URI uri, Object data, Class<?> objectClass, MediaType mediaType, Boolean auth) throws Exception;
}
