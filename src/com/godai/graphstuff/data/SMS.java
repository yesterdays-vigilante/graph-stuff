package com.godai.graphstuff.data;

import java.util.ArrayList;

/**
 * Represents an SMS in the system. No, really.
 * 
 * @author zedd
 * @author josht
 * 
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
	
	public ArrayList<String> getWordArray(){
		String[] arr = _body.split(" ");
		ArrayList<String> words = new ArrayList<String>(); 
		
		for(int i = 0; i <arr.length;i++) {
			if(!(arr[i].equals(" "))) {
				words.add(arr[i]);
			}
		}
		
		return words;
	}
	
	
	public float getMeanWordLen(){
		float total = 0;
		
		ArrayList<String> words = getWordArray();
		
		for(int i=0;i<words.size();i++){
			total += words.get(i).length();
		}
		return (total/words.size());
	}
}
