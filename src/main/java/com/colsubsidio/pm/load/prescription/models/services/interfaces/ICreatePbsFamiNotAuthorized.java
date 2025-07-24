/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.colsubsidio.pm.load.prescription.models.services.interfaces;

/**
 *
 * @author Camilo
 */
import com.colsubsidio.pm.load.prescription.enums.EEps;
import com.colsubsidio.pm.load.prescription.models.dto.RequestReceiveEps;
import java.util.List;

public interface ICreatePbsFamiNotAuthorized {

    void receivePrescription( EEps eps, List<RequestReceiveEps> requestReceiveEpsList );
}
