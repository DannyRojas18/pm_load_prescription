package com.colsubsidio.pm.load.prescription.controller;

import com.colsubsidio.pm.load.prescription.enums.EEps;
import com.colsubsidio.pm.load.prescription.models.services.ValidationFormulaFactory;
import com.colsubsidio.pm.load.prescription.models.services.interfaces.IValidationFormulaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * author: Alberto Palencia <alberto.palencia@ingeneo.com.co>
 * CreateTime: 10/19/2020 Description: Controlador para validar la formula de los controler.
 */
@CrossOrigin( "*" )
@RestController
@RequestMapping( value = "${path.config.version}/${path.config.prescription}" )
public class ValidationFormulaController {

    private final ValidationFormulaFactory validationFormulaFactory;

    @Autowired
    public ValidationFormulaController( ValidationFormulaFactory validationFormulaFactory ) {
        this.validationFormulaFactory = validationFormulaFactory;
    }

    /**
     * Metodo encargado de consultar las formulas sin metadata en la eps 
     * consultando en la tabla treatment las formulas cuya fecha actualización 
     * es menor a los minutos parametrizados y la epsmetadata sea null
     * Solo NEPS y FAMI NOPBS
     */
    @PostMapping( "/validacion/eps/{eps}" )
    public void validation( @PathVariable( "eps" ) String eps ) {
        EEps eeps = EEps.getEListEpsShortname( eps );
        IValidationFormulaService service = this.validationFormulaFactory.create( eeps );
        service.validateFormula();
    }
}
