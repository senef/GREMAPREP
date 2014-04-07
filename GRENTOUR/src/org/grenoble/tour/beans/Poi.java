/**
 * 
 */
package org.grenoble.tour.beans;

import org.mapsforge.core.model.GeoPoint;

public class Poi {
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
}
