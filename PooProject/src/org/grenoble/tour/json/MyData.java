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
package org.grenoble.tour.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.webkit.JavascriptInterface;

public class MyData {

	@JavascriptInterface
	public String getData() throws JSONException {
		JSONObject json = new JSONObject();
		json.put("title", " value");
		json.put("description", " value");
		json.put("image", "file:///android_asset/img/1078.png");
		return (json.toString());

	}

}
