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
public enum ECategory {
    HEALTH_ADDRESING_TYPE_TECHNOLOGY( "HEALTH_ADDRESING_TYPE_TECHNOLOGY" ),
    REPORT_DELIVERY_ORIGIN_PRESCRIPTION( "REPORT_DELIVERY_ORIGIN_PRESCRIPTION" ),
    REPORT_DELIVERY_PHARMA_STATE( "REPORT_DELIVERY_PHARMA_STATE" ),
    REPORT_DELIVERY_STATE_PRESCRIPTION( "REPORT_DELIVERY_STATE_PRESCRIPTION" ),
    REPORT_DELIVERY_TYPE_OPERATION( "REPORT_DELIVERY_TYPE_OPERATION" ),
    PRESCRIPTION_STATUS_NEPS( "PRESCRIPTION_STATUS_NEPS" );

    private final String name;

    private ECategory( String name ) {
        this.name = name;
    }

    public static ECategory getCategoryByName( String name ) {
        if( name == null ) {
            return null;
        }

        return Stream.of( ECategory.values() )
            .filter( category -> category.getName().equals( name ) )
            .findFirst()
            .orElse( null );
    }
}
