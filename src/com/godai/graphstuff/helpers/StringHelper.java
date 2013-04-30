package com.godai.graphstuff.helpers;

/**
 * Just a collection of string utilities. Nothing special here.
 * 
 * @author zedd
 */
public class StringHelper {
	
	/**
	 * Adds spaces to a phone number as a string
	 * 
	 * @param phoneNumber The unspaced number to 'fix'
	 * @return The same number, but with beautiful spaces added
	 */
	public static String addSpacesToPhone(String phoneNumber) {
		
		StringBuffer buffer = new StringBuffer(phoneNumber);
				
		for(int i = 3 + (phoneNumber.length() % 3); i < phoneNumber.length(); i += 4) {
			buffer.insert(i, ' ');
		}
		
		return buffer.toString();
		
	}

}
