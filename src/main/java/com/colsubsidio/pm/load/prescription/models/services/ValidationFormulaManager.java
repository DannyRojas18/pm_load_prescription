package com.colsubsidio.pm.load.prescription.models.services;

import com.colsubsidio.pm.load.prescription.helpers.BasicHelper;
import com.colsubsidio.utilities.miscellaneous.commons.models.TokenRequest;
import com.colsubsidio.utilities.miscellaneous.http.client.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import java.net.URI;
import lombok.Getter;

/**
 * Author: Alberto Palencia <alberto.palencia@ingeneo.com.co>
 * CreateTime: 10/20/2020 Description: Clase para administrar los metodos comunes de validacion formula
 */
@Component
@Getter
public class ValidationFormulaManager {

    @Value( "${colsubsidio.token.url}" )
    private String tokenUrl;

    @Value( "${colsubsidio.token.timeout}" )
    private String timeout;

    @Value( "${colsubsidio.token.clientid}" )
    private String clientId;

    @Value( "${colsubsidio.token.clientsecret}" )
    private String clientSecret;

    @Value( "${neps.additionaldays}" )
    private Integer additionalDaysNeps;
    
    @Value( "${neps.attempt.minutesmax}" )
    private Integer attemptMinutesMax;
    
    @Value( "${fami.attempt.minutesmax}" )
    private Integer attemptMinutesMaxFami;
    
    @Value( "${coom.attempt.minutesmax}" )
    private Integer attemptMinutesMaxCoom;

    @Value( "${neps.serviceurl}" )
    private String serviceUrl;
    
    @Value( "${fami.serviceurl}" )
    private String serviceUrlFami;
    
    @Value( "${coom.serviceurl}" )
    private String serviceUrlCoom;
    
    @Value( "${oi.serviceurl}" )
    private String serviceUrlOI;
    
    @Value( "${oi.attempt.minutesmax}" )
    private Integer attemptMinutesMaxOI;

    private final HttpClient httpClient;

    public ValidationFormulaManager( HttpClient httpClient ) {
        this.httpClient = httpClient;
    }

    /**
     * Consume el servicio RestFull perteneciente a la EPS para obtener los medicamentos relacionados a la prescripcion
     *
     * @param url
     * @param mipresNumber
     * @return retorna el body de la respuesta del servicio
     * @throws HttpClientErrorException
     */
    public String consumingService( String url, String mipresNumber )
        throws HttpClientErrorException {
        URI uri;
    
        uri = BasicHelper.buildUri( url, mipresNumber );

        TokenRequest tr = tokenRequest();
    
        ResponseEntity<?> response = this.httpClient.get( uri, String.class, MediaType.APPLICATION_JSON,
                                                          Boolean.TRUE, tr );

        return (String) response.getBody();
    }
    

    public TokenRequest tokenRequest() {
        return TokenRequest.builder()
            .tokenURL( this.tokenUrl )
            .clientId( this.clientId )
            .clientSecret( this.clientSecret )
            .timeout( this.timeout )
            .build();
    }
}
