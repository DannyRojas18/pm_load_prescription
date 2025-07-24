/*
 * Codigo fuente propiedad de Colsubsidio
 * Gerencia de Tecnologia
 */
package com.colsubsidio.pm.load.prescription.helpers;

import com.colsubsidio.pm.load.prescription.enums.EEps;
import com.colsubsidio.pm.load.prescription.enums.EProcessState;
import com.colsubsidio.pm.load.prescription.models.dao.FieldValidatorDao;
import com.colsubsidio.pm.load.prescription.models.dao.MedicalPrescriptionNepsDao;
import com.colsubsidio.pm.load.prescription.models.dao.ReceivedPrescriptionPbsDao;
import com.colsubsidio.pm.load.prescription.models.dao.TreatmentNepsDao;
import com.colsubsidio.pm.load.prescription.models.entities.MedicalPrescriptionNeps;
import com.colsubsidio.pm.load.prescription.models.entities.ReceivedPrescriptionPbs;
import com.colsubsidio.pm.load.prescription.models.entities.TreatmentNeps;
import com.colsubsidio.utilities.log.LogsManager;
import com.colsubsidio.utilities.miscellaneous.json.JsonUtil;
import com.colsubsidio.utilities.miscellaneous.rabbit.receive.LogicConsumer;
import com.google.common.base.Strings;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.EmptyStackException;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 *
 * @author Camilo Olivo
 */
@Component
public class LogicConsumerPBSNeps extends LogicConsumer {

    private final LogsManager log;
    private final PrescriptionHelper prescriptionHelper;
    private final TreatmentNepsDao treatmentDao;
    private final ReceivedPrescriptionPbsDao receivedPrescriptionPbsDao;
    private final FieldValidatorDao fieldValidatorDao;
    private final MedicalPrescriptionNepsDao medicalPrescriptionDao;

    @Value( "${colsubsidio.cum.separator}" )
    private String separatorCum;

    @Autowired
    public LogicConsumerPBSNeps( LogsManager log, 
                                 PrescriptionHelper prescriptionHelper,
                                 TreatmentNepsDao treatmentDao, 
                                 ReceivedPrescriptionPbsDao receivedPrescriptionPbsDao,
                                 FieldValidatorDao fieldValidatorDao,
                                 MedicalPrescriptionNepsDao medicalPrescriptionDao ) {
        this.log = log;
        this.prescriptionHelper = prescriptionHelper;
        this.treatmentDao = treatmentDao;
        this.receivedPrescriptionPbsDao = receivedPrescriptionPbsDao;
        this.fieldValidatorDao = fieldValidatorDao;
        this.medicalPrescriptionDao = medicalPrescriptionDao;
    }

    public void setProperties( String separatorCum ) {
        this.separatorCum = separatorCum;
    }
    
    @Override
    public void handleDelivery( String idReceivedPbs ) {
        ReceivedPrescriptionPbs receivedPrescriptionPbs;

        log.info( "Receive idReceivedPbs {} to start the prescription process neps", idReceivedPbs );

        Optional<ReceivedPrescriptionPbs> optionalReceivePres =
            this.receivedPrescriptionPbsDao.findByIdAndState( idReceivedPbs, EProcessState.UNPROCESSED.getId() );

        if( optionalReceivePres.isPresent() ) {
            receivedPrescriptionPbs = optionalReceivePres.get();

            log.trace( "Found the idReceivedPbs {} in receivedPrescriptionPbs with the information: {}", idReceivedPbs,
                       receivedPrescriptionPbs );

            processReceiveEps( receivedPrescriptionPbs );

            log.debug( "Finish idReceivedPbs: {} from queue PRESCRIPTION_PBS_NEPS.", idReceivedPbs );

        }
        else {
            log.trace( "Register not found for {}", idReceivedPbs );
        }
    }

    @SuppressWarnings( "UseSpecificCatch" )
    private void processReceiveEps( ReceivedPrescriptionPbs receivedPrescriptionPbs ) {
        JsonArray prescriptionList;

        String idReceivedPbs, metadata;

        idReceivedPbs = receivedPrescriptionPbs.getId();
        metadata = receivedPrescriptionPbs.getMetadata();

        try {
            prescriptionList = JsonParser.parseString( metadata ).getAsJsonArray();

            processsReceiveEps( prescriptionList, idReceivedPbs );

            receivedPrescriptionPbs.setState( EProcessState.PROCESSED.getId() );

            log.trace( "Finish ok the process. idReceivedPbs: {}. Metadata: {}", idReceivedPbs, metadata );
        }
        catch( Exception exe ) {
            receivedPrescriptionPbs.setState( EProcessState.FAILED.getId() );

            log.error( "Error in process. Error {}. idReceivedPbs: {}. Metadata: {}", exe, idReceivedPbs, metadata );
        }
        finally {
            this.receivedPrescriptionPbsDao.save( receivedPrescriptionPbs );
        }
    }

    private void processsReceiveEps( JsonArray prescriptionList, String idReceivedPbs ) throws Exception {

        int countError = 0;
        log.debug( "Enter to the method processsReceiveEps. idReceivedPbs {}., for: {}.", idReceivedPbs, prescriptionList );

        for(JsonElement prescription: prescriptionList){
            List<JsonElement> elementList = this.prescriptionHelper.splitPrescriptionByTreatment( prescription );
            for(JsonElement jsonElement: elementList){
                
                JsonElement element;

                String mipresNumber;

                //* Obtenemos el número de MIPRES
                element = JsonUtil.getElementFromPath( jsonElement, "formula.mipres" );
                mipresNumber = JsonUtil.getElementAsString( element );

                //* Validamos si es NoPBS o PBS.
                if( !StringUtils.hasLength( mipresNumber ) || Strings.isNullOrEmpty(mipresNumber) || mipresNumber.toLowerCase().equals("null") ) {
                    processReceiveEpsPBS( jsonElement, idReceivedPbs );
                }else{
                    countError++;
                    if(prescriptionList.size() == countError){
                        throw new EmptyStackException(); 
                    }
                }
            }
        }
    }

    private void processReceiveEpsPBS( JsonElement jsonElement, String idReceivedPbs ) throws Exception {
        Optional<TreatmentNeps> optTreatment;

        Optional<MedicalPrescriptionNeps> optMedicalPrescription;

        TreatmentNeps treatment;

        JsonElement element, elementCum, elementMapis;

        String numberPrescription, cum, mapis;

        //* Obtenemos el numero de prescripcion.
        element = JsonUtil.getElementFromPath( jsonElement, "formula.numero" );
        numberPrescription = JsonUtil.getElementAsString( element );

        if( !StringUtils.isEmpty( numberPrescription ) ) {

            log.debug( "Init the process processReceiveEpsPBS. IdReceivedPbs {}. numberPrescription: {}.",
                    idReceivedPbs, numberPrescription );

            //* Obtenemos el cum o el mapis del tratamiento.
            elementCum = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.cum" );
            cum = JsonUtil.getElementAsString( elementCum );

            elementMapis = JsonUtil.getElementFromPath( jsonElement, "formula.tratamiento[0].producto.mapis" );
            mapis = JsonUtil.getElementAsString( elementMapis );

            if( !Strings.isNullOrEmpty( cum ) && !cum.equalsIgnoreCase( "null" ) && !cum.equals( "" ) ) {
                optTreatment = this.treatmentDao.findByNumberPrescriptionAndCum( numberPrescription, cum );
            }else{
                optTreatment = this.treatmentDao.findByNumberPrescriptionAndMaterialId( numberPrescription, mapis );
            }

            if( optTreatment.isPresent() ) {
                treatment = optTreatment.get();

                log.trace( "Process processReceiveEpsPBS. IdReceivedPbs {}. updateTreatmentNepsFromMetadata: {}.",
                        idReceivedPbs, treatment );

                this.prescriptionHelper.updateTreatmentFromMetadata( jsonElement, treatment );
            }
            else {
                optMedicalPrescription =
                    this.medicalPrescriptionDao.findByNumberPrescription( numberPrescription );

                if( optMedicalPrescription.isPresent() ) {
                    log.trace( "Process processReceiveEpsPBS. IdReceivedPbs {}. createTreatment: {}.",
                            idReceivedPbs, optMedicalPrescription.get() );

                    this.prescriptionHelper.createTreatment( jsonElement, optMedicalPrescription.get() );
                }
                else {
                    log.trace( "Process processReceiveEpsPBS. IdReceivedPbs {}. createMedicalPrescription: {}.",
                            idReceivedPbs, jsonElement );
                    this.prescriptionHelper.createMedicalPrescription( jsonElement, EEps.NEPS );
                }
            }
        }else{
            log.error("Does not contain formula number. IdReceivedPbs {}.", idReceivedPbs);
            throw new EmptyStackException(); 
        }
      
    }

    @Override
    @SuppressWarnings( "CloneDoesntCallSuperClone" )
    public Object clone()
        throws CloneNotSupportedException {
        LogicConsumerPBSNeps logicConsumer;

        logicConsumer = new LogicConsumerPBSNeps( this.log, this.prescriptionHelper, this.treatmentDao,
                                                  this.receivedPrescriptionPbsDao, this.fieldValidatorDao,
                                                  this.medicalPrescriptionDao );

        logicConsumer.setProperties( this.separatorCum );

        return logicConsumer;
    }
}
