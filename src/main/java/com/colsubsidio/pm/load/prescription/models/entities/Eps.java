package com.colsubsidio.pm.load.prescription.models.entities;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 * Clase para la persistencia de los datos
 * @author Ingeneo
 *
 */
@Data
@Entity
@Table(name = "eps")
public class Eps implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "id")
	private int id;

	@Column(name = "epsname")
	private String epsName;

	@Column(name = "factorybusinesslogic")
	private String factoryBussinesLogic;

	@Column(name = "configuration")
	private String configuration;

	@Column(name = "orden")
	private int order;

	@Column(name = "isactive")
	private boolean isActive;

	@Column(name = "nit")
	private String nit;
	
	@Column(name = "epsid")
	private String codigo;
	
	@Column(name = "codeinsurance")
	private String codigoAseguradora;

}
