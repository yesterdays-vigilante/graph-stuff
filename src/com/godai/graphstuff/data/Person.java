package com.godai.graphstuff.data;

import com.godai.graphstuff.helpers.StringHelper;

/**
 * ...Represents a contact on the phone.
 * 
 * @author zedd
 */
public class Person {
	
	private int _id;
	private String _name;
	private String _phone;
	
	public Person(int id, String name, String phone) {
		
		_id = id;
		_name = name;
		
		if(phone.charAt(0) == '+')
			_phone = phone;
		else
			_phone = String.format("%+d%s", AppData.countryCode(), phone.substring(1));
		
	}

	public int id() {
		
		return _id;
		
	}

	public String name() {
		
		return _name;
		
	}

	public String phone() {
		
		return _phone;
		
	}
	
	public String phoneNoAreaCode() {
		
		// The +1 is for the plus
		return "0" + _phone.substring(AppData.countryCodeLength() + 1);
		
	}
	
	public String spacedPhone() {
		
		return StringHelper.addSpacesToPhone(_phone);
		
	}
	
	public String spacedPhoneNoAreaCode() {
		
		return StringHelper.addSpacesToPhone(phoneNoAreaCode());
	
	}
	
	public String [] allPhonePermutations() {
		
		return new String [] { _phone, phoneNoAreaCode(), spacedPhone(), spacedPhoneNoAreaCode() };
		
	}

}
