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
public enum EProcessState {
    UNPROCESSED( 0, "Unprocessed" ),
    PROCESSED( 1, "Processed" ),
    FAILED( 2, "Failed" );

    private final Integer id;
    private final String name;

    private EProcessState( Integer id, String name ) {
        this.id = id;
        this.name = name;
    }

    public static EProcessState getProcessStateById( Integer id ) {
        if( id == null ) {
            return null;
        }

        return Stream.of( EProcessState.values() )
            .filter( processState -> processState.getId().equals( id ) )
            .findFirst()
            .orElse( null );
    }
}
