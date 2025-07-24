package com.colsubsidio.pm.load.prescription.models.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 * Clase para la persistencia de los datos
 *
 * @author Ingeneo
 *
 */
@Data
@Entity
@Table(name = "prescriptionsource")
public class PrescriptionSource implements Serializable{

    @Id
    private Long id;

    @Column(name = "sourcename")
    private String sourceName;

    @Column(name = "configuration")
    private String configuration;

    @Column(name = "isactive")
    private boolean isActive;

}
