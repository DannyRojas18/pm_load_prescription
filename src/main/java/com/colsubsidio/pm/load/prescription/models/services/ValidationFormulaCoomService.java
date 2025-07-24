package com.colsubsidio.pm.load.prescription.models.services;

import com.colsubsidio.pm.load.prescription.enums.EEps;
import com.colsubsidio.pm.load.prescription.enums.EValidationFormula;
import com.colsubsidio.pm.load.prescription.helpers.PrescriptionHelper;
import com.colsubsidio.pm.load.prescription.models.dao.MedicalPrescriptionCoomDao;
import com.colsubsidio.pm.load.prescription.models.dao.MedicalTreatmentResult;
import com.colsubsidio.pm.load.prescription.models.dao.TreatmentCoomDao;
import com.colsubsidio.pm.load.prescription.models.dto.MedicalTreatmentDto;
import com.colsubsidio.pm.load.prescription.models.entities.TreatmentCoom;
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
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
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
public class ValidationFormulaCoomService implements IValidationFormulaService {

    private final LogsManager log;
    private final MedicalPrescriptionCoomDao medicalPrescriptionDao;
    private final ValidationFormulaManager validationFormula;
    private final PrescriptionHelper prescriptionHelper;
    private final TreatmentCoomDao  treatmentDao;
    private static boolean active;
    private static HashMap<String, MedicalTreatmentDto> medicalTreatmentDtoHashMap;

    @Override
    public void validateFormula() {
        synchronized( ValidationFormulaCoomService.class ) {
            if( active ) {
                log.trace( "Process validateFormula  active. Not continue the process. For Coomeva " );
                return;
            }

            log.trace( "Process validateFormula inactive. Will be executed. For Coomeva" );
            active = Boolean.TRUE;
        }
    
        medicalTreatmentDtoHashMap = new HashMap<>();
        String currentDate, epsName;
        Integer attemptMinutesMax;
        currentDate = DateUtils.getDateString( EDateFormat.ISO_8601_SHORT.getFormat() );
        epsName = EEps.COOM.getName();
        attemptMinutesMax = this.validationFormula.getAttemptMinutesMaxCoom();

        log.trace( "Init validateFormula. EPS: {}. CurrenDate: {}", epsName, currentDate );

        try {
            log.info( "Searching prescription without MetadataEps. EPS: {}. CurrenDate: {}", epsName,
                     currentDate ) ;

            List<MedicalTreatmentResult> medicalTreatmentList =
                    medicalPrescriptionDao.findPrescriptionPendingNoPBS( attemptMinutesMax );

            if( medicalTreatmentList.isEmpty() ) {
                log.info( "Not found prescription pending process EPS: {}. CurrenDate: {}",
                          epsName, currentDate );
            }
            else {
                log.info( "Total prescriptions: {} . EPS: {}. CurrenDate: {}",
                        medicalTreatmentList.size(), epsName, currentDate );
                
                proccesMedicalTreatments( currentDate, medicalTreatmentList );
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
    
    private void proccesMedicalTreatments( String currentDate,  List<MedicalTreatmentResult> results ) {
    
        results.forEach( medicalTreatment -> {
    
            String epsName, serviceUrl, idTreatment, mipresNumber;
    
            JsonElement response;
            epsName = EEps.COOM.getName();
            serviceUrl = this.validationFormula.getServiceUrlCoom();
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
                                             
                log.debug( "Call services: {}. mipresnumber: {}. EPS: {}. CurrenDate: {}",
                           serviceUrl, mipresNumber, epsName, currentDate );
        
               JsonArray prescriptionsArray;
                
                MedicalTreatmentDto treatmentMap;
                treatmentMap = medicalTreatmentDtoHashMap.get( mipresNumber );
                if( treatmentMap == null ) {
                    String epsMetadata = this.validationFormula.consumingService( serviceUrl, mipresNumber );
                    epsMetadata = epsMetadata != null ? epsMetadata.trim() : null;
                    response = JsonParser.parseString( epsMetadata );
                    prescriptionsArray = JsonUtil.getElementFromPath( response,
                            "obtenerPrescripcion" ).getAsJsonArray();
                    medicalTreatmentDto.setMetadata( prescriptionsArray );
                    
                    medicalTreatmentDtoHashMap.put( mipresNumber,  medicalTreatmentDto );
                } else {
                    prescriptionsArray = treatmentMap.getMetadata();
                    medicalTreatmentDto.setMetadata( prescriptionsArray );
                }
                
                log.trace( "Result service coom: {}. IdTreatment: {}. EPS: {}. CurrenDate: {}",
                        prescriptionsArray, idTreatment, epsName, currentDate );
               
                if( prescriptionsArray.size() > 0 ) {
                    createOrUpdateTreatment( currentDate, idTreatment, prescriptionsArray, medicalTreatmentDto );
                }
                else {
                      medicalTreatmentDtoHashMap.remove( mipresNumber );
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
    
    private void createOrUpdateTreatment( String currentDate, String idTreatment, JsonArray jsonArray,
            MedicalTreatmentDto medicalTreatmentDto ) {
        
        String epsName;
        epsName = EEps.COOM.getName();
    
        Optional<TreatmentCoom> optionalTreatmentCoom = treatmentDao.findById( idTreatment );
        
        jsonArray.forEach( prescriptionElement -> {
        
         log.info( "Split for prescription: {}. EPS: {}. CurrenDate: {}", prescriptionElement, epsName, currentDate );
        
        List<JsonElement> treatmentList = this.prescriptionHelper.splitPrescriptionByTreatment( prescriptionElement );
        
        for( JsonElement jsonElement : treatmentList ) {
               JsonElement treatmentElement;
               
               String mipresNumber;
               String numberPrescription;
               String preauthorization;
               String authorization;
               String materialId;
               String consecutiveTechnology;
               String deliveryNumber;
               String typeTechnology;
               
               log.info( "IdTreatment: {}, json element: {}. EPS: {}. CurrenDate: {}",
                            idTreatment, jsonElement, epsName, currentDate );
    
               treatmentElement = JsonUtil.getElementFromPath( jsonElement, "formula.mipres" );
               mipresNumber = JsonUtil.getElementAsString( treatmentElement );
    
               treatmentElement =  JsonUtil.getElementFromPath( jsonElement, "formula.numero" );
               numberPrescription = JsonUtil.getElementAsString( treatmentElement );
    
               treatmentElement = JsonUtil.getElementFromPath( jsonElement, "formula.preautorizacion" );
               preauthorization = JsonUtil.getElementAsString( treatmentElement );
    
    
               treatmentElement =
                       JsonUtil.getElementFromPath( jsonElement, "formula.autorizacion" );
               authorization = JsonUtil.getElementAsString( treatmentElement );
    
               treatmentElement =
                       JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.mapis");
               materialId = JsonUtil.getElementAsString( treatmentElement );
    
               treatmentElement =
                       JsonUtil.getElementFromPath( jsonElement,"formula.tratamiento[0].orden");
               consecutiveTechnology = JsonUtil.getElementAsString( treatmentElement );
    
               treatmentElement = JsonUtil .getElementFromPath( jsonElement,"formula.tratamiento[0].numeroEntrega");
               deliveryNumber = JsonUtil.getElementAsString( treatmentElement );
    
               treatmentElement = JsonUtil.getElementFromPath( jsonElement,
                                                        "formula.tratamiento[0].producto.tecnologia.tipo" );
               typeTechnology = JsonUtil.getElementAsString( treatmentElement );
             
               TreatmentCoom treatmentCoom = optionalTreatmentCoom.get();
            
                if ( medicalTreatmentDto.getTypeTechnology().equalsIgnoreCase( typeTechnology ) &&
                     medicalTreatmentDto.getConsecutiveTechnology().equalsIgnoreCase( consecutiveTechnology ) &&
                     medicalTreatmentDto.getDeliveryNumber().equalsIgnoreCase( deliveryNumber ) &&
                     medicalTreatmentDto.getMipresNumber().equalsIgnoreCase( mipresNumber ) &&
                     Strings.isNullOrEmpty( treatmentCoom.getEpsMetadata() ) ) {
                        
                      log.info( "Update: {}. EPS: {}. CurrenDate: {}",treatmentCoom, epsName, currentDate );
            
                      if( StringUtils.hasLength( numberPrescription ) ) {
                          treatmentCoom.setNumberPrescription( numberPrescription );
                      }
                      if( StringUtils.hasLength( authorization ) &&
                          !authorization.equalsIgnoreCase( EValidationFormula.AUTHORIZATION_NULL.getName() )) {
                           treatmentCoom.setAuthorizationNumber( authorization );
                      }
                      
                      if( Strings.isNullOrEmpty( treatmentCoom.getPreauthorizationNumber() ) &&
                          StringUtils.hasLength( preauthorization ) ) {
                          treatmentCoom.setPreauthorizationNumber( preauthorization );
                      }
            
                      treatmentCoom.setMaterialId( materialId );
                      treatmentCoom.setEpsMetadata( new Gson().toJson( jsonElement ) );
                      log.info( "Save or Update treatment: {}. EPS: {}. CurrenDate: {}", treatmentCoom,
                               epsName, currentDate );
                     
                       this.treatmentDao.save( treatmentCoom );
                       return;
                 }
            }
        });
        
    }
}
