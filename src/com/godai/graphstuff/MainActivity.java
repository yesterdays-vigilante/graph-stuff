package com.godai.graphstuff;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.godai.graphstuff.data.Person;
import com.godai.graphstuff.data.SMS;
import com.godai.graphstuff.data.repositories.PersonRepository;
import com.godai.graphstuff.data.repositories.SMSRepository;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
    	super.onCreate(savedInstanceState);        
        setContentView(R.layout.activity_main);
        
        TextView view = (TextView) findViewById(R.id.text_field_one);
        
        Person person = PersonRepository.getContactByID(getContentResolver(), 17);
        List<SMS> messages = SMSRepository.getAllMessagesFromAndToContact(getContentResolver(),
        																  person);
        
        String msgText = person.name() + " and I have exchanged a total of " + 
        				 messages.size() + " messages";
        
        view.setText(msgText);
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
