package com.colsubsidio.pm.load.prescription;

import org.apache.logging.log4j.util.Strings;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PmLoadPrescriptionApplicationTests {

	@Test
	void contextLoads() {
            
            getFileExtension("prueba.txt");
	}
        
        
       private String getFileExtension(String name) {
       
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return Strings.EMPTY; 
        }
        return name.substring(lastIndexOf);
    }

}
