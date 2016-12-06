package com.kvvssut.contentprovider;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.kvvssut.utils.ApplicationUtils;

public class AddContactActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addcontact);
		
		((Button) findViewById(R.id.callButton)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				String name = ((EditText) findViewById(R.id.contactsName)).getText().toString();
				String number = ((EditText) findViewById(R.id.phoneNumber)).getText().toString();
				String emailId = ((EditText) findViewById(R.id.emailIdText)).getText().toString();
				
				String[] names = name.trim().split("\\s+", 2);
				
				boolean flag = names.length == 2 ? ApplicationUtils.isContactAdded(getContentResolver(), names[0], names[1], number, emailId) :
					ApplicationUtils.isContactAdded(getContentResolver(), names[0], "", number, emailId);
				
				Log.v("AddContactActivity", String.format("Contact with name: %s, number: %s, email: %s added %s.", 
						name, number, emailId, flag ? "successfully" : "with failure"));
				
				finish();
			}
		});
		
	}
	
}
