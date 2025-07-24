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
public enum EFieldValidatorState {
    INACTIVE( 0, "Inactive" ),
    ACTIVE( 1, "Active" );

    private final Integer id;
    private final String name;

    EFieldValidatorState( Integer id, String name ) {
        this.id = id;
        this.name = name;
    }

    public static EFieldValidatorState getFieldValidatorStateById( Integer id ) {
        if( id == null ) {
            return null;
        }

        return Stream.of( EFieldValidatorState.values() )
            .filter( fieldValidatorState -> fieldValidatorState.getId().equals( id ) )
            .findFirst()
            .orElse( null );
    }
}
