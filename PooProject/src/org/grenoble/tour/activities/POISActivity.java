package org.grenoble.tour.activities;

import java.util.ArrayList;

import org.grenoble.tour.adapter.ItemAdapter;
import org.grenoble.tour.adapter.Model;
import org.grenoble.tour.beans.Poi;
import org.mapsforge.applications.android.samples.R;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class POISActivity extends Activity {

	ListView listView;
	ArrayList<Poi> lp = new ArrayList<Poi>();

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.listofpoi);
		// lp = getIntent().getExtras().getParcelableArrayList("pois");
		Log.d("tt", "OK " + lp.size());
		try {
			Model.LoadModel(this, lp);
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		listView = (ListView) findViewById(R.id.listviewperso);
		String[] ids = new String[Model.Items.size()];
		for (int i = 0; i < ids.length; i++) {

			ids[i] = Integer.toString(i + 1);
		}

		ItemAdapter adapter = new ItemAdapter(this, R.layout.row, ids);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				// on récupère la HashMap contenant les infos de notre item (titre, description, img)
				// on créer une boite de dialogue
				AlertDialog.Builder adb = new AlertDialog.Builder(POISActivity.this);
				// on attribut un titre à notre boite de dialogue
				adb.setTitle("Sélection Item");
				// on insère un message à notre boite de dialogue, et ici on affiche le titre de l'item cliqué
				adb.setMessage("Votre choix : ");
				// on indique que l'on veut le bouton ok à notre boite de dialogue
				adb.setPositiveButton("Ok", null);
				// on affiche la boite de dialogue
			}
		});

	}
}