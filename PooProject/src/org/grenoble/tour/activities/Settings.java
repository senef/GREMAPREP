package org.grenoble.tour.activities;

import org.mapsforge.applications.android.samples.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class Settings extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		setContentView(R.layout.settings_layout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

}
