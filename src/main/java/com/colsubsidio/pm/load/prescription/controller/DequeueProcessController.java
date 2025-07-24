package com.colsubsidio.pm.load.prescription.controller;

import com.colsubsidio.pm.load.prescription.models.services.interfaces.IDequeueExecutorPBSServices;
import com.colsubsidio.utilities.log.LogsManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Camilo Olivo
 */
@CrossOrigin("*")
@RestController
@RequestMapping(value = "${path.config.version}/${path.config.prescription}")
public class DequeueProcessController {

	private final LogsManager log;
	private final IDequeueExecutorPBSServices dequeueExecutorPBSServices;

	@Autowired
	public DequeueProcessController(LogsManager log, IDequeueExecutorPBSServices dequeueExecutorPBSServices) {
		this.log = log;
		this.dequeueExecutorPBSServices = dequeueExecutorPBSServices;
	}

	/**
	 * Método encargado de procesar las formulas pbs de la tabla
	 * receivedprescriptionpbs de cada eps con estado unprocess
	 * para probar se debe encolar en PRESCRIPTION_PBS_NEPS o FAMI 
	 * 
	 * @return
	 */
	@PostMapping("/prescripcion/cola/eps")
	public ResponseEntity<?> dequeueProcessPBS() {
		log.info("Init listener dequeueProcessPBS");

		try {
			dequeueExecutorPBSServices.dequeueExecutor();

			return new ResponseEntity<String>(HttpStatus.OK);
		} catch (Exception ex) {
			log.error("Error listener dequeueProcessPBS", ex);
		}

		log.info("Finished listener dequeueProcessPBS");

		return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);

	}

}
