/*
 * Codigo fuente propiedad de Colsubsidio
 * Gerencia de Tecnologia
 */
package com.colsubsidio.pm.load.prescription.enums;

/**
 *
 * @author Robert Barraza
 */
public enum EStatus {
    INITIAL( 0 ),
    PROCESS( 1 ),
    ERROR( 2 );


    private final Integer value;

    private EStatus( int value ) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
