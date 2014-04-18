package org.grenoble.tour.overlays;

import org.grenoble.tour.views.MapView;
import org.mapsforge.android.maps.overlay.Marker;

import android.content.Context;

public interface OnMarkerClickListener {

	public void onMarkerClick(Marker poi, Context ctx, MapView mv);

}
