package com.colsubsidio.pm.load.prescription.models.dao;

import com.colsubsidio.pm.load.prescription.models.entities.TreatmentNeps;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Interface de implementacion CrudRepository
 *
 * @author Ingeneo
 *
 */
public interface TreatmentNepsDao extends CrudRepository<TreatmentNeps, String> {

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT * " +
                    "FROM PM.TreatmentNeps AS T WITH ( NOLOCK ) " +
                    "WHERE T.IdPrescriptionStatus = ?1 AND " +
                    "      T.AddressingId IS NOT NULL" )
    List<TreatmentNeps> findByTreatmentStatusIdPending( Integer prescriptionStatusId );

    @Query( nativeQuery = true,
            value = "SELECT TOP 1 * " +
                    "FROM PM.TreatmentNeps AS TN WITH ( NOLOCK ) " +
                    "     LEFT JOIN PM.DeliveryNeps AS DN WITH ( NOLOCK ) " +
                    "       ON TN.IdTreatmentNeps = DN.IdTreatmentNeps " +
                    "WHERE TN.MaxDeliveryDate >= ?2 AND TN.MipresId = ?1 " +
                    "ORDER BY TN.DateMaxDelivery Asc" )
    Optional<TreatmentNeps> searchTop1ByPrescriptionIdAndDateMaxDelivery( String number, String requestDate );

    @Query( nativeQuery = true,
            value = "SELECT  TN.* " +
                    "FROM PM.MedicalPrescriptionNeps AS MPN WITH( NOLOCK ) " +
                    "     JOIN PM.TreatmentNeps AS TN  WITH( NOLOCK ) " +
                    "       ON TN.IdMedicalPrescriptionNeps = MPN.IdMedicalPrescriptionNeps " +
                    "WHERE MPN.MipresNumber = ?1 AND TN.MipresId = ?2 " )
    Optional<TreatmentNeps> findByMipresNumberAndPrescriptionId( String mipresNumber, String mipresId );

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT T.* " +
                    "FROM PM.TreatmentNeps T " +
                    "     JOIN PM.IdMedicalPrescriptionNeps MP " +
                    "       ON T.IdMedicalPrescription = MP.IdMedicalPrescription " +
                    "WHERE MP.MipresNumber = :MipresNumber AND " +
                    "      T.DeliveryNumber = :DeliveryNumber AND " +
                    "      ( T.CUM = :CUM OR " +
                    "        PM.ExtractFirstPart( T.CodeTechnology, :Separator ) = " +
                    "          PM.ExtractFirstPart( :CUM, :Separator ) )" )
    public Optional<TreatmentNeps> findTreatmentByMipresDeliveryCum( @Param( "MipresNumber" ) String mipresNumber,
                                                                     @Param( "DeliveryNumber" ) String deliveryNumber,
                                                                     @Param( "CUM" ) String cum,
                                                                     @Param( "Separator" ) String separator );

    Optional<TreatmentNeps> findByNumberPrescriptionAndCum( String mipresNumber, String cum );

    Optional<TreatmentNeps> findByNumberPrescriptionAndMaterialId( String mipresNumber, String materialId );

    @Query( nativeQuery = true,
            value = "SELECT * " +
                    "FROM PM.TreatmentNeps TN WITH ( NOLOCK ) " +
                    "WHERE TN.IdMedicalPrescriptionNeps = :IdMedicalPrescriptionNeps AND " +
                    "      TN.TypeTechnology = :TypeTechnology AND " +
                    "      TN.ConsecutiveTechnology = :ConsecutiveTechnology AND " +
                    "      TN.DeliveryNumber = :DeliveryNumber AND " +
                    "      TN.IdFormulaState <> :IdFormulaState AND " +
                    "      TN.TN.EpsMetadata IS NULL  " )
    List<TreatmentNeps> findTreamentDifferentToState(@Param("IdMedicalPrescriptionNeps") String idMedical,
                                                     @Param("TypeTechnology") String typeTechnology,
                                                     @Param("ConsecutiveTechnology") String consecutiveTechnology,
                                                     @Param("DeliveryNumber") String deliveryNumber,
                                                     @Param("IdFormulaState") Integer idFormulaState );
    
    
    @Query( nativeQuery = true,
            value = "SELECT *" +
                            "FROM PM.TreatmentNeps TN WITH ( NOLOCK ) " +
                            "WHERE TN.IdMedicalPrescriptionNeps = :IdMedicalPrescription AND " +
                            "      TN.IdFormulaState <> :IdFormulaState AND " +
                            "      TN.TN.EpsMetadata IS NULL " )
    List<TreatmentNeps> findTreamentListDifferentToState(@Param( "IdMedicalPrescription" ) String idMedicalPrescription,
                                                     @Param( "IdFormulaState" ) Integer idFormulaState );
    

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = " SELECT T.* " +
                    " FROM  PM.TreatmentNeps AS T " +
                    " JOIN PM.MedicalPrescriptionNeps AS M" +
                    "        ON M.idMedicalPrescriptionNeps = T.idMedicalPrescriptionNeps " +
                    " WHERE M.MipresNumber = :mipresNumber AND T.MipresId = :mipresId" )
    public Optional<TreatmentNeps> findByMipressNumberAndMipresId( @Param( "mipresNumber" ) String mipresNumber,
                                                                   @Param( "mipresId" ) String mipresId );


    @Query( nativeQuery = true,
            value = "SELECT TOP 1 TN.* " +
                    "FROM PM.TreatmentNeps TN WITH ( NOLOCK ) " +
                    "     JOIN PM.MedicalPrescriptionNeps MP " +
                    "       ON TN.IdMedicalPrescriptionNeps = MP.IdMedicalPrescriptionNeps " +
                    "WHERE MP.MipresNumber = :MipresNumber AND " +
                    "      TN.TypeTechnology = :TypeTechnology AND " +
                    "      TN.ConsecutiveTechnology = :ConsecutiveTechnology AND " +
                    "      TN.DeliveryNumber = :DeliveryNumber " )
    Optional<TreatmentNeps> findTreamentNepsEps( @Param( "MipresNumber" ) String mipresNumber,
                                                 @Param( "TypeTechnology" ) String typeTechnology,
                                                 @Param( "ConsecutiveTechnology" ) String consecutiveTechnology,
                                                 @Param( "DeliveryNumber" ) String deliveryNumber );
}
