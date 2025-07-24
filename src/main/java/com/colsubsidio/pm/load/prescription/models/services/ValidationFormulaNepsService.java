package com.colsubsidio.pm.load.prescription.models.services;

import com.colsubsidio.pm.load.prescription.enums.EEps;
import com.colsubsidio.pm.load.prescription.enums.EQueueRabbitmq;
import com.colsubsidio.pm.load.prescription.enums.EValidationFormula;
import com.colsubsidio.pm.load.prescription.helpers.PMPublishExchange;
import com.colsubsidio.pm.load.prescription.helpers.PrescriptionHelper;
import com.colsubsidio.pm.load.prescription.models.dao.MedicalPrescriptionNepsDao;
import com.colsubsidio.pm.load.prescription.models.dao.TreatmentNepsDao;
import com.colsubsidio.pm.load.prescription.models.dto.MedicalTreatmentDto;
import com.colsubsidio.pm.load.prescription.models.dao.MedicalTreatmentResult;
import com.colsubsidio.pm.load.prescription.models.entities.TreatmentNeps;
import com.colsubsidio.pm.load.prescription.models.services.interfaces.IValidationFormulaService;
import com.colsubsidio.utilities.log.LogsManager;
import com.colsubsidio.utilities.miscellaneous.date.DateUtils;
import com.colsubsidio.utilities.miscellaneous.enumeration.EDateFormat;
import com.colsubsidio.utilities.miscellaneous.json.JsonUtil;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * author: Alberto Palencia <alberto.palencia@ingeneo.com.co>
 * CreateTime: 10/19/2020
 */
@AllArgsConstructor
@Service
@Component
public class ValidationFormulaNepsService implements IValidationFormulaService {

    private final LogsManager log;
    private final MedicalPrescriptionNepsDao medicalPrescriptionDao;
    private final ValidationFormulaManager validationFormula;
    private final PrescriptionHelper prescriptionHelper;
    private final TreatmentNepsDao treatmentDao;
    private static boolean active;
    private static HashMap<String, MedicalTreatmentDto> treatmentHasMap;
    @Autowired
    private final PMPublishExchange pMPublishExchange;
    
    
    @Override
    public void validateFormula() {
        synchronized( ValidationFormulaNepsService.class ) {
            if( active ) {
                log.trace( "Process validateFormula active. Not continue the process." );
                return;
            }

            log.trace( "Process validateFormula inactive. Will be executed." );
            active = Boolean.TRUE;
        }
    
        treatmentHasMap = new HashMap<>();
        
        String currentDate, epsName;

        Integer attemptMinutesMax;

        currentDate = DateUtils.getDateString( EDateFormat.ISO_8601_SHORT.getFormat() );
        epsName = EEps.NEPS.getName();
        attemptMinutesMax = this.validationFormula.getAttemptMinutesMax();

        log.trace( "Init the validateFormula. EPS: {}. CurrenDate: {}", epsName, currentDate );

        try {
            log.info( "Consultando las prescripciones que no tienen MetadataEps, validateFormula. EPS: {}. " +
                      "CurrenDate: {}", epsName, currentDate );

            List<MedicalTreatmentResult> medicalTreatmentList = medicalPrescriptionDao
                                                             .findPrescriptionPendingNoPBS( attemptMinutesMax );

            if( medicalTreatmentList.isEmpty() ) {
                log.info( "No hay prescripciones pendientes por procesar. EPS: {}. CurrenDate: {}",
                          epsName, currentDate );
            }
            else {
                log.info( "Se encontraron sin procesar {} prescripciones. EPS: {}. CurrenDate: {}",
                        medicalTreatmentList.size(), epsName, currentDate );
                
                proccesDataNueva( currentDate, medicalTreatmentList );
            }
        }
        catch( Exception e ) {
            e.printStackTrace();
            log.error( "Error al procesar la validacion de formula. EPS: {}. CurrenDate: {}",
                       e, epsName, currentDate );
        }
        finally {

            log.trace( "Finalizo el proceso de validacion de la formula. EPS: {}. CurrenDate: {}",
                       epsName, currentDate );

            active = Boolean.FALSE;
        }
    }
    
    private void proccesDataNueva( String currentDate,  List<MedicalTreatmentResult> medicalTreatmentList ) {
    
        medicalTreatmentList.forEach( medicalTreatment -> {
    
            String epsName, serviceUrl, idTreatment, mipresNumber;
    
            JsonElement response;
            epsName = EEps.NEPS.getName();
            serviceUrl = this.validationFormula.getServiceUrl();
            mipresNumber = medicalTreatment.getMipresNumber();
            idTreatment = medicalTreatment.getIdTreatment();
    
            try {
                
                MedicalTreatmentDto medicalTreatmentDto;
                medicalTreatmentDto = MedicalTreatmentDto.builder()
                                      .mipresNumber( mipresNumber )
                                      .idTreatment( idTreatment )
                                      .typeTechnology( medicalTreatment.getTypeTechnology())
                                      .consecutiveTechnology( medicalTreatment.getConsecutiveTechnology() )
                                      .deliveryNumber( medicalTreatment.getDeliveryNumber() )
                                      .build();
                                             
                log.debug( "Consumiendo servicio: {}. mipresnumber: {}. EPS: {}. CurrenDate: {}",
                           serviceUrl, mipresNumber, epsName, currentDate );
        
               JsonArray prescriptions;
                
                MedicalTreatmentDto treatmentMap;
                treatmentMap = treatmentHasMap.get( mipresNumber );
                if( treatmentMap == null ) {
                    String epsMetadata = this.validationFormula.consumingService( serviceUrl, mipresNumber );
                    epsMetadata = epsMetadata != null ? epsMetadata.trim() : null;
                    response = JsonParser.parseString( epsMetadata );
                    prescriptions = JsonUtil.getElementFromPath( response,
                            "obtenerPrescripcion" ).getAsJsonArray();
                    medicalTreatmentDto.setMetadata( prescriptions );
                    
                    treatmentHasMap.put( mipresNumber,  medicalTreatmentDto );
                } else {
                    prescriptions = treatmentMap.getMetadata();
                    medicalTreatmentDto.setMetadata( prescriptions );
                }
                
                log.trace( "Result service neps: {}. IdTreatment: {}. EPS: {}. CurrenDate: {}", prescriptions,
                          idTreatment, epsName, currentDate );
               
                if( prescriptions.size() > 0 ) {
                    updateTreatmentNew( currentDate, idTreatment, prescriptions, medicalTreatmentDto );
                }
                else {
                      treatmentHasMap.remove( mipresNumber );
                      log.info( "Not found mipresnumber: {}, idTreatment: {}. EPS: {}. CurrenDate: {}",
                                mipresNumber, idTreatment, epsName, currentDate );
                }
            }
            catch( Exception e ) {
                log.error( "Mipresnumber :{}. idTreatment: {}. EPS: {}. " +
                                   "CurrenDate: {}", e, mipresNumber, idTreatment, epsName, currentDate );
            }
    
            log.trace( "Finish: {}, idTreatment: {}. EPS: {}. " +
                               "CurrenDate: {}", mipresNumber, idTreatment, epsName, currentDate );
            
        });
    
    }
    
    private void updateTreatmentNew( String currentDate, String idTreatment, JsonArray jsonArray,
            MedicalTreatmentDto medicalTreatmentDto ) {
        
        final String epsName;
        epsName = EEps.NEPS.getName();
    
        Optional<TreatmentNeps> optionalTreatmentNeps = treatmentDao.findById( idTreatment );
        
        jsonArray.forEach( prescription -> {
        
         log.info( "Split for prescription: {}. EPS: {}. CurrenDate: {}", prescription, epsName, currentDate );
        
        List<JsonElement> treatmentList = this.prescriptionHelper.splitPrescriptionByTreatment( prescription );
        
        for( JsonElement jsonElement : treatmentList ) {
               JsonElement mipresNumberElement;
               JsonElement numberPrescriptionElement;
               JsonElement preautorizationElement;
               JsonElement autorizationElement;
               JsonElement materialIdElement;
               JsonElement consecutiveTechnologyElement;
               JsonElement deliveryNumberElement;
               JsonElement typeTechnologyElement;
               
               String mipresNumber;
               String numberPrescription;
               String preauthorization;
               String authorization;
               String materialId;
               String consecutiveTechnology;
               String deliveryNumber;
               String typeTechnology;
               
               log.info( "formula IdTreatment: {}, json element: {}. EPS: {}. CurrenDate: {}",
                            idTreatment, jsonElement, epsName, currentDate );
    
                mipresNumberElement = JsonUtil.getElementFromPath( jsonElement, "formula.mipres" );
            
               numberPrescriptionElement = JsonUtil
                                                .getElementFromPath( jsonElement, "formula.numero" );
                    
               preautorizationElement = JsonUtil
                                             .getElementFromPath( jsonElement, "formula.preautorizacion" );
                    
               autorizationElement = JsonUtil.
                                           getElementFromPath( jsonElement, "formula.autorizacion" );
                    
               materialIdElement = JsonUtil
                                        .getElementFromPath( jsonElement, "formula.tratamiento[0].producto.mapis");
                    
               consecutiveTechnologyElement = JsonUtil
                                                   .getElementFromPath( jsonElement,"formula.tratamiento[0].orden");
                    
               deliveryNumberElement = JsonUtil
                                            .getElementFromPath( jsonElement,"formula.tratamiento[0].numeroEntrega");
                    
               typeTechnologyElement = JsonUtil
                                           .getElementFromPath( jsonElement,
                                                        "formula.tratamiento[0].producto.tecnologia.tipo" );
               
               mipresNumber = JsonUtil.getElementAsString( mipresNumberElement );
               numberPrescription = JsonUtil.getElementAsString( numberPrescriptionElement );
               preauthorization = JsonUtil.getElementAsString( preautorizationElement );
               authorization = JsonUtil.getElementAsString( autorizationElement );
               materialId = JsonUtil.getElementAsString( materialIdElement );
               consecutiveTechnology = JsonUtil.getElementAsString( consecutiveTechnologyElement );
               deliveryNumber = JsonUtil.getElementAsString( deliveryNumberElement );
               typeTechnology = JsonUtil.getElementAsString( typeTechnologyElement );
    
               TreatmentNeps treatmentNeps = optionalTreatmentNeps.get();
            
                if ( medicalTreatmentDto.getTypeTechnology().equalsIgnoreCase( typeTechnology ) &&
                     medicalTreatmentDto.getConsecutiveTechnology().equalsIgnoreCase( consecutiveTechnology ) &&
                     medicalTreatmentDto.getDeliveryNumber().equalsIgnoreCase( deliveryNumber ) &&
                     medicalTreatmentDto.getMipresNumber().equalsIgnoreCase( mipresNumber ) &&
                     Strings.isNullOrEmpty( treatmentNeps.getEpsMetadata() ) ) {
                        
                      log.info( "Entro actualizar la informacion actual de neps: {}. EPS: {}. CurrenDate: {}",
                                treatmentNeps, epsName, currentDate );
            
                      if( StringUtils.hasLength( numberPrescription ) ) {
                          treatmentNeps.setNumberPrescription( numberPrescription );
                      }
                      if( StringUtils.hasLength( authorization ) &&
                          !authorization.equalsIgnoreCase( EValidationFormula.AUTHORIZATION_NULL.getName() )) {
                           treatmentNeps.setAuthorizationNumber( authorization );
                      }
            
                      treatmentNeps.setPreauthorizationNumber( preauthorization );
                      treatmentNeps.setMaterialId( materialId );
                      treatmentNeps.setEpsMetadata( new Gson().toJson( jsonElement ) );
                      log.info( "Persistiendo treatment neps: {}. EPS: {}. CurrenDate: {}", treatmentNeps,
                               epsName, currentDate );
                      treatmentNeps= this.treatmentDao.save( treatmentNeps );
                      log.info( "Se encola la informacion de PROCESS_INFO_USER_NEPS {} ",
                    		  treatmentNeps.getIdTreatmentNeps() );
                      try {
						this.pMPublishExchange.publishExchangeValidation( EQueueRabbitmq.PROCESS_INFO_USER_NEPS,  treatmentNeps.getIdTreatmentNeps() );
					} catch (Exception e) {
						// TODO Auto-generated catch block
						 log.error( "Error encolando informacion de PROCESS_INFO_USER_NEPS {} ",
	                    		  treatmentNeps.getIdTreatmentNeps() );
					}
                    
                     
                      return;
                 }
            }
        });
        
    }
}
