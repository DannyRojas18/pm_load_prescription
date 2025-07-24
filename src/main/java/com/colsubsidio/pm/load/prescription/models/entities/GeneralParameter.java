package com.colsubsidio.pm.load.prescription.models.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "generalparameter")
public class GeneralParameter implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;
	
	@Column(name = "parametername")
	private String parameterName;
	
	@Column(name = "parametervalue")
	private String parameterValue;
	
	@Column(name = "extradata")
	private String extraData;
}
