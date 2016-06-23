package com.anigeek.morse;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import static java.lang.Thread.sleep;

public class Beeper
{
	int d = 50;
	double p = 550;
	AudioTrack dot, dash;

	public String input;
	
	public Beeper()
	{
		dot = generateTone(p, d * 1);
		dash = generateTone(p, d * 3);
	}
	
	void beep(String in) throws InterruptedException
	{
		input = in;
		final Thread thread = new Thread(new Runnable()
		{
			public void run()
			{
				try
				{
					String[] lett = input.split(" ");
					for (int i = 0; i < lett.length; i++)
					{
						char[] m = lett[i].toCharArray();
						for (int j = 0; j < m.length; j++)
						{
							if (m[j] == '.')
							{
								dot.play();
								sleep(d);
								dot.stop();
							}
							else if (m[j] == '-')
							{
								dash.play();
								sleep(d * 3);
								dash.stop();
							}
							else if (m[j] == '|')
							{
								sleep(d);
							}
							sleep(d);
						}
						sleep(d * 2);
					}
				}
				catch (Exception e){}
			}
		});
		thread.start();
	}

	private AudioTrack generateTone(double freqHz, int durationMs)
	{
		int count = (int)(44100.0 * 2.0 * (durationMs / 1000.0)) & ~1;
		short[] samples = new short[count];
		for(int i = 0; i < count; i += 2){
			short sample = (short)(Math.sin(2 * Math.PI * i / (44100.0 / freqHz)) * 0x7FFF);
			samples[i + 0] = sample;
			samples[i + 1] = sample;
		}
		AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
		                                  AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
		                                  count * (Short.SIZE / 8), AudioTrack.MODE_STATIC);
		track.write(samples, 0, count);
		return track;
	}
}
