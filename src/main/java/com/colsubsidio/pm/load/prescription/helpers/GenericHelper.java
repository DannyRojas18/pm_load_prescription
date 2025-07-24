package com.colsubsidio.pm.load.prescription.helpers;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import com.colsubsidio.pm.load.prescription.utilities.HttpClientLoad;
import com.colsubsidio.utilities.log.LogsManager;
import com.google.gson.Gson;
import org.springframework.util.StringUtils;

@Component
public class GenericHelper {

    private final HttpClientLoad httpClient;
    private final LogsManager log;
    
    @Autowired
    public GenericHelper( HttpClientLoad httpClient, LogsManager log ) {
        this.httpClient = httpClient;
        this.log = log;
    }    

    public ResponseEntity<?> getService( String url, MultiValueMap<String, String> paramsApigee, Class<?> objectClass )
        throws Exception {

        URI uri = UriComponentsBuilder.fromUriString( url ).queryParams( paramsApigee ).build().toUri();

        return getService( uri, objectClass );
    }

    public ResponseEntity<?> getService( String url, String[] paramsApigee, Class<?> objectClass )
        throws Exception {

        URI uri = BasicHelper.buildUri( url, paramsApigee );

        return getService( uri, objectClass );
    }

    public ResponseEntity<?> getService( URI uri, Class<?> objectClass )
        throws Exception {

        ResponseEntity<?> response;

        response = ( ResponseEntity<?> ) this.httpClient.get( uri,
                                                              objectClass,
                                                              MediaType.APPLICATION_JSON,
                                                              Boolean.TRUE );
        
        if( !StringUtils.isEmpty( response ) ) {
            log.trace( "Service " + uri + " response with http status code: " + response.getStatusCode() + ". " +
                       "Response:  " + ( new Gson() ).toJson( response ) );
        }
        else {
            log.warn( "Response is empty: " + response );
        }
        
        return response;
    }

}
