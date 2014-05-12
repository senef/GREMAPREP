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
package org.grenoble.tour.activities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.genoble.tour.services.GPSTracker;
import org.grenoble.tour.beans.Poi;
import org.grenoble.tour.beans.Way;
import org.grenoble.tour.provider.POIRetriever;
import org.grenoble.tour.views.Boussole;
import org.grenoble.tour.views.MapView;
import org.grenoble.tour.views.Marker;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.overlay.ListOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.android.maps.overlay.Polyline;
import org.mapsforge.applications.android.samples.R;
import org.mapsforge.core.model.GeoPoint;
import org.mapsforge.map.reader.header.FileOpenResult;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.Toast;

public class MapViewer extends MapActivity implements SensorEventListener {
	private static final File MAP_FILE = new File(Environment.getExternalStorageDirectory().getPath(),
			"rhone-alpes.map");
	MapView mapView;
	// my place
	private Marker myPlace;
	//
	private LocationManager locationManager;
	//
	private ListOverlay listOverlay = new ListOverlay();
	private ImageView imageBoussole;
	private float currentDegree = 0f;
	private SensorManager mSensorManager;
	private GeoPoint currentPoint;
	ArrayList<Poi> pois = new ArrayList<Poi>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Location management
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		updateLocation(locationManager);

		LayoutInflater factory = LayoutInflater.from(this);
		final View aView = factory.inflate(R.layout.activity_map, null);
		mapView = (MapView) aView.findViewById(R.id.map);

		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		FileOpenResult fileOpenResult = this.mapView.setMapFile(MAP_FILE);
		if (!fileOpenResult.isSuccess()) {
			Toast.makeText(this, fileOpenResult.getErrorMessage(), Toast.LENGTH_LONG).show();
			finish();
		}
		setContentView(aView);
		try {
			set_PointsOfInterest();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// boussole
		imageBoussole = (ImageView) aView.findViewById(R.id.boussole_view);
		// initialiser sensor capabilities
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

	}

	// met les POI sur la map
	public void set_PointsOfInterest() throws XmlPullParserException {

		// -----remplir listPoi
		try {
			// retrieving POI from POI.osm file
			pois.addAll(POIRetriever.parse(getAssets().open("listeOfPoi.osm")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		// // ---------ajouter des marqueur---------

		ListOverlay listOverlay = new ListOverlay();
		Marker mark;
		Point point = new Point();
		/*
		 * List<GeoPoint> lgp = new ArrayList<GeoPoint>(); for (Poi p : listPoi) { lgp.add(p.getNewPoint()); }
		 * //this.mapView.addRoute(1, lgp, Color.RED, 18);
		 */
		for (Poi p : pois) {
			mark = this.mapView.createMarker(R.drawable.marker_blue, p.getNewPoint(), p.getName(), p.getDesc());
			mark.setId(Integer.parseInt(p.getId()));
			this.mapView.addMarker(mark);
			// lgp.add(p.getNewPoint());

		}

	}

	private void updateLocation(LocationManager locationManager) {
		// provider
		String provi = "network";
		// location listener
		LocationListener lis = new LocationListener() {

			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {

			}

			@Override
			public void onProviderEnabled(String provider) {

			}

			@Override
			public void onProviderDisabled(String provider) {

			}

			@Override
			public void onLocationChanged(Location location) {
				if (location != null) {
					Log.d("LOCALISATION",
							"Latitude " + location.getLatitude() + " et longitude " + location.getLongitude());
					// GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6),
					// (int) (location.getLongitude() * 1E6));
					double lati = location.getLatitude();
					Log.d("LOCALISATION", "Latitude : " + lati);
					double longi = location.getLongitude();
					Log.d("LOCALISATION", "Longitude : " + longi);

					GeoPoint point = new GeoPoint(lati, longi);

					// add marker
					List<OverlayItem> overlayItems = listOverlay.getOverlayItems();
					overlayItems.remove(myPlace);
					myPlace = mapView.createMarker(R.drawable.marker_red, point, "position actuelle", "position");
					overlayItems.add(myPlace);
					// MapViewer.mapView.getOverlays().clear();
					mapView.getOverlays().add(listOverlay);
					mapView.getMapViewPosition().setCenter(point);
					currentPoint = point;
				}

			}
		};
		Criteria criteria = new Criteria();
		provi = locationManager.getBestProvider(criteria, true);
		Log.d("LOCALISATION", provi);
		// demander localisation
		locationManager.requestLocationUpdates(provi, 0, 0, lis);

	}

	// dessine les routes sur la carte
	public void setUp_Ways() throws IOException {
		// // ---------dessiner les chemins---------

		ListOverlay listOverlay = new ListOverlay();
		List<OverlayItem> overlayItems = listOverlay.getOverlayItems();
		ArrayList<Way> listWay = new ArrayList<Way>();
		try {
			// retrieving Ways from POI.osm file
			listWay.addAll(POIRetriever.parseWays(this.getAssets().open("POI.osm")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Polyline polyline;
		for (Way w : listWay.subList(15, 50)) {
			w.setNodes(this.getAssets().open("POI.osm"));
			setMarkers(w.getNoeuds());
			mapView.addRoute(w.getRouteID(), w.getNoeuds(), Color.BLUE, 18);

		}

		// Polyline polyline = createPolyline();
		mapView.getOverlays().add(listOverlay);
	}

	public void setMarkers(List<GeoPoint> lm) {
		Marker mark;
		for (GeoPoint p : lm) {
			mark = this.mapView.createMarker(R.drawable.node, p, "node", null);
			this.mapView.addMarker(mark);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);

		menu.add("ListPOI");
		menu.add("Itineraire");
		menu.add("Settings");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("Settings")) {
			Intent intent = new Intent(this, Settings.class);
			startActivity(intent);
		} else if (item.getTitle().equals("Itineraire")) {
			Toast.makeText(this, "not done", Toast.LENGTH_SHORT);
		} else if (item.getItemId() == R.id.menu_locate) {
			GPSTracker gps = null;
			gps = new GPSTracker(this);

			// check if GPS enabled
			if (gps.canGetLocation()) {

				double latitude = gps.getLatitude();
				double longitude = gps.getLongitude();
				mapView.removeMarker(myPlace);
				myPlace = mapView.createMarker(R.drawable.marker_red, new GeoPoint(latitude, longitude),
						"position actuelle", "position");

				mapView.addMarker(myPlace);
				mapView.showUserMarker(true);
				mapView.getMapViewPosition().setCenter(new GeoPoint(latitude, longitude));

			} else {
				Toast.makeText(this, "position inconnue...", Toast.LENGTH_SHORT).show();
			}
		} else {
			Intent intent = new Intent(this, POISActivity.class);
			startActivity(intent);
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see org.mapsforge.android.maps.MapActivity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
		if (mapView.getTts() != null) {
			mapView.getTts().stop();
			mapView.getTts().shutdown();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.mapsforge.android.maps.MapActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

		mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				SensorManager.SENSOR_DELAY_GAME);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// get the angle around the z-axis rotated

		float degree = Math.round(event.values[0]);
		RotateAnimation ra = new RotateAnimation(currentDegree, -degree, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		ra.setDuration(210);
		// set the animation after the end of the reservation status
		ra.setFillAfter(true);
		// Start the animation
		imageBoussole.startAnimation(ra);
		currentDegree = -degree;
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	public void showBoussole(View v) {

		Intent i = new Intent(this, Boussole.class);
		this.startActivity(i);
	}

	public void showListOfPOI(View v) throws InterruptedException {
		/*
		 * Animation animRotate = AnimationUtils.loadAnimation(this, R.anim.translate); v.startAnimation(animRotate);
		 */

		Intent i = new Intent(this, POISActivity.class);

		i.putParcelableArrayListExtra("pois", pois);
		Log.d("tgh", "" + pois.size());
		this.startActivity(i);

	}

}
