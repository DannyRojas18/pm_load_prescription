/*
 * Codigo fuente propiedad de Colsubsidio
 * Gerencia de Tecnologia
 */
package com.colsubsidio.pm.load.prescription;

import com.colsubsidio.utilities.log.LogsManager;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.colsubsidio.pm.load.prescription.models.services.interfaces.IDequeueExecutorPBSServices;

/**
 *
 * @author Robert Barraza
 */
@Component
public class LoadPrescriptionInitializing implements InitializingBean {
    private final IDequeueExecutorPBSServices dequeueExecutorPBSServices;
    private final LogsManager log;

    @Autowired
    public LoadPrescriptionInitializing( IDequeueExecutorPBSServices dequeueExecutorPBSServices,
                                         LogsManager log ) {

        this.dequeueExecutorPBSServices = dequeueExecutorPBSServices;
        this.log = log;
    }

    @Override
    public void afterPropertiesSet()
        throws Exception {
        log.info( "Init LoadPrescription" );

        try {
            this.dequeueExecutorPBSServices.dequeueExecutor();
        }
        catch( Exception ex ) {
            log.error( "Error to initialize the class DequeueExecutorPBSServices.", ex );
        }
        finally {
            log.trace( "Finish the process DequeueExecutorPBSServices" );
        }
    }
}
