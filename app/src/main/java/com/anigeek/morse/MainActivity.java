package com.anigeek.morse;

import android.app.Activity;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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


public class MainActivity extends AppCompatActivity implements SettingsDialog.SettingsDialogListener
{
	private static final String INVALID_INPUT = "Invalid input: ",
			ERROR = "Something went wrong!";
	private String channel;
	private Converter converter;
	private EditText morseIn;
	private Beeper beeper;
	private View tView;
	private Socket socket;
	private boolean disregard = false;
	private ArrayList<String> messages;
	private ArrayAdapter<String> adapter;
	private Activity activity;
	private ActiveElement active;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		activity = this;
		tView = findViewById(R.id.contentroot);
		getPrefs();
		getSocket();

		messages = new ArrayList<>();//Helper.getMessages(this);
		messages.add("");
		ListView listView = (ListView) findViewById(R.id.msglist);
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messages);
		if (listView != null)
		{
			listView.setAdapter(adapter);
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
						snack(ERROR);
					}
					catch (Exception e)
					{
						Log.d("ListViewClick General", e.getMessage());
						snack(ERROR);
					}
				}
			});
		}

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
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings)
		{
			dialogHandler();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private void getPrefs()
	{
		Helper.getPreferences(activity);
		channel = MorseOptions.getChannel();
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
			if(!con.isEmpty())
			{
				beeper.beep(con);
				socket.emit("message", channel, con);
				addMessage(con);
			}
		}
		catch (Exception e)
		{
			snack(String.format("%s%s", INVALID_INPUT, morseIn.getText().toString().trim()));
		}
	}

	private void convertMorse(String alpha)//FROM alpha TO morse
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

	private void convertAlpha(String morse)//FROM morse TO alpha
	{
		try
		{
			if(!morse.matches("([.-]{1,5}(?> [.-]{1,5})*(?>   [.-]{1,5}(?> [.-]{1,5})*)*(\\s\\|\\s)*)*"))
				throw new Exception();
			if(!morse.isEmpty())
			{
				beeper.beep(morse);
				addMessage(morse);
			}
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

		if (!socket.connected())
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
					socket.on(channel, msg);
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
					snack("Connected to channel: " + channel);
				}
			});
		}
	};

	private void snack(String msg)
	{
		Snackbar.make(tView, msg, Snackbar.LENGTH_SHORT).show();
	}

	private void dialogHandler()
	{
		SettingsDialog settingsDialog = new SettingsDialog();
		settingsDialog.show(getFragmentManager(), "settings");
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog)
	{
		socket.disconnect();
		Helper.savePreferences(activity);
		channel = MorseOptions.getChannel();
		socket.connect();
		dialog.dismiss();
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog)
	{
		dialog.dismiss();
	}
}
