package com.anigeek.morse;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class SettingsDialog extends DialogFragment
{
	public SettingsDialog(){}

	interface SettingsDialogListener
	{
		void onDialogPositiveClick(DialogFragment dialog);
		void onDialogNegativeClick(DialogFragment dialog);
	}

	private SettingsDialogListener listener;

	private RadioGroup radioGroup;
	private EditText editText;
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		View v = getActivity().getLayoutInflater().inflate(R.layout.morse_prefs, null);

		radioGroup = (RadioGroup) v.findViewById(R.id.morseradio);
		RadioButton radioButton;
		int history = MorseOptions.getShowHistory();
		Log.d("History", history+"");
		radioButton = ((RadioButton)radioGroup.getChildAt(history));
		radioButton.setChecked(true);
		editText = (EditText) v.findViewById(R.id.morsechannel);
		editText.setText(MorseOptions.getChannel());

		builder.setTitle(R.string.action_settings)
				.setView(v);

		builder.setPositiveButton(R.string.save, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				int radioButtonID = radioGroup.getCheckedRadioButtonId();
				View button = radioGroup.findViewById(radioButtonID);
				int index = radioGroup.indexOfChild(button);
				MorseOptions.setShowHistory(index);
				MorseOptions.setChannel(editText.getText().toString());
				listener.onDialogPositiveClick(SettingsDialog.this);
			}
		});
		builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				listener.onDialogNegativeClick(SettingsDialog.this);
			}
		});

		return builder.create();
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		try
		{
			listener = (SettingsDialogListener) activity;
		}
		catch (ClassCastException e)
		{
			Log.d("DialogAttach", e.getMessage());
		}
	}
}
