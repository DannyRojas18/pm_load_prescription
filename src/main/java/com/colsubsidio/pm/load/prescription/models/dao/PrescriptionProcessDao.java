package com.colsubsidio.pm.load.prescription.models.dao;

import com.colsubsidio.pm.load.prescription.models.entities.GetPrescriptionProcess;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PrescriptionProcessDao extends CrudRepository<GetPrescriptionProcess, String> {

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT TOP ( ?2 ) ED.* " +
                    "FROM PM.getaddressingtemp ED WITH ( NOLOCK ) " +
                    "WHERE ED.Status = ?1" )
    public Iterable<GetPrescriptionProcess> findByStatus( Integer status, Long amount );

    @Modifying
    @Transactional
    @Query( nativeQuery = true,
            value = "UPDATE PM.getaddressingtemp " +
                    "SET [status] = ?1 " +
                    "WHERE [mipres] = ?2 " )
    void updateByIdSetState( String state,
                                           String mipres );
}
