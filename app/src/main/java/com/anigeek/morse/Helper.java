package com.anigeek.morse;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


class Helper
{
	static void saveMessages(Activity activity, ArrayList<String> messages)
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

	public static ArrayList<Object> getMessages(Activity activity)
	{
		SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
		Set<String> messageSet = preferences.getStringSet("messages", new HashSet<String>());
		return new ArrayList<>(Arrays.asList(messageSet.toArray()));
	}

	static void savePreferences(Activity activity)
	{
		SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString("Channel", MorseOptions.getChannel());
		editor.putInt("History", MorseOptions.getShowHistory());
		editor.commit();
	}

	static void getPreferences(Activity activity)
	{
		SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);
		MorseOptions.setChannel(preferences.getString("Channel", "morse"));
		MorseOptions.setShowHistory(preferences.getInt("History", 0));
	}
}