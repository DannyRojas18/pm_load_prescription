package com.colsubsidio.pm.load.prescription.enums;
import lombok.Getter;

/**
 * @author: Alberto Palencia <alberto.palencia@ingeneo.com.co>
 * CreateTime: 11/6/2020
 * Description: Enumeracion para validar si tiene null o no
 */

@Getter
public enum EValidationFormula {
    AUTHORIZATION_NULL("null");
    
    private String name;
    
    EValidationFormula( String name ) {
        this.name = name;
    }
}
