/**
 * 
 */
package com.grenoble.tour.JavaBeans;

import java.io.Serializable;

/**
 * @author falou
 *
 */
public class Poi implements Serializable {
	private String id;
/*	private String ref;
	private double lat;
	private double lon;
	private String audio;
	private String html;*/

	
	
	/**
	 * 
	 */
	public Poi(String id2) {
		//super();
		this.id=id2;
	}


	public Poi() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


}
