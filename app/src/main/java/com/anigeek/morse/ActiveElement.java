package com.anigeek.morse;

import android.view.View;

public class ActiveElement
{
	public View view;
	public String morse;
	public ActiveElement(String m, View v)
	{
		morse = m;
		view = v;
	}
}
