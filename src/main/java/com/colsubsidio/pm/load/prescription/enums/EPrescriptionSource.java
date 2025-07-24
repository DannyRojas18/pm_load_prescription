/*
 * Codigo fuente propiedad de Colsubsidio
 * Gerencia de Tecnologia
 */
package com.colsubsidio.pm.load.prescription.enums;

import java.util.stream.Stream;
import lombok.Getter;

/**
 *
 * @author Robert Barraza
 */
@Getter
public enum EPrescriptionSource {
    MIPRES( 1, "MIPRES" ),
    SURA_MIPRES( 2, "SURA_MIPRES" ),
    FAMI( 3, "FAMISANAR" ),
    COOM( 4, "COOMEVA" ),
    NEPS( 5, "NUEVA EPS" ),
    OI( 6, "OI" );

    private final Integer id;
    private final String name;

    private EPrescriptionSource( Integer id, String name ) {
        this.id = id;
        this.name = name;
    }

    public static EPrescriptionSource getPrescriptionSourceById( Integer id ) {
        if( id == null ) {
            return null;
        }

        return Stream.of( EPrescriptionSource.values() )
            .filter( prescriptionSource -> prescriptionSource.getId().equals( id ) )
            .findFirst()
            .orElse( null );
    }
}
