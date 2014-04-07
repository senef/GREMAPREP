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

import org.grenoble.tour.beans.Poi;
import org.grenoble.tour.beans.Way;
import org.grenoble.tour.provider.POIRetriever;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapView;
import org.mapsforge.android.maps.overlay.ListOverlay;
import org.mapsforge.android.maps.overlay.Marker;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.android.maps.overlay.PolygonalChain;
import org.mapsforge.android.maps.overlay.Polyline;
import org.mapsforge.applications.android.samples.R;
import org.mapsforge.core.model.GeoPoint;
import org.mapsforge.map.reader.header.FileOpenResult;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * A simple application which demonstrates how to use a MapView.
 */
public class MapViewer extends MapActivity {
	private static final File MAP_FILE = new File(Environment.getExternalStorageDirectory().getPath(),
			"rhone-alpes.map");

	// current localization
	// private static final GeoPoint MY_LOCATION = new GeoPoint(52.516273, 13.377725);

	//
	MapView mapView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ---------loc
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// LocationListener locationListener = new GPSLocationListener();
		// locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
		// double lati = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
		// double longi = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
		// GeoPoint gp = new GeoPoint(longi, lati);

		Criteria critere = new Criteria();

		// Pour indiquer la précision voulue
		// On peut mettre ACCURACY_FINE pour une haute précision ou ACCURACY_COARSE pour une moins bonne précision
		critere.setAccuracy(Criteria.ACCURACY_COARSE);

		// Est-ce que le fournisseur doit être capable de donner une altitude ?
		critere.setAltitudeRequired(true);

		// Est-ce que le fournisseur doit être capable de donner une direction ?
		// critere.setBearingRequired(true);

		// Est-ce que le fournisseur peut être payant ?
		critere.setCostAllowed(false);

		// Pour indiquer la consommation d'énergie demandée
		// Criteria.POWER_HIGH pour une haute consommation, Criteria.POWER_MEDIUM pour une consommation moyenne et
		// Criteria.POWER_LOW pour une basse consommation
		critere.setPowerRequirement(Criteria.POWER_HIGH);

		// Est-ce que le fournisseur doit être capable de donner une vitesse ?
		// critere.setSpeedRequired(true);
		List<String> listProvi = locationManager.getProviders(critere, true);
		Log.d("LISTPROV", listProvi.toString());

		String provi = locationManager.getBestProvider(critere, true);
		Log.d("PROVI", "provi = " + provi);
		// locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 60000, 150, new LocationListener() {
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
					Log.d("GPS", "Latitude " + location.getLatitude() + " et longitude " + location.getLongitude());
					// GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6),
					// (int) (location.getLongitude() * 1E6));
					double lati = location.getLatitude();
					Log.d("Lat", "Latitude : " + lati);
					double longi = location.getLongitude();
					Log.d("Longi", "Longitude : " + longi);

					GeoPoint point = new GeoPoint(lati, longi);
					Log.d("Sattar", "sattare  ");

					// add marker
					Marker marker1 = createMarker(R.drawable.marker_red, point);
					ListOverlay listOverlay = new ListOverlay();
					List<OverlayItem> overlayItems = listOverlay.getOverlayItems();
					overlayItems.add(marker1);
					MapViewer.this.mapView.getOverlays().add(listOverlay);
					mapView.getMapViewPosition().setCenter(point);
				}

			}
		};

		locationManager.requestLocationUpdates(provi, 60000, 150, lis);
		// Location loc = locationManager.getLastKnownLocation(provi);
		Log.d("BLA", " lol hhh kikikik");

		// ---------afficher le map---------
		this.mapView = new MapView(this);

		this.mapView.setClickable(true);
		this.mapView.setBuiltInZoomControls(true);
		FileOpenResult fileOpenResult = this.mapView.setMapFile(MAP_FILE);
		if (!fileOpenResult.isSuccess()) {
			Toast.makeText(this, fileOpenResult.getErrorMessage(), Toast.LENGTH_LONG).show();
			finish();
		}
		setContentView(mapView);

		setUp_PointsOfInterest();
		try {
			setUp_Ways();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	Marker createMarker(int resourceIdentifier, GeoPoint geoPoint) {
		Drawable drawable = getResources().getDrawable(resourceIdentifier);
		return new Marker(geoPoint, Marker.boundCenterBottom(drawable));
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
		List<OverlayItem> overlayItems = listOverlay.getOverlayItems();

		Marker mark;
		for (Poi p : listPoi) {
			mark = createMarker(R.drawable.marker_green, p.getNewPoint());
			overlayItems.add(mark);
		}
		mapView.getOverlays().add(listOverlay);

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
		for (Way w : listWay) {
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

}
