package com.colsubsidio.pm.load.prescription.models.services;
import org.springframework.stereotype.Component;

import com.colsubsidio.pm.load.prescription.enums.EEps;
import com.colsubsidio.pm.load.prescription.models.services.interfaces.IValidationFormulaService;

import lombok.AllArgsConstructor;

/**
 * @author: Alberto Palencia <alberto.palencia@ingeneo.com.co>
 * CreateTime: 10/19/2020
 * Description: Clase manejador de eps.
 */

@AllArgsConstructor
@Component
public class ValidationFormulaFactory {


    private final ValidationFormulaNepsService newEps;
    
    private final ValidationFormulaFamiService fami;
    
    private final ValidationFormulaCoomService coom;

    /**
     * Metodo para crear una instancia de la clase que implementa a esta interfaz
     * @param eps parametro de la eps
     * @return IValidationFormulaService nueva instancia
     */
    public IValidationFormulaService create( EEps eps ) {
        switch( eps ) {
            case NEPS:
                return this.newEps;
            case FAMI:
                return this.fami;
            case COOM:
                return  this.coom;
            default:
                return null;
        }
    }
}
