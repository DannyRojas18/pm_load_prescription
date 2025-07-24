package com.colsubsidio.pm.load.prescription.models.dao;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;

import com.colsubsidio.pm.load.prescription.models.entities.FieldValidator;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FieldValidatorDao extends CrudRepository<FieldValidator, String> {

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT TOP 1 Field.* " +
                    "FROM PM.FieldValidator Field " +
                    "WHERE Field.[Values] = :ValueField AND " +
                    "	   Field.Category = :Category AND " +
                    "      Field.State = :State" )
    public Optional<FieldValidator> findValuesByCategory( @Param( "ValueField" ) String valueField,
                                                          @Param( "Category" ) String category,
                                                          @Param( "State" ) Integer state );

}
