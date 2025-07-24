package com.colsubsidio.pm.load.prescription.utilities;

/**
 *
 * @author Alberto Palencia Benedetti
 */
public class NumberUtil {

    public static boolean isNumeric(String cadena) {
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
