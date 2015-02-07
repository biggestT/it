package com.thingsbook.it;

import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.RelativeLayout;
import android.util.AttributeSet;
import android.content.Context;

import com.thingsbook.it.Logger;

public class CameraButtonsGroup extends RelativeLayout {

	public CameraButtonsGroup(Context context) {
		super(context);
	}

	public CameraButtonsGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CameraButtonsGroup(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override 
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (w != 0 && h != 0) {
			// only show a square camera preview window to the user
			ViewGroup.LayoutParams params = getLayoutParams();
			params.height = h - w; // makes the buttonscontainer hide the rest of the previewview
			setLayoutParams(params); 
		}
	}
}