package com.colsubsidio.pm.load.prescription.models.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

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

@Data
@Entity
@Table(name = "Medication")
public class Medication implements Serializable {
	
	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	private UUID id;
	
	@Column(name = "prescriptionid")
	private String prescriptionId;
	
	@Column(name = "epsid")
	private Integer epsId;        
        
        @Column(name = "numpreauthorization")
        private String numPreauthorization;
	
	@Column(name = "metadata")
	private String metadata;
	
        @Column(name = "extradata")
	private String extradata;
	
	@Column(name = "timestamp")
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt;
	
	@PrePersist
	public void prePersist() {
		createdAt = new Date();
	}
	
}
