package org.grenoble.tour.activities;

import org.mapsforge.applications.android.samples.R;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

/**
 * @author KaHvEcI
 */
public class WebViewActivity extends Activity {

	private WebView webView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);

		this.webView = (WebView) findViewById(R.id.webView1);
		this.webView.getSettings().setJavaScriptEnabled(true);
		String url = this.getIntent().getExtras().getString("url");
		this.webView.loadUrl("file:///android_asset/" + url);

	}

}