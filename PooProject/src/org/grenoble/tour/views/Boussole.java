/*
 * Copyright 2010, 2011, 2012 mapsforge.org
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.grenoble.tour.views;

import org.mapsforge.applications.android.samples.R;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Boussole extends Activity implements SensorEventListener {

	// define the display assembly compass picture

	private ImageView image;

	// record the compass picture angle turned

	private float currentDegree = 0f;

	// device sensor manager

	private SensorManager mSensorManager;

	TextView tvHeading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.compas_dialog_view);

		//

		image = (ImageView) findViewById(R.id.imageViewCompass);

		// TextView that will tell the user what degree is he heading

		tvHeading = (TextView) findViewById(R.id.tvHeading);

		// initialize your android device sensor capabilities

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		Button b = (Button) this.findViewById(R.id.button1);
		b.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

	}

	@Override
	protected void onResume() {

		super.onResume();

		// for the system's orientation sensor registered listeners

		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),

		SensorManager.SENSOR_DELAY_GAME);

	}

	@Override
	protected void onPause() {

		super.onPause();

		// to stop the listener and save battery

		mSensorManager.unregisterListener(this);

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		// get the angle around the z-axis rotated

		float degree = Math.round(event.values[0]);

		tvHeading.setText("Heading: " + Float.toString(degree) + " degrees");

		// create a rotation animation (reverse turn degree degrees)

		RotateAnimation ra = new RotateAnimation(

		currentDegree,

		-degree,

		Animation.RELATIVE_TO_SELF, 0.5f,

		Animation.RELATIVE_TO_SELF,

		0.5f);

		// how long the animation will take place

		ra.setDuration(210);

		// set the animation after the end of the reservation status

		ra.setFillAfter(true);

		// Start the animation

		image.startAnimation(ra);

		currentDegree = -degree;

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

		// not in use

	}

}
