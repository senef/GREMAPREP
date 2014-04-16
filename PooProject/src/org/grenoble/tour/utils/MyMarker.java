package org.grenoble.tour.utils;

import org.mapsforge.android.maps.overlay.Marker;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.GeoPoint;
import org.mapsforge.core.model.Point;
import org.mapsforge.core.util.MercatorProjection;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class MyMarker extends Marker {

	private String text;

	private Paint paint;
	private int offsetX = 0;
	private int offsetY = 0;

	/**
	 * @param txt
	 *            the title
	 * @param paint
	 *            The paint used for the text (e.g. color, size, style)
	 * @param geoPoint
	 *            the initial geographical coordinates of this marker (may be null).
	 * @param drawable
	 *            the initial {@code Drawable} of this marker (may be null).
	 */

	public MyMarker(String txt, Paint paint, GeoPoint geoPoint, Drawable drawable) {

		super(geoPoint, drawable);
		this.text = txt;

		this.paint = paint;

	}

	@Override
	public synchronized boolean draw(BoundingBox boundingBox, byte zoomLevel, Canvas canvas, Point canvasPosition) {
		if (getGeoPoint() == null || getDrawable() == null) {
			return false;
		}

		double latitude = getGeoPoint().latitude;
		double longitude = getGeoPoint().longitude;
		int pixelX = (int) (MercatorProjection.longitudeToPixelX(longitude, zoomLevel) - canvasPosition.x);
		int pixelY = (int) (MercatorProjection.latitudeToPixelY(latitude, zoomLevel) - canvasPosition.y);

		Rect drawableBounds = getDrawable().copyBounds();
		int left = pixelX + drawableBounds.left;
		int top = pixelY + drawableBounds.top;
		int right = pixelX + drawableBounds.right;
		int bottom = pixelY + drawableBounds.bottom;

		if (!intersect(canvas, left, top, right, bottom)) {
			return false;
		}

		getDrawable().setBounds(left, top, right, bottom);
		getDrawable().draw(canvas);
		getDrawable().setBounds(drawableBounds);

		canvas.drawText(text, pixelX + offsetX, pixelY + offsetY, paint);

		return true;
	}

	private static boolean intersect(Canvas canvas, float left, float top, float right, float bottom) {
		return right >= 0 && left <= canvas.getWidth() && bottom >= 0 && top <= canvas.getHeight();
	}

	public void setOffsetX(int offsetX) {
		this.offsetX = offsetX;
	}

	public void setOffsetY(int offsetY) {
		this.offsetY = offsetY;
	}
}