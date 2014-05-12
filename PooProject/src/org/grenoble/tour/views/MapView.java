package org.grenoble.tour.views;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.grenoble.tour.activities.WebViewActivity;
import org.grenoble.tour.overlays.MarkerImplementor;
import org.grenoble.tour.overlays.OnMarkerClickListener;
import org.grenoble.tour.receivers.TTSReceiver;
import org.mapsforge.android.maps.overlay.Circle;
import org.mapsforge.android.maps.overlay.ListOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.android.maps.overlay.PolygonalChain;
import org.mapsforge.android.maps.overlay.Polyline;
import org.mapsforge.applications.android.samples.R;
import org.mapsforge.core.model.GeoPoint;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.speech.tts.TextToSpeech;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

/**
 * This class extends mapsforge MapView and adds support for user overlay, location and heading updates. A MapView
 * object must be initialized with a context object.
 */

public class MapView extends org.mapsforge.android.maps.MapView implements TextToSpeech.OnInitListener {

	// ================================================================================
	// Members
	// ================================================================================

	// context
	private static Context ctx;

	// Entities
	private Marker mUserMarker; // User marker
	private boolean mCenterOnUser; // Center map on user for each location udpates
	private GeoPoint mUserLocation; // Current user location. Default : 0 0
	private boolean mShowUserMarker; // Show user marker. Default : false
	private boolean mShowLocationAccuracy; // Show user accuracy with a blue circle. Default : false
	private boolean mIsTraceEnabled; // When true trace is displayed. Default : false
	public List<Marker> mMarkers; // All markers used for POI
	private List<Marker> visibleBubbles;
	private List<Marker> hiddenBubbles;
	private boolean bubbleShown;

	// Drawing
	private Polyline mTrace; // User trace
	private ListOverlay mMarkerOverlays; // Marker overlays of the map
	private ListOverlay mUserOverlays; // User overlays of the map
	private ListOverlay mTraceOverlays; // Trace overlays of the map
	private Circle mAccuracyCircle; // Overlay showing location accuracy
	private SparseArray<Polyline> mRouteMap; // Map associating route id with a polyline.
	public OnMarkerClickListener onMarkerClickListener;

	// text to speech
	private static String TTSDialog = "org.grenoble.intent.action.TTSDialog";
	private TextToSpeech tts;
	private Button btnSpeak;

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
	 * @return the mMarkerOverlays
	 */
	public ListOverlay getmMarkerOverlays() {
		return mMarkerOverlays;
	}

	/**
	 * @param mMarkerOverlays
	 *            the mMarkerOverlays to set
	 */
	public void setmMarkerOverlays(ListOverlay mMarkerOverlays) {
		this.mMarkerOverlays = mMarkerOverlays;
	}

	/**
	 * Inits map view with content
	 * 
	 * @param context
	 * @param attributeSet
	 */
	public MapView(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		this.ctx = context;
		setThemeFromAssets("assets.xml");

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
		mUserMarker = createMarker(R.drawable.marker_red, new GeoPoint(0, 0), "Votre Position", "position courante");

		// Create POI list
		mMarkers = new ArrayList<Marker>();
		this.visibleBubbles = new ArrayList<Marker>();
		this.onMarkerClickListener = new MarkerImplementor();
		this.setOnMarkerClickListener(onMarkerClickListener);
		bubbleShown = false;
		setTts(new TextToSpeech(this.getContext(), this));

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
		// mUserMarker.setOrientation(orientation);

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

	public Marker createMarker(int resourceIdentifier, GeoPoint geoPoint, String string, String desc) {
		Drawable drawable = getResources().getDrawable(resourceIdentifier);
		return new Marker(this.getContext(), geoPoint, Marker.boundCenter(drawable), string, desc);
	}

	public MarkerBubble createMarkerbis(Drawable drawable, Drawable b, GeoPoint geoPoint, String string) {
		return new MarkerBubble(this.getContext(), geoPoint, MarkerBubble.boundCenter(drawable),
				MarkerBubble.boundCenter(b), string);
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

	public void addBubble(Marker marker) {

		mMarkerOverlays.getOverlayItems().add(marker);

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

		int width, height, left, top;

		if (onMarkerClickListener == null) {
			return true;
		}
		if (event.getAction() != MotionEvent.ACTION_DOWN) {
			return true;
		}

		this.hiddenBubbles = new ArrayList<Marker>();
		this.hiddenBubbles = this.visibleBubbles;
		this.visibleBubbles = new ArrayList<Marker>();
		// if (event.getAction() != MotionEvent.ACTION_MOVE && event.getPointerCount() == 1)
		for (Marker marker : mMarkers) {
			if (!this.bubbleShown) {
				width = marker.getDrawable().getIntrinsicWidth();
				height = marker.getDrawable().getIntrinsicHeight();
				Point p = this.getProjection().toPixels(marker.getGeoPoint(), null);
				left = p.x - width / 2;
				top = p.y - height / 2;
				if ((event.getX(0) >= left) && (event.getY(0) >= top) && (event.getX(0) <= left + width)
						&& (event.getY(0) <= top + height)) {

					this.visibleBubbles.add(createMarkerbis(this.getResources().getDrawable(R.drawable.marker_red),
							this.toDrawable(marker.getName()),
							new GeoPoint(marker.getGeoPoint().latitude, marker.getGeoPoint().longitude),
							marker.getName()));
					TranslateAnimation translateAnim = new TranslateAnimation(200, 1000, 2000, 1000);
					// Use (0, 0, 200, 0 ) if you would like to animate this in a mobile device rather than a tab

					translateAnim.setDuration(500);
					translateAnim.setFillBefore(true);

					marker.setAnimation(translateAnim);
					marker.startAnimation(translateAnim);
					this.myRedraw();
					Canvas c = new Canvas();
					Paint paint = new Paint();
					paint.setColor(Color.parseColor("#CD5C5C"));
					c.drawRect(50, 50, 200, 200, paint);
					// this.getLayoutAnimation().
					// c.drawColor(Color.RED);
					// this.myRedraw();
					// onMarkerClickListener.onMarkerClick(marker, this.getContext(), this);

				}
			} else {
				width = toDrawable(marker.getName()).getIntrinsicWidth();
				height = toDrawable(marker.getName()).getIntrinsicHeight();
				Point p = this.getProjection().toPixels(marker.getGeoPoint(), null);
				p = this.getProjection().toPixels(marker.getGeoPoint(), null);
				p = this.getProjection().toPixels(marker.getGeoPoint(), null);
				left = p.x - width / 2;
				top = p.y - height / 2;
				if ((event.getX(0) >= left + width / 2 + width / 4) && (event.getY(0) >= top - height)
						&& (event.getX(0) <= left + width) && (event.getY(0) <= top)) {

					LayoutInflater factory = LayoutInflater.from(this.getContext());
					final View alertDialogView = factory.inflate(R.layout.dialog_poi, null);
					TextView tName = (TextView) alertDialogView.findViewById(R.id.textView_poiname);
					TextView tLat = (TextView) alertDialogView.findViewById(R.id.textView_lat);
					TextView tLon = (TextView) alertDialogView.findViewById(R.id.textView_lon);
					tName.setText(marker.getName());
					tLat.setText(" " + marker.getGeoPoint().latitude);
					tLon.setText("" + marker.getGeoPoint().longitude);
					Button bWebView = (Button) alertDialogView.findViewById(R.id.button_webview);
					final Marker m = marker;
					bWebView.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							Intent intent = new Intent(getContext(), WebViewActivity.class);

							intent.putExtra("url", "" + m.getId());
							intent.putExtra("desc", "" + m.getDesc());

							getContext().startActivity(intent);
						}
					});
					final String txt = marker.getName() + ".dont la latitude est égale à "
							+ marker.getGeoPoint().latitude + " et la longitude." + marker.getGeoPoint().longitude;
					this.btnSpeak = (Button) alertDialogView.findViewById(R.id.button_tts);
					btnSpeak.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View view) {
							speakOut(txt);
						}
					});

					AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.getContext());
					alertDialogBuilder.setTitle(marker.getName());
					alertDialogBuilder.setIcon(this.getContext().getResources().getDrawable(R.drawable.poi));
					alertDialogBuilder.setView(alertDialogView);
					alertDialogBuilder.setPositiveButton("Cancel", null);
					alertDialogBuilder.show();
				}
			}
		}
		if (this.visibleBubbles.isEmpty()) {
			hideBubbles();
			this.bubbleShown = false;
		} else {
			showbubbles();
			this.bubbleShown = true;
		}
		/*
		 * Intent intent = new Intent(ctx, TTSReceiver.class); intent.setAction(TTSDialog); intent.putExtra("TTStxt",
		 * "test"); ctx.sendBroadcast(intent);
		 */
		return true;
	}

	protected void sendTTS(String txt) {
		ctx = this.getContext();
		Intent intent = new Intent(ctx, TTSReceiver.class);
		intent.setAction(TTSDialog);
		// Bundle extras = new Bundle();
		// extras.putString("TTSDialog", "test");
		intent.putExtra("TTStxt", txt);
		ctx.sendBroadcast(intent);

	}

	public void showbubbles() {
		for (Marker m : this.visibleBubbles) {
			this.addBubble(m);

		}

		this.myRedraw();
	}

	public void hideBubbles() {
		if (!this.hiddenBubbles.isEmpty()) {
			for (Marker m : hiddenBubbles) {
				this.removeMarker(m);
			}
			this.myRedraw();
		}
	}

	public Drawable toDrawable(String txt) {

		TextView v = new TextView(this.getContext());
		v.setText(txt);
		Typeface font = Typeface.createFromAsset(this.getContext().getAssets(), "fonts/Walkway Black.ttf");
		v.setTypeface(font);
		v.setTextSize(15);
		v.setTextColor(Color.WHITE);
		v.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.bub));
		v.setDrawingCacheEnabled(true);

		v.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
		v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());

		v.buildDrawingCache(true);
		Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
		v.setDrawingCacheEnabled(false); //

		return new BitmapDrawable(getResources(), b);
	}

	public void setOnMarkerClickListener(OnMarkerClickListener onMarkerClickListener) {
		this.onMarkerClickListener = onMarkerClickListener;
	}

	public void myRedraw() {
		getOverlayController().redrawOverlays();
		invalidateOnUiThread();
	}

	@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {

			int result = getTts().setLanguage(Locale.FRANCE);

			if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "language pas bon");
			}

		} else {
			Log.e("TTS", "Init failed");
		}

	}

	private void speakOut(String txt) {

		getTts().speak(txt, TextToSpeech.QUEUE_FLUSH, null);
	}

	public TextToSpeech getTts() {
		return tts;
	}

	public void setTts(TextToSpeech tts) {
		this.tts = tts;
	}

}
