package com.colsubsidio.pm.load.prescription.models.dao;

import com.colsubsidio.pm.load.prescription.models.dto.Prescription;
import com.colsubsidio.pm.load.prescription.enums.EPrescriptionStatus;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import java.sql.SQLException;
import org.springframework.stereotype.Repository;

/**
 * Interface de implementacion CrudRepository
 *
 * @author Ingeneo
 *
 */
@Repository
public interface PrescriptionDao extends CrudRepository<Prescription, String> {

    @Transactional( readOnly = true )
    public List<Prescription> findByNoPrescription( String noPrescription );

    @Transactional( readOnly = true )
    public List<Prescription> findByPrescriptionStatusId( EPrescriptionStatus prescriptionStatus );
    
    
    @Transactional( readOnly = true )
    public List<Prescription> findByPrescriptionStatusIdAndNoPrescriptionIsNotNull( EPrescriptionStatus prescriptionStatus );
    
    @Transactional( readOnly = true )
    public Optional<Prescription> findTop1ByNoPrescriptionAndAddressingIdIsNull( String noPrescription );

    @Transactional( readOnly = true )
    public Boolean existsByNoPrescription( String noPrescription );

    @Transactional( readOnly = true )
    public Boolean existsByPrescriptionId( String prescriptionId );

    @Transactional( readOnly = true )
    public Optional<Prescription> findTop1ByNoPrescription( String noPrescription );

    @Transactional( readOnly = true )
    public Optional<Prescription> findByNoPrescriptionEps( String noPrescriptionEps );

    @Transactional( readOnly = true )
    public List<Prescription> findByCreateDateBetweenAndNoPrescriptionAndTypeIdPatientAndNoIdPatient( Date start,
                                                                                                      Date end,
                                                                                                      String noPrescription,
                                                                                                      String typeIdPatient,
                                                                                                      String noIdPatient );

    @Transactional( readOnly = true )
    public Optional<Prescription> findByPrescriptionIdAndPrescriptionStatusId( Long prescription_id, EPrescriptionStatus status );

    @Transactional( readOnly = true )
    public Optional<Prescription> findByPrescriptionId( String prescription_id );

    @Transactional( readOnly = true )
    public Optional<Prescription> findByPrescriptionIdAndNoIdEps( Long prescription_id, String noIdEps );

    @Transactional( readOnly = true )
    public Iterable<Prescription> findByCreateDateBetweenAndNoIdEps( Date start, Date end, String nit );
    
    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT * " +
                    "FROM PM.Prescription AS P WITH ( NOLOCK ) " +
                    "WHERE P.PrescriptionStatusId = ?1 AND " +
                    "      P.NoPrescription IS NOT NULL AND " +
                    "      P.NoPrescription <> ''" )
    public List<Prescription> findByPrescriptionStatusIdPending( Integer prescriptionStatusId );
    
    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT TOP 1 P.* " +
                    "FROM PM.Prescription AS P WITH ( NOLOCK ) " +
                    "WHERE P.NoPrescription = ?1 AND " +
                    "      P.TypeTechnology = ?2 AND " +
                    "      P.DeliveryNumber = ?3 AND " +
                    "      P.ConsecutiveTechnology = ?4 AND " +
                    "      P.IdFormulaState <> ?5" )
    public Optional<Prescription> findPrescriptionWithoutState( String noPrescripcion, String tipoTec, String noEntrega,
                                                                String conTec, Integer formulaState );

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT TOP 1 P.* " +
                    "FROM PM.Prescription AS P WITH ( NOLOCK ) " +
                    "WHERE P.NoPrescription = ?1 AND " +
                    "      P.TypeTechnology = ?2 AND " +
                    "      P.DeliveryNumber = ?3 AND " +
                    "      P.ConsecutiveTechnology = ?4" )
    public Optional<Prescription> findPrescriptionPending( String noPrescripcion, String tipoTec, String noEntrega,
                                                           String conTec );

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT P.* " +
                    "FROM PM.Prescription AS P " +
                    "     JOIN PM.EPS AS E " +
                    "       ON P.NoIDEPS = E.Nit AND " +
                    "          E.Id = ?1 " +
                    "     LEFT JOIN PM.Medication AS M " +
                    "       ON P.Id = M.PrescriptionId " +
                    "WHERE M.Id IS NULL AND " +
                    "      P.Timestamp >= DATEADD( MINUTE, ?2 * -1, ?3 )" )
    public Iterable<Prescription> findPrescriptionPendingForProcess( Integer epsId,
                                                                     Integer minutes,
                                                                     Date currentDate )
        throws SQLException;

    @Transactional( readOnly = true )
    public Iterable<Prescription> findByNoIdPatient( String noIdPatient );

    @Transactional( readOnly = true )
    public Iterable<Prescription> findByNoIdEps( String noIdEps );

    @Transactional( readOnly = true )
    public Iterable<Prescription> findByNoIdPatientAndNoIdEps( String noIdPatient, String noIdEps );

    @Transactional( readOnly = true )
    public Iterable<Prescription> findByCreateDateBetween( Date start, Date end );

    @Transactional( readOnly = true )
    public Iterable<Prescription> findByCreateDateBetweenAndNoIdPatientAndNoIdEps( Date start, Date end, String noIdPatient, String noIdEps );

    @Transactional( readOnly = true )
    public Iterable<Prescription> findByCreateDateBetweenAndNoIdPatient( Date start, Date end, String noIdPatient );

    @Transactional( readOnly = true )
    public Iterable<Prescription> findTop500ByOrderByCreateDateDesc();

    @Transactional( readOnly = true )
    @Query( nativeQuery = true, value = "SELECT TOP 1 Pres.* " +
                                        "FROM PM.Prescription AS Pres " +
                                        "	LEFT JOIN PM.PrescriptionDelivery AS PD ON Pres.Id = PD.PrescriptionId " +
                                        "WHERE Pres.DateMaxDelivery >= ?2 AND Pres.NoPrescription = ?1 AND PD.PrescriptionId IS NULL " +
                                        "ORDER BY Pres.DateMaxDelivery Asc" )
    public Optional<Prescription> searchTop1ByNoPrescriptionAndDateMaxDelivery( String noprescription, String datemax );

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT TOP 1 P.* FROM PM.Medication AS M " +
                    "INNER JOIN PM.Prescription AS P ON (P.Id = M.PrescriptionId) " +
                    "WHERE P.TypeIdPatient = ?1 " +
                    "AND P.NoIdPatient = ?2 " +
                    "AND P.DateMaxDelivery >= ?3 " +
                    "AND M.EpsId = ?4" )
    public Optional<Prescription> findTypeIdAndNumId( String typeId,
                                                      String numId,
                                                      String dateMaxDelivery,
                                                      Integer epsId );

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT TOP 1 P.* FROM PM.Medication AS M " +
                    "INNER JOIN PM.Prescription AS P ON (P.Id = M.PrescriptionId) " +
                    "WHERE P.TypeIdPatient = ?1 " +
                    "AND P.NoIdPatient = ?2 " +
                    "AND M.NumPreauthorization = ?3 " +
                    "AND P.DateMaxDelivery >= ?4 " +
                    "AND M.EpsId = ?5" )
    public Optional<Prescription> findTypeIdAndNumIdAndNumPreauthorization( String typeId,
                                                                            String numId,
                                                                            String numPreauthorization,
                                                                            String dateMaxDelivery,
                                                                            Integer epsId );

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT TOP 1 P.* FROM PM.Medication AS M " +
                    "INNER JOIN PM.Prescription AS P ON (P.Id = M.PrescriptionId) " +
                    "WHERE M.NumPreauthorization = ?1 " +
                    "AND P.DateMaxDelivery >= ?2 " +
                    "AND M.EpsId = ?3" )
    public Optional<Prescription> findNumPreauthorization( String numPreauthorization,
                                                           String dateMaxDelivery,
                                                           Integer epsId );
    
    @Transactional( readOnly = true )
    @Query( nativeQuery = true, value = "SELECT TOP 1 Pres.* " +
                                        "FROM PM.Prescription AS Pres " +
                                        "	LEFT JOIN PM.PrescriptionDelivery AS PD ON Pres.Id = PD.PrescriptionId " +
                                        "WHERE Pres.DateMaxDelivery >= ?2 AND Pres.PrescriptionId = ?1  " +
                                        "ORDER BY Pres.DateMaxDelivery Asc" )
    public Optional<Prescription> searchTop1ByPrescriptionIdAndDateMaxDelivery( String prescriptionId, String datemax );
}
