package com.kvvssut.contentprovider;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kvvssut.services.ModifiedTreeMap;
import com.kvvssut.utils.ApplicationUtils;
import com.kvvssut.utils.ExtendedApplicationUtils;

public class DeleteContactActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deletecontact);
		
		Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
		Map<String, List<String>> contactsMap = ApplicationUtils.readCachedContacts(MainActivity.mainActivity);
		
		if(contactsMap.size() == 0){
			Log.i("DeleteContactActivity" , "No cached data found! Caching contacts..");
			contactsMap = ApplicationUtils.fetchAllContacts(getContentResolver(), contactsMap);
			ApplicationUtils.chacheContacts(MainActivity.mainActivity, contactsMap);
			Log.i("DeleteContactActivity" , "Contacts Loaded..");
		}

		final ListAdapter listAdapter = new ListAdapter(DeleteContactActivity.this, ApplicationUtils.setGroupItems(contactsMap), new int[]{R.layout.delete_group_view, R.layout.delete_child_view});
		ExpandableListView expandList = (ExpandableListView) findViewById(R.id.deleteContactsView);
		AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.deleteSearchView);
		ExtendedApplicationUtils.setAutoCompleteAndExpandListItems(DeleteContactActivity.this, listAdapter, new Object[]{expandList, autoCompleteTextView}, contactsMap);
		
		
//		((CheckBox) findViewById(R.id.checkBoxGroup)).setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				LinearLayout selectedChildLayout = (LinearLayout) v.getParent();
//				String selected = ((TextView) selectedChildLayout.findViewById(R.id.parent)).getText().toString();
//				System.out.println(selected);
//				
//			}
//		});
		
		System.out.println("st");
//		((Button) findViewById(R.id.deleteButton)).setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				
//				Map<String, String> deleteMap = new ModifiedTreeMap<String, String>();
//				
//				
//				
//				String name = "A";
//				String number = "08095334035";
//				
//				System.out.println("star");
//				
//				boolean flag = ApplicationUtils.isContactDeleted(getContentResolver(), name, number);
//				
//				Log.v("AddContactActivity", String.format("Contact with name: %s, number: %s is %s deleted.", 
//						name, number, flag ? "successfully" : "not"));
//				
//				finish();
//			}
//		});
		System.out.println("here");
	}
	
	public void onDeleteChildCheckBoxClick(View v) {
		LinearLayout selectedChildLayout = (LinearLayout) v.getParent();
		String selected = ((TextView)selectedChildLayout.findViewById(R.id.childrenTextNumber)).getText().toString();
		
		System.out.println("num " + selected);


		ExpandableListView expandableListView = (ExpandableListView) selectedChildLayout.getParent();
		
		System.out.println(expandableListView.getPackedPositionChild(0));
		String name = ((TextView)expandableListView.findViewById(R.id.parent)).getText().toString();
		
		System.out.println(name);
		
	}
	

}
