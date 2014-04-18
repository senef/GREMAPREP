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
package org.grenoble.tour.overlays;

import org.grenoble.tour.views.MapView;
import org.mapsforge.android.maps.Projection;
import org.mapsforge.android.maps.overlay.Marker;
import org.mapsforge.core.model.GeoPoint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.widget.Toast;

public class MarkerImplementor implements OnMarkerClickListener {
	private Marker m;

	@Override
	public void onMarkerClick(Marker poi, Context ctx, MapView mv) {
		// TODO Auto-generated method stub
		Projection p = mv.getProjection();
		GeoPoint gp = p.fromPixels(poi.getPixelX(), poi.getPixelY());
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(ctx, gp.latitude + ", " + gp.longitude, duration / 2);
		toast.show();

	}

	public void Draw(Canvas canvas) {

		// Creating paint instance
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// Setting the paint's style
		paint.setStyle(Style.FILL);
		// Setting the width of the stroke
		paint.setStrokeWidth(5);
		// Setting the color of the object to be drawn
		paint.setColor(Color.BLACK);
		// Draw the circle usin the specified paint
		canvas.drawText("text", 100, 100, paint);
	}

}
