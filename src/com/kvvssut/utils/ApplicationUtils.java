package com.kvvssut.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;

import com.kvvssut.constants.ApplicationConstants;
import com.kvvssut.contentprovider.ChildList;
import com.kvvssut.contentprovider.GroupList;
import com.kvvssut.services.ModifiedTreeMap;

public final class ApplicationUtils {
	private ApplicationUtils(){
	}

	public static boolean isValidPhoneNumber(String daillerNumber) {
		Pattern pattern = Pattern.compile("(0|[+]{1}91)?(\\d{10})");
		Matcher matcher = pattern.matcher(daillerNumber);
		return matcher.matches();
	}

	public static ArrayList<GroupList> setGroupItems(Map<String, List<String>> contactsMap) {
		ArrayList<GroupList> groupLists = new ArrayList<GroupList>();

		for(Entry<String, List<String>> entry : contactsMap.entrySet()){
			ArrayList<ChildList> childLists = new ArrayList<ChildList>();
			GroupList groupList = new GroupList();
			groupList.setDisplayName(entry.getKey());

			for (String number : entry.getValue()) {
				ChildList childList = new ChildList();
				childList.setNumber(number);
				childLists.add(childList);
			}

			groupList.setItems(childLists);
			groupLists.add(groupList);
		}
		return groupLists;
	}

	public static Map<String, List<String>> fetchAllContacts(ContentResolver contentResolver, Map<String, List<String>> contactsMap) {

		Uri CONTENT_URI = ContactsContract.Contacts.CONTENT_URI;
		String _ID = ContactsContract.Contacts._ID;
		String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
		String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

		Uri PhoneCONTENT_URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
		String Phone_CONTACT_ID = ContactsContract.CommonDataKinds.Phone.CONTACT_ID;
		String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

		List<String> contactsList = null;
		contactsMap = new ModifiedTreeMap<String, List<String>>();

		Cursor cursor = contentResolver.query(CONTENT_URI, null,null, null, null); 

		if (cursor.getCount() > 0) {
			// Loops for every contact in the phone
			while (cursor.moveToNext()) {
				contactsList = new ArrayList<String>();

				if (Integer.parseInt(cursor.getString(cursor.getColumnIndex( HAS_PHONE_NUMBER ))) > 0) {
					Cursor phoneCursor = contentResolver.query(PhoneCONTENT_URI, null, Phone_CONTACT_ID + " = ?", new String[] { cursor.getString(cursor.getColumnIndex( _ID )) }, null);

					// Queries and loops for every phone number of the contact
					while (phoneCursor.moveToNext()) {
						contactsList.add(phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER)));
					}

					phoneCursor.close();
					contactsMap.put(cursor.getString(cursor.getColumnIndex( DISPLAY_NAME )), contactsList);
				}
			}
		}
		return contactsMap;
	}

	public static void deleteLastCallLog(Context context, String phoneNumber, boolean deleteCall) {
		System.out.println("deleting");
		Log.i("deleteLastCallLog", "Removing # from call log: " + phoneNumber);
		try {
			ContentResolver contentResolver = context.getContentResolver();
			Uri uriCalls = null;
			if (deleteCall) {
				uriCalls =	Uri.parse("content://call_log/calls");
				int noOfRowsDeleted = contentResolver.delete(uriCalls, CallLog.Calls.NUMBER +"=?",new String[]{ phoneNumber});
				System.out.println("msg: " + noOfRowsDeleted);
				if(noOfRowsDeleted == 0) {
					String strNumberOne[] = { phoneNumber };
					Cursor cursor = context.getContentResolver().query(
							CallLog.Calls.CONTENT_URI, null,
							CallLog.Calls.NUMBER + " = ? ", strNumberOne, CallLog.Calls.DATE + " DESC");

					if (cursor.moveToFirst()) {
						int idOfRowToDelete = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));                      
						noOfRowsDeleted = contentResolver.delete(CallLog.Calls.CONTENT_URI, CallLog.Calls._ID + " = ? ",
								new String[] { String.valueOf(idOfRowToDelete) });
						System.out.println("msg: " + noOfRowsDeleted);
					}
				}
			} else {
				Cursor cursor = contentResolver.query(uriCalls, null, null, null, null);
				long thread_id = cursor.getLong(1);
				uriCalls = Uri.parse("content://sms/sent" + thread_id);
				int noOfRowsDeleted = contentResolver.delete(uriCalls, null, null);
				System.out.println("msg: " + noOfRowsDeleted);
				if(noOfRowsDeleted == 0) {
					String strNumberOne[] = { phoneNumber };
					cursor = context.getContentResolver().query(
							CallLog.Calls.CONTENT_URI, null,
							CallLog.Calls.NUMBER + " = ? ", strNumberOne, CallLog.Calls.DATE + " DESC");

					if (cursor.moveToFirst()) {
						int idOfRowToDelete = cursor.getInt(cursor.getColumnIndex(CallLog.Calls._ID));                      
						noOfRowsDeleted = contentResolver.delete(CallLog.Calls.CONTENT_URI, CallLog.Calls._ID + " = ? ",
								new String[] { String.valueOf(idOfRowToDelete) });
						System.out.println("msg: " + noOfRowsDeleted);
					}
				}
			}
		} catch(Exception ex) {
			Log.v("deleteLastCallLog", "Exception, unable to remove # from call log: " + ex.toString());
		} 
	}

	public static void chacheContacts(Context context, Map<String, List<String>> contactsMap) {
		File file = new File(context.getCacheDir(), ApplicationConstants.CACHE_FILENAME);
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(contactsMap);
			oos.close();
		} catch (IOException e) {
			Log.v("chacheContacts", "IOException: " + e.getMessage());
		}
	}


	@SuppressWarnings("unchecked")
	public static Map<String, List<String>> readCachedContacts(Context context) {
		Log.i("readCachedContacts" , "Reading cached contacts..");

		Map<String, List<String>> contactsMap = new TreeMap<String, List<String>>();
		
		File file = new File(context.getCacheDir(), ApplicationConstants.CACHE_FILENAME);
		FileInputStream fis = null;
		ObjectInputStream ois = null;

		try {
			try{
				fis = new FileInputStream(file);
			} catch (FileNotFoundException e){
				return contactsMap;
			}
			ois = new ObjectInputStream(fis);
			contactsMap  = (Map<String, List<String>>) ois.readObject();
		} catch (IOException e) {
			Log.v("readCachedContacts", "IOException: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			Log.v("readCachedContacts", "ClassNotFoundException: " + e.getMessage());
		} finally {
			try {
				if (ois != null) {
					ois.close();
				}
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {
				Log.v("readCachedContacts", "IOException in finally{}: " + e.getMessage());
			}
		}
		return contactsMap;
	}

	public static boolean isContactAdded(ContentResolver contentResolver, String firstName, String lastName, String phoneNumber, String emailId) {
		ArrayList<ContentProviderOperation> op_list = new ArrayList<ContentProviderOperation>(); 
		op_list.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI) 
				.withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null) 
				.withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null) 
				//.withValue(RawContacts.AGGREGATION_MODE, RawContacts.AGGREGATION_MODE_DEFAULT) 
				.build()); 

		op_list.add(ContentProviderOperation.newInsert(Data.CONTENT_URI) 
				.withValueBackReference(Data.RAW_CONTACT_ID, 0) 
				.withValue(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE) 
				.withValue(StructuredName.GIVEN_NAME, firstName) 
				.withValue(StructuredName.FAMILY_NAME, lastName) 
				.build()); 

		op_list.add(ContentProviderOperation.newInsert(Data.CONTENT_URI) 
				.withValueBackReference(Data.RAW_CONTACT_ID, 0) 
				.withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber)
				.withValue(ContactsContract.CommonDataKinds.Phone.TYPE, Phone.TYPE_MAIN)
				.build());

		op_list.add(ContentProviderOperation.newInsert(Data.CONTENT_URI) 
				.withValueBackReference(Data.RAW_CONTACT_ID, 0)
				.withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
				.withValue(ContactsContract.CommonDataKinds.Email.DATA, emailId)
				.withValue(ContactsContract.CommonDataKinds.Email.TYPE, Email.TYPE_WORK)
				.build());

		boolean flag = true;
		try{ 
			contentResolver.applyBatch(ContactsContract.AUTHORITY, op_list); 
		}catch(Exception e){ 
			flag = false;
		} 
		return flag;
	}

	public static boolean isContactDeleted(ContentResolver contentResolver, String name, String number) {
	    Uri contactUri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
	    Cursor cur = contentResolver.query(contactUri, null, null, null, null);
	    boolean flag = false;
	    try {
	        if (cur.moveToFirst()) {
	            do {
	                if (cur.getString(cur.getColumnIndex(PhoneLookup.DISPLAY_NAME)).equals(name)) {
	                    String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
	                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
	                    contentResolver.delete(uri, null, null);
	                    flag = true;
	                }

	            } while (cur.moveToNext());
	        } else {
				System.out.println("isContactDeleted- contact not matched");
			}

	    } catch (Exception e) {
	    	Log.v("isContactDeleted", "Exception: " + e.getMessage());
	    }
	    return flag;
	}

}
