package org.grenoble.tour.adapter;

import java.io.IOException;
import java.io.InputStream;

import org.grenoble.tour.anim.MyAnim;
import org.mapsforge.applications.android.samples.R;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ItemAdapter extends ArrayAdapter {
	private final Context context;
	private final String[] Ids;
	private final int rowResourceId;

	public ItemAdapter(Context context, int textViewResourceId, String[] objects) {

		super(context, textViewResourceId, objects);
		this.context = context;
		this.Ids = objects;
		this.rowResourceId = textViewResourceId;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View rowView = inflater.inflate(rowResourceId, parent, false);
		ImageView imageView = (ImageView) rowView.findViewById(R.id.imageView);
		TextView name = (TextView) rowView.findViewById(R.id.name);
		TextView desc = (TextView) rowView.findViewById(R.id.description);

		int id = Integer.parseInt(Ids[position]);
		String imageFile = Model.GetbyId(id).IconFile;

		name.setText(Model.GetbyId(id).Name);

		desc.setText(Model.GetbyId(id).Desc);

		// get input stream
		InputStream ims = null;

		try {
			ims = context.getAssets().open(imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// load image as Drawable
		Drawable d = Drawable.createFromStream(ims, null);
		// set image to ImageView
		imageView.setImageDrawable(d);
		imageView.setOnClickListener(new OnClickListener() {
			MyAnim anim = new MyAnim((float) 1, (float) 3.0, (float) 1.0, (float) 3.0, 50, 50);;

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				anim.setDuration(1500);
				anim.setRepeatMode(ScaleAnimation.REVERSE);
				anim.setRepeatCount(1);
				anim.pause();

				// anim.resume();

				v.startAnimation(anim);

			}
		});
		return rowView;

	}

	public void animIMG(ImageView v) {
		Animation animRotate = AnimationUtils.loadAnimation(this.context, R.anim.translate);
		v.startAnimation(animRotate);
	}
}