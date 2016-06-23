package com.anigeek.morse;

import android.support.annotation.NonNull;

import java.util.Arrays;
import java.util.List;

class Converter
{

	private List<String> alphabet = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
			"k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
			"w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8",
			"9", "0", " ", ".", ",", ":", "?", "'", "-", "/", "\\", "{",
			"}", "(", ")", "\"", "@", "=", "");
	private List<String> morse = Arrays.asList(".-", "-...", "-.-.", "-..", ".", "..-.", "--.",
			"....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.",
			"--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-",
			"-.--", "--..", ".----", "..---", "...--", "....-", ".....",
			"-....", "--...", "---..", "----.", "-----", "|", ".-.-.-", "--..--",
			"---...", "..--..", ".----.", "-....-", "-..-.", "-..-.",
			"-.--.-", "-.--.-", "-.--.-", "-.--.-", ".-..-.", ".--.-.", "-...-", "");

	Converter() {}

	String toMorse(String in)
	{
		return worker(in, false);
	}
	
	String toAlpha(String in)
	{
		return worker(in, true);
	}

	@NonNull
	private String worker(String in, boolean isMorse)
	{
		StringBuilder out = new StringBuilder();
		String[] temp = in.toLowerCase().split(isMorse ? " " : "");
		for (String aTemp : temp)
		{
			if (!isMorse)
				out.append(morse.get(alphabet.indexOf(aTemp))).append(" ");
			else
				out.append(alphabet.get(morse.indexOf(aTemp)));
		}
		return out.toString().trim();
	}
}
