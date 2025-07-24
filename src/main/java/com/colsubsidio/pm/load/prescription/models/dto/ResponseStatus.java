package com.colsubsidio.pm.load.prescription.models.dto;

/**
 *
 * @author Alberto Palencia
 */
public  class  ResponseStatus {
    
    public static final String SUCCESS = "200";    
    public static final String INTERNAL_ERROR = "500";    
    public static final String MESSAGE_SUCCESS = "formula registrada correctamente";    
    public static final String INTERNAL_ERROR_MESSAGE = "Se presento un error ";    
    public static final String NOFOUND = "404";
    public static final String EPSNOFOUND = "No se encontro el codigo de la aseguradora ";
          
}
