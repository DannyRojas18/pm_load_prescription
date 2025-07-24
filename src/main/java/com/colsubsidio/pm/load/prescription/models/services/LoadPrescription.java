/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.colsubsidio.pm.load.prescription.models.services;

import com.colsubsidio.pm.load.prescription.enums.EEps;
import com.colsubsidio.pm.load.prescription.enums.EFormulaState;
import com.colsubsidio.pm.load.prescription.enums.EPrescriptionStatus;
import com.colsubsidio.pm.load.prescription.enums.ETypePrescription;
import com.colsubsidio.pm.load.prescription.helpers.BasicHelper;
import com.colsubsidio.pm.load.prescription.models.dao.*;
import com.colsubsidio.pm.load.prescription.models.dto.Prescription;
import com.colsubsidio.pm.load.prescription.models.entities.Eps;
import com.colsubsidio.pm.load.prescription.models.entities.Medication;
import com.colsubsidio.pm.load.prescription.models.entities.PrescriptionSource;
import com.colsubsidio.pm.load.prescription.models.entities.ReceivedPrescriptionPbs;
import com.colsubsidio.pm.load.prescription.models.services.interfaces.ILoadPrescription;
import com.colsubsidio.pm.load.prescription.utilities.JsonUtil;
import com.colsubsidio.utilities.log.LogsManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;


@Service
public class LoadPrescription implements ILoadPrescription {

    @Autowired
    private PrescriptionSourceDao sourceDao;

    @Autowired
    private PrescriptionDao prescriptionDao;

    @Autowired
    private EpsDao epsDao;

    @Autowired
    private MedicationDao medicationDao;

    @Autowired
    private ReceivedPrescriptionPbsDao receivedPrescriptionPbsDao;

    @Autowired
    private LogsManager log;

    @Override
    public void savePrescription( String idReceivedPbs ) {

        ReceivedPrescriptionPbs receivedPrescriptionPbs;

        JsonArray jsonObject;

        String jsonMetadata;
        try {
            Optional<ReceivedPrescriptionPbs> optionalReceivePres =
            			this.receivedPrescriptionPbsDao.findByIdAndState(idReceivedPbs, 0 );
            
            
            log.info( "Recibe id {} para iniciar el proceso de la prescripcion", idReceivedPbs );

            log.trace( "Consulta id {} en la tabla de ReceivedPrescriptionPbs", idReceivedPbs, optionalReceivePres );
            if( optionalReceivePres.isPresent() ) {
                receivedPrescriptionPbs = optionalReceivePres.get();
                EEps eEps = EEps.valueOf(receivedPrescriptionPbs.getEpsId().toUpperCase());
                try {

                    log.trace( "Se encuentra el id {} en la tabla de ReceivedEps con la informacion {}",
                               idReceivedPbs,
                               receivedPrescriptionPbs );
                    jsonObject = new Gson().fromJson(receivedPrescriptionPbs.getMetadata(), JsonArray.class);
                    jsonMetadata = BasicHelper.toJson( jsonObject );
                    
                    System.out.println(jsonObject.toString());
                    log.debug( "Prescripcion a procesar JSON: {} ", jsonMetadata );

                    PrescriptionSource source = sourceDao.findBySourceNameAndIsActive( eEps.getShortName(), true );

                    if( source != null ) {
                    	
                    	processPrescripcion( jsonMetadata, source, eEps );
                        receivedPrescriptionPbs.setState( 1 );
                        receivedPrescriptionPbsDao.save( receivedPrescriptionPbs );
                        
                    }

                }
                catch( Exception e ) {
                    log.error( "Se ha presentado una excepcion ", e );
                    receivedPrescriptionPbs.setState( 2 );
                    receivedPrescriptionPbsDao.save( receivedPrescriptionPbs );
                }
            }
        }
        catch( Exception ex ) {
            log.error( "Se ha presentado una excepcion ", ex );
        }
    }

    private void processPrescripcion( String metadata,
                                                PrescriptionSource source, EEps eEps)
        throws Exception, UnsupportedOperationException {
        JsonArray prescriptionList = new Gson().fromJson(metadata, JsonArray.class);
        String mipres;

        for( JsonElement prescription : prescriptionList ) {

            JsonElement formula = prescription.getAsJsonObject().get( "formula" );
            JsonElement paciente = prescription.getAsJsonObject().get( "paciente" );
            JsonArray tratamiento = formula.getAsJsonObject().get( "tratamiento" ).getAsJsonArray();

            if( tratamiento != null && tratamiento.size() > 0 ) {

                mipres = JsonUtil.parse(formula, "mipres" );
                if ( !StringUtils.isEmpty( mipres ) 
                		&& ( !mipres.equals( "null" ) 
                				&& !mipres.equals( "Null" ) 
                				&& !mipres.equals( "NULL" ) ) ) {
                	
                	log.trace( "Guardando prescription. MIPRES ", mipres );
                    this.savePrescriptionNOPBS( new Prescription(),
                                                        source,
                                                        mipres,
                                                        paciente,
                                                        metadata, eEps);
					
				}else {
					
					log.trace( "Guardando prescription. Numero formula ", JsonUtil.parse(formula, "numero") );
	                this.savePrescriptionPBS( new Prescription(),
	                                                       source,
	                                                       formula,
	                                                       paciente,
	                                                       metadata, eEps);
					
				}
                

                log.info( "Ha finalizado el proceso de guardado" );
            }
            else {
                log.info( "No hay tratamientos para procesar de la prescripcion." );
            }
        }

    }

    /**
     * Guarda los datos en prescription del tipo POS
     * @param prescription
     * @param source
     * @param mipres
     * @param paciente
     * @param json
     * @param eEps
     * @throws Exception
     */
    private void savePrescriptionNOPBS( Prescription prescription,
                                                PrescriptionSource source,
                                                String mipres,
                                                JsonElement paciente,
                                                String json, EEps eEps )
        throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
        Optional<Prescription> pres;
        pres = prescriptionDao.findTop1ByNoPrescription( mipres );

        String uuid = null;

        
        

        if( !pres.isPresent() ) {

            uuid = savePrescption(prescription, source, mipres, eEps, sdf, paciente, null);
        }

        Optional<Eps> eps = epsDao.findByNit( eEps.getNit() );

        Optional<Medication> med;
        med = medicationDao.findByPrescriptionId( uuid );

        Medication medication;

        if( !pres.isPresent() ) {

            if( !med.isPresent() ) {

                log.info( "La prescriptionId {} no existe en medicamentos", uuid );
                log.trace( "Se procede a persistir los medicamentos de la prescripcion con id {}", uuid );
                medication = new Medication();
            }
            else {

                log.info( "La prescripcion con id {} ya fue registrada en los medicamentos", uuid );
                log.trace( "Se procede a actualizar los medicamentos de la prescripcion con id {}", uuid );
                medication = med.get();
            }

        }
        else {

            log.info( "La prescripcion con id {} ya fue registrada en los medicamentos", uuid );
            log.trace( "Se procede a actualizar los medicamentos de la prescripcion con id {}", uuid );
            medication = med.get();
        }

        medication.setPrescriptionId( uuid );
        medication.setEpsId( eps.get().getId() );
        medication.setNumPreauthorization( null );
        medication.setMetadata( json );
        medicationDao.save( medication );
    }

	private String savePrescption(	Prescription prescription, 
									PrescriptionSource source, 
									String mipres, 
									EEps eEps,
									SimpleDateFormat sdf, 
									JsonElement paciente,
									String prescriptionId
									) {

		String dateMaxDelivery;
		
		JsonObject document = paciente.getAsJsonObject().get( "documento" ).getAsJsonObject();
		
		prescription.setPrescriptionSourceId( source.getId() );
		prescription.setNoPrescription( mipres );
		prescription.setPrescriptionId( prescriptionId );
		prescription.setNoIdEps( eEps.getNit() );
		prescription.setPrescriptionStatusId( EPrescriptionStatus.CONSULTADA );

		Calendar calendar = Calendar.getInstance();
		calendar.setTime( new Date( System.currentTimeMillis() ) );
		calendar.add( Calendar.MONTH, 1 );

		dateMaxDelivery = sdf.format( calendar.getTime() );

		prescription.setDateMaxDelivery( dateMaxDelivery );
		prescription.setAttempts( 0 ); // Se inicia los reintentos en 0
		prescription.setTypePrescription( ETypePrescription.PBS );
		prescription.setNoPrescriptionEps( null );
		prescription.setTypeTechnology( null );
		prescription.setDeliveryNumber( null );
		prescription.setConsecutiveTechnology( null );
		prescription.setNoIdPatient( document.get( "numero" ).getAsString() );
		prescription.setTypeIdPatient( document.get( "tipo" ).getAsString() );
		prescription.setMetaData( BasicHelper.toJson( prescription ) );
		prescription.setIdFormulaState( EFormulaState.NO_ENTREGADO);

		Prescription p = this.prescriptionDao.save( prescription );
		return p.getId();
	}

    /**
     *  Guarda los datos en prescription del tipo POS
     * @param prescription
     * @param source
     * @param formula
     * @param paciente
     * @param json
     * @param eEps
     * @throws Exception
     */
    private void savePrescriptionPBS( Prescription prescription,
                                                   PrescriptionSource source,
                                                   JsonElement formula,
                                                   JsonElement paciente,
                                                   String json, EEps eEps)
        throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd" );
        Optional<Prescription> pres;
        pres = prescriptionDao.findByPrescriptionId( JsonUtil.parse(formula, "numero") );

        String uuid = null;        

        if( !pres.isPresent() ) {
        	uuid = savePrescption(  prescription, source, null, eEps, sdf, paciente, JsonUtil.parse(formula, "numero"));
        }
        else {
            Prescription p = pres.get();
            uuid = p.getId();
        }

        Optional<Eps> eps = epsDao.findByNit( eEps.getNit() );

        Optional<Medication> med;
        med = medicationDao.findByPrescriptionId( uuid );

        Medication medication;

        if( !pres.isPresent() ) {

            if( !med.isPresent() ) {

                log.info( "La prescriptionId {} no existe en medicamentos", uuid );
                log.trace( "Se procede a persistir los medicamentos de la prescripcion con id {}", uuid );
                medication = new Medication();
            }
            else {

                log.info( "La prescripcion con id {} ya fue registrada en los medicamentos", uuid );
                log.trace( "Se procede a actualizar los medicamentos de la prescripcion con id {}", uuid );
                medication = med.get();
            }

        }
        else {

            log.info( "La prescripcion con id {} ya fue registrada en los medicamentos", uuid );
            log.trace( "Se procede a actualizar los medicamentos de la prescripcion con id {}", uuid );
            medication = med.get();
        }

        medication.setPrescriptionId( uuid );
        medication.setEpsId( eps.get().getId() );
        medication.setNumPreauthorization( JsonUtil.parse(formula, "orden") );
        medication.setMetadata( json );
        medicationDao.save( medication );
    }

}
