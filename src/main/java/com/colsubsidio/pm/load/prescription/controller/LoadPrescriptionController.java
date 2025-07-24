package com.colsubsidio.pm.load.prescription.controller;

import com.colsubsidio.pm.load.prescription.rabbit.PMReceive;
import com.colsubsidio.pm.load.prescription.enums.QueveRabbitmqEnum;
import com.colsubsidio.pm.load.prescription.models.dto.RequestCreate;
import com.colsubsidio.pm.load.prescription.models.dto.ResponseDto;
import com.colsubsidio.pm.load.prescription.models.services.PrescriptionsService;
import com.colsubsidio.pm.load.prescription.models.services.PrescriptionNoPbsNepsService;
import com.colsubsidio.utilities.log.LogsManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Alberto Palencia Benedetti
 */
@CrossOrigin( "*" )
@RestController
@RequestMapping( value = "${path.config.version}/${path.config.prescription}" )
public class LoadPrescriptionController {

    private final LogsManager log;

    private final PMReceive pMReceive;

    private final PrescriptionNoPbsNepsService prescriptionNoPbsNepsService;

    private final PrescriptionsService getPrescriptionsService;

    @Autowired
    public LoadPrescriptionController( LogsManager log, PMReceive pMReceive,
                                       PrescriptionNoPbsNepsService prescriptionNoPbsNepsService,
                                       PrescriptionsService getPrescriptionsService ) {
        this.log = log;
        this.pMReceive = pMReceive;
        this.prescriptionNoPbsNepsService = prescriptionNoPbsNepsService;
        this.getPrescriptionsService = getPrescriptionsService;
    }

    /**
     * Metodo deprecado, apunta a la tabla de
     *  medicattion para almacenar las formulas de neps pero ya no se usa 
     * @return
     */
    @PostMapping( "/LoadPrescription" )
    public ResponseEntity<?> loadPrescriptionAuthorized() {

        log.info( "Inicio de listener loadPrescriptionAuthorized" );

        try {

            pMReceive.receives( QueveRabbitmqEnum.PRESCRIPCION_PBS );
            return new ResponseEntity<String>( HttpStatus.OK );
        }
        catch( Exception ex ) {
            log.error( "error en el listener loadPrescriptionAuthorized", ex );
        }
        log.info( "termino listener loadPrescriptionAuthorized" );
        return new ResponseEntity<String>( HttpStatus.BAD_REQUEST );

    }

    /**
     * Método encargado de consultar una formula por mipres y
     *  numero preautorizacion, se consulta el direccionamiento y luego se consulta en la eps 
     * @param request numero preautorizacion, mipres 
     * SOLO NEPS NOPBS
     */
    @PostMapping( "/crearPrescripcion" )
    public ResponseEntity< Object> reportInvoice( @RequestBody RequestCreate request )
        throws Exception {

        return ResponseEntity.ok( this.prescriptionNoPbsNepsService.processPrescription( request.getNumPreauthorization(),
                                                                                         request.getNumberMipres() ) );

    }

    /**
     * Metodo encargado de consultar los direccionamientos
     *  a mipres de las formulas almacenadas en la tabla getaddressingtemp
     */
    @PostMapping( "/processPrescriptions" )
    public ResponseEntity<ResponseDto> enqueueTransaction() {
        ResponseDto responseDto = new ResponseDto();
        responseDto = this.getPrescriptionsService.buildResponse( HttpStatus.OK.value(),
                                                               "Se ha iniciado el proceso de consulta direccionamientos " +
                                                               "vez  finalice se obtendra el resultado en las " + "carpetas de logs ",
                                                               responseDto );
        this.getPrescriptionsService.processPrescriptions();

        return new ResponseEntity( responseDto, HttpStatus.OK );
    }
}
