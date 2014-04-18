package org.grenoble.tour.views;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.grenoble.tour.overlays.MarkerImplementor;
import org.grenoble.tour.overlays.OnMarkerClickListener;
import org.mapsforge.android.maps.overlay.Circle;
import org.mapsforge.android.maps.overlay.ListOverlay;
import org.mapsforge.android.maps.overlay.Marker;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.android.maps.overlay.PolygonalChain;
import org.mapsforge.android.maps.overlay.Polyline;
import org.mapsforge.applications.android.samples.R;
import org.mapsforge.core.model.GeoPoint;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * This class extends mapsforge MapView and adds support for user overlay, location and heading updates. A MapView
 * object must be initialized with a context object.
 */
public class MapView extends org.mapsforge.android.maps.MapView {

	// ================================================================================
	// Members
	// ================================================================================

	// Entities
	private Marker mUserMarker; // User marker
	private boolean mCenterOnUser; // Center map on user for each location udpates
	private GeoPoint mUserLocation; // Current user location. Default : 0 0
	private boolean mShowUserMarker; // Show user marker. Default : false
	private boolean mShowLocationAccuracy; // Show user accuracy with a blue circle. Default : false
	private boolean mIsTraceEnabled; // When true trace is displayed. Default : false
	private List<Marker> mMarkers; // All markers used for POI

	// Drawing
	private Polyline mTrace; // User trace
	private ListOverlay mMarkerOverlays; // Marker overlays of the map
	private ListOverlay mUserOverlays; // User overlays of the map
	private ListOverlay mTraceOverlays; // Trace overlays of the map
	private Circle mAccuracyCircle; // Overlay showing location accuracy
	private SparseArray<Polyline> mRouteMap; // Map associating route id with a polyline.
	private OnMarkerClickListener onMarkerClickListener;
	LinearLayout bubble;

	// ================================================================================
	// Constructor
	// ================================================================================

	/**
	 * Inits map view with content
	 * 
	 * @param context
	 */
	public MapView(Context context) {
		this(context, null);
	}

	/**
	 * Inits map view with content
	 * 
	 * @param context
	 * @param attributeSet
	 */
	public MapView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);

		// Init list overlay
		mMarkerOverlays = new ListOverlay();
		mUserOverlays = new ListOverlay();
		mTraceOverlays = new ListOverlay();
		getOverlays().add(mMarkerOverlays);
		getOverlays().add(mTraceOverlays);
		getOverlays().add(mUserOverlays);

		// Init route map
		mRouteMap = new SparseArray<Polyline>();

		// Init user marker visibility
		mShowUserMarker = false;
		mShowLocationAccuracy = false;

		// Init center on user
		mCenterOnUser = false;

		// Trace init
		mIsTraceEnabled = false;

		// Create radius accuracy
		mAccuracyCircle = createAccuracyCircle();

		// Create marker
		mUserMarker = createMarker(R.drawable.poi, new GeoPoint(0, 0));

		// Create POI list
		mMarkers = new ArrayList<Marker>();
		this.onMarkerClickListener = new MarkerImplementor();
		this.setOnMarkerClickListener(onMarkerClickListener);

	}

	// ================================================================================
	// Drawing
	// ================================================================================

	/**
	 * Adds the user marker to the map
	 */
	public void showUserMarker(boolean accuracyNeeded) {

		// User marker already shown
		if (mShowUserMarker) {
			return;
		}

		// Updates user marker visibility
		mShowUserMarker = true;

		// Updates accuracy needs state
		mShowLocationAccuracy = accuracyNeeded;

		List<OverlayItem> overlayItems = mUserOverlays.getOverlayItems();

		/* Adds marker to the map if possible */
		if (!overlayItems.contains(mUserMarker)) {

			// Add accuracy below user to the map
			if (mShowLocationAccuracy) {
				overlayItems.add(mAccuracyCircle);
			}

			// Add user marker
			overlayItems.add(mUserMarker);

			// Updates map
			myRedraw();
		}
	}

	/**
	 * Hides the user marker from the map
	 */
	public void hideUserMarker() {

		// User marker already hidden
		if (!mShowUserMarker) {
			return;
		}

		// Updates user marker visibility
		mShowUserMarker = false;

		/* Removes marker if possible */
		List<OverlayItem> overlayItems = mUserOverlays.getOverlayItems();

		if (overlayItems.contains(mUserMarker)) {
			overlayItems.remove(mUserMarker);

			if (overlayItems.contains(mAccuracyCircle)) {
				overlayItems.remove(mAccuracyCircle);
			}

			// Updates map
			myRedraw();
		}
	}

	/**
	 * Enable trace on the map
	 * 
	 * @param enabled
	 *            If {@code true} user trace will be drawn
	 */
	public void enableTrace(boolean enabled) {
		mIsTraceEnabled = enabled;
	}

	public void clearTrace() {

		// Updates polygonal chain to null
		mTrace.setPolygonalChain(null);

		// Updates map
		myRedraw();
	}

	/**
	 * Draws on the map view the specified route
	 * 
	 * @param routeID
	 *            Route id
	 * @param locations
	 *            Nodes describing the route
	 * @param color
	 *            The route color
	 * @param strokeWidth
	 *            Set the width for stroking
	 */
	public void addRoute(int routeID, List<GeoPoint> locations, int color, int strokeWidth) {

		if (locations != null) {

			PolygonalChain routeChain = new PolygonalChain(locations);

			Paint paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
			paintStroke.setStyle(Paint.Style.STROKE);
			paintStroke.setColor(color);
			paintStroke.setAlpha(128); // Opacity : 50 %
			paintStroke.setStrokeWidth(strokeWidth);

			// Remove polyline with same id
			List<OverlayItem> overlayItems = mMarkerOverlays.getOverlayItems();
			Polyline previous_polyline = mRouteMap.get(routeID);

			if (previous_polyline != null) {
				mRouteMap.remove(routeID);
				overlayItems.remove(previous_polyline);
			}

			// Add the current polyline to route map with its id
			Polyline polyline = new Polyline(routeChain, paintStroke);
			mRouteMap.put(routeID, polyline);

			// Add the polyline to map overlay
			overlayItems.add(polyline);

			// Exchange user marker and polyline overlay
			if (overlayItems.contains(mUserMarker)) {
				Collections.swap(overlayItems, overlayItems.indexOf(polyline), overlayItems.indexOf(mUserMarker));
			}

			// Updates map
			myRedraw();
		}
	}

	/**
         * 
         */
	public void removeRoute(int routeID) {

		// Looking for polyline associated to the route id
		Polyline polyline = mRouteMap.get(routeID);

		// Remove it if possible
		if (polyline != null) {

			// Updates overlay
			List<OverlayItem> overlayItems = mMarkerOverlays.getOverlayItems();
			overlayItems.remove(polyline);

			// Updates route map
			mRouteMap.remove(routeID);

			// Updates map
			myRedraw();
		}
	}

	// ================================================================================
	// Setters
	// ================================================================================

	/**
	 * Updates current user marker with the given location
	 * 
	 * @param latitude
	 *            The new latitude to be applied
	 * @param longitude
	 *            The new longitude to be applied
	 */
	public void setUserLocation(double latitude, double longitude, float accuracy) {

		// Updates user location
		mUserLocation = new GeoPoint(latitude, longitude);

		// Updates marker location
		mUserMarker.setGeoPoint(mUserLocation);
		mAccuracyCircle.setGeoPoint(mUserLocation);
		mAccuracyCircle.setRadius(accuracy);

		// Updates trace
		if (mIsTraceEnabled) {
			updateTrace(mUserLocation);
		}

		// Adds markers (user and accuracy) if needed and not added
		List<OverlayItem> overlayItems = mUserOverlays.getOverlayItems();

		if (mShowUserMarker && !overlayItems.contains(mUserMarker)) {

			if (mShowLocationAccuracy) {
				overlayItems.add(mAccuracyCircle);
			}
			// Adds to the map
			overlayItems.add(mUserMarker);
		}

		// Center on user
		if (mCenterOnUser) {
			getMapViewPosition().setCenter(mUserLocation);
		}

		// Refresh
		myRedraw();
	}

	/**
	 * Updates current user heading with the given heading.
	 * 
	 * @param heading
	 *            In respect with the north, values in 0..359 range
	 */
	public void setUserOrientation(double orientation) {

		// Updates marker if not null
		mUserMarker.setOrientation(orientation);

		// Refresh
		myRedraw();
	}

	/**
	 * Set if the map must always be centered on user location
	 * 
	 * @param isTracked
	 *            If {@code true} map will always be centered on user location
	 */
	public void setCenterMapOnUser(boolean isTracked) {
		mCenterOnUser = isTracked;

		// Center on user location
		if (isTracked && mUserLocation != null) {
			getMapViewPosition().setCenter(mUserLocation);
		}
	}

	/**
	 * Sets the user marker.
	 * 
	 * @param drawable
	 *            The drawable to be applied for the user marker
	 */
	public void setUserOverlayMarker(int icon) {

		Drawable drawable = getResources().getDrawable(icon);

		// Updates drawable
		mUserMarker.setDrawable(Marker.boundCenter(drawable));

		// Updates map
		myRedraw();
	}

	/**
	 * Set current map zoom level. If the given value is higher than the maximum that is supported this value will be
	 * replaced by the maximum one
	 * 
	 * @param level
	 */
	public void setZoomLevel(Integer level) {
		getMapViewPosition().setZoomLevel(level.byteValue());
	}

	public void setCenter(GeoPoint geoPoint) {
		getMapViewPosition().setCenter(geoPoint);
	}

	// ================================================================================
	// Getters
	// ================================================================================

	/**
	 * @return Returns the coordinates of the current center
	 */
	public GeoPoint getCenter() {
		return getMapViewPosition().getCenter();
	}

	/**
	 * @return
	 */
	public int getZoomLevel() {
		Byte zoomLevel = getMapViewPosition().getZoomLevel();
		return zoomLevel.intValue();
	}

	public boolean isCenterMapOnUser() {
		return mCenterOnUser;
	}

	// ================================================================================
	// Internal
	// ================================================================================

	public Marker createMarker(int resourceIdentifier, GeoPoint geoPoint) {
		Drawable drawable = getResources().getDrawable(resourceIdentifier);
		return new Marker(geoPoint, Marker.boundCenter(drawable));
	}

	private Circle createAccuracyCircle() {

		Paint paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintFill.setStyle(Paint.Style.FILL);
		paintFill.setColor(Color.BLUE);
		paintFill.setAlpha(64);

		Paint paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintStroke.setStyle(Paint.Style.STROKE);
		paintStroke.setColor(Color.BLUE);
		paintStroke.setAlpha(128);
		paintStroke.setStrokeWidth(3);

		return new Circle(new GeoPoint(0, 0), 0, paintFill, paintStroke);
	}

	private void updateTrace(GeoPoint newLocation) {

		if (mTrace == null) {

			// Sets paint stroke
			Paint paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
			paintStroke.setStyle(Paint.Style.STROKE);
			paintStroke.setColor(Color.RED);
			paintStroke.setAlpha(128); // Opacity : 50 %
			paintStroke.setStrokeWidth(7);

			// Sets chain
			PolygonalChain chain = new PolygonalChain(Arrays.asList(newLocation));
			mTrace = new Polyline(chain, paintStroke);

			// Adds to map
			mTraceOverlays.getOverlayItems().add(mTrace);

			// Exchange with user overlay
			// List<OverlayItem> overlayItems = mUserOverlays.getOverlayItems();
			// if(mUserOverlays.getOverlayItems().contains(mUserMarker)) {
			// Collections.swap(overlayItems, overlayItems.indexOf(mTrace), overlayItems.indexOf(mUserMarker));
			// }

		} else {

			// Update polygonal chain with new location
			List<GeoPoint> locations = mTrace.getPolygonalChain().getGeoPoints();
			locations.add(newLocation);
			mTrace.setPolygonalChain(new PolygonalChain(locations));
		}

		// Updates map
		myRedraw();
	}

	/**
	 * Apply given theme to assets
	 * 
	 * @param fileName
	 */
	public void setThemeFromAssets(String fileName) {

		try {
			setRenderTheme(new File(getContext().getFilesDir(), fileName));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	/**
	 * Add the given collection of marker to the map
	 * 
	 * @param markers
	 */
	public void addMarkers(Collection<Marker> markers) {

		for (Marker marker : markers) {
			addMarker(marker);
		}
	}

	/**
	 * Add marker to the map
	 * 
	 * @param marker
	 */
	public void addMarker(Marker marker) {

		mMarkerOverlays.getOverlayItems().add(marker);
		mMarkers.add(marker);

	}

	/**
	 * Remove marker from the map
	 * 
	 * @param marker
	 */
	public void removeMarker(Marker marker) {
		mMarkerOverlays.getOverlayItems().remove(marker);
		mMarkers.remove(marker);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		if (onMarkerClickListener == null) {
			Log.i("toucht", "thr!");
			return true;
		}

		if (event.getAction() != MotionEvent.ACTION_DOWN) {
			return true;
		}

		int width, height, left, top;

		for (Marker marker : mMarkers) {

			width = marker.getDrawable().getIntrinsicWidth();
			height = marker.getDrawable().getIntrinsicHeight();

			left = marker.getPixelX() - width / 2;
			top = marker.getPixelY() - height / 2;

			if ((event.getX(0) >= left) && (event.getY(0) >= top) && (event.getX(0) <= left + width)
					&& (event.getY(0) <= top + height)) {
				onMarkerClickListener.onMarkerClick(marker, this.getContext(), this);
			}
		}

		return true;
	}

	public void setOnMarkerClickListener(OnMarkerClickListener onMarkerClickListener) {
		this.onMarkerClickListener = onMarkerClickListener;
	}

	public void myRedraw() {
		getOverlayController().redrawOverlays();
		invalidateOnUiThread();
	}

	// ===========================================================
	// Public Classes
	// ===========================================================

	public static class LayoutParams extends ViewGroup.LayoutParams {
		public static final int MODE_MAP = 0;
		public static final int MODE_VIEW = 1;
		public static final int LEFT = 3;
		public static final int RIGHT = 5;
		public static final int TOP = 48;
		public static final int BOTTOM = 80;
		public static final int CENTER_HORIZONTAL = 1;
		public static final int CENTER_VERTICAL = 16;
		public static final int CENTER = 17;
		public static final int TOP_LEFT = 51;
		public static final int BOTTOM_CENTER = 81;

		public int mode;
		public GeoPoint point;
		public int x;
		public int y;
		public int alignment;

		public LayoutParams(int width, int height, GeoPoint point, int alignment) {
			super(width, height);
		}

		public LayoutParams(int width, int height, GeoPoint point, int x, int y, int alignment) {
			super(width, height);
		}

		public LayoutParams(int width, int height, int x, int y, int alignment) {
			super(width, height);
		}

		public LayoutParams(int width, int height) {
			super(width, height);
		}

		public LayoutParams(ViewGroup.LayoutParams source) {
			super(source);
		}
	}
}
