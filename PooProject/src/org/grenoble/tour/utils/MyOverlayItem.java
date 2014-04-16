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
package org.grenoble.tour.utils;

import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.GeoPoint;
import org.mapsforge.core.model.Point;

import android.graphics.Canvas;

public class MyOverlayItem implements OverlayItem {
	protected GeoPoint point;
	protected String snippet;
	protected String title;

	// protected Drawable marker;

	/**
	 * @param point
	 * @param snippet
	 * @param title
	 * @param marker
	 */
	public MyOverlayItem(GeoPoint point, String snippet, String title) {
		super();
		this.point = point;
		this.snippet = snippet;
		this.title = title;
		// this.marker = marker;
	}

	@Override
	public boolean draw(BoundingBox arg0, byte arg1, Canvas arg2, Point arg3) {
		// TODO Auto-generated method stub
		return false;
	}

}
