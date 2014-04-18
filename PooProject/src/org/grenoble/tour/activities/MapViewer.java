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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.grenoble.tour.beans.Poi;
import org.grenoble.tour.beans.Way;
import org.grenoble.tour.provider.POIRetriever;
import org.grenoble.tour.views.MapView;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.overlay.ListOverlay;
import org.mapsforge.android.maps.overlay.Marker;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.android.maps.overlay.PolygonalChain;
import org.mapsforge.android.maps.overlay.Polyline;
import org.mapsforge.applications.android.samples.R;
import org.mapsforge.core.model.GeoPoint;
import org.mapsforge.map.reader.header.FileOpenResult;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MapViewer extends MapActivity {
	private static final File MAP_FILE = new File(Environment.getExternalStorageDirectory().getPath(),
			"rhone-alpes.map");
	MapView mapView;
	// my place
	private Marker myPlace;
	//
	private LocationManager locationManager;
	//
	private ListOverlay listOverlay = new ListOverlay();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// pour débogage
		String sfile = Environment.getExternalStorageDirectory().getPath();
		Log.d("MAPFILE", sfile);
		// Location management
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		updateLocation(locationManager);
		mapView = new MapView(this);
		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);
		FileOpenResult fileOpenResult = this.mapView.setMapFile(MAP_FILE);
		if (!fileOpenResult.isSuccess()) {
			Toast.makeText(this, fileOpenResult.getErrorMessage(), Toast.LENGTH_LONG).show();
			finish();
		}
		this.mapView.showUserMarker(true);
		setContentView(mapView);

		setUp_PointsOfInterest();

	}

	// met les POI sur la map
	public void setUp_PointsOfInterest() {
		// -----remplir listPoi
		ArrayList<Poi> listPoi = new ArrayList<Poi>();
		try {
			// retrieving POI from POI.osm file
			listPoi.addAll(POIRetriever.parsebis(this.getAssets().open("POI.osm")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// // ---------ajouter des marqueur---------

		ListOverlay listOverlay = new ListOverlay();
		Marker mark;
		for (Poi p : listPoi.subList(1, 18)) {
			mark = this.mapView.createMarker(R.drawable.marker_green, p.getNewPoint());
			this.mapView.addMarker(mark);
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
					myPlace = mapView.createMarker(R.drawable.marker_red, point);
					overlayItems.add(myPlace);
					// MapViewer.mapView.getOverlays().clear();
					mapView.getOverlays().add(listOverlay);
					mapView.getMapViewPosition().setCenter(point);
				}

			}
		};
		// demander localisation
		locationManager.requestLocationUpdates(provi, 60000, 150, lis);

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
		for (Way w : listWay.subList(1, 5)) {
			w.setNodes(this.getAssets().open("POI.osm"));
			polyline = createPolyline(this, w.getNoeuds());
			Toast.makeText(this, "lat=" + w.getNoeuds().get(0).latitude, Toast.LENGTH_LONG);

			overlayItems.add(polyline);
		}

		// Polyline polyline = createPolyline();
		mapView.getOverlays().add(listOverlay);
	}

	private static Polyline createPolyline(Context ctx, List<GeoPoint> geoPoints) {
		PolygonalChain polygonalChain = new PolygonalChain(geoPoints);
		if (polygonalChain == null) {
			Log.i("jj", "is null");
		}
		Toast.makeText(ctx, polygonalChain.toString() + "test", Toast.LENGTH_LONG);
		Paint paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintStroke.setStyle(Paint.Style.STROKE);
		paintStroke.setColor(Color.MAGENTA);
		paintStroke.setAlpha(128);
		paintStroke.setStrokeWidth(7);
		paintStroke.setPathEffect(new DashPathEffect(new float[] { 25, 15 }, 0));

		return new Polyline(polygonalChain, paintStroke);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("ListPOI");
		menu.add("Itineraire");
		menu.add("Settings");

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getTitle().equals("Settings")) {
			Intent intent = new Intent(this, Settings.class);
			startActivity(intent);
		} else if (item.getTitle().equals("Itineraire")) {
			Toast.makeText(this, "not done", Toast.LENGTH_SHORT);
		} else {
			Intent intent = new Intent(this, PoisActivity.class);
			startActivity(intent);
		}

		return true;
	}

	public void installMapIntoSD() {
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
				in = assetManager.open("Files/" + filename); // if files resides inside the "Files" directory itself
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

	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}

}
