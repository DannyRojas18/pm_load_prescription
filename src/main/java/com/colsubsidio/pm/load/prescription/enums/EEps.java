package com.colsubsidio.pm.load.prescription.enums;

import lombok.Getter;

/**
 * Lista de tipos de log que maneja la aplicacion
 *
 * @author Ingeneo
 *
 */
@Getter
public enum EEps {
    NEPS( "900156264", "Nueva EPS", 1, "NEPS", "PRESCRIPTION_PBS_NEPS" ),
    FAMI( "830003564", "Famisanar", 2, "FAMI", "PRESCRIPTION_PBS_FAMI" ),
    COOM( "805000427", "Coomeva", 3, "COOM", "PRESCRIPTION_PBS_COOM" ),
    OI( "899999063", "Otro: Universidad nacional", 5, "OI", "PRESCRIPTION_PBS_OI" );

    private final String nit;
    private final String name;
    private final Integer idEps;
    private final String shortName;
    private final String queuePrescripcionPbs;

    EEps( String nit, String name, Integer epsId, String shortName, String queuePrescripcionPbs ) {
        this.nit = nit;
        this.name = name;
        this.idEps = epsId;
        this.shortName = shortName;
        this.queuePrescripcionPbs = queuePrescripcionPbs;
    }

    public static EEps getEListEpsShortname( String eps ) {
        for( EEps EListEps : EEps.values() ) {
            if( EListEps.getShortName().equalsIgnoreCase( eps ) ) {
                return EListEps;
            }
        }

        return null;
    }
}
