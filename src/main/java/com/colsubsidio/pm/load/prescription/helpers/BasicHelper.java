package com.colsubsidio.pm.load.prescription.helpers;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import java.net.URISyntaxException;
import java.text.ParseException;

/*
 * Clase que contiene metodos comunes usados en la aplicacion
 *
 * @author Ingeneo
 *
 */
public class BasicHelper {

    /*
     * Genera un String id unico de 36 caracteres
     *
     * @return
     */
    public static String Uuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /*
     * Convierte un Objecto Java en un String Json
     *
     * @param object
     * @return
     */
    public static String toJson( Object object ) {
        Gson gson = new Gson();
        return gson.toJson( object );
    }

    /*
     * Convierte de un String Json a una Clase Java
     *
     * @param json
     * @param object
     * @return
     */
    public static Object fromJson( String json, Class<?> object ) {
        Gson gson = new Gson();
        return gson.fromJson( json, object );
    }

    /*
     * Convierte un string en un formato Date
     *
     * @param strdate
     * @param datePattern
     * @return
     */
    public static Date convertToDate( String strdate, String datePattern ) {

        Date date = null;

        if( !StringUtils.isEmpty( datePattern ) ) {
            try {
                SimpleDateFormat dateFormatter = new SimpleDateFormat( datePattern );
                date = dateFormatter.parse( strdate );
            }
            catch( ParseException ex ) {
                date = null;
            }
        }

        return date;
    }

    /*
     * Devuelve el tamaño de un Iterable
     *
     * @param data Iterable
     * @return
     */
    public static int sizeIterable( Iterable<?> data ) {

        int counter = 0;

        if( data instanceof Collection ) {
            counter = ( ( Collection<?> ) data ).size();
        }

        return counter;
    }

    /*
     * construye una url y le asigna los parametros busca {index} y lo reemplaza
     * por el parametro indicado en su respectiva posicion
     *
     * @param url
     * @param params
     * @return
     */
    public static URI buildUri( String url, String... params ) {

        URI uri;

        try {

            Integer count = 0;

            for( String param : params ) {
                url = url.replace( "{" + ( count++ ) + "}", param );
            }

            uri = new URI( url );
        }
        catch( URISyntaxException ex ) {
            uri = null;
        }

        return uri;
    }

}
