package com.anigeek.morse;

class MorseOptions
{
	private static int showHistory = 0;
	private static String channel = "morse";

	static String getChannel()
	{
		return channel;
	}

	static void setChannel(String ch)
	{
		channel = ch;
	}

	static int getShowHistory()
	{
		return showHistory;
	}

	static void setShowHistory(int sh)
	{
		showHistory = sh;
	}
}
