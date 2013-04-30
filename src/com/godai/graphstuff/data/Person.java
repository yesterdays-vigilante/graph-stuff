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
		_phone = phone;
		
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
	
	public String spacedPhone() {
		
		return StringHelper.addSpacesToPhone(_phone);
		
	}

}
