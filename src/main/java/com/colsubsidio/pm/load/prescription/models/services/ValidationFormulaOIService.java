package com.colsubsidio.pm.load.prescription.models.services;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.colsubsidio.pm.load.prescription.enums.EEps;
import com.colsubsidio.pm.load.prescription.helpers.BasicHelper;
import com.colsubsidio.pm.load.prescription.helpers.PrescriptionHelper;
import com.colsubsidio.pm.load.prescription.helpers.PrescriptionHelperOI;
import com.colsubsidio.pm.load.prescription.models.dao.MedicalPrescriptionOIDao;
import com.colsubsidio.pm.load.prescription.models.dao.MedicalTreatmentResult;
import com.colsubsidio.pm.load.prescription.models.dao.TreatmentOIDao;
import com.colsubsidio.pm.load.prescription.models.dto.MedicalTreatmentDto;
import com.colsubsidio.pm.load.prescription.models.entities.TreatmentOI;
import com.colsubsidio.pm.load.prescription.models.services.interfaces.IValidationFormulaService;
import com.colsubsidio.utilities.log.LogsManager;
import com.colsubsidio.utilities.miscellaneous.commons.models.TokenRequest;
import com.colsubsidio.utilities.miscellaneous.date.DateUtils;
import com.colsubsidio.utilities.miscellaneous.enumeration.EDateFormat;
import com.colsubsidio.utilities.miscellaneous.json.JsonUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import lombok.RequiredArgsConstructor;

/**
 * 
 * @author dchavarro
 * @version 1.0
 * Clase encargada de validar las formulas
 *
 */
@RequiredArgsConstructor
@Service
public class ValidationFormulaOIService implements IValidationFormulaService {
	
	private final MedicalPrescriptionOIDao medicalPrescriptionDao;
    private final ValidationFormulaManager validationFormulaManager;
    private final PrescriptionHelper prescriptionHelper;
    private final PrescriptionHelperOI prescriptionOIHelper;
    private final TreatmentOIDao treatmentDao;
    private static HashMap<String, MedicalTreatmentDto> treatmentHasMap;
    private final LogsManager log;
    private static boolean active;

    /**
	 * Metodo encargado de administrar la validación formular
	 * @author dchavarro
     * @since 19-09-2024
     * @version 1.0
	 */
    public void validateFormula() {

        synchronized( ValidationFormulaOIService.class ) {
            if( active ) {
                log.trace( "Process validateFormula Famisanar active. Not continue the process." );
                return;
            }

            log.trace( "Process validateFormula Famisanar inactive. Will be executed." );
            active = Boolean.TRUE;
        }

        treatmentHasMap = new HashMap<>();

        String currentDate, epsName;

        Integer attemptMinutesMax;

        currentDate = DateUtils.getDateString( EDateFormat.ISO_8601_SHORT.getFormat() );
        epsName = EEps.OI.getName();
        attemptMinutesMax = this.validationFormulaManager.getAttemptMinutesMaxOI();

        log.trace( "Init validateFormula. EPS: {}. CurrenDate: {}", epsName, currentDate );

        try {
            log.info( "Finding prescriptions without MetadataEps EPS: {}. CurrenDate: {}", epsName, currentDate );

            List<MedicalTreatmentResult> medicalPrescriptionList =
                this.medicalPrescriptionDao.findPrescriptionPendingNoPBS( attemptMinutesMax );

            if( medicalPrescriptionList.isEmpty() ) {
                log.info( "Not found prescription EPS: {}. CurrenDate: {}", epsName, currentDate );
            }
            else {
                log.info( "Total: {}. EPS: {}. CurrenDate: {}", medicalPrescriptionList.size(), epsName,
                          currentDate );

                prescriptionPendingOI( currentDate, medicalPrescriptionList );
            }
        }
        catch( Exception e ) {
            log.error( "Error validate . EPS: {}. CurrenDate: {}", e, epsName, currentDate );
        }
        finally {

            log.trace( "Finish validate. EPS: {}. CurrenDate: {}", epsName, currentDate );

            active = Boolean.FALSE;
        }

    }

    /**
     * Metodo encargado de procesar las prescripciones pendiente de OI
	 * @author dchavarro
     * @since 19-09-2024
     * @version 1.0 
     * @param currentDate
     * @param medicalTreatmentList
     */
    private void prescriptionPendingOI( String currentDate, List<MedicalTreatmentResult> medicalTreatmentList ) {

        medicalTreatmentList.forEach( medicalTreatment -> {

            String epsName, serviceUrl, idTreatment, mipresNumber;

            JsonElement response;
            epsName = EEps.OI.getName();
            serviceUrl = this.validationFormulaManager.getServiceUrlOI();
            mipresNumber = medicalTreatment.getMipresNumber();
            idTreatment = medicalTreatment.getIdTreatment();

            try {

                MedicalTreatmentDto medicalTreatmentDto;
                medicalTreatmentDto = MedicalTreatmentDto.builder()
                    .mipresNumber( mipresNumber )
                    .idTreatment( idTreatment )
                    .typeTechnology( medicalTreatment.getTypeTechnology() )
                    .consecutiveTechnology( medicalTreatment.getConsecutiveTechnology() )
                    .deliveryNumber( medicalTreatment.getDeliveryNumber() )
                    .createDate( medicalTreatment.getCreateDate() )
                    .typeIdPatient( medicalTreatment.getTypeIdPatient() )
                    .numberIdPatient( medicalTreatment.getNumberIdPatient() )
                    .build();

                log.debug( "Consuming service: {}. mipresnumber: {}. EPS: {}. CurrenDate: {}",
                           serviceUrl, mipresNumber, epsName, currentDate );

                JsonArray prescriptions;

                MedicalTreatmentDto treatmentMap;
                treatmentMap = treatmentHasMap.get( mipresNumber );
                if( treatmentMap == null ) {
                    String epsMetadata = consumingServiceOI( serviceUrl, medicalTreatmentDto );
                    epsMetadata = epsMetadata != null ? epsMetadata.trim() : null;
                    response = JsonParser.parseString( epsMetadata );
                    prescriptions = JsonUtil.getElementFromPath( response,
                                                                 "obtenerPrescripcion" ).getAsJsonArray();
                    medicalTreatmentDto.setMetadata( prescriptions );

                    treatmentHasMap.put( mipresNumber, medicalTreatmentDto );
                }
                else {
                    prescriptions = treatmentMap.getMetadata();
                    medicalTreatmentDto.setMetadata( prescriptions );
                }

                log.trace( "Result service OI: {}. IdTreatment: {}. EPS: {}. CurrenDate: {}", prescriptions,
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

        } );

    }

    /**
     * Metodo encargado de actualizar tratamiento nuevo
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0 
     * @param currentDate
     * @param idTreatment
     * @param jsonArray
     * @param medicalTreatmentDto
     */
    private void updateTreatmentNew( String currentDate, String idTreatment, JsonArray jsonArray,
                                     MedicalTreatmentDto medicalTreatmentDto ) {

        String epsName;
        epsName = EEps.OI.getName();

        Optional<TreatmentOI> optionalTreatmentOI = treatmentDao.findById( idTreatment );
        TreatmentOI treatmentOI = optionalTreatmentOI.get();

        jsonArray.forEach( prescription -> {

            log.info( "Split for prescription: {}. EPS: {}. CurrenDate: {}", prescription, epsName, currentDate );

            List<JsonElement> treatmentList = this.prescriptionHelper.splitPrescriptionByTreatment( prescription );

            for( JsonElement jsonElement : treatmentList ) {

                JsonElement treatmentElement;

                String mipresNumber, deliveryNumber, typeTechnology, consecutiveTechnology;

                log.info( "formula IdTreatment: {}, json element: {}. EPS: {}. CurrenDate: {}",
                          idTreatment, jsonElement, epsName, currentDate );

                treatmentElement = JsonUtil.getElementFromPath( jsonElement, "formula.mipres" );
                mipresNumber = JsonUtil.getElementAsString( treatmentElement );

                treatmentElement = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].orden" );
                consecutiveTechnology = JsonUtil.getElementAsString( treatmentElement );

                treatmentElement = JsonUtil
                    .getElementFromPath( jsonElement, "formula.tratamiento[0].numeroEntrega" );
                deliveryNumber = JsonUtil.getElementAsString( treatmentElement );

                treatmentElement = JsonUtil
                    .getElementFromPath( jsonElement,
                                         "formula.tratamiento[0].producto.tecnologia.tipo" );
                typeTechnology = JsonUtil.getElementAsString( treatmentElement );

                if( existsTreatments( medicalTreatmentDto, mipresNumber, deliveryNumber, typeTechnology,
                                      consecutiveTechnology, treatmentOI ) ) {

                    log.info( "Update data: {}. EPS: {}. CurrenDate: {}", treatmentOI, epsName, currentDate );

                    try {
						this.prescriptionOIHelper.updateTreatmentFromMetadata( jsonElement, treatmentOI );
					} catch (Exception e) {
						 log.error("OCURRIO UN ERROR AL ENCOLAR LA FORMULA EN RIPS", e.getStackTrace());

					}

                }
            }
        } );

    }

    /**
     * Metodo encargado de existe tratamientos
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0 
     * @param medicalTreatmentDto
     * @param mipresNumber
     * @param deliveryNumber
     * @param typeTechnology
     * @param consecutiveTechnology
     * @param treatmentOi
     * @return
     */
    private boolean existsTreatments( MedicalTreatmentDto medicalTreatmentDto, String mipresNumber,
                                      String deliveryNumber, String typeTechnology, String consecutiveTechnology, TreatmentOI treatmentOi ) {
        return medicalTreatmentDto.getTypeTechnology().equalsIgnoreCase( typeTechnology ) &&
               medicalTreatmentDto.getDeliveryNumber().equalsIgnoreCase( deliveryNumber ) &&
               medicalTreatmentDto.getMipresNumber().equalsIgnoreCase( mipresNumber ) &&
               medicalTreatmentDto.getConsecutiveTechnology().equalsIgnoreCase( consecutiveTechnology ) &&
               !StringUtils.hasLength( treatmentOi.getEpsMetadata() );
    }

    /**
     * Consume el servicio RestFull perteneciente a la EPS para obtener los medicamentos relacionados a la prescripcion
     *
     * @author dchavarro
     * @since 19-09-2024
     * @version 1.0 
     * @param url parametro de la url del servicio de famisanar
     * @param medicalTreatment entidad para procesar la informacion
     * @return retorna la respuesta de famisanar
     */
    private String consumingServiceOI( String url, MedicalTreatmentDto medicalTreatment ) {

        String dateInitial, dateEnd;
        URI uri;
        ResponseEntity<?> response;

        SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyyMMdd" );

        dateInitial = dateFormat.format( medicalTreatment.getCreateDate() );
        dateEnd = dateFormat.format( medicalTreatment.getCreateDate() );

        uri = BasicHelper.buildUri( url,
                                    medicalTreatment.getMipresNumber(), Strings.EMPTY,
                                    dateInitial,
                                    dateEnd,
                                    medicalTreatment.getTypeIdPatient(),
                                    medicalTreatment.getNumberIdPatient() );

        TokenRequest tokenRequest = this.validationFormulaManager.tokenRequest();

        response = this.validationFormulaManager.getHttpClient()
            .get( uri, String.class,
                  MediaType.APPLICATION_JSON,
                  Boolean.TRUE, tokenRequest );

        return ( String ) response.getBody();
    }
}
