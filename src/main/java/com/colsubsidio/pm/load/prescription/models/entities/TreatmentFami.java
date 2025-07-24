package com.colsubsidio.pm.load.prescription.models.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * @author: Alberto Palencia <alberto.palencia@ingeneo.com.co>
 * CreateTime: 11/19/2020
 * Description: Entidad de famisanar
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table( name = "treatmentfami" )
public class TreatmentFami {
    
    @Id
    @GeneratedValue( generator = "UUID" )
    @GenericGenerator( name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator" )
    @Column( name = "idtreatmentfami" )
    private String IdTreatmentFami;
    
    @Column( name = "idmedicalprescriptionfami" )
    private String idMedicalPrescriptionFami;
    
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
