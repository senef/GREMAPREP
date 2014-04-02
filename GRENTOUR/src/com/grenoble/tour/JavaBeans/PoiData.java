/**
 * 
 */
package com.grenoble.tour.JavaBeans;

import java.io.Serializable;

import android.location.Location;

/**
 * @author falou
 *
 */
public class PoiData implements Serializable {
	
	private int id;
	private String name;
	private String picture;
	private Location location;
	/**
	 * @param id
	 * @param name
	 * @param picture
	 * @param location
	 * @param description
	 * @param trigger
	 * @param wiki
	 */
	public PoiData(int id, String name, String picture, Location location,
			String description, int trigger, String wiki) {
		super();
		this.id = id;
		this.name = name;
		this.picture = picture;
		this.location = location;
		this.description = description;
		this.trigger = trigger;
		this.wiki = wiki;
	}

	private String description;
	private int trigger;
	private String wiki;

	/**
	 * 
	 */
	public PoiData() {
		super();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getTrigger() {
		return trigger;
	}

	public void setTrigger(int trigger) {
		this.trigger = trigger;
	}

	public String getWiki() {
		return wiki;
	}

	public void setWiki(String wiki) {
		this.wiki = wiki;
	}

	

}
