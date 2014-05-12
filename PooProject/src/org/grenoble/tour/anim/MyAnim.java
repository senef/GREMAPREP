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
package org.grenoble.tour.anim;

import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;

public class MyAnim extends ScaleAnimation {

	public MyAnim(float fromX, float toX, float fromY, float toY, float pivotX, float pivotY) {
		super(fromX, toX, fromY, toY, pivotX, pivotY);
		// TODO Auto-generated constructor stub
	}

	private long mElapsedAtPause = 0;
	private boolean mPaused = false;

	@Override
	public boolean getTransformation(long currentTime, Transformation outTransformation) {
		if (mPaused && mElapsedAtPause == 0) {
			mElapsedAtPause = currentTime - getStartTime();
		}
		if (mPaused)
			setStartTime(currentTime - mElapsedAtPause);
		return super.getTransformation(currentTime, outTransformation);
	}

	public void pause() {
		mElapsedAtPause = 0;
		mPaused = true;

		Thread logoTimer = new Thread() {
			public void run() {
				try {
					int logoTimer = 0;
					while (logoTimer < 2000) {
						sleep(100);
						logoTimer = logoTimer + 100;
					};

					mPaused = false;

				}

				catch (InterruptedException e) {
					e.printStackTrace();
				}

			}
		};
		logoTimer.start();
	}

	public void resume() {
		mPaused = false;
	}
}
