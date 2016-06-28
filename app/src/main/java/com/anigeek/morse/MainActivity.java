package com.anigeek.morse;

import android.app.Activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity
{
	private static final String INVALID_INPUT = "Invalid input: ";
	private Converter converter;
	private EditText morseIn;
	private Beeper beeper;
	private View tView;
	private Socket socket;
	private boolean disregard = false;
	private ArrayList messages;
	private ArrayAdapter adapter;
	private Activity activity;
	private ActiveElement active;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		tView = findViewById(R.id.contentroot);
		getSocket();

		activity = this;

		messages = Helper.getMessages(this);
		ListView listView = (ListView) findViewById(R.id.msglist);
		adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, messages);
		listView.setAdapter(adapter);

		socket.on("connect", onConnect);
		socket.on("id", id);
		socket.connect();

		morseIn = (EditText) findViewById(R.id.morsein);
		Button convertButton = (Button) findViewById(R.id.convertbutton);

		converter = new Converter();
		beeper = new Beeper();

		if (convertButton != null)
			convertButton.setOnClickListener(new Button.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					disregard = true;
					convertMorse();
					morseIn.setText("");
				}
			});

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				try
				{
					if (active != null)
						((TextView) active.view).setText(active.morse);

					TextView tv = (TextView) view;
					String temp = tv.getText().toString();
					beeper.beep(temp);
					active = new ActiveElement(temp, view);
					tv.setText(converter.toAlpha(temp));
				}
				catch (InterruptedException e)
				{
					Log.d("ListViewClick", e.getMessage());
				}
			}
		});
	}

	private void getSocket()
	{
		try
		{
			socket = IO.socket("http://clipt.azurewebsites.net");
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}
	}

	private void addMessage(String msg)
	{
		messages.add(msg);
		adapter.notifyDataSetInvalidated();
	}

	private void convertMorse()
	{
		try
		{
			String con = converter.toMorse(morseIn.getText().toString().trim());
			beeper.beep(con);
			socket.emit("message", "morse", con);
			addMessage(con);
		}
		catch (Exception e)
		{
			snack(String.format("%s%s", INVALID_INPUT, morseIn.getText().toString().trim()));
		}
	}

	private void convertMorse(String alpha)
	{
		try
		{
			String con = converter.toMorse(alpha);
			beeper.beep(con);
			addMessage(con);
		}
		catch (Exception e)
		{
			snack(String.format("%s%s", INVALID_INPUT, alpha));
		}
	}

	private void convertAlpha(String morse)
	{
		try
		{
			beeper.beep(morse);
			addMessage(morse);
		}
		catch (Exception e)
		{
			convertMorse(morse);
		}
	}

	@Override
	public void onStop()
	{
		super.onStop();

		socket.disconnect();
		Helper.saveMessages(activity, messages);
	}

	public void onStart()
	{
		super.onStart();

		if(!socket.connected())
			socket.connect();
	}

	private Emitter.Listener id = new Emitter.Listener()
	{
		@Override
		public void call(final Object... args)
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					//myid = (String) args[0];
					//Snackbar.make(tView, "Your ID is " + myid, Snackbar.LENGTH_SHORT).show();
					socket.on("morse", msg);
				}
			});
		}
	};

	private Emitter.Listener msg = new Emitter.Listener()
	{
		@Override
		public void call(final Object... args)
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (!disregard)
						convertAlpha((String) args[0]);
					disregard = false;
				}
			});
		}
	};

	private Emitter.Listener onConnect = new Emitter.Listener()
	{
		@Override
		public void call(final Object... args)
		{
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					snack("Connected to Morse chat");
				}
			});
		}
	};

	private void snack(String msg)
	{
		Snackbar.make(tView, msg, Snackbar.LENGTH_SHORT).show();
	}
}
