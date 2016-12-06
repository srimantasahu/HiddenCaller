package com.kvvssut.contentprovider;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;

import com.kvvssut.utils.ApplicationUtils;

public class LogsContentObserver extends ContentObserver {

	private Context context;
	private String phoneNumber;
	private boolean deleteCall;


	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setDeleteCall(boolean deleteCall) {
		this.deleteCall = deleteCall;
	}

	public LogsContentObserver(Handler handler, Context context) {
		super(handler);
		this.context=context;
		this.registerContentObserver(this, true);
	}

	@Override
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		ApplicationUtils.deleteLastCallLog(context, phoneNumber, deleteCall);
	} 

	@Override
	public boolean deliverSelfNotifications() {
		return true;
		
	}

	public void registerContentObserver(ContentObserver mContentObserver, boolean shouldRegister) {
		if(shouldRegister)
		{
			context.getContentResolver().registerContentObserver(android.provider.CallLog.Calls.CONTENT_URI,
					true, mContentObserver);
		} else {
			try {  
				context.getContentResolver().unregisterContentObserver(mContentObserver);  
			} catch (IllegalStateException illegalStateException) {  
				// Do Nothing.  Observer has already been unregistered.  
			}  
		}
	}

}