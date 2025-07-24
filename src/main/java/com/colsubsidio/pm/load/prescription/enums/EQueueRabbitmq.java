package com.colsubsidio.pm.load.prescription.enums;

public enum EQueueRabbitmq {
    PROCESS_INFO_USER_NEPS( "PROCESS_INFO_USER_NEPS" ),
    PROCESS_INFO_USER_FAMI( "PROCESS_INFO_USER_FAMI" ),
    PROCESS_INFO_USER_OI( "PROCESS_INFO_USER_OI" );

    private final String name;

    private EQueueRabbitmq( String name ) {

        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static EQueueRabbitmq getEListQueueById( String nameQueue ) {
        for( EQueueRabbitmq eQueueRabbitmq : EQueueRabbitmq.values() ) {
            if( eQueueRabbitmq.getName().equals( nameQueue ) ) {
                return eQueueRabbitmq;
            }
        }

        return null;
    }
}
