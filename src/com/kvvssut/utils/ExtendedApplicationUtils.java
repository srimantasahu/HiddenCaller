package com.kvvssut.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ExpandableListView;

import com.kvvssut.contentprovider.ListAdapter;

public final class ExtendedApplicationUtils {

	public static void setAutoCompleteAndExpandListItems(Context context,
			final ListAdapter listAdapter, Object [] views, final Map<String, List<String>> contactsMap) {
		
		final ExpandableListView expandList = (ExpandableListView) views[0];
		expandList.setAdapter(listAdapter);
		expandList.setGroupIndicator(null);

		AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) views[1];
		autoCompleteTextView.setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, (String[]) contactsMap.keySet()
				.toArray(new String[contactsMap.keySet().size()])));
		autoCompleteTextView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				int index = Arrays.binarySearch((String[]) contactsMap.keySet()
						.toArray(new String[contactsMap.keySet().size()]), (String)arg0.getItemAtPosition(arg2));
				expandList.expandGroup(index);
				expandList.setSelection(index);
			}
		});
	}

}
