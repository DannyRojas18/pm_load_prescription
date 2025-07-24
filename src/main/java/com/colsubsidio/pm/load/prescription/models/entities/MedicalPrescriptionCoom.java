package com.colsubsidio.pm.load.prescription.models.entities;
/**
 * Author: Alberto Palencia <alberto.palencia@ingeneo.com.co>
 * CreateTime: 12/22/2020
 */

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table( name = "medicalprescriptioncoom" )
public class MedicalPrescriptionCoom {
    
    @Id
    @GeneratedValue( generator = "UUID" )
    @GenericGenerator( name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator" )
    @Column( name = "idmedicalprescriptioncoomeva" )
    private String idMedicalPrescriptionCoomeva;
    
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
