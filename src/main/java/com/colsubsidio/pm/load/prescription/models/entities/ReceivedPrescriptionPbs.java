/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.colsubsidio.pm.load.prescription.models.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Nathaly Gutierrez
 */
@Data
@Entity
@Table( name = "receivedprescriptionpbs" )
public class ReceivedPrescriptionPbs implements Serializable {

    @Id
    @GeneratedValue( generator = "UUID" )
    @GenericGenerator( name = "UUID",
                       strategy = "org.hibernate.id.UUIDGenerator" )
    private String id;

    @Column( name = "metadata" )
    private String metadata;

    @Column( name = "state" )
    private Integer state;    
    
    @Column( name = "isauthorized" )
    private boolean authorized;    
    
    @Column( name = "epsid" )
    private String epsId;

    @Column( name = "createdate" )
    @Temporal( TemporalType.TIMESTAMP )
    private Date createDate;

    @Column( name = "timestamp" )
    @Temporal( TemporalType.TIMESTAMP )
    private Date timeStamp;

    @PrePersist
    public void prePersist() {
        createDate = new Date();
    }

}
