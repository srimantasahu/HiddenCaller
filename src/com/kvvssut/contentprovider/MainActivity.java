package com.kvvssut.contentprovider;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kvvssut.constants.ApplicationConstants;
import com.kvvssut.interfaces.ModifiedAsyncTask;
import com.kvvssut.utils.ApplicationUtils;
import com.kvvssut.utils.ExtendedApplicationUtils;

public class MainActivity extends Activity {

	public static MainActivity mainActivity;
	ModifiedAsyncTask<Void, Void, Void> asyncTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mainActivity = MainActivity.this;

		Thread.currentThread().setPriority(Thread.MAX_PRIORITY - 1);
		asyncTask = new ModifiedAsyncTask<Void, Void, Void>() {
			private ExpandableListView expandList = null;
			private AutoCompleteTextView autoCompleteTextView = null;
			private ProgressDialog dialog = new ProgressDialog(MainActivity.this);
			private Map<String, List<String>> contactsMap;
			private LogsContentObserver contentObserver = new LogsContentObserver(new Handler(), MainActivity.this);

			@Override
			protected void onPreExecute() {
				autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.searchView);

				((Button) findViewById(R.id.callButton)).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						verifyAndProcess(autoCompleteTextView.getText().toString(), true);
					}
				});


				((Button) findViewById(R.id.msgButton)).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						verifyAndProcess(autoCompleteTextView.getText().toString(), false);
					}
				});

				dialog.setTitle(ApplicationConstants.PROGRESS_DIALOG_TITLE);
				dialog.setMessage(ApplicationConstants.PROGRESS_DIALOG_MESSAGE);
				dialog.setCancelable(true);
				dialog.setIndeterminate(true);
				dialog.show();
			}

			@Override
			protected Void doInBackground(Void... arg0) {
				Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
				contactsMap = ApplicationUtils.readCachedContacts(MainActivity.this);
				if(contactsMap.size() == 0){
					Log.i("doInBackground" , "No cached data found! Caching contacts..");
					contactsMap = ApplicationUtils.fetchAllContacts(getContentResolver(), contactsMap);
					ApplicationUtils.chacheContacts(MainActivity.this, contactsMap);
					Log.i("doInBackground" , "Contacts Loaded..");
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				dialog.dismiss();
				final ListAdapter listAdapter = new ListAdapter(MainActivity.this, ApplicationUtils.setGroupItems(contactsMap), new int[]{R.layout.group_view, R.layout.child_view});
				expandList = (ExpandableListView) findViewById(R.id.contactsView);
				ExtendedApplicationUtils.setAutoCompleteAndExpandListItems(MainActivity.this, listAdapter, new Object[]{expandList, autoCompleteTextView}, contactsMap);
				new ContactsContentObserver(new Handler(), MainActivity.this, asyncTask);
			}
			
			@Override
			public void backgroundContactsProcessing() {
				Thread.currentThread().setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

					@Override
					public void uncaughtException(Thread thread, Throwable ex) {
						Log.v("UncaughtException in backgroundContactsProcessing: ", ex.getMessage());
					}
				});
				
				Log.i("backgroundContactsProcessing" , "Background Thread! Caching & Reloading contacts..");
				contactsMap = ApplicationUtils.fetchAllContacts(getContentResolver(), contactsMap);
				ApplicationUtils.chacheContacts(MainActivity.this, contactsMap);

				final ListAdapter listAdapter = new ListAdapter(MainActivity.this, ApplicationUtils.setGroupItems(contactsMap), new int[]{R.layout.group_view, R.layout.child_view});
				expandList = (ExpandableListView) findViewById(R.id.contactsView);
				ExtendedApplicationUtils.setAutoCompleteAndExpandListItems(MainActivity.this, listAdapter, new Object[]{expandList, autoCompleteTextView}, contactsMap);
				Log.i("backgroundContactsProcessing" , "Contacts Reloaded!!");
			}
			

			@Override
			public void expandAll() {
				int count = expandList.getExpandableListAdapter().getGroupCount();
				for (int i = 0; i < count; i++) {
					expandList.expandGroup(i);
				}
			}

			@Override
			public void collapseAll() {
				int count = expandList.getExpandableListAdapter().getGroupCount();
				for (int i = 0; i < count; i++) {
					expandList.collapseGroup(i);
				}
			}

			@Override
			public LogsContentObserver getLogContentObserver() {
				return contentObserver;
			}

			@Override
			public AutoCompleteTextView getAutoCompleteTextView() {
				return autoCompleteTextView;
			}

			@Override
			public Map<String, List<String>> getContactsMap() {
				return contactsMap;
			}	
		};
		asyncTask.execute((Void[])null);
	}

	public void onChildButtonClick(View v) {
		LinearLayout selectedChildLayout = (LinearLayout) v.getParent();
		String selected = ((TextView)selectedChildLayout.findViewById(R.id.childrenTextNumber)).getText().toString();
		asyncTask.getAutoCompleteTextView().setText(selected);

		switch (v.getId()) {
		case R.id.childCallButton:
			verifyAndProcess(selected, true);
			break;
		case R.id.childMsgButton:
			verifyAndProcess(selected, false);
			break;
		default:
			break;
		}
	}

	private void verifyAndProcess(String diallerNumber, boolean call) {
		if(ApplicationUtils.isValidPhoneNumber(diallerNumber)){
			LogsContentObserver contentObserver = asyncTask.getLogContentObserver();
			contentObserver.setPhoneNumber(diallerNumber);
			contentObserver.setDeleteCall(call);

			Intent dailIntent = call ?  new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + diallerNumber)) : 
				new Intent(Intent.ACTION_SENDTO, Uri.parse("sms:" + diallerNumber)) ;
			dailIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
			startActivity(dailIntent);
		} else {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
			alertDialogBuilder.setTitle(ApplicationConstants.ALERT_DIALOG_TITLE);
			alertDialogBuilder.setMessage(String.format(ApplicationConstants.ALERT_DIALOG_MESSAGE, diallerNumber));
			alertDialogBuilder.setIcon(R.drawable.icon_err);
			alertDialogBuilder.show();
		}

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 0, "Add a Contact");
		menu.add(0, 2, 1, "Expand All");
		menu.add(0, 3, 2, "Collapse All");
		menu.add(0, 4, 3, "Delete Contacts");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case 1:
			showAddContactView();
			break;
		case 2:
			expandAllContacts();
			break;
		case 3:
			collapseAllContacts();
			break;
		case 4:
			showDeleteContactsView();
			break;
		default:
			break;
		}
		return true;
	}

	private void expandAllContacts() {
		asyncTask.expandAll();
	}

	private void collapseAllContacts() {
		asyncTask.collapseAll();
	}

	private void showAddContactView() {
		Intent intent = new Intent(MainActivity.this, AddContactActivity.class);
		startActivity(intent);
	}

	private void showDeleteContactsView() {
		Intent intent = new Intent(MainActivity.this, DeleteContactActivity.class);
		startActivity(intent);
	}

	@Override
	public void finish() {
		try {
			LogsContentObserver logContentObserver = asyncTask.getLogContentObserver();
			logContentObserver.registerContentObserver(logContentObserver, false);
		} finally {
			super.finish();
		}
	}
}
