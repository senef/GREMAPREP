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

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class AssetsLoader {
	private Context ctx;

	public AssetsLoader(Context context) {
		this.ctx = context;
	}

	public Drawable loadDrawable(String path) throws IOException {

		InputStream ims = ctx.getAssets().open(path);
		// return an image as Drawable
		return Drawable.createFromStream(ims, null);
	}

	public String loadText(String path) throws IOException {

		// get input stream for text
		InputStream is = ctx.getAssets().open(path);
		// check size
		int size = is.available();
		// create buffer for IO
		byte[] buffer = new byte[size];
		// get data to buffer
		is.read(buffer);
		// close stream
		is.close();

		return new String(buffer);

	}

}
