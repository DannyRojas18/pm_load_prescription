package com.colsubsidio.pm.load.prescription.utilities;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.colsubsidio.pm.load.prescription.models.dao.EpsDao;
import com.colsubsidio.pm.load.prescription.models.entities.Eps;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class Utils {
	
	private final EpsDao epsRepository;
	 /**
     * Metodo encargado de obtener la eps por el codigo de aseguradora
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0
     * @param codeInsurance
     * @return Devuelve la eps si la encuentra de lo contrario null
     */
    public Eps getEpsToInsurance(final String codeInsurance) {
    	log.trace("Inicio metodo getEpsToInsurance: {} ", codeInsurance);
    	Optional<Eps> eps = this.epsRepository.findByCodigoAseguradora(codeInsurance);
    	if(eps.isPresent()) {
    		log.trace("Fin metodo getEpsToInsurance: {} ", eps.get());
    		return eps.get();
    	}
    	log.trace("Fin metodo getEpsToInsurance NO ENCONTRADA ");
    	return null;
    }
}
