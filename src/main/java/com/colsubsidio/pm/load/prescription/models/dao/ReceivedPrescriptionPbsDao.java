/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.colsubsidio.pm.load.prescription.models.dao;

import com.colsubsidio.pm.load.prescription.models.entities.ReceivedPrescriptionPbs;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Nathaly Gutierrez
 */
@Repository
public interface ReceivedPrescriptionPbsDao extends CrudRepository<ReceivedPrescriptionPbs, String> {
    Optional<ReceivedPrescriptionPbs> findByIdAndState( String id, int state );
}