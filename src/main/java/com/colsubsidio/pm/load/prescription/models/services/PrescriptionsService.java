/*
 * Codigo fuente propiedad de Colsubsidio
 * Gerencia de Tecnologia
 */
package com.colsubsidio.pm.load.prescription.models.services;

import com.colsubsidio.pm.load.prescription.enums.EStatus;
import com.colsubsidio.pm.load.prescription.models.dto.ResponseDto;
import com.colsubsidio.pm.load.prescription.models.dto.ResultDto;
import com.colsubsidio.pm.load.prescription.models.entities.GetPrescriptionProcess;
import com.colsubsidio.utilities.log.LogsManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.colsubsidio.pm.load.prescription.models.dao.PrescriptionProcessDao;

@Component
public class PrescriptionsService {

    private final LogsManager log;
    private final PrescriptionNoPbsNepsService prescriptionNoPbsNepsService;
    private final PrescriptionProcessDao getPrescriptionProcessDao;

    @Value( "${colsubsidio.page.size:900}" )
    private long pageSize;

    private static boolean isActive = false;

    @Autowired
    public PrescriptionsService( LogsManager log, PrescriptionNoPbsNepsService prescriptionNoPbsNepsService,
                                    PrescriptionProcessDao getPrescriptionProcessDao
    ) {
        this.log = log;
        this.getPrescriptionProcessDao = getPrescriptionProcessDao;
        this.prescriptionNoPbsNepsService = prescriptionNoPbsNepsService;
    }

    @Async
    public void processPrescriptions() {
        Collection<GetPrescriptionProcess> mipresList;

        Date currentDate;

        Integer sizeData;

        currentDate = new Date();

        synchronized( PrescriptionsService.class ) {
            log.trace( "Validate if is active the ProcessMipres: " + currentDate );

            if( PrescriptionsService.isActive ) {
                log.trace( "Transaction rejected there is already a process in progress." );
                return;
            }
            else {
                PrescriptionsService.isActive = true;
                log.trace( "The process will run." + currentDate );
            }
        }

        try {
            while( PrescriptionsService.isActive ) {
                mipresList =
                    ( Collection<GetPrescriptionProcess> ) this.getPrescriptionProcessDao.findByStatus( EStatus.INITIAL.getValue(),
                                                                                                        this.pageSize );

                sizeData = mipresList.size();

                if( sizeData > 0 ) {
                    processPrescriptions( mipresList );
                    log.trace( "Finish the package of register." );
                }
                else {
                    log.trace( "Not register found" );
                    PrescriptionsService.isActive = false;
                }
            }
        }
        catch( Exception e ) {
            log.error( "Error in process enqueue", e );
        }
        finally {
            log.trace( "Finish the process of enqueue" );

            PrescriptionsService.isActive = false;
        }
    }

    public ResponseDto buildResponse( int code, String description, ResponseDto responseDto ) {
        ArrayList<ResultDto> resultDTOs;

        ResultDto resultDto;

        resultDto = new ResultDto();
        resultDto.setCode( code );
        resultDto.setDescription( description );

        if( responseDto.getResult() == null ) {
            resultDTOs = new ArrayList<>();
            resultDTOs.add( resultDto );
            responseDto.setResult( resultDTOs );
        }
        else {
            resultDTOs = responseDto.getResult();
            resultDTOs.add( resultDto );
            responseDto.setResult( resultDTOs );
        }

        return responseDto;
    }

    private void processPrescriptions( Collection<GetPrescriptionProcess> mipresList ) {

        for( GetPrescriptionProcess prescription : mipresList ) {
            try {
                prescriptionNoPbsNepsService.getAddressingPrescription( prescription.getMipres() );

            }
            catch( Exception ex ) {
                log.error( "Error in the process addressing for mipresNumber: {} ", ex, prescription.toString() );
            }
            getPrescriptionProcessDao.updateByIdSetState( EStatus.PROCESS.getValue().toString(), prescription.getMipres() );
        }
    }

}
