package com.colsubsidio.pm.load.prescription.models.dto;

import java.io.File;
import lombok.Data;

/**
 *
 * @author Alberto Palencia Benedetti
 */

@Data
public class FileShareConfigDto {
    
    private File tempFile;
    private File logFile;
    private String fileName;
    private String storageConnectionString; 
    private String fileShare;
    private String directoryError;
}
