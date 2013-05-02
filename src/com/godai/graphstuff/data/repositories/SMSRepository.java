package com.godai.graphstuff.data.repositories;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;

import com.godai.graphstuff.data.Person;
import com.godai.graphstuff.data.SMS;

/**
 * Helps in getting SMSes from the system. We don't want to be throwing around
 * raw ContentResolver calls now, do we?
 * 
 * @author zedd
 */
public class SMSRepository {
	
	/* This is an undocumented content provider. Not good, but there doesn't 
	 * appear to be any other way to do it. It's not for clients, so no matter. */
	private static Uri INBOX_URI = Uri.parse("content://sms/inbox");
	private static Uri OUTBOX_URI = Uri.parse("content://sms/sent");
	
	private static String [] SMS_COLUMNS = { "_id", "thread_id", "address",
											 "body", "person", "date",
											 "date_sent", "status", "protocol",
											 "read", "seen" };
	
	public static List<SMS> getAllMessagesFromAndToContact(ContentResolver cr, Person contact) {
		
		List<SMS> messages = getIncomingMessagesFromContact(cr, contact);
		messages.addAll(getOutgoingMessagesFromContact(cr, contact));
		
		return messages;
		
	}
	
	public static List<SMS> getIncomingMessagesFromContact(ContentResolver cr, Person contact) {
		
		Cursor cursor = cr.query(INBOX_URI, SMS_COLUMNS, 
								 "person = ?",
							     new String[] { Integer.toString(contact.id()) },
								 null);
		
		return getMessagesFromCursor(cursor);
		
	}
	
	public static List<SMS> getOutgoingMessagesFromContact(ContentResolver cr, Person contact) {
		
		Cursor cursor = cr.query(OUTBOX_URI, SMS_COLUMNS, 
				 				 "address = ? OR address = ? OR  address = ? OR address = ?",
				 				 contact.allPhonePermutations(),
				 				 null);
		
		return getMessagesFromCursor(cursor);
		
	}
	
	private static List<SMS> getMessagesFromCursor(Cursor cursor) {
		
		List<SMS> messages = new ArrayList<SMS>();
		
		if(cursor.moveToFirst()) {
			do {
				messages.add(createSMSFromCursorRow(cursor));
			} while(cursor.moveToNext());
		}
		
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
