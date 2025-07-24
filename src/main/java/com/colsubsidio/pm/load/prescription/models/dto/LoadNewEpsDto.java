package com.colsubsidio.pm.load.prescription.models.dto;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 *
 * @author ALBERTO PALENCIA
 */
@Data
public class LoadNewEpsDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
    private String loadNewEpsId;
    private String codePreauthorizationNeps;
    private String authorizationCode;
    private String operator;
    private String documentType;
    private String documentId;
    private String userName;
    private String gender;
    private String birthDate;
    private String address;
    private String phone;
    private String ipsCode;
    private String ipsPrescription;
    private String ipsName;
    private String prescriptionDate;
    private String codeAA;
    private String epsCode;
    private String medicineName;
    private String prescribedMedication;
    private String amountMedication;
    private String dosingRegimen;
    private String municipalityName;
    private String deliveryDate;
    private String cellPhone;
    private String email;
    private String professionalDocumentType;
    private String professionalDocument;
    private String professionalName;
    private String professionalSurname;
    private String professionalSecondSurname;
    private String metadata;
    private Date createDate;
    private Date timeStamp;
    private Integer status;

}
