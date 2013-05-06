package com.godai.graphstuff.data.repositories;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

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
		
		return getMessages(INBOX_URI, SMS_COLUMNS, 
						   "person = ?",
						   new String[] { Integer.toString(contact.id()) },
						   null);
		
	}
	
	public List<SMS> getOutgoingMessagesFromContact(Person contact) {
		
		return getMessages(OUTBOX_URI, SMS_COLUMNS, 
				 		   "address = ? OR address = ? OR  address = ? OR address = ?",
				 		   contact.allPhonePermutations(), null);
	}
	
	public List<SMS> getMessagesForDayOf(Person contact, Date date) {
		
		DateTime startOfDay = new DateTime(date).withTimeAtStartOfDay();
		// Really truly seems to be the nicest way to get the end of the day x_X
		DateTime endOfDay = new DateTime(date).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
		
		String where = "(person = ? OR (address = ? OR address = ? OR address = ?" +
					   " OR address = ?)) AND date BETWEEN ? AND ?";
		String [] params = { Integer.toString(contact.id()), contact.phone(),
							 contact.phoneNoAreaCode(), contact.spacedPhone(),
							 contact.spacedPhoneNoAreaCode(),
							 Long.toString(startOfDay.getMillis()), 
							 Long.toString(endOfDay.getMillis()) };
		
		return getMessages(ALL_SMS_URI, SMS_COLUMNS, where, params,"date ASC");
		
	}
	
	/** 
	 * @return A map of dates to message counts
	 */
	public Map<Date, Integer> getMessageCountsForDates(Person contact) {
		
		Map<Date, Integer> values = new LinkedHashMap<Date, Integer>();
		
		// Unfortunately, we can't do GROUP BY in a ContentResolver
		String where = "person = ? OR (address = ? OR address = ? OR address = ? OR address = ?)";
		String [] params = { Integer.toString(contact.id()), contact.phone(), 
							 contact.phoneNoAreaCode(), contact.spacedPhone(),
							 contact.spacedPhoneNoAreaCode() };
		
		Cursor cursor = _resolver.query(ALL_SMS_URI, new String [] { "date" }, where, params, "date ASC");
		cursor.moveToFirst();
		
		do {
			Date date = new LocalDate(cursor.getLong(0)).toDate();
			Integer count = values.get(date);
					
			if(count == null)
				values.put(date, 1);
			else
				values.put(date, count + 1);
		} while(cursor.moveToNext());
		
		return values;
		
	}
	
	private List<SMS> getMessages(Uri uri, String [] columns, String condition, String [] params, String order) {
		
		Cursor cursor = _resolver.query(uri, columns, condition, params, order);
		cursor.moveToFirst();
		
		return getMessagesFromCursor(cursor);
		
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
