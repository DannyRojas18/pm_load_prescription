package com.colsubsidio.pm.load.prescription.utilities.token;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Calendar;
/**
 * Contiene el token de acceso
 * @author Ingeneo
 *
 */
public class TokenLoad {
	
	 /**
     * valor del token de acceso.
     */
    @JsonProperty( "access_token" )
    private String value;

    @JsonProperty( "expires_in" )
    private int expiresIn;
    
    @JsonProperty( "issued_at" )
    private long issuedAt;
    
    private Calendar expireDate;

    public String getValue() {
        return value;
    }

    public void setValue( String value ) {
        this.value = value;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn( int expiresIn ) {        
        this.expiresIn = expiresIn;

        this.expireDate  = Calendar.getInstance();
        this.expireDate.add( Calendar.SECOND, this.expiresIn - 60 );
    }

    public long getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt( long issuedAt ) {
        this.issuedAt = issuedAt;
    }
    
    public boolean isExpired() {
        Calendar now;
        
        now = Calendar.getInstance();
        
        return now.after( this.expireDate );
    }

    @Override
    public String toString() {
        return value;
    }

}
