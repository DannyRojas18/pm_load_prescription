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
@Table( name = "medicalprescriptionoi" )
public class MedicalPrescriptionOI {
	
	@Id
    @GeneratedValue( generator = "UUID" )
    @GenericGenerator( name = "UUID",
                       strategy = "org.hibernate.id.UUIDGenerator" )
    @Column( name = "idmedicalprescriptionoi" )
    private String idMedicalPrescription;

	@Column( name = "idprescriptionsource" )
    private Integer idPrescriptionSource;

    @Column( name = "mipresnumber" )
    private String mipresNumber;

    @Column( name = "ideps" )
    private Integer idEps;

    @Column( name = "epsnit" )
    private String epsNit;

    @Column( name = "epscode" )
    private String epsCode;

    @Column( name = "prescriptiontype" )
    private Integer prescriptionType;

    @Column( name = "typeidpatient" )
    private String typeIdPatient;

    @Column( name = "numberidpatient" )
    private String numberIdPatient;

    @Column( name = "createdate" )
    @Temporal( TemporalType.TIMESTAMP )
    private Date createDate;

    @Column( name = "updatedate" )
    @Temporal( TemporalType.TIMESTAMP )
    private Date updateDate;
    
    @Column( name = "idseat" )
    private Integer idSeat;
    
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
