package com.colsubsidio.pm.load.prescription.models.services;

import com.colsubsidio.pm.load.prescription.enums.EEps;
import com.colsubsidio.pm.load.prescription.models.dao.EpsDao;
import com.colsubsidio.pm.load.prescription.models.entities.Eps;
import com.colsubsidio.pm.load.prescription.models.services.interfaces.IDequeueExecutorPBSServices;
import com.colsubsidio.utilities.log.LogsManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 *
 * @author Camilo Olivo
 */
@Service
public class DequeueExecutorPBSServices implements IDequeueExecutorPBSServices {

    private final LogsManager log;
    private final DequeueListenerPBS dequeueListenerPBS;
    private final EpsDao epsDao;

    @Autowired
    public DequeueExecutorPBSServices( LogsManager log,
                                       DequeueListenerPBS dequeueListenerPBS,
                                       EpsDao epsDao ) {
        this.log = log;
        this.dequeueListenerPBS = dequeueListenerPBS;
        this.epsDao = epsDao;
    }

    @Async( "dequeueProcess" )
    @Override
    public void dequeueExecutor() {
        log.debug( "Init the method dequeueExecutor" );

        Iterable<Eps> iterableEps = epsDao.findAllByOrderByOrderAsc();

        for( Eps eps : iterableEps ) {

            if( eps.getId() == EEps.NEPS.getIdEps() ) {
                log.debug( "Init the method dequeueExecutor dequeueListenerPBS.dequeueProcessNeps" );
                dequeueListenerPBS.dequeueProcessNeps();
                log.debug( "Finished the method dequeueExecutor dequeueListenerPBS.dequeueProcessNeps" );
            }
            else if( eps.getId() == EEps.FAMI.getIdEps() ) {
                log.debug( "Init the method dequeueExecutor dequeueListenerPBS.dequeueProcessFami" );
                dequeueListenerPBS.dequeueProcessFami();
                log.debug( "Finished the method dequeueExecutor dequeueListenerPBS.dequeueProcessFami" );
            }
            else if( eps.getId() == EEps.COOM.getIdEps() ) {
                log.debug( "Init the method dequeueExecutor dequeueListenerPBS.dequeueProcessCoom" );
                dequeueListenerPBS.dequeueProcessCoom();
                log.debug( "Finished the method dequeueExecutor dequeueListenerPBS.dequeueProcessCoom" );
            }
            else if( eps.getId() == EEps.OI.getIdEps() ) {
                log.debug( "Init the method dequeueExecutor dequeueListenerPBS.dequeueProcessOI" );
                dequeueListenerPBS.dequeueProcessOI();
                log.debug( "Finished the method dequeueExecutor dequeueListenerPBS.dequeueProcessOI" );
            }

        }

    }

}
