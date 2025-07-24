package com.colsubsidio.pm.load.prescription.models.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table( name = "treatmentoi" )
public class TreatmentOI {
	
	@Id
    @GeneratedValue( generator = "UUID" )
    @GenericGenerator( name = "UUID",
                       strategy = "org.hibernate.id.UUIDGenerator" )
    @Column( name = "idtreatmentoi" )
    private String idTreatment;

    @Column( name = "idmedicalprescriptionoi" )
    private String idMedicalPrescription;

    @Column( name = "numberprescription" )
    private String numberPrescription;
    
    @Column( name = "mipresid" )
    private String mipresId;
    
    @Column( name = "preauthorizationnumber" )
    private String preauthorizationNumber;
    
    @Column( name = "authorizationnumber" )
    private String authorizationNumber;
    
    @Column( name = "typetechnology" )
    private String typeTechnology;
    
    @Column( name = "consecutivetechnology" )
    private String consecutiveTechnology;
    
    @Column( name = "deliverynumber" )
    private String deliveryNumber;
    
    @Column( name = "codetechnology" )
    private String codeTechnology;
    @Column( name = "cum" )
    private String cum;
    
    @Column( name = "addressingid" )
    private Integer addressingId;
    
    @Column( name = "programationid" )
    private String programationId;
    
    @Column( name = "deliveryreportid" )
    private String deliveryReportId;
    
    @Column( name = "idprescriptionstatus" )
    private Integer idPrescriptionStatus;
    
    @Column( name = "maxdeliverydate" )
    @Temporal( TemporalType.TIMESTAMP )
    private Date maxDeliveryDate;
    
    @Column( name = "addressingmetadata" )
    private String addressingMetaData;
    
    @Column( name = "epsmetadata" )
    private String epsMetadata;
    
    @Column( name = "attempts" )
    private Integer attempts;
    
    @Column( name = "codebranchprovisioning" )
    private String codeBranchProvisioning;
    
    @Column( name = "addressingdate" )
    @Temporal( TemporalType.TIMESTAMP )
    private Date addressingDate;
    
    @Column( name = "idformulastate" )
    private Integer idFormulaState;
    
    @Column( name = "materialid" )
    private String materialId;
    
    @Column( name = "createdate" )
    @Temporal( TemporalType.TIMESTAMP )
    private Date createDate;
    
    @Column( name = "updatedate" )
    @Temporal( TemporalType.TIMESTAMP )
    private Date updateDate;
    
    @Column( name = "voiddate" )
    @Temporal( TemporalType.TIMESTAMP )
    private Date voidDate;
    
    @PrePersist
    public void prePersist() {
        this.createDate = new Date();
        this.updateDate = new Date();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updateDate = new Date();
    }
}
