package org.grenoble.tour.activities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.grenoble.tour.beans.Poi;
import org.grenoble.tour.provider.POIRetriever;
import org.mapsforge.applications.android.samples.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class PoisActivity extends Activity {

	private ListView maListViewPerso;
	private Button more;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listofpoi);

		maListViewPerso = (ListView) findViewById(R.id.listviewperso);

		ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

		HashMap<String, String> map;

		map = new HashMap<String, String>();
		map.put("titre", "Ecole de la paix");
		map.put("description",
				"L'Ecole de la paix est une association, créée en 1998, qui agit pour la promotion de la culture de la paix par le biais de l'éducation. "
						+ "A Grenoble, son équipe intervient dans de nombreuses écoles primaires, collèges et lycées. A l'étranger, c'est au Rwanda, au Congo Brazzaville, "
						+ "en Colombie et en Tunisie que l'association intervient pour aider les populations après des conflits."
						+ "Pour en savoir plus: www.ecoledelapaix.org");
		map.put("img", String.valueOf(R.drawable.ecoledelapaix_logo));
		listItem.add(map);
		map = new HashMap<String, String>();
		map.put("titre", "Casemate");
		map.put("description",
				"Patrie des fortification Haxo construite au milieu du 19e pour protéger la ville des invasions italiennes."
						+ " On retrouve ce type de construction militaire tout le long de la chainedes alpes, de Grenoble à Briançon.");
		map.put("img", String.valueOf(R.drawable.casemate));
		listItem.add(map);

		map = new HashMap<String, String>();
		map.put("titre", "Rue TC");
		map.put("description",
				"Patrie des fortification Haxo construite au milieu du 19e pour protéger la ville des invasions italiennes."
						+ " On retrouve ce type de construction militaire tout le long de la chainedes alpes, de Grenoble à Briançon.");
		map.put("img", String.valueOf(R.drawable.test));
		listItem.add(map);

		map = new HashMap<String, String>();
		map.put("titre", "Renovation Rue Servan");
		map.put("description",
				"La rue Servan qui relie le quartier des Antiquaires de Grenoble à la rue Très­Cloîtres, "
						+ "a fait l'objet d'une rénovation en deux temps dans les années 1980­90. Comme vous pouvez le constater sur "
						+ "cette photo, la plupart des logements étaient devenus trop insalubres et avaient dû être murés. La rénovation a "
						+ "permis l'arrivée de nouveaux habitants grâce à un programme d'accession à la propriété, tout en conservant le tracé "
						+ "originel des immeubles du 19e siècle.");
		map.put("img", String.valueOf(R.drawable.rueservan));
		listItem.add(map);

		map = new HashMap<String, String>();
		map.put("titre", "Hotel Vaucanson et son escalier");
		map.put("description", " Hotel Vaucanson et son escalier est un hotel de grenobe " + "situé au 8 rue Chenoise");
		map.put("img", String.valueOf(R.drawable.hotelvaucanson));
		listItem.add(map);

		map = new HashMap<String, String>();
		map.put("titre", "Place de philippeville");
		map.put("description",
				"Sur cette place s’élevait le couvent et l’église des Augustins, qui avaient été "
						+ "construits en 1623. L’ordre des Augustins, extrêmement puissant, disparut en 1789. Lorsque le couvent fut détruit,"
						+ " construction d’une manutention militaire à la place. Disparut à son tour début XXème pour laisser  place à la place."
						+ " Jusqu’au XVIIème coulait à cet endroit le Draquet, petit bras du Drac, qui se jetait dans l’Isère près du  pont Marius-Gontard.");
		map.put("img", String.valueOf(R.drawable.test));
		listItem.add(map);

		List<Poi> listPoi = new ArrayList<Poi>();
		try {
			listPoi.addAll(POIRetriever.parsebis(this.getAssets().open("POI.osm")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (Poi p : listPoi) {
			String s = p.getImage();
			map = new HashMap<String, String>();
			map.put("titre", p.getName());
			map.put("description", p.getDesc());
			map.put("img", String.valueOf(R.drawable.notfound));
			listItem.add(map);
		}

		SimpleAdapter mSchedule = new SimpleAdapter(this.getBaseContext(), listItem, R.layout.affichageitempoi,
				new String[] { "img", "titre", "description" }, new int[] { R.id.img, R.id.name, R.id.description });

		maListViewPerso.setAdapter(mSchedule);

		maListViewPerso.setOnItemClickListener(new OnItemClickListener() {
			@Override
			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> a, View v, int position, long id) {
				// on récupère la HashMap contenant les infos de notre item (titre, description, img)
				HashMap<String, String> map = (HashMap<String, String>) maListViewPerso.getItemAtPosition(position);
				// on créer une boite de dialogue
				AlertDialog.Builder adb = new AlertDialog.Builder(PoisActivity.this);
				// on attribut un titre à notre boite de dialogue
				adb.setTitle("Sélection Item");
				// on insère un message à notre boite de dialogue, et ici on affiche le titre de l'item cliqué
				adb.setMessage("Votre choix : " + map.get("titre"));
				// on indique que l'on veut le bouton ok à notre boite de dialogue
				adb.setPositiveButton("Ok", null);
				// on affiche la boite de dialogue

				adb.show();
			}
		});

	}

}