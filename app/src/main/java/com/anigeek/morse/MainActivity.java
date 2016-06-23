package com.anigeek.morse;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MainActivity extends AppCompatActivity
{
	private Converter converter;
	private EditText morseIn;
	private TextView morseOut;
	private Beeper beeper;
	private View tView;
	private String myid;
	private Socket socket;
	private boolean disregard = false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
				        .setAction("Action", null).show();
			}
		});

		tView = (View) findViewById(R.id.contentroot);

		try
		{
			socket = IO.socket("http://clipt.azurewebsites.net");
		}
		catch (URISyntaxException e)
		{
			e.printStackTrace();
		}

		socket.on("connect", onConnect);
		socket.on("id", id);
		socket.connect();

		morseIn = (EditText)findViewById(R.id.morsein);
		morseOut = (TextView)findViewById(R.id.morseout);
		Button convertButton = (Button)findViewById(R.id.convertbutton);

		converter = new Converter();
		beeper = new Beeper();

		convertButton.setOnClickListener(new Button.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				disregard = true;
				convertMorse();
			}
		});
	}

	private void convertMorse()
	{
		try
		{
			String con = converter.toMorse(morseIn.getText().toString().trim());
			beeper.beep(con);
			socket.emit("message", "morse", con);
			morseOut.setText(con);
		}
		catch(Exception e){}
	}

	private void convertMorse(String alpha)
	{
		try
		{
			morseIn.setText(alpha.toLowerCase());
			String con = converter.toMorse(alpha);
			beeper.beep(con);
			morseOut.setText(con);
		}
		catch (Exception e){}
	}

	private void convertAlpha(String morse)
	{
		try
		{
			morseIn.setText(converter.toAlpha(morse));
			beeper.beep(morse);
			morseOut.setText(morse);
		}
		catch (Exception e)
		{
			convertMorse(morse);
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();

		socket.disconnect();
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
			return true;
		}

		return super.onOptionsItemSelected(item);
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
					myid = (String)args[0];
					Snackbar.make(tView, "Your ID is " + myid, Snackbar.LENGTH_SHORT).show();
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
					if(!disregard)
						convertAlpha((String)args[0]);
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
					Snackbar.make(tView, "Connected to Morse chat", Snackbar.LENGTH_SHORT).show();
				}
			});
		}
	};
}
