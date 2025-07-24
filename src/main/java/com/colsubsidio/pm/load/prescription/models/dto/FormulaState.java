package com.colsubsidio.pm.load.prescription.models.dto;

public enum FormulaState {
    
    NO_ENTREGADO( 0, "NO ENTREGADO" ),
    ENTREGADO( 1, "ENTREGADO" ),
    ANULADO( 2, "ANULADO" );

    private final Integer idFormulaState;
    private final String name;

    private FormulaState( Integer idFormulaState, String name ) {
        this.idFormulaState = idFormulaState;
        this.name = name;
    }

    public Integer getIdFormulaState() {
        return this.idFormulaState;
    }

    public String getName() {
        return this.name;
    }
}
