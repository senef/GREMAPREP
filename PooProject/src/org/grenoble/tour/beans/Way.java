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
/**
 * 
 */
package org.grenoble.tour.beans;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.grenoble.tour.provider.POIRetriever;
import org.mapsforge.core.model.GeoPoint;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * @author falou
 */
public class Way implements Parcelable {
	// private String id;
	private static final String TAG = "way";
	private int routeID;
	private List<GeoPoint> noeuds;
	private List<String> references;

	// private String name;
	// private double timeStamp;
	/**
	 * @param references
	 */
	public Way(List<String> references) {
		super();
		this.noeuds = new ArrayList<GeoPoint>();

	}

	public Way(int ID) {
		super();
		this.routeID = ID;
		this.noeuds = new ArrayList<GeoPoint>();
		this.references = new ArrayList<String>();
	}

	/**
	 * @return the routeID
	 */
	public int getRouteID() {
		return routeID;
	}

	/**
	 * @param routeID
	 *            the routeID to set
	 */
	public void setRouteID(int routeID) {
		this.routeID = routeID;
	}

	/**
	 * @return the references
	 */
	public List<String> getReferences() {
		return references;
	}

	/**
	 * @param references
	 *            the references to set
	 */
	public void setReferences(List<String> references) {
		this.references = references;
	}

	/**
	 * @return the noeuds
	 */
	public List<GeoPoint> getNoeuds() {
		return noeuds;
	}

	/**
	 * @param noeuds
	 *            the noeuds to set
	 */
	public void setNoeuds(List<GeoPoint> noeuds) {
		this.noeuds = noeuds;
	}

	public void setNodes(InputStream in) {
		List<Poi> listPoi = new ArrayList<Poi>();
		Log.i(TAG, "avant le for");
		listPoi.addAll(POIRetriever.parsebis(in));
		for (int i = 0; i < listPoi.size(); i++) {
			Log.i(TAG, "dans le for");
			if (this.references.contains(listPoi.get(i).getId())) {
				Log.i(TAG, "lat=" + listPoi.get(i).getLat());
				this.noeuds.add(listPoi.get(i).getNewPoint());
			}

		}

	}

	public void addRef(String s) {
		// Log.i(TAG, s);
		this.references.add(s);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

}
