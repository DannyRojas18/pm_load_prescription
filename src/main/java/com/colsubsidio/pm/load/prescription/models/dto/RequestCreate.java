/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.colsubsidio.pm.load.prescription.models.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class RequestCreate implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String numPreauthorization;

    private String numberMipres;

}
