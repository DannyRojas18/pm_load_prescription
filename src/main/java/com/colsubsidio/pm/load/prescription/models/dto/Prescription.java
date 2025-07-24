package com.colsubsidio.pm.load.prescription.models.dto;


import com.colsubsidio.pm.load.prescription.enums.EFormulaState;
import com.colsubsidio.pm.load.prescription.enums.EPrescriptionStatus;
import com.colsubsidio.pm.load.prescription.enums.ETypePrescription;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Clase para establecer la persistencia en la base de datos
 *
 * @author Ingeneo
 *
 */
@Data
@Entity
@Table( name = "prescription" )
public class Prescription implements Serializable {

    @Id
    @GeneratedValue( generator = "UUID" )
    @GenericGenerator( name = "UUID",
                       strategy = "org.hibernate.id.UUIDGenerator" )
    private String id;

    @Column( name = "prescriptionsourceid" )
    private Long prescriptionSourceId;

    @Column( name = "prescriptionid" )
    private String prescriptionId;

    @Column( name = "addressingid" )
    private String addressingId;

    @Column( name = "noprescription" )
    private String noPrescription;

    @Column( name = "programationid" )
    private String programationId;

    @Column( name = "deliveryreportid" )
    private String deliveryReportId;

    @Column( name = "noprescriptioneps" )
    private String noPrescriptionEps;

    @Column( name = "noideps" )
    private String noIdEps;

    @Column( name = "codeps" )
    private String codEps;

    @Enumerated( EnumType.ORDINAL )
    @Column( name = "prescriptionstatusid" )
    private EPrescriptionStatus prescriptionStatusId;

    @Column( name = "datemaxdelivery" )
    private String dateMaxDelivery;

    @Column( name = "typetechnology" )
    private String typeTechnology;

    @Column( name = "deliverynumber" )
    private String deliveryNumber;

    @Column( name = "consecutivetechnology" )
    private String consecutiveTechnology;

    @Column( name = "metadata", length = 4000 )
    private String metaData;

    @Column( name = "createdate" )
    @Temporal( TemporalType.TIMESTAMP )
    private Date createDate;

    @Column( name = "timestamp" )
    @Temporal( TemporalType.TIMESTAMP )
    private Date timeStamp;

    @Column( name = "Attemps" )
    private int attempts;

    @Enumerated( EnumType.ORDINAL )
    @Column( name = "typeprescription" )
    private ETypePrescription typePrescription;

    @Column( name = "typeidpatient" )
    private String typeIdPatient;

    @Column( name = "noidpatient" )
    private String noIdPatient;

    @Column( name = "codebranchprov" )
    private String codeBranchProv;

    @Enumerated( EnumType.ORDINAL )
    @Column( name = "idformulastate" )
    private EFormulaState idFormulaState;

    @PrePersist
    public void prePersist() {
        createDate = new Date();
        timeStamp = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        timeStamp = new Date();
    }

}
