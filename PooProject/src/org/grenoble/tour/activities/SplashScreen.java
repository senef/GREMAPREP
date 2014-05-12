package org.grenoble.tour.activities;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mapsforge.applications.android.samples.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		// lancement du splashscreen: voir dans res/anim

		// Animation animation;

		Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadeout);
		ImageView v = (ImageView) this.findViewById(R.id.splash);
		animation.setDuration(5000);
		v.startAnimation(animation);
		/*
		 * animation.setAnimationListener(new AnimationListener() { public void onAnimationEnd(Animation _animation) {
		 * // Que faire quand l'animation se termine ? (n'est pas lancé à la fin d'une répétition) } public void
		 * onAnimationRepeat(Animation _animation) { // Que faire quand l'animation se répète ? } public void
		 * onAnimationStart(Animation _animation) { // Que faire au premier lancement de l'animation ? } });
		 */

		// preparation des éléments (carte,etc..) pendant ce temps
		// utiliser des Threads pour des tâches parallèles, ca ira plus vite!

		Thread logoTimer = new Thread() {
			public void run() {
				try {
					int logoTimer = 0;
					while (logoTimer < 5000) {
						sleep(100);
						logoTimer = logoTimer + 100;
					};

					Intent i = new Intent(SplashScreen.this, MapViewer.class);
					startActivity(i);

				}

				catch (InterruptedException e) {
					e.printStackTrace();
				}

				finally {
					finish();
				}
			}
		};

		logoTimer.start();

		/****************************************************/
		// lancement du splashscreen: voir dans res/anim
		// preparation des éléments (carte,etc..) pendant ce temps
		// utiliser des Threads pour des tâches parallèles, ca ira plus vite!

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

	public void installMapIntoSD(String mapName) {
		AssetManager assetManager = getAssets();
		String[] files = null;
		try {
			files = assetManager.list("Files");
		} catch (IOException e) {
			Log.e("tag", e.getMessage());
		}

		for (String filename : files) {
			System.out.println("File name => " + filename);
			InputStream in = null;
			OutputStream out = null;
			System.out.println(filename);
			try {
				in = assetManager.open("Files/" + filename);
				out = new FileOutputStream(Environment.getExternalStorageDirectory().getPath() + "/" + filename);
				copyFile(in, out);
				in.close();
				in = null;
				out.flush();
				out.close();
				out = null;
			} catch (Exception e) {
				Log.e("tag", e.getMessage());
			}
		}
	}

	public void removeFromApp() {
		// A FAIRE
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

}
