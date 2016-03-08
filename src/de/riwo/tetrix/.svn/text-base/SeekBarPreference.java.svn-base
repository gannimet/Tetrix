package de.riwo.tetrix;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekBarPreference extends DialogPreference implements OnSeekBarChangeListener {

	private View contentView;
	private SeekBar seekBar;
	private TextView valueTxt;
	private int defaultValue;
	
	public SeekBarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		defaultValue = attrs.getAttributeIntValue(
			"http://schemas.android.com/apk/res/android",	// namespace
			"defaultValue",	// attribute name
			50	// default value
		);
	}

	@Override
	protected View onCreateDialogView() {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		
		contentView = inflater.inflate(R.layout.velocity_seek_dialog, null, false);
		
		valueTxt = (TextView) contentView.findViewById(R.id.valueText);
		seekBar = (SeekBar) contentView.findViewById(R.id.velocity_seek_bar);
		
		seekBar.setProgress(getPersistedInt(defaultValue));
		seekBar.setOnSeekBarChangeListener(this);
		valueTxt.setText(getValueText(seekBar.getProgress()));
		
		return contentView;
	}
	
	private String getValueText(int progress) {
		return progress + "%";
	}

	@Override
	protected void onPrepareDialogBuilder(Builder builder) {
		builder
		.setPositiveButton(R.string.save, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				persistInt(seekBar.getProgress());
			}
			
		})
		.setNegativeButton(R.string.cancel, null);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		valueTxt.setText(getValueText(progress));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

}