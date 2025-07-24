package com.colsubsidio.pm.load.prescription.models.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table( name = "fieldvalidator" )
@SuppressWarnings( "ValidAttributes" )
public class FieldValidator implements Serializable {
    private static final long serialVersionUID = -2607248002553420619L;

    @Id
    private String id;

    @Column( name = "values" )
    private String value;

    @Column( name = "changevalue" )
    private String changeValue;

    @Column( name = "validatordetail" )
    private String validatorDetail;

    @Column( name = "category" )
    private String category;

    @Column( name = "state" )
    private Integer state;

    @Column( name = "createdate" )
    @Temporal( TemporalType.TIMESTAMP )
    private Date createDate;

    @Column( name = "timestamp" )
    @Temporal( TemporalType.TIMESTAMP )
    private Date timestamp;

    @PrePersist
    public void prePersist() {
        this.createDate = new Date();
        this.timestamp = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        this.timestamp = new Date();
    }
}
