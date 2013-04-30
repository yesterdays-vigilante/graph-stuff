package com.godai.graphstuff.data.repositories;

import java.util.ArrayList;
import java.util.List;

import com.godai.graphstuff.data.Person;
import com.godai.graphstuff.data.SMS;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

/**
 * Helps in getting SMSes from the system. We don't want to be throwing around
 * raw ContentResolver calls now, do we?
 * 
 * @author zedd
 */
public class SMSRepository {
	
	/* This is an undocumented content provider. Not good, but there doesn't 
	 * appear to be any other way to do it. It's not for clients, so no matter. */
	private static Uri ALL_SMS_URI = Uri.parse("content://sms");
	private static String [] SMS_COLUMNS = { "_id", "thread_id", "address",
											 "body", "person", "date",
											 "date_sent", "status", "protocol",
											 "read", "seen" };
	
	public static List<SMS> getAllMessagesFromAndToContact(ContentResolver cr, Person contact) {
		
		List<SMS> messages = new ArrayList<SMS>();
		Cursor cursor = cr.query(ALL_SMS_URI, SMS_COLUMNS, 
								 "(type = 1 AND person = ?) OR " + 
							     "(type = 2 AND (address = ? OR address = ?))",
							     new String[] { Integer.toString(contact.id()),
								                contact.spacedPhone(), contact.phone() },
								 null);
		
		if(cursor.moveToFirst()) {
			do {
				messages.add(createSMSFromCursorRow(cursor));
			} while (cursor.moveToNext());
		}
			
		cursor.close();
		
		return messages;
		
	}
	
	private static SMS createSMSFromCursorRow(Cursor cursor) {
		
		/* Pretty much the idea here is to just go through each of the columns
		 * in order. */
		return new SMS(cursor.getInt(0), cursor.getInt(1), cursor.getString(2),
					   cursor.getString(3), cursor.getInt(4), cursor.getLong(5),
					   cursor.getLong(6), cursor.getInt(7), cursor.getInt(8), 
					   cursor.getInt(9) > 0, cursor.getInt(10) > 0);
		
	}

}
