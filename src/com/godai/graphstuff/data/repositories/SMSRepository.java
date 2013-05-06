package com.godai.graphstuff.data.repositories;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

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
	private static Uri ALL_SMS_URI = Uri.parse("content://sms");
	
	private static String [] SMS_COLUMNS = { "_id", "thread_id", "address",
											 "body", "person", "date",
											 "date_sent", "status", "protocol",
											 "read", "seen" };
	
	private ContentResolver _resolver;
	
	public SMSRepository(ContentResolver cr) {
		
		_resolver = cr;
		
	}
	
	public List<SMS> getAllMessagesFromAndToContact(Person contact) {
		
		List<SMS> messages = getIncomingMessagesFromContact(contact);
		messages.addAll(getOutgoingMessagesFromContact(contact));
		
		return messages;
		
	}
	
	public List<SMS> getIncomingMessagesFromContact(Person contact) {
		
		Cursor cursor = _resolver.query(INBOX_URI, SMS_COLUMNS, 
								 	    "person = ?",
								 	    new String[] { Integer.toString(contact.id()) },
								 	    null);
		
		List<SMS> messages =  getMessagesFromCursor(cursor);
		cursor.close();
		
		return messages;
		
	}
	
	public List<SMS> getOutgoingMessagesFromContact(Person contact) {
		
		Cursor cursor = _resolver.query(OUTBOX_URI, SMS_COLUMNS, 
				 				 	    "address = ? OR address = ? OR  address = ? OR address = ?",
				 				 	    contact.allPhonePermutations(),
				 				 	    null);
		
		List<SMS> messages = getMessagesFromCursor(cursor);
		cursor.close();
		
		return messages;
		
	}
	
	/** 
	 * @return A map of dates to message counts
	 */
	public Map<Calendar, Integer> getMessageCountsForDates(Person contact) {
		
		String where = "person = ? OR (address = ? OR address = ? OR address = ? OR address = ?)";
		String [] params = { Integer.toString(contact.id()), contact.phone(), 
							 contact.phoneNoAreaCode(), contact.spacedPhone(),
							 contact.spacedPhoneNoAreaCode() };
		
		Cursor cursor = _resolver.query(ALL_SMS_URI, new String [] { "date", "count(_id)" }, where, params, "date ASC");
		cursor.moveToFirst();
		
		do {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(new Date(cursor.getInt(0)));
			Log.d("PIE", "date: " + calendar.toString() + " count: " + cursor.getString(1) );
		} while(cursor.moveToNext());
		
		return null;
		
	}
	
	private List<SMS> getMessagesFromCursor(Cursor cursor) {
		
		List<SMS> messages = new ArrayList<SMS>();
		
		if(cursor.moveToFirst()) {
			do {
				messages.add(createSMSFromCursorRow(cursor));
			} while(cursor.moveToNext());
		}
		
		return messages;
		
	}
	
	private SMS createSMSFromCursorRow(Cursor cursor) {
		
		/* Pretty much the idea here is to just go through each of the columns
		 * in order. */
		return new SMS(cursor.getInt(0), cursor.getInt(1), cursor.getString(2),
					   cursor.getString(3), cursor.getInt(4), cursor.getLong(5),
					   cursor.getLong(6), cursor.getInt(7), cursor.getInt(8), 
					   cursor.getInt(9) > 0, cursor.getInt(10) > 0);
		
	}

}
