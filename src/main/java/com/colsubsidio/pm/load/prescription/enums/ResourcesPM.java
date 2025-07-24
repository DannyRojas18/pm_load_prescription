package com.colsubsidio.pm.load.prescription.enums;

/**
 *
 * @author Ingeneo
 */
public enum ResourcesPM{
    
    MIPRES(1, "MIPRES"),
    SURA_MIPRES(2, "SURA_MIPRES"),
    FAMI(3, "FAMI"),
    COOMEVA(4, "COOMEVA"),
    NEWEPS(5, "NEWEPS");
    
    private Integer id;
    private String name;
    
    private ResourcesPM(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public String getName() {
        return this.name;
    }
    
}
