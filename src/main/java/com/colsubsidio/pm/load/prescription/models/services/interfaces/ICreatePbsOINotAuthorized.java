package com.colsubsidio.pm.load.prescription.models.services.interfaces;

import java.util.List;

import com.colsubsidio.pm.load.prescription.enums.EEps;
import com.colsubsidio.pm.load.prescription.models.dto.RequestReceiveEps;

/**
 * 
 * @author dchavarro
 * @version 1.0
 * Interface encargada de definir las firmas de los metodos
 *
 */
public interface ICreatePbsOINotAuthorized {
	
	/**
     * Metodo encargado de resivir la prescripciones de otros canales
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0
     * @param eps Enum de las eps
     * @param List<RequestReceiveEps>
     */
	void receivePrescription( EEps eps, List<RequestReceiveEps> requestReceiveEpsList );
}
