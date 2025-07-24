package com.colsubsidio.pm.load.prescription.utilities.token;

import com.colsubsidio.pm.load.prescription.models.dao.GeneralParameterDao;
import com.colsubsidio.pm.load.prescription.models.entities.GeneralParameter;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Scope
public class TokenSingletonConsult{

    private static final Logger LOGGER = LoggerFactory.getLogger(TokenSingletonConsult.class );
    
    private String uRLToken;
    
    private String tokenKey;
    
    private String tokenSecret;
    
    private Integer timeout;
    
    private TokenLoad token;
    
    @Autowired
    private GeneralParameterDao parameter;

    /**
     * Obtiene el TokenLoad de Apigee.
     *
     * @return El TokenLoad de Apigee.
     * @throws URISyntaxException Error en la invocación del servicio.
     */
    public TokenLoad getToken()
            throws URISyntaxException{
        
        if( this.token == null || this.token.isExpired() ){
            
            Iterable<GeneralParameter> gp;
            
            gp = parameter.findAll();
            
            for( GeneralParameter generalParameter : gp ){               
                                
                switch( generalParameter.getParameterName() ){
                    case "TOKEN_URL":
                        this.uRLToken = generalParameter.getParameterValue();
                        break;
                    case "TOKEN_CLIENT_ID":
                        this.tokenKey = generalParameter.getParameterValue();
                        break;
                    case "TOKEN_CLIENT_SECRET":
                        this.tokenSecret = generalParameter.getParameterValue();
                        break;
                    case "TIMEOUT_MAX":
                        this.timeout = Integer.parseInt( generalParameter.getParameterValue() );
                        break;
                    default:
                        break;
                }
            }
            
            LOGGER.debug( "Se generara nuevo token." );
            this.token = generateToken();
            LOGGER.debug( "Se genero nuevo token." );
        }
        
        return this.token;
    }

    /**
     * Genera el TokenLoad de Apigee.
     *
     * @return La información del TokenLoad.
     * @throws URISyntaxException Error para obtener el TokenLoad.
     */
    private TokenLoad generateToken()
            throws URISyntaxException{
        RestTemplate restTemplate;
        
        URI tokenRestURI;
        
        HttpHeaders headers;
        
        HttpEntity<String> request;
        
        ResponseEntity<TokenLoad> response;
        
        restTemplate = getRestTemplateTokenAuthentication();
        
        tokenRestURI = new URI( this.uRLToken );
        
        headers = new HttpHeaders();
        headers.add( "content-type", "application/x-www-form-urlencoded" );
        headers.add( "accept", "application/x-www-form-urlencoded" );
        
        request = new HttpEntity<>( headers );
        
        response = restTemplate.exchange(tokenRestURI, HttpMethod.POST,
                                          request, TokenLoad.class );
        
        if( response == null ){
            throw new IllegalStateException( "GenerateAPIGeeToken no se pudo generar token." );
        }
        
        return response.getBody();
    }

    /**
     * Obtiene el <code>RestTemplate</code> con los encabezados para la
 obtención del TokenLoad ya cargados.
     *
     * @return El <code>RestTemplate</code> para invocar la obtención del TokenLoad.
     */
    private RestTemplate getRestTemplateTokenAuthentication(){
        RestTemplate restTemplate;
        
        RestTemplateBuilder builder;
        
        int timeOut;
        
        builder = new RestTemplateBuilder();
        
        timeOut = ( this.timeout * 60 ) * 1000;
        
        restTemplate = builder
                .setReadTimeout( Duration.ofMillis( timeOut ) )
                .setConnectTimeout( Duration.ofMillis( timeOut ) )
                .basicAuthentication( this.tokenKey, this.tokenSecret )
                .build();
        
        return restTemplate;
    }
}
