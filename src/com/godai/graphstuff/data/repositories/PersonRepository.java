package com.godai.graphstuff.data.repositories;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.RawContacts.Entity;

import com.godai.graphstuff.data.Person;

/**
 * Helps get contacts from the database.
 * 
 * @author zedd
 */
public class PersonRepository {
	
	private static final String [] MIMETYPES = { CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
												 CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE };
	
	public static Person getContactByID(ContentResolver cr, int id) {
		
		String phone;
		String name;
		
		Uri uri = ContentUris.withAppendedId(RawContacts.CONTENT_URI, id);
		uri = Uri.withAppendedPath(uri, Entity.CONTENT_DIRECTORY);
		
		/* Opaque though this is, it fetches the contact's name and phone
		 * number based on the ID given. */
		Cursor cursor = cr.query(uri, new String [] { Entity.DATA1 },
								 Entity.MIMETYPE + " = ? OR " +
								 Entity.MIMETYPE + " = ?",
								 MIMETYPES, Entity.MIMETYPE);
		
		/* The above SHOULD always return two rows with the name first, then
		 * the phone number. if either row doesn't exist, we can't make the 
		 * contact. */
		if(cursor.moveToFirst())
			name = cursor.getString(0);		
		else
			return null;
		
		if(cursor.moveToNext())
			phone = cursor.getString(0);
		else	
			return null;
		
		cursor.close();
		
		return new Person(id, name, phone);
		
	}

}
