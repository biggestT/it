// Class to show messages to the user/programmer. At the moment just used for debugging but should 
// further on be developed to be used to help the user get understandable feedback from the program
package com.thingsbook.it;

import android.util.Log;

public class Logger {
	
	private static final String TAG = "ItApplication (java)";
	
	public static void log(String s) {
		Log.d(TAG, s);
	}

	public static void log(Exception e) {
		Log.e(TAG, "Exception: ", e);
	}
}