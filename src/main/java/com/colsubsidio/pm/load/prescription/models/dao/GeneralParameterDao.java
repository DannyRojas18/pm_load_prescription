package com.colsubsidio.pm.load.prescription.models.dao;

import com.colsubsidio.pm.load.prescription.models.entities.GeneralParameter;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Interface de implementacion CrudRepository
 *
 * @author Ingeneo
 */
@Repository
public interface GeneralParameterDao extends CrudRepository<GeneralParameter, Long> {

    String PARAMETER_NAME = "'[WS_CANCEL_DELIVERY_NOTIFICATION_MIPRES, WS_CANCEL_DELIVERY_REPORT_MIPRES, " +
                            "TOKEN_CLIENT_ID, TOKEN_CLIENT_SECRET, TOKEN_URL, WS_REPORT_DELIVERY_ID, WS_DELIVERY_ID]'";

    @Transactional( readOnly = true )
    GeneralParameter findByParameterName( String parameterName );

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT * " +
                    "FROM PM.GeneralParameter " +
                    "WHERE CHARINDEX( CAST( ParameterName AS VARCHAR ), " + PARAMETER_NAME + " ) <> 0 " )
    List<GeneralParameter> findParametersToCancelDelivery();
    
    

}
