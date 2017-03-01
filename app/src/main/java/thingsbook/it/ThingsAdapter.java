package com.thingsbook.it;

import java.util.ArrayList;

import android.widget.ImageView;
import android.widget.BaseAdapter;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.content.Context;
import android.view.View;
import android.widget.GridView;
import android.net.Uri;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;


public class ThingsAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Thing> things;

	public ThingsAdapter(Context c, ArrayList<Thing> t) {
		mContext = c;
		things = t;
	}

	public ThingsAdapter(Context c) {
		this(c, new ArrayList<Thing>());
	}

	public int getCount() {
		return things.size();
	}

	public Object getItem(int position) {
		return things.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;
		if (convertView == null) {  // if it's not recycled, initialize some attributes
			imageView = new ImageView(mContext);
			imageView.setAdjustViewBounds(true);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		} else {
			imageView = (ImageView) convertView;
		}
		things.get(position).setImageViewImage(imageView);
		return imageView;
	}
}