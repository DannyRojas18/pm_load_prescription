package com.colsubsidio.pm.load.prescription.enums;

import lombok.Getter;

@Getter
public enum EPrescriptionStatus {

    CONSULTADA( 0, "Aun sin leer de direccionamiento o estado inicial" ),
    DIRECCIONADA( 1, "La prescripcion fue leida de mipres" ),
    VALIDADA_EPS( 2, "La prescripcion fue leida de mipres" ),
    PROGRAMADA_SEDE( 3, "La prescripcion se programo" ),
    PROGRAMACION_REPORTADA( 4, "La programacion de la prescripcion fue notificada a mipres" ),
    ENTREGADA_DISPENSADOR( 5, "La prescripcion fue entregada en el dispensador" ),
    ENTREGA_NOTIFICADA( 6, "La entrega de la prescripcion fue notificada a mipres" ),
    REPORTE_ENTREGA( 7, "Ya se notifico a mipres que la prescripcion fue entregada" ),
    ANULADA( 8, "Indica que la prescripcion fue anulada" ),
    FALLO_PROGRAMACION_SEDE( 9, "Fallo en Programacion Sede Estado 3" ),
    FALLO_ENTREGA_REPORTE_MIPRES( 10, "Fallo en Entrega reportada MIPRE Estado 6" ),
    PRESCRIPCION_INVALIDA( 11, "Prescripcion No Pertenece a Ninguna EPS" ),
    FACTURA_ENTREGADA( 12, "El dispensador ha reportado la factura" ),
    FACTURA_REPORTADA( 13, "La factura ha sido reportada a MIPRES" ),
    CONSULTA_SURA( 14, "La prescripcion fue leida de SURA" ),
    REPORTE_DESPACHO_SURA( 15, "Reporte despacho SURA" ),
    DIRECCIONAMIENTO_SALUD( 16, "Reporte Direccionamiento SALUD" ),
    REPORTE_DESPACHO_FARMADOMICILIOS( 17, "Reporte despacho FARMADOMICILIOS" ),
    COLA_SURA_PRUEBAS( 18, "Cola de pruebas Sura" );

    private Integer id;
    private String description;

    EPrescriptionStatus( Integer id, String description ) {
        this.id = id;
        this.description = description;
    }
}
