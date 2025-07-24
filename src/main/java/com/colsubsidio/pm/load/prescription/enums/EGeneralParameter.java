package com.colsubsidio.pm.load.prescription.enums;
import lombok.Getter;

/**
 * @author: Alberto Palencia <alberto.palencia@ingeneo.com.co>
 * CreateTime: 10/20/2020
 * Description: Enumeracion que tiene los parametros de general parameter
 */
@Getter
public enum EGeneralParameter {

    ATTEMPT_MINUTES_MAX("ATTEMPT_MINUTES_MAX"),
    NEPS_WS("NEPS_WS_1");

    private String name;

    EGeneralParameter(String name) {
        this.name = name;
    }
}
