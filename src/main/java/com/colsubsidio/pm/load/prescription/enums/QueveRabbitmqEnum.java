
package com.colsubsidio.pm.load.prescription.enums;

import lombok.Getter;

/**
 *
 * @author Nathaly Gutierrez
 */
@Getter
public enum QueveRabbitmqEnum {

    /**
     *
     */
    PRESCRIPCION_PBS (0, "PRESCRIPCION_PBS ");

    private final Integer idQueve;
    private final String name;

    QueveRabbitmqEnum(Integer idFormulaState, String name) {
        this.idQueve = idFormulaState;
        this.name = name;
    }


}
