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
	private static final Uri INBOX_URI = Uri.parse("content://sms/inbox");
	private static final Uri OUTBOX_URI = Uri.parse("content://sms/sent");
	private static final Uri ALL_SMS_URI = Uri.parse("content://sms");
	
	private static final String [] SMS_COLUMNS = { "_id", "thread_id", "address",
											 "body", "person", "date",
											 "date_sent", "status", "protocol",
											 "read", "seen" };
	
	private ContentResolver _resolver;
	
	public SMSRepository(ContentResolver cr) {
		
		_resolver = cr;
		
	}
	
	public List<SMS> getAllMessagesFromAndToContact(Person contact) {
		
		List<SMS> messages =  getMessages(ALL_SMS_URI, SMS_COLUMNS, 
				   createWhere(contact),
				   createWhereData(contact),
				   null);
		
		for(String number : contact.allPhonePermutations())
			Log.d("PENGUIN", number);
		
		Log.d("PENGUIN", Integer.toString(messages.size()));
		
		return messages;
		
	}
	
	public List<SMS> getIncomingMessagesFromContact(Person contact) {
		
		return getMessages(INBOX_URI, SMS_COLUMNS, 
						   createWhere(contact),
						   createWhereData(contact),
						   null);
		
	}
	
	public List<SMS> getOutgoingMessagesFromContact(Person contact) {
		
		return getMessages(OUTBOX_URI, SMS_COLUMNS, 
						   createWhere(contact),
				   		   createWhereData(contact), null);
	}
	
	public List<SMS> getMessagesForDayOf(Person contact, Date date) {
		
		DateTime startOfDay = new DateTime(date).withTimeAtStartOfDay();
		// Really truly seems to be the nicest way to get the end of the day x_X
		DateTime endOfDay = new DateTime(date).withHourOfDay(23).withMinuteOfHour(59).withSecondOfMinute(59);
		
		String where = createWhere(contact, "AND date BETWEEN ? AND ?");
		
		List<String> extras = new ArrayList<String>();
		extras.add(Long.toString(startOfDay.getMillis())); 
		extras.add(Long.toString(endOfDay.getMillis()));
		
		return getMessages(ALL_SMS_URI, SMS_COLUMNS, where, createWhereData(contact, extras),"date ASC");
		
	}
	
	/** 
	 * @return A map of dates to message counts
	 */
	public Map<Date, Integer> getMessageCountsForDates(Person contact) {
		
		Map<Date, Integer> values = new LinkedHashMap<Date, Integer>();
		
		Cursor cursor = _resolver.query(ALL_SMS_URI, new String [] { "date" },
										createWhere(contact), 
										createWhereData(contact), 
										"date ASC");
		
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
	
	private String createWhere(Person contact) {
		
		return createWhere(contact, null);
		
	}
	
	private String createWhere(Person contact, String extras) {
		
		StringBuilder builder = new StringBuilder("(address IN (");
		
		int numPermutations = contact.phone().size() * Person.PERMUTATIONS;
		
		for(int i = 0; i < numPermutations; i++) {
			builder.append("?");
			
			if(i != (numPermutations - 1))
				builder.append(",");
		}
		
		builder.append("))\n");
		builder.append("OR person = ?");
		
		if(extras != null)
			builder.append("\n" + extras);
		
		return builder.toString();
		
	}
	
	private String [] createWhereData(Person contact) {
		
		return createWhereData(contact, null);
		
	}
	
	private String [] createWhereData(Person contact, List<String> extras) {
		
		List<String> data = contact.allPhonePermutations();
		
		data.add(Integer.toString(contact.id()));
		
		if(extras != null)
			data.addAll(extras);
		
		return data.toArray(new String[data.size()]);
		
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
	
	
	
	/*
	 * NOTE: while I'm pretty sure this works, there are issues upstream with receiving full messages sets
	 * that courses this to seem incorrect. 
	 */
	public SMS getFirstInConvo( SMS mess, List<SMS> messages, int hours ){
	
		
		int index = messages.indexOf(mess);
		
		long gap = 3600000 * hours;
		
		while(index+1 < messages.size()){
			if( (messages.get(index).date() - messages.get(index+1).date()) > gap){
				return messages.get(index);
			}else{
				index++;
			}			
		}
		return messages.get(index);	
	}
	
	public SMS getLastInConvo( SMS mess, List<SMS> messages, int hours ){
	
		
		int index = messages.indexOf(mess);
		
		long gap = 3600000 * hours;
		
		while(index-1 >= 0){
			if( (messages.get(index-1).date()) - messages.get(index).date()  > gap){
				return messages.get(index);
			}else{
				index--;
			}			
		}
		return messages.get(index);	
	}
	
	public List<SMS> getConvo(SMS mess,  List<SMS> messages, int hours){		
		return messages.subList( messages.indexOf(getLastInConvo(mess, messages, hours)) ,messages.indexOf(getFirstInConvo(mess, messages, hours)));
	}
	
}
