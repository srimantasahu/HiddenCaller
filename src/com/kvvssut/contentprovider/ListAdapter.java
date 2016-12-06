package com.kvvssut.contentprovider;
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class ListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<GroupList> groups;
    private int[] expandableListViews;
    
    public ListAdapter(Context context, ArrayList<GroupList> groups, int[] views) {
        this.context = context;
        this.groups = groups;
        this.expandableListViews = views;
    }
     
    public void addItem(ChildList item, GroupList group) {
        if (!groups.contains(group)) {
            groups.add(group);
        }
        int index = groups.indexOf(group);
        ArrayList<ChildList> ch = groups.get(index).getItems();
        ch.add(item);
        groups.get(index).setItems(ch);
    }
    
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).getItems().get(childPosition).getNumber();
    }

    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view,
            ViewGroup parent) {
        String childNumber = (String) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(expandableListViews[1], null);
        }
        TextView tv = (TextView) view.findViewById(R.id.childrenTextNumber);
        tv.setText(childNumber);
        
        return view;
    }

    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).getItems().size();
    }

    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    public int getGroupCount() {
        return groups.size();
    }

    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    public View getGroupView(int groupPosition, boolean isLastChild, View view,
            ViewGroup parent) {
        GroupList group = (GroupList) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(expandableListViews[0], null);
        }
        TextView tv = (TextView) view.findViewById(R.id.parent);
        tv.setText(group.getDisplayName());
        return view;
    }

    public boolean hasStableIds() {
        return true;
    }

    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }

}
