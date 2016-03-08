package de.riwo.tetrix;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class TetrisPreferencesActivity extends PreferenceActivity
		implements OnPreferenceChangeListener {

	private ListPreference colorOptions;
	private Preference colorChoice;
	private ListPreference holdButtonsOptions;
	private Preference dropSpeedChoice;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		colorOptions = (ListPreference) findPreference("stone_colors");
		colorChoice = (Preference) findPreference("single_color_choice");
		holdButtonsOptions = (ListPreference) findPreference("hold_control_buttons");
		dropSpeedChoice = (Preference) findPreference("stone_velocity");
		
		handleColorPreferenceEnabling(colorOptions.getValue());
		handleHoldButtonsPreferenceEnabling(holdButtonsOptions.getValue());
		
		// register listener for dynamically enabling/disabling the resp. choice
		colorOptions.setOnPreferenceChangeListener(this);
		holdButtonsOptions.setOnPreferenceChangeListener(this);
	}
	
	private void handleColorPreferenceEnabling(String optionsValue) {
		// if the single color strategy was selected, enable the color choice
		// preference, otherwise disable it
		if("single".equals(optionsValue)) {
			colorChoice.setEnabled(true);
		} else {
			colorChoice.setEnabled(false);
		}
	}
	
	private void handleHoldButtonsPreferenceEnabling(String newValue) {
		// disable on none, enable on everything else
		if("none".equals(newValue)) {
			dropSpeedChoice.setEnabled(false);
		} else {
			dropSpeedChoice.setEnabled(true);
		}
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if("stone_colors".equals(preference.getKey())) {
			handleColorPreferenceEnabling((String) newValue);
		}
		
		if("hold_control_buttons".equals(preference.getKey())) {
			handleHoldButtonsPreferenceEnabling((String) newValue);
		}
		
		return true;
	}

}