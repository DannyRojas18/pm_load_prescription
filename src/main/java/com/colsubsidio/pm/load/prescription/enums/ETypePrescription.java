package com.colsubsidio.pm.load.prescription.enums;

import lombok.Getter;

/**
 * Implementa la lista de los tipos de prescripciones manejados por prescription manager
 *
 * @author Ingeneo
 *
 */
@Getter
public enum ETypePrescription {
    PBS( 1, "PBS" ),
    NOPBS( 2, "NO PBS" );

    private final Integer id;
    private final String name;

    ETypePrescription( Integer id, String name ) {
        this.id = id;
        this.name = name;
    }
}
