package com.anigeek.morse;

import java.util.Arrays;
import java.util.List;

public class Converter
{

	List<String> alphabet = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v",
            "w", "x", "y", "z", "1", "2", "3", "4", "5", "6", "7", "8",
            "9", "0", " ", ".", ",", ":", "?", "'", "-", "/", "\\", "{", "}", "(", ")", "\"", "@", "=", "");
	List<String> morse = Arrays.asList(".-", "-...", "-.-.", "-..", ".", "..-.", "--.",
            "....", "..", ".---", "-.-", ".-..", "--", "-.", "---", ".--.",
            "--.-", ".-.", "...", "-", "..-", "...-", ".--", "-..-",
            "-.--", "--..", ".----", "..---", "...--", "....-", ".....",
            "-....", "--...", "---..", "----.", "-----", "|", ".-.-.-", "--..--", "---...", "..--..", ".----.", "-....-", "-..-.", "-..-.",
            "-.--.-", "-.--.-", "-.--.-", "-.--.-", ".-..-.", ".--.-.", "-...-", "");
	public Converter(){}

	String toMorse(String in)
	{
		StringBuffer out = new StringBuffer();
		String[] temp = in.toLowerCase().split("");
		for(int i = 0; i < temp.length; i++)
		{
			out.append(morse.get(alphabet.indexOf(temp[i])) + " ");
		}
		return out.toString().trim();
	}
	
	String toAlpha(String in)
	{
		StringBuffer out = new StringBuffer();
		String[] temp = in.toLowerCase().split(" ");
		for(int i = 0; i < temp.length; i++)
		{
			out.append(alphabet.get(morse.indexOf(temp[i])));
		}
		return out.toString().trim();
	}
}
