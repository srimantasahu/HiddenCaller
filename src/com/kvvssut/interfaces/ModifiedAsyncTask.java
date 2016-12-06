package com.kvvssut.interfaces;

import java.util.List;
import java.util.Map;

import android.os.AsyncTask;
import android.widget.AutoCompleteTextView;

import com.kvvssut.contentprovider.LogsContentObserver;

public abstract class ModifiedAsyncTask<K, V, W> extends AsyncTask<K, V, W>{
	
	
	public abstract Map<String, List<String>> getContactsMap();
	
	public abstract LogsContentObserver getLogContentObserver();
	
	public abstract AutoCompleteTextView getAutoCompleteTextView();
	
	public abstract void backgroundContactsProcessing();
	
	public abstract void expandAll();
	
	public abstract void collapseAll();
	
}
