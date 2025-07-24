package com.colsubsidio.pm.load.prescription.helpers;

import com.colsubsidio.pm.load.prescription.enums.*;
import com.colsubsidio.pm.load.prescription.models.dao.FieldValidatorDao;
import com.colsubsidio.pm.load.prescription.models.dao.MedicalPrescriptionNepsDao;
import com.colsubsidio.pm.load.prescription.models.dao.TreatmentNepsDao;
import com.colsubsidio.pm.load.prescription.models.entities.FieldValidator;
import com.colsubsidio.pm.load.prescription.models.entities.MedicalPrescriptionNeps;
import com.colsubsidio.pm.load.prescription.models.entities.TreatmentNeps;
import com.colsubsidio.utilities.log.LogsManager;
import com.colsubsidio.utilities.miscellaneous.date.DateUtils;
import com.colsubsidio.utilities.miscellaneous.enumeration.EDateFormat;
import com.colsubsidio.utilities.miscellaneous.json.JsonUtil;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author: Alberto Palencia <alberto.palencia@ingeneo.com.co> CreateTime:
 *          10/21/2020 Description:
 */
@Component
public class PrescriptionHelper {

	private final LogsManager log;
	private final TreatmentNepsDao treatmentDao;
	private final MedicalPrescriptionNepsDao medicalPrescriptionDao;
	private final FieldValidatorDao fieldValidatorDao;
	private final PMPublishExchange pMPublishExchange;

	@Autowired
	public PrescriptionHelper(LogsManager log, TreatmentNepsDao treatmentDao, PMPublishExchange pMPublishExchange,
			MedicalPrescriptionNepsDao medicalPrescriptionDao, FieldValidatorDao fieldValidatorDao) {
		this.log = log;
		this.treatmentDao = treatmentDao;
		this.medicalPrescriptionDao = medicalPrescriptionDao;
		this.fieldValidatorDao = fieldValidatorDao;
		this.pMPublishExchange = pMPublishExchange;
	}

	public List<JsonElement> splitPrescriptionByTreatment(JsonElement prescription) {
		ArrayList<JsonElement> elementList;

		JsonElement prescriptionTarget, prescriptionClone, element;

		JsonArray treatmentArray;

		int treatmentLength;

		elementList = new ArrayList<>();

		prescriptionClone = JsonUtil.deepCopy(prescription);

		element = JsonUtil.getElementFromPath(prescriptionClone, "formula.tratamiento").getAsJsonArray();

		treatmentArray = element != null ? element.getAsJsonArray() : new JsonArray();

		JsonUtil.getElementFromPath(prescriptionClone, "formula").getAsJsonObject().add("tratamiento", new JsonArray());

		treatmentLength = treatmentArray.size();

		for (int i = 0; i < treatmentLength; i++) {
			prescriptionTarget = JsonUtil.deepCopy(prescriptionClone);

			JsonUtil.getElementFromPath(prescriptionTarget, "formula.tratamiento").getAsJsonArray()
					.add(treatmentArray.get(i));

			elementList.add(prescriptionTarget);
		}

		return elementList;
	}

	public void createMedicalPrescription(JsonElement jsonElement, EEps eps) throws Exception {

		MedicalPrescriptionNeps medicalPrescription;

		JsonElement element;

		String nit, numberIdPatient, typeIdPatient, mipresNumber;

		// * Obtenemos el número de la fórmula.
		element = JsonUtil.getElementFromPath(jsonElement, "paciente.documento.tipo");
		typeIdPatient = JsonUtil.getElementAsString(element);

		// * Obtenemo el número de entrega.
		element = JsonUtil.getElementFromPath(jsonElement, "paciente.documento.numero");
		numberIdPatient = JsonUtil.getElementAsString(element);

		// * Obtenemo el número de entrega.
		element = JsonUtil.getElementFromPath(jsonElement, "formula.mipres");
		mipresNumber = JsonUtil.getElementAsString(element);

		// * Obtenemos el NIT de la aseguradora
		element = JsonUtil.getElementFromPath(jsonElement, "aseguradora.nit");
		nit = JsonUtil.getElementAsString(element);

		medicalPrescription = new MedicalPrescriptionNeps();
		medicalPrescription.setEpsNit(nit);
		medicalPrescription.setMipresNumber(mipresNumber);
		medicalPrescription.setIdPrescriptionSource(EPrescriptionSource.NEPS.getId());
		medicalPrescription.setIdEps(eps.getIdEps());
		medicalPrescription.setMipresNumber(mipresNumber);
		medicalPrescription.setTypeIdPatient(typeIdPatient);
		medicalPrescription.setNumberIdPatient(numberIdPatient);
		medicalPrescription.setPrescriptionType(
				mipresNumber == null ? ETypePrescription.PBS.getId() : ETypePrescription.NOPBS.getId());

		medicalPrescription = this.medicalPrescriptionDao.save(medicalPrescription);

		createTreatment(jsonElement, medicalPrescription);
	}

	public void createTreatment(JsonElement jsonElement, MedicalPrescriptionNeps medicalPrescription) throws Exception {
		TreatmentNeps treatment;

		EFormulaState formulaState;

		JsonElement element;

		Gson gson;

		String deliveryNumber, cum, status, maxDeliveryDate, prescriptionNumber, idMedicalPrescription;
		String preauthorization, authorization, materialId;

		gson = new Gson();
		idMedicalPrescription = medicalPrescription.getIdMedicalPrescriptionNeps();

		// * Obtenemos el número de entrega.
		element = JsonUtil.getElementFromPath(jsonElement, "formula.tratamiento[0].producto.numeroEntregaActual");
		deliveryNumber = JsonUtil.getElementAsString(element);

		// * Obtenemos el cum del tratamiento.
		element = JsonUtil.getElementFromPath(jsonElement, "formula.tratamiento[0].producto.cum");
		cum = JsonUtil.getElementAsString(element);

		// * Obtenemos el estado de la fórmula.
		element = JsonUtil.getElementFromPath(jsonElement, "formula.status");
		status = JsonUtil.getElementAsString(element);

		// * Obtenemos la fecha de vencimiento.
		element = JsonUtil.getElementFromPath(jsonElement, "formula.fechaVencimiento");
		maxDeliveryDate = JsonUtil.getElementAsString(element);

		if (Strings.isNullOrEmpty(maxDeliveryDate)) {

			maxDeliveryDate = DateUtils.getDateString(EDateFormat.ISO_8601_SHORT.getFormat());
		}

		// * Obtenemos el número de la fórmula.
		element = JsonUtil.getElementFromPath(jsonElement, "formula.numero");
		prescriptionNumber = JsonUtil.getElementAsString(element);

		// * Obtenemos el número de la fórmula.
		element = JsonUtil.getElementFromPath(jsonElement, "formula.preautorizacion");
		preauthorization = JsonUtil.getElementAsString(element);

		// * Obtenemos el número de la fórmula.
		element = JsonUtil.getElementFromPath(jsonElement, "formula.autorizacion");
		authorization = JsonUtil.getElementAsString(element);

		// * Obtenemos el numero del material cliente.
		element = JsonUtil.getElementFromPath(jsonElement, "formula.tratamiento[0].producto.mapis");
		materialId = JsonUtil.getElementAsString(element);

		treatment = new TreatmentNeps();
		treatment.setIdMedicalPrescriptionNeps(idMedicalPrescription);
		treatment.setNumberPrescription(prescriptionNumber);
		treatment.setPreauthorizationNumber(preauthorization);
		treatment.setAuthorizationNumber(authorization);
		treatment.setAttempts(0);

		treatment.setCum(cum);
		treatment.setDeliveryNumber(deliveryNumber);
		treatment.setMaterialId(materialId);
		treatment.setEpsMetadata(gson.toJson(jsonElement));
		treatment
				.setMaxDeliveryDate(DateUtils.getStringToDate(maxDeliveryDate, EDateFormat.ISO_8601_SHORT.getFormat()));

		treatment.setIdPrescriptionStatus(EPrescriptionStatus.CONSULTADA.getId());

		formulaState = transformStatusNepsToFormulaState(status);

		if (formulaState != null) {
			treatment.setIdFormulaState(formulaState.getId());
		} else {
			treatment.setIdFormulaState(EFormulaState.NO_ENTREGADO.getId());
		}
		 log.info( "Se encola la informacion de NEPS PROCESS_INFO_USER_NEPS {} ",
				 treatment.getIdTreatmentNeps() );
	       
		treatment = this.treatmentDao.save(treatment);
		this.pMPublishExchange.publishExchangeValidation(EQueueRabbitmq.PROCESS_INFO_USER_NEPS,
				treatment.getIdTreatmentNeps());

	}

	public EFormulaState transformStatusNepsToFormulaState(String status) {

		Optional<FieldValidator> optFieldValidator;

		FieldValidator fieldValidator;

		EFormulaState formulaState;

		String changeValue;

		formulaState = null;

		optFieldValidator = this.fieldValidatorDao.findValuesByCategory(status,
				ECategory.PRESCRIPTION_STATUS_NEPS.getName(), EFieldValidatorState.ACTIVE.getId());

		if (optFieldValidator.isPresent()) {
			fieldValidator = optFieldValidator.get();

			changeValue = fieldValidator.getChangeValue();

			if (changeValue != null) {
				formulaState = EFormulaState.getFormulaStateById(Integer.parseInt(changeValue));
			}
		}

		return formulaState;
	}

	public void updateTreatmentFromMetadata(JsonElement jsonElement, TreatmentNeps treatment) throws Exception {

		JsonElement element;

		Gson gson;

		Integer currentFormulaState;

		String prescriptionNumber, preauthorization, authorizationNumber, cum, materialId;

		currentFormulaState = treatment.getIdFormulaState();

		gson = new Gson();

		// * Obtenemos el número de la fórmula.
		element = JsonUtil.getElementFromPath(jsonElement, "formula.numero");
		prescriptionNumber = JsonUtil.getElementAsString(element);

		// * Obtenemos el número de autorizacion
		element = JsonUtil.getElementFromPath(jsonElement, "formula.autorizacion");
		authorizationNumber = JsonUtil.getElementAsString(element);

		// * Obtenemos el número de la pre-autorizacion.
		element = JsonUtil.getElementFromPath(jsonElement, "formula.preautorizacion");
		preauthorization = JsonUtil.getElementAsString(element);

		// * Obtenemos el número de la fórmula.
		element = JsonUtil.getElementFromPath(jsonElement, "formula.tratamiento[0].producto.cum");
		cum = JsonUtil.getElementAsString(element);

		// * Obtenemos el numero del material cliente.
		element = JsonUtil.getElementFromPath(jsonElement, "formula.tratamiento[0].producto.mapis");
		materialId = JsonUtil.getElementAsString(element);

		treatment.setNumberPrescription(prescriptionNumber);

		if (Strings.isNullOrEmpty(authorizationNumber)) {
			authorizationNumber = treatment.getAuthorizationNumber();
		}

		if (Strings.isNullOrEmpty(preauthorization)) {
			preauthorization = treatment.getPreauthorizationNumber();
		}

		treatment.setPreauthorizationNumber(preauthorization);
		treatment.setAuthorizationNumber(authorizationNumber);
		treatment.setIdFormulaState(currentFormulaState);

		treatment.setCum(cum);
		treatment.setMaterialId(materialId);

		// * Se cambia el estado de la fórmula solo si está en NO ENTREGADO, de lo
		// contrario se deja el que está.
		if (EFormulaState.NO_ENTREGADO.getId().equals(currentFormulaState)) {
			// * Obtenemos el estado de la fórmula.
			element = JsonUtil.getElementFromPath(jsonElement, "formula.status");

			treatment.setEpsMetadata(gson.toJson(jsonElement));
		} else {
			log.trace("Status will not changed because the formula state is differente of NOT DELIVERY. " + "Id: ",
					treatment.getIdTreatmentNeps());
		}
		treatment = this.treatmentDao.save(treatment);
		
		log.info("Se encola en PROCESS_INFO_USER_NEPS {} ",
				treatment.getIdTreatmentNeps());
		this.pMPublishExchange.publishExchangeValidation(EQueueRabbitmq.PROCESS_INFO_USER_NEPS,
				treatment.getIdTreatmentNeps());

	}

	public void createMedicalPrescriptionAddressing(JsonElement jsonElement, EEps eps) {

		MedicalPrescriptionNeps medicalPrescription;

		JsonElement element;

		String nit, numberIdPatient, typeIdPatient, mipresNumber, epsCode;

		// * Obtenemos el número de la fórmula.
		element = JsonUtil.getElementFromPath(jsonElement, "TipoIDPaciente");
		typeIdPatient = JsonUtil.getElementAsString(element);

		// * Obtenemo el número de entrega.
		element = JsonUtil.getElementFromPath(jsonElement, "NoIDPaciente");
		numberIdPatient = JsonUtil.getElementAsString(element);

		// * Obtenemo el número de entrega.
		element = JsonUtil.getElementFromPath(jsonElement, "NoPrescripcion");
		mipresNumber = JsonUtil.getElementAsString(element);

		// * Obtenemos el NIT de la aseguradora
		element = JsonUtil.getElementFromPath(jsonElement, "NoIDEPS");
		nit = JsonUtil.getElementAsString(element);

		// * Obtenemos el NIT de la aseguradora
		element = JsonUtil.getElementFromPath(jsonElement, "CodEPS");
		epsCode = JsonUtil.getElementAsString(element);

		medicalPrescription = new MedicalPrescriptionNeps();
		medicalPrescription.setIdPrescriptionSource(EPrescriptionSource.MIPRES.getId());
		medicalPrescription.setMipresNumber(mipresNumber);
		medicalPrescription.setIdEps(eps.getIdEps());
		medicalPrescription.setEpsNit(nit);
		medicalPrescription.setEpsCode(epsCode);
		medicalPrescription.setTypeIdPatient(typeIdPatient);
		medicalPrescription.setNumberIdPatient(numberIdPatient);
		medicalPrescription.setPrescriptionType(ETypePrescription.NOPBS.getId());

		medicalPrescription = this.medicalPrescriptionDao.save(medicalPrescription);

		createTreatmentAddressing(jsonElement, medicalPrescription);
	}

	public void createTreatmentAddressing(JsonElement jsonElement, MedicalPrescriptionNeps medicalPrescription) {
		TreatmentNeps treatment;

		JsonElement element, elementAnnulment = null;

		Gson gson;

		String deliveryNumber, maxDeliveryDate, prescriptionNumber, idMedicalPrescription, idAddressing;
		String mipresId, typeTechnology, consecutiveTechnology, addressingDate, codeTechnology, dateAnnulment = null;

		gson = new Gson();
		idMedicalPrescription = medicalPrescription.getIdMedicalPrescriptionNeps();

		// * Obtenemos el número de entrega.
		element = JsonUtil.getElementFromPath(jsonElement, "NoEntrega");
		deliveryNumber = JsonUtil.getElementAsString(element);

		// * Obtenemos el IDDireccionamiento.
		element = JsonUtil.getElementFromPath(jsonElement, "IDDireccionamiento");
		idAddressing = JsonUtil.getElementAsString(element);

		// * Obtenemos el tipo de tecnologia.
		element = JsonUtil.getElementFromPath(jsonElement, "TipoTec");
		typeTechnology = JsonUtil.getElementAsString(element);

		// * Obtenemos el codigo de tecnologia.
		element = JsonUtil.getElementFromPath(jsonElement, "ConTec");
		consecutiveTechnology = JsonUtil.getElementAsString(element);

		// * Obtenemos la fecha de direccionamiento.
		element = JsonUtil.getElementFromPath(jsonElement, "FecDireccionamiento");
		addressingDate = JsonUtil.getElementAsString(element);

		// * Obtenemos el codigo de tecnologia.
		element = JsonUtil.getElementFromPath(jsonElement, "CodSerTecAEntregar");
		codeTechnology = JsonUtil.getElementAsString(element);

		// * Obtenemos la fecha de vencimiento.
		element = JsonUtil.getElementFromPath(jsonElement, "FecMaxEnt");

		maxDeliveryDate = JsonUtil.getElementAsString(element);

		if (!Strings.isNullOrEmpty(maxDeliveryDate)) {
			maxDeliveryDate = DateUtils.getDateString(EDateFormat.ISO_8601_SHORT.getFormat());
		}

		if (!Strings.isNullOrEmpty(addressingDate)) {
			addressingDate = DateUtils.getDateString(EDateFormat.ISO_8601_SHORT.getFormat());
		}
		// * Obtenemos la fecha de anulación.
		
		elementAnnulment = JsonUtil.getElementFromPath(jsonElement, "FecAnulacion");
		
		
		if (!Strings.isNullOrEmpty(JsonUtil.getElementAsString(elementAnnulment))) {
			dateAnnulment = JsonUtil.getElementAsString(elementAnnulment);
		}

		if (!Strings.isNullOrEmpty(dateAnnulment)) {
			dateAnnulment = DateUtils.getDateString(EDateFormat.ISO_8601_SHORT.getFormat());
		}

		// * Obtenemos el número de la fórmula.
		element = JsonUtil.getElementFromPath(jsonElement, "NoPrescripcion");
		prescriptionNumber = JsonUtil.getElementAsString(element);

		// * Obtenemos el numero del material cliente.
		element = JsonUtil.getElementFromPath(jsonElement, "ID");
		mipresId = JsonUtil.getElementAsString(element);

		treatment = new TreatmentNeps();
		treatment.setIdMedicalPrescriptionNeps(idMedicalPrescription);
		treatment.setNumberPrescription(prescriptionNumber);
		treatment.setAttempts(0);
		treatment.setAddressingId(Integer.parseInt(idAddressing));
		treatment.setDeliveryNumber(deliveryNumber);
		treatment.setMipresId(mipresId);
		treatment.setAddressingMetaData(gson.toJson(jsonElement));
		treatment.setTypeTechnology(typeTechnology);
		treatment.setConsecutiveTechnology(consecutiveTechnology);
		treatment
				.setMaxDeliveryDate(DateUtils.getStringToDate(maxDeliveryDate, EDateFormat.ISO_8601_SHORT.getFormat()));
		treatment.setAddressingDate(DateUtils.getStringToDate(addressingDate, EDateFormat.ISO_8601_SHORT.getFormat()));

		treatment.setCodeTechnology(codeTechnology);
		treatment.setIdPrescriptionStatus(EPrescriptionStatus.CONSULTADA.getId());
		treatment.setIdFormulaState(EFormulaState.NO_ENTREGADO.getId());
		treatment.setVoidDate(DateUtils.getStringToDate(dateAnnulment, EDateFormat.ISO_8601_SHORT.getFormat()));

		this.treatmentDao.save(treatment);
	}

	public void updateTreatmentAddressing(JsonElement jsonElement, TreatmentNeps treatment) {

		JsonElement element, elementAnnulment;

		Gson gson;

		String dateAnnulment = null, idAddressing, addressingDate, maxDeliveryDate, codeTechnology, typeTechnology,
				consecutiveTechnology;

		gson = new Gson();

		// * Obtenemos el IDDireccionamiento.
		element = JsonUtil.getElementFromPath(jsonElement, "IDDireccionamiento");
		idAddressing = JsonUtil.getElementAsString(element);

		// * Obtenemos la fecha de direccionamiento.
		element = JsonUtil.getElementFromPath(jsonElement, "FecDireccionamiento");
		addressingDate = JsonUtil.getElementAsString(element);

		// * Obtenemos el tipo de tecnologia.
		element = JsonUtil.getElementFromPath(jsonElement, "TipoTec");
		typeTechnology = JsonUtil.getElementAsString(element);

		if (Strings.isNullOrEmpty(addressingDate)) {
			addressingDate = DateUtils.getDateString(EDateFormat.ISO_8601_SHORT.getFormat());
		}
		// * Obtenemos la fecha de vencimiento.
		element = JsonUtil.getElementFromPath(jsonElement, "FecMaxEnt");
		maxDeliveryDate = JsonUtil.getElementAsString(element);

		// * Obtenemos el codigo de tecnologia.
		element = JsonUtil.getElementFromPath(jsonElement, "ConTec");
		consecutiveTechnology = JsonUtil.getElementAsString(element);

		// * Obtenemos el codigo de tecnologia.
		element = JsonUtil.getElementFromPath(jsonElement, "CodSerTecAEntregar");
		codeTechnology = JsonUtil.getElementAsString(element);

		if (Strings.isNullOrEmpty(maxDeliveryDate)) {
			maxDeliveryDate = DateUtils.getDateString(EDateFormat.ISO_8601_SHORT.getFormat());
		}

		// * Obtenemos la fecha de anulación.
		elementAnnulment = JsonUtil.getElementFromPath(jsonElement, "FecAnulacion");
		if (!elementAnnulment.isJsonNull()) {
			dateAnnulment = JsonUtil.getElementAsString(elementAnnulment);
		}

		if (!Strings.isNullOrEmpty(dateAnnulment)) {
			dateAnnulment = DateUtils.getDateString(EDateFormat.ISO_8601_SHORT.getFormat());
		}

		if (Strings.isNullOrEmpty(dateAnnulment)) {
			dateAnnulment = DateUtils.getDateString(EDateFormat.ISO_8601_SHORT.getFormat());
		}

		treatment
				.setMaxDeliveryDate(DateUtils.getStringToDate(maxDeliveryDate, EDateFormat.ISO_8601_SHORT.getFormat()));
		treatment.setAddressingDate(DateUtils.getStringToDate(addressingDate, EDateFormat.ISO_8601_SHORT.getFormat()));

		treatment.setAddressingId(Integer.parseInt(idAddressing));
		treatment.setTypeTechnology(typeTechnology);
		treatment.setAddressingMetaData(gson.toJson(jsonElement));
		treatment.setConsecutiveTechnology(consecutiveTechnology);
		treatment.setVoidDate(DateUtils.getStringToDate(dateAnnulment, EDateFormat.ISO_8601_SHORT.getFormat()));

		treatment.setCodeTechnology(codeTechnology);
		treatment =  this.treatmentDao.save( treatment );

	}

}
