package com.thingsbook.it;

import java.io.File;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.lang.CharSequence;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;

import com.thingsbook.it.TingApp;
import com.thingsbook.it.Logger;
import com.thingsbook.it.MainActivity;

public class CreateTingActivity extends Activity {

	private ImageView tingPreview;
	private EditText tingTagsView;
	private CreateTingActivity thisActivity;
	private String previewUrl;
	private TingApp myApp;

	@Override 
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_ting);

		myApp = (TingApp) getApplicationContext();

		thisActivity = this;
		// set up tag input box
		tingTagsView= (EditText) findViewById(R.id.ting_description);
		tingTagsView.setSelected(true);
		OnEditorActionListener mEditorActionListener = new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				Logger.log("onEditorAction called with actionid:" + actionId + "looking for: " + EditorInfo.IME_ACTION_DONE);
				boolean handled = false;
				if (event != null) { // because actionId doesnt work
					thisActivity.saveTing(v.getText().toString());

				}
				return handled;
			}
		};
		tingTagsView.setOnEditorActionListener(mEditorActionListener);

		// set preview image
		tingPreview = (ImageView) findViewById(R.id.ting_preview);
		previewUrl = myApp.getStoragePath() + "/.temp/.thumbnail.jpg";
		Bitmap thumbnailBitmap = BitmapFactory.decodeFile(previewUrl);
		if (thumbnailBitmap == null) 
			Logger.log("unable to find and decode temporary ting image");
		tingPreview.setImageBitmap(thumbnailBitmap);
	}

	private void saveTing(String desc) {
		Logger.log("got description: " + desc);
		String tingName = desc.split("\\s")[0]; // get first word for name
		try {
			FileOutputStream fos = new FileOutputStream(tingName + "_info.txt");
			if (fos != null) {
				PrintStream out = new PrintStream(fos);
				out.print(desc);
				if (out.checkError()) {
					Logger.log("could not write to file");
				}
			}
		} catch (FileNotFoundException ex) {
			Logger.log("file not found");
		}
		File newTingDir = new File(myApp.getStoragePath(), tingName);
		newTingDir.mkdirs();
		File previewImageFile = new File(previewUrl);
		previewImageFile.renameTo(new File(newTingDir, previewImageFile.getName()));
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
	}
}