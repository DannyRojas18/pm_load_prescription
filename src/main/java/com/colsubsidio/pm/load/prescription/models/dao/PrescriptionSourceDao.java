package com.colsubsidio.pm.load.prescription.models.dao;

import com.colsubsidio.pm.load.prescription.models.entities.PrescriptionSource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Repository;

/**
 * Implementa la consulta de las fuentes
 * de datos de prescripciones
 * @author Ingeneo
 *
 */
@Repository
public interface PrescriptionSourceDao extends CrudRepository<PrescriptionSource, Long> {

	@Transactional(readOnly = true)
	PrescriptionSource findBySourceNameAndIsActive(String source_name, boolean is_active);
	
}
