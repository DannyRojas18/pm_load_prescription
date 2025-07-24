package com.colsubsidio.pm.load.prescription.enums;
import lombok.Getter;

/**
 * @author: Alberto Palencia <alberto.palencia@ingeneo.com.co>
 * CreateTime: 10/15/2020
 * Description:
 */

@Getter
public enum ENumber {
    ZERO(0);

    private Integer id;

    ENumber(Integer id) {
        this.id = id;
    }
}
