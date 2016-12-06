package com.kvvssut.services;

import java.util.TreeMap;

@SuppressWarnings("serial")
public class ModifiedTreeMap<K, V> extends TreeMap<K, V>{
	
	@Override
	public V put(K key, V value) {
		if(key != null){
			value = super.put(key, value);
		}
		return value;
	}

}
