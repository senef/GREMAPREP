package org.grenoble.tour.views;

import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.GeoPoint;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.MercatorProjection;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;

public class MarkerBubble extends Marker {
	private Drawable drawable;
	private Drawable bubble;
	private GeoPoint geoPoint;
	private String name;

	/**
	 * @param geoPoint
	 *            the initial geographical coordinates of this marker (may be null).
	 * @param drawable
	 *            the initial {@code Drawable} of this marker (may be null).
	 */
	public MarkerBubble(Context ctx, GeoPoint geoPoint, Drawable drawable, Drawable bubble, String name) {
		super(ctx);
		this.geoPoint = geoPoint;
		this.drawable = drawable;
		this.bubble = bubble;
		this.name = name;
	}

	public MarkerBubble(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Sets the bounds of the given drawable so that (0,0) is the center of its bounding box.
	 * 
	 * @param drawable
	 *            the drawable whose bounds should be set.
	 * @return the given drawable with set bounds.
	 */
	public static Drawable boundCenter(Drawable drawable) {
		int intrinsicWidth = drawable.getIntrinsicWidth();
		int intrinsicHeight = drawable.getIntrinsicHeight();
		drawable.setBounds(intrinsicWidth / -2, intrinsicHeight / -2, intrinsicWidth / 2, intrinsicHeight / 2);
		return drawable;
	}

	/**
	 * Sets the bounds of the given drawable so that (0,0) is the center of its bottom row.
	 * 
	 * @param drawable
	 *            the drawable whose bounds should be set.
	 * @return the given drawable with set bounds.
	 */
	public static Drawable boundCenterBottom(Drawable drawable) {
		int intrinsicWidth = drawable.getIntrinsicWidth();
		int intrinsicHeight = drawable.getIntrinsicHeight();
		drawable.setBounds(intrinsicWidth / -2, -intrinsicHeight, intrinsicWidth / 2, 0);
		return drawable;
	}

	private static boolean intersect(Canvas canvas, float left, float top, float right, float bottom) {
		return right >= 0 && left <= canvas.getWidth() && bottom >= 0 && top <= canvas.getHeight();

	}

	@Override
	public synchronized boolean draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point canvasPosition) {
		if (this.geoPoint == null || this.drawable == null) {
			return false;
		}
		Log.d("draw", "zoom : " + zoomLevel);
		double latitude = this.geoPoint.latitude;
		double longitude = this.geoPoint.longitude;
		int pixelX = (int) (MercatorProjection.longitudeToPixelX(longitude, zoomLevel) - canvasPosition.x);
		int pixelY = (int) (MercatorProjection.latitudeToPixelY(latitude, zoomLevel) - canvasPosition.y);

		Rect drawableBounds = this.drawable.copyBounds();
		int left = pixelX + drawableBounds.left;
		int top = pixelY + drawableBounds.top;
		int right = pixelX + drawableBounds.right;
		int bottom = pixelY + drawableBounds.bottom;

		if (!intersect(canvas, left, top, right, bottom)) {
			return false;
		}

		this.drawable.setBounds(left, top, right, bottom);
		this.drawable.draw(canvas);
		this.drawable.setBounds(drawableBounds);

		/*
		 * Paint paint = new Paint(); paint.setColor(Color.parseColor("#CD5C5C")); canvas.drawRect(50, 50, 200, 200,
		 * paint);
		 */

		if (this.geoPoint == null || this.bubble == null) {
			return false;
		}
		pixelX = (int) (MercatorProjection.longitudeToPixelX(longitude, zoomLevel) - canvasPosition.x);
		pixelY = (int) (MercatorProjection.latitudeToPixelY(latitude, zoomLevel) - canvasPosition.y);

		drawableBounds = this.bubble.copyBounds();
		left = pixelX + drawableBounds.left;
		top = pixelY + drawableBounds.top + this.drawable.copyBounds().top - 40;
		right = pixelX + drawableBounds.right;
		bottom = pixelY + drawableBounds.bottom - this.drawable.copyBounds().bottom - 40;

		if (!intersect(canvas, left, top, right, bottom)) {
			return false;
		}

		this.bubble.setBounds(left, top, right, bottom);
		this.bubble.draw(canvas);
		this.bubble.setBounds(drawableBounds);
		return true;
	}

	/**
	 * @return the {@code Drawable} of this marker (may be null).
	 */
	public synchronized Drawable getDrawable() {
		return this.drawable;
	}

	/**
	 * @return the geographical coordinates of this marker (may be null).
	 */
	public synchronized GeoPoint getGeoPoint() {
		return this.geoPoint;
	}

	/**
	 * @param drawable
	 *            the new {@code Drawable} of this marker (may be null).
	 */
	public synchronized void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}

	/**
	 * @param geoPoint
	 *            the new geographical coordinates of this marker (may be null).
	 */
	public synchronized void setGeoPoint(GeoPoint geoPoint) {
		this.geoPoint = geoPoint;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(this.getContext(), "yes", duration);
		toast.show();

		Log.i("tet", "yy");
		return true;

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub

	}

}
