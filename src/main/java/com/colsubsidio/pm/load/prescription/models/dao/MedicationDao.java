package com.colsubsidio.pm.load.prescription.models.dao;

import com.colsubsidio.pm.load.prescription.models.entities.Medication;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicationDao extends CrudRepository<Medication, String> {

    @Transactional( readOnly = true )
    public Optional<Medication> findByPrescriptionId( String prescriptionId );

    @Transactional( readOnly = true )
    public Optional<Medication> findByPrescriptionIdAndEpsId( String prescriptionId, Integer epsId );

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT med.Id, med.PrescriptionId, med.EpsId, med.Metadata, med.Timestamp FROM Medication AS med " +
                    "WHERE med.PrescriptionId = ?1 AND " +
                    "dbo.GetJsonStringValue('$.obtenerPrescripcion.formula.preautorizacion', med.MetaData) = ?2 AND " +
                    "med.EpsId = ?3" )
    public Optional<Medication> queryPrescription( String prescriptionId, String preautoriza, Integer epsId );

    @Transactional( readOnly = true )
    public Boolean existsByPrescriptionId( String prescriptionId );

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT DISTINCT M.MetaData FROM PM.Medication AS M " +
                    "INNER JOIN PM.Prescription AS P ON (P.Id = M.PrescriptionId) " +
                    "WHERE P.TypeIdPatient = ?1 " +
                    "AND P.NoIdPatient = ?2 " +
                    "AND P.DateMaxDelivery >= ?3 " +
                    "AND M.EpsId = ?4 " +
                    "AND ((?5 IS NULL) OR (P.IdFormulaState = ?5))" )
    public List<String> findTypeIdAndNumId( String typeId,
                                            String numId,
                                            String dateMaxDelivery,
                                            Integer epsId,
                                            String status );

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT DISTINCT M.MetaData FROM PM.Medication AS M " +
                    "INNER JOIN PM.Prescription AS P ON (P.Id = M.PrescriptionId) " +
                    "WHERE P.TypeIdPatient = ?1 " +
                    "AND P.NoIdPatient = ?2 " +
                    "AND M.NumPreauthorization = ?3 " +
                    "AND P.DateMaxDelivery >= ?4 " +
                    "AND M.EpsId = ?5 " +
                    "AND ((?6 IS NULL) OR (P.IdFormulaState = ?6))" )
    public List<String> findTypeIdAndNumIdAndNumPreauthorization( String typeId,
                                                                  String numId,
                                                                  String numPreauthorization,
                                                                  String dateMaxDelivery,
                                                                  Integer epsId,
                                                                  String status );

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "SELECT DISTINCT M.MetaData FROM PM.Medication AS M " +
                    "INNER JOIN PM.Prescription AS P ON (P.Id = M.PrescriptionId) " +
                    "WHERE M.NumPreauthorization = ?1 " +
                    "AND P.DateMaxDelivery >= ?2 " +
                    "AND M.EpsId = ?3 " +
                    "AND ((?4 IS NULL) OR (P.IdFormulaState = ?4))" )
    public List<String> findNumPreauthorization( String numPreauthorization,
                                                 String dateMaxDelivery,
                                                 Integer epsId,
                                                 String status );

    @Transactional( readOnly = true )
    @Query( nativeQuery = true,
            value = "EXEC PM.SP_UPDATE_METADATA_PRESCRIPTION ?1, ?2" )
    public void updateMetadatabyPrescription( String json,
                                              String noPrescription );

    @Modifying
    @Transactional
    @Query( nativeQuery = true,
            value = "INSERT INTO PM.Medication " +
                    "SELECT " +
                    "		NEWID(), Pres.Id, Med.EpsId, Med.NumPreauthorization, Med.MetaData, NULL, GETDATE() " +
                    "FROM " +
                    "		PM.Prescription Pres " +
                    "		JOIN PM.EPS Eps " +
                    "		  ON Pres.NoIDEPS = Eps.Nit " +
                    "		LEFT JOIN PM.Medication Med " +
                    "		  ON Pres.Id = Med.PrescriptionId " +
                    "WHERE " +
                    "		Med.Id IS NULL AND " +
                    "		Pres.NoIDEPS = ?1 AND " +
                    "		Pres.NoPrescription = ?2 AND " +
                    "		Pres.PrescriptionId = ?3" )
    public void insertMedicationByPrescription( String noIDEPS,
                                                String noPrescription,
                                                String PrescriptionId );

}
