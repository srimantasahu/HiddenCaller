package com.kvvssut.constants;

/**
 * This is a constant class that holds all literal type constants for the application.
 */
public final class ApplicationConstants {
	private ApplicationConstants(){
	}
	
	public static final String PROGRESS_DIALOG_TITLE = "Processing...";
	public static final String PROGRESS_DIALOG_MESSAGE = "Retrieving Contacts..";
	
	public static final String ALERT_DIALOG_TITLE = "Format Error";
	public static final String ALERT_DIALOG_MESSAGE = "Dialled number: %s, Not in correct format (0|+91)XXXXXXXXXX.";
	public static final String DIALOG_BUTTON = "Re-enter number";
	
	public static final String CACHE_FILENAME = "HiddenCaller_Cache.txt";
	
}
