package com.godai.graphstuff.data.repositories;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;

import com.godai.graphstuff.data.Person;

/**
 * Helps get contacts from the database.
 * 
 * @author zedd
 */
public class PersonRepository {
	
	private static final Uri DATA_URI = Data.CONTENT_URI;
	
	// Currently only returns FIRST phone number
	public static Person getContactByID(ContentResolver cr, int id) {
		
		int rawID = -1;
		String phone = null;
		
		//Fetch the contact name separately.
		String name = getName(cr, id);
		
		// Set up fields to get the real ID and the phone number
		String where = String.format("%s = ? AND %s = ?", RawContacts.CONTACT_ID, Data.MIMETYPE);
		String [] data = { Integer.toString(id), CommonDataKinds.Phone.CONTENT_ITEM_TYPE };
		
		Cursor cursor = cr.query(DATA_URI, new String[] { Data.RAW_CONTACT_ID, Data.DATA1  }, where, data, null);
		
		if(cursor.moveToFirst()) {
			rawID = cursor.getInt(0);
			phone = cursor.getString(1);
		}
		
		if(rawID == -1)
			return null;
		
		return new Person(rawID, name, phone);
		
	}
	
	private static String getName(ContentResolver cr, int id) {
		
		String where = String.format("%s = ? AND %s = ?", RawContacts.CONTACT_ID, Data.MIMETYPE);
		String [] data = { Integer.toString(id), CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
		
		Cursor cursor = cr.query(DATA_URI, new String[] { Data.DATA1 }, where, data, null);
		
		if(cursor.moveToFirst())
			return cursor.getString(0);
		else
			return null;
		
	}
	

}
