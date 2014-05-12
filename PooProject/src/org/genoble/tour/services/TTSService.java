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
package org.genoble.tour.services;

import java.util.Locale;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

public class TTSService extends Service implements TextToSpeech.OnInitListener {
	private Context ctx;
	private TextToSpeech tts;
	private String TTSDialog = "org.grenoble.intent.action.TTSDialog";

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub

		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		Log.i("test", "tuu");
		int result = tts.setLanguage(Locale.FRANCE);
		tts.speak("testeri", TextToSpeech.QUEUE_FLUSH, null);

		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown tts!
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.US);

			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			} else {
				speakOut("TTS en langue " + result);
			}

		} else {
			Log.e("TTS", "Initilization Failed!");
		}
	}

	private void speakOut(String txt) {

		tts.speak(txt, TextToSpeech.QUEUE_FLUSH, null);
	}

}
