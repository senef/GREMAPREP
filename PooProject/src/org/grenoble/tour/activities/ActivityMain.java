package org.grenoble.tour.activities;

import org.mapsforge.applications.android.samples.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class ActivityMain extends Activity {

	private Button createButton(final Class<?> clazz) {
		final Button button = new Button(this);
		button.setText(clazz.getSimpleName());
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(button.getContext(), clazz));
			}
		});
		return button;
	}

	public void onCreate(Bundle savedInstanceState) {

		final Context context = this;

		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.samples);
		linearLayout.addView(createButton(SplashScreen.class));
		linearLayout.addView(createButton(MapViewer.class));
		linearLayout.addView(createButton(Settings.class));

	}

	private Button createButton2(final Class<?> clazz) {
		final Button button = new Button(this);
		button.setText(clazz.getSimpleName());
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent intent = new Intent(button.getContext(), clazz);
				intent.putExtra("url", "test.html");
				startActivity(intent);
			}
		});
		return button;
	}

}