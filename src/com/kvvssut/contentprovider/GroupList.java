package com.kvvssut.contentprovider;
import java.util.ArrayList;
 
public class GroupList {
  
    private String displayName;
    private ArrayList<ChildList> items;
    
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public ArrayList<ChildList> getItems() {
		return items;
	}
	public void setItems(ArrayList<ChildList> items) {
		this.items = items;
	}
    
}
