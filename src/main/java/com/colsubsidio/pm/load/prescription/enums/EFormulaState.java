package com.colsubsidio.pm.load.prescription.enums;

import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum EFormulaState {

    NO_ENTREGADO( 0, "NO ENTREGADO" ),
    ENTREGADO( 1, "ENTREGADO" ),
    ANULADO( 2, "ANULADO" );

    private final Integer id;
    private final String name;

    EFormulaState( Integer id, String name ) {
        this.id = id;
        this.name = name;
    }

    public static EFormulaState getFormulaStateById( Integer id ) {
        if( id == null ) {
            return null;
        }

        return Stream.of( EFormulaState.values() )
                .filter( state -> state.getId().equals( id ) )
                .findFirst()
                .orElse( null );
    }
}
