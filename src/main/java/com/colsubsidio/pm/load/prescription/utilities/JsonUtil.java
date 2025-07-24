package com.colsubsidio.pm.load.prescription.utilities;

import com.google.gson.JsonElement;

public class JsonUtil {

	public static String parse( JsonElement json, String text ) {
		try {
			return json.getAsJsonObject().get( text ).getAsString();
		} catch (Exception e) {
			return "";
		}
	}
}
