package org.grenoble.tour.activities;

import java.util.Locale;

import org.mapsforge.applications.android.samples.R;

import android.app.Activity;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class WebViewActivity extends Activity implements TextToSpeech.OnInitListener {

	private WebView webView;
	private TextToSpeech tts;
	private String description = "ok";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);

		this.webView = (WebView) findViewById(R.id.webView1);
		this.webView.getSettings().setJavaScriptEnabled(true);
		String url = this.getIntent().getExtras().getString("url");
		this.description = this.getIntent().getExtras().getString("desc");
		this.webView.loadUrl("file:///android_asset/" + url + ".html");

		tts = new TextToSpeech(this, this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_web, menu);
		menu.add("Settings");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_mic) {
			speakOut(description);
		}

		return true;
	}

	@Override
	public void onDestroy() {
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public void onInit(int status) {

		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.FRANCE);

			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "language pas bon");
			}

		} else {
			Log.e("TTS", "Init failed");
		}

	}

	private void speakOut(String txt) {

		tts.speak(txt, TextToSpeech.QUEUE_FLUSH, null);
	}

}