package com.godai.graphstuff.data;

import java.util.ArrayList;
import java.util.List;

import com.godai.graphstuff.helpers.StringHelper;

/**
 * ...Represents a contact on the phone.
 * 
 * @author zedd
 */
public class Person {
	
	public static int PERMUTATIONS = 4;
	
	private int _id;
	private String _name;
	private List<String> _phoneNumbers;
	
	public Person(int id, String name, List<String> phoneNumbers) {
		
		_id = id;
		_name = name;
		_phoneNumbers = addAreaCodeToPhone(phoneNumbers);
				
	}

	public int id() {
		
		return _id;

		
	}

	public String name() {
		
		return _name;
		
	}

	public List<String> phone() {
		
		return _phoneNumbers;
		
	}
	
	public List<String> allPhonePermutations() {
		
		List<String> numbers = new ArrayList<String>();
		
		for(String number : _phoneNumbers) {
			numbers.add(number);
			numbers.add(stripAreaCode(number));
			numbers.add(StringHelper.addSpacesToPhone(number));
			numbers.add(StringHelper.addSpacesToPhone(stripAreaCode(number)));
		}
		
		return numbers;		
	}
	
	private String stripAreaCode(String number) {
		
		// The +1 is for the plus character
		String numberPart = number.substring(AppData.countryCodeLength() + 1);
		
		if(numberPart.length() >= 9)		
			return "0" + numberPart;
		else
			return numberPart;
		
	}
	
	private List<String> addAreaCodeToPhone(List<String> numbers) {
		
		List<String> modNumbers = new ArrayList<String>();
		
		for(String number : numbers) {
			if(number.charAt(0) == '+')
				modNumbers.add(number);
			else {
				/* If the number has a '0' at the start, it needs to
				 * be replaced with the area code. Otherwise, the area
				 * code is just added on. */
				if(number.charAt(0) == '0')
					number = number.substring(1);
				
				modNumbers.add(String.format("%+d%s", AppData.countryCode(), number));
			}
		}
		
		return modNumbers;
		
	}

}
