/**
 * 
 */
package org.grenoble.tour.beans;

import org.mapsforge.core.model.GeoPoint;

import android.os.Parcel;
import android.os.Parcelable;

public class Poi implements Parcelable {
	private String id;
	private double lat;
	private double lon;

	public Poi() {

	}

	public Poi(String id, double lat, double lon) {
		super();
		this.id = id;
		this.lat = lat;
		this.lon = lon;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLon() {
		return lon;
	}

	public void setLon(double lon) {
		this.lon = lon;
	}

	public GeoPoint getNewPoint() {
		return new GeoPoint(lat, lon);
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub

	}
}
