package de.riwo.tetrix;

import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.TextView;

public class HelpActivity extends Activity {

	private TextView faqExplain;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
		
		faqExplain = (TextView) findViewById(R.id.faq_explain);
		Linkify.addLinks(faqExplain, Linkify.EMAIL_ADDRESSES);
	}

}