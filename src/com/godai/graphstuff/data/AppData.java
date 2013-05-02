package com.godai.graphstuff.data;

import android.content.res.Resources;
import android.telephony.TelephonyManager;

public class AppData {
	
	public static final String PACKAGE = "com.godai.graphstuff";
	
	private static TelephonyManager _telephony;
	private static Resources _resources;
	
	public static void intialise(TelephonyManager telephony, Resources resources) {
		
		_telephony = telephony;
		_resources = resources;
		
	}
	
	public static int countryCode() {
		
		String code = _telephony.getSimCountryIso().toUpperCase(_resources.getConfiguration().locale);
		
		return getIntegerByName(code);
		
	}
	
	public static int countryCodeLength() {
		
		return Integer.toString(countryCode()).length();
		
	}
	
	public static int getIntegerByName(String name) {
		
		return _resources.getInteger(_resources.getIdentifier(name, "integer", PACKAGE));
		
	}

}
