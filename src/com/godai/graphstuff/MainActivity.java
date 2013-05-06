package com.godai.graphstuff;

import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.Contacts;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.godai.graphstuff.data.AppData;
import com.godai.graphstuff.data.Person;
import com.godai.graphstuff.data.SMS;
import com.godai.graphstuff.data.repositories.PersonRepository;
import com.godai.graphstuff.data.repositories.SMSRepository;

public class MainActivity extends Activity {

	private static final int CONTACT_PICKER_RESULT = 1001; 
	
	TextView text;
	SMSRepository repository;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);        
        setContentView(R.layout.activity_main);
        
        AppData.intialise((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE), getResources());
                
        text = (TextView) findViewById(R.id.text_field_one);
        getResources();
        
        text.setText("Please select a contact");
        
        repository = new SMSRepository(getContentResolver());
        
    }
    
    public void launchContactPicker(View view) {
    	
    	Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Contacts.CONTENT_URI);
    	startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    
    }
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	
    	if(resultCode == RESULT_OK) {
    		if(requestCode == CONTACT_PICKER_RESULT) {
    			int id = Integer.parseInt(data.getData().getLastPathSegment());
    			Person person = PersonRepository.getContactByID(getContentResolver(), id);
    			
    			if(person == null || person.phone() == null)
    				text.setText("No phone number recorded");
    			else {
    		        List<SMS> messages = repository.getAllMessagesFromAndToContact(person);
    		        Map<Date, Integer> counts = repository.getMessageCountsForDates(person);
    		        for(Date date : counts.keySet()) {
    		        	Log.d("PIE", date.toString() + ": " + counts.get(date));
    		        }

    		        String msgText = person.name() + " and I have exchanged a total of " + 
    		        				 messages.size() + " messages";

    		        text.setText(msgText);
    			}
    		}
    	}
    	else {
    		Log.w("DEBUG", "Contact picker did not return OK");
    	}
    	
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
