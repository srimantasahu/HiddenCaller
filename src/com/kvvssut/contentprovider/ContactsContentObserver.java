package com.kvvssut.contentprovider;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

import com.kvvssut.interfaces.ModifiedAsyncTask;

public class ContactsContentObserver extends ContentObserver {

	private Context context;
	private ModifiedAsyncTask<Void, Void, Void> asyncTask;

	public ContactsContentObserver(Handler handler, Context context, ModifiedAsyncTask<Void, Void, Void> asyncTask) {
		super(handler);
		this.context = context;
		this.asyncTask = asyncTask;
		this.registerContentObserver(this);
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		asyncTask.backgroundContactsProcessing();
	} 

	@Override
	public boolean deliverSelfNotifications() {
		return true;
	}

	public void registerContentObserver(ContentObserver mContentObserver) {
			context.getContentResolver().registerContentObserver(android.provider.ContactsContract.Contacts.CONTENT_URI,
					true, mContentObserver);
	}

}