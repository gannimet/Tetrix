package de.riwo.tetrix;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TetroidActivity extends Activity implements OnClickListener {
    
	private Button playBtn, settingsBtn, helpBtn;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.title);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        
        initUI();
    }
	
	private void initUI() {
		playBtn = (Button) findViewById(R.id.playBtn);
        settingsBtn = (Button) findViewById(R.id.settingsBtn);
        helpBtn = (Button) findViewById(R.id.helpBtn);
        
        playBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);
        helpBtn.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) {
		if(view == playBtn) {
			Intent intent = new Intent(TetroidActivity.this, GameActivity.class);
			startActivity(intent);
		}
		
		if(view == settingsBtn) {
			Intent intent = new Intent(TetroidActivity.this, TetrisPreferencesActivity.class);
			startActivity(intent);
		}
		
		if(view == helpBtn) {
			Intent intent = new Intent(TetroidActivity.this, HelpActivity.class);
			startActivity(intent);
		}
	}
	
}