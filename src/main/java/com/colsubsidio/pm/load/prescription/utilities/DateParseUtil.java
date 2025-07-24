package com.colsubsidio.pm.load.prescription.utilities;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;

import com.colsubsidio.utilities.log.LogsManager;

/**
 *
 * @author ALBERTO PALENCIA
 */

public class DateParseUtil {
	
	@Autowired
    private static LogsManager log;

    public static Boolean dateValidate( String date ) {

        boolean response = true;

        try {

            SimpleDateFormat formatoFecha = new SimpleDateFormat( Constants.FORMAT_YEAR_MONTH_DAY );

            formatoFecha.setLenient( false );

            formatoFecha.parse( date );

        }
        catch( ParseException e ) {
            response = false;
        }

        return response;
    }

    public static String setDateYearMonthDay( String date ) {

        String response = "";

        try {

            SimpleDateFormat formatoFecha = new SimpleDateFormat( Constants.FORMAT_YEAR_MONTH_DAY );

            formatoFecha.setLenient( false );

            formatoFecha.parse( date );

        }
        catch( ParseException e ) {
            response = "";
        }

        return response;
    }

    /**
     * Retorna un date con el formato yyyy-mm-dd
     *
     * @return
     */
    public static String getDateNow() {
        String strDate;
        Date dateParse = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat( Constants.FORMAT_YEAR_MONTH_DAY );
        strDate = dateFormat.format( dateParse );
        return strDate;
    }

    /**
     * Retorna un string y le anade 365 dias a la fecha con el formato yyyy-mm-dd
     *
     * @return
     */
    public static String dateNowAdd365Day() {
        Calendar calendar = Calendar.getInstance();
        Date date = sumRestAddDay( calendar.getTime(), Constants.DAY_OF_YEAR );
        String dateFormat = parseDateString( date );
        return dateFormat;
    }

    public static Date sumRestAddDay( Date fecha, int dias ) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime( fecha );
        calendar.add( Calendar.DAY_OF_YEAR, dias );
        return calendar.getTime();
    }

    /*
	 * Permite convertir un String en fecha (Date).
	 * 
	 * @param fecha Cadena de fecha dd/MM/yyyy 2019-01-01
	 * 
	 * @return Objeto Date
     */
    public static Date parseStringDate( String fecha ) {
        SimpleDateFormat formatter = new SimpleDateFormat( Constants.FORMAT_YEAR_MONTH_DAY, Locale.ENGLISH );
        Date date = null;
        try {
            date = formatter.parse( fecha );
        }
        catch( ParseException e ) {
            log.error( "Error en el metodo parseStringDate: " + e );
        }

        return date;
    }

    /*
	 * Permite convertir un Date en String 
	 * 
	 * @param fecha Cadena de fecha dd/MM/yyyy 2019-01-01
	 * 
	 * @return Objeto Date
     */
    public static String parseDateString( Date object ) {
        DateFormat formatter = new SimpleDateFormat( Constants.FORMAT_YEAR_MONTH_DAY, Locale.ENGLISH );
        String date = formatter.format( object );
        return date;
    }

    public static String parseDate( String inputString ) {
        SimpleDateFormat fromUser = new SimpleDateFormat( "yyyyMMdd" );
        SimpleDateFormat myFormat = new SimpleDateFormat( "yyyy-MM-dd" );
        String reformattedStr = "";
        try {
            reformattedStr = myFormat.format( fromUser.parse( inputString ) );
        }
        catch( ParseException e ) {
            try {
                myFormat.format( myFormat.parse( inputString ) );
                reformattedStr = myFormat.format( inputString );
            }
            catch( ParseException ex ) {
                log.error( "El formato de la fecha " + inputString + " es invalido, formatos aceptados 'yyyyMMdd' y 'yyyy-MM-dd'" );
            }
        }
        return reformattedStr;
    }

}
