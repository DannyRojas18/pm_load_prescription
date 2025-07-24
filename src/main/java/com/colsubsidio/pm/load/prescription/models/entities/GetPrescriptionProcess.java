/*
 * Codigo fuente propiedad de Colsubsidio
 * Gerencia de Tecnologia
 */
package com.colsubsidio.pm.load.prescription.models.entities;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Table( name = "getaddressingtemp" )
public class GetPrescriptionProcess implements Serializable {
    private static final long serialVersionUID = 3858339856718929391L;

    @Id
    @Column( name = "idgetaddressingtemp" )
    @GeneratedValue( generator = "UUID" )
    @GenericGenerator( name = "UUID", strategy = "org.hibernate.id.UUIDGenerator" )
    private UUID idEnqueueData;

    @Column( name = "mipres" )
    private String mipres;

    @Column( name = "status" )
    private String status;

}
