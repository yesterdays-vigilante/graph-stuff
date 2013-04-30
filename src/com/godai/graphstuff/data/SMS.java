package com.godai.graphstuff.data;

/**
 * Represents an SMS in the system. No, really.
 * 
 * @author zedd
 */
public class SMS {
	
	private int _id;
	private int _threadID;
	private String _number;
	private String _body;
	private int _personID;
	private long _date;
	private long _dateSent;
	private int _status;
	private int _protocol;
	private boolean _read;
	private boolean _seen;
	
	public SMS(int id, int threadID, String number, String body, 
				   int personID, long date, long dateSent, int status, 
				   int protocol, boolean read, boolean seen) {
		
		_id = id;
		_threadID = threadID;
		_number = number;
		_body = body;
		_personID = personID;
		_date = date;
		_dateSent = dateSent;
		_status = status;
		_protocol = protocol;
		_read = read;
		_seen = seen;
		
	}

	public int id() {
		
		return _id;
		
	}

	public int threadID() {
		
		return _threadID;
		
	}

	public String number() {
		
		return _number;
		
	}

	public String body() {
		
		return _body;
		
	}

	public int personID() {
		
		return _personID;
		
	}

	public long date() {
		
		return _date;
		
	}

	
	public long dateSent() {
		
		return _dateSent;
		
	}

	public int status() {
		
		return _status;
		
	}

	public int protocol() {
		
		return _protocol;
		
	}

	public boolean isRead() {
		
		return _read;
		
	}

	public boolean isSeen() {
		
		return _seen;
		
	}
	
}
