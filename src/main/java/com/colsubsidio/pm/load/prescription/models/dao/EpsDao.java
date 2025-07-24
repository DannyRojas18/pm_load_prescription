package com.colsubsidio.pm.load.prescription.models.dao;

import com.colsubsidio.pm.load.prescription.models.entities.Eps;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.stereotype.Repository;

/**
 * Interface de implementacion CrudRepository
 * @author Ingeneo
 *
 */
@Repository
public interface EpsDao extends CrudRepository<Eps, Integer> {
	
	@Transactional(readOnly = true)
	public Iterable<Eps> findAllByOrderByOrderAsc();
	
	@Transactional(readOnly = true)
	public Optional<Eps> findByNit(String nit);
	
	@Transactional(readOnly = true,  isolation = Isolation.READ_UNCOMMITTED)
	public Optional<Eps> findByCodigoAseguradora(String codigoAseguradora);

}
