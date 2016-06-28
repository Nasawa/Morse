package com.anigeek.morse;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class Helper
{
	public static void saveMessages(Activity activity, ArrayList<String> messages)
	{
		SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		Set<String> messageSet = preferences.getStringSet("messages", new HashSet<String>());
		editor.clear();
		messageSet.clear();
		messageSet.addAll(messages);
		editor.putStringSet("messages", messageSet);
		editor.commit();
	}

	public static ArrayList getMessages(Activity activity)
	{
		SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
		Set<String> messageSet = preferences.getStringSet("messages", new HashSet<String>());
		ArrayList temp = new ArrayList(Arrays.asList(messageSet.toArray()));
		return temp;
	}
}