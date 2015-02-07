package com.thingsbook.it;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

// camera shit
import android.provider.MediaStore;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

import android.widget.ImageView;
import android.widget.FrameLayout;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Button;

import android.view.View;
import android.view.SurfaceView;
import android.view.ViewGroup;

import com.thingsbook.it.Logger;
import com.thingsbook.it.TingApp;
import com.thingsbook.it.Thing;
import com.thingsbook.it.CreateTingActivity;

public class TakePictureActivity extends Activity {

  static final String EXTRA_IMAGE_PATH = "com.thingsbook.it.EXTRA_IMAGE_PATH";
  static final int REQUEST_IMAGE_CAPTURE = 1;
  static final int THUMBNAIL_SIZE = 612; // Instagram size hehe

	private TingApp myApp;
	private Intent takePictureIntent;
	private File imageFile, tempTingDir;
	private Thing tempTing;
  private Camera mCamera;
  private SurfaceView mPreview;
  private Context thisActivity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.log("oncreate in TingPictureActivity");
		setContentView(R.layout.take_picture);

		myApp = (TingApp) getApplicationContext();

		// Create an instance of Camera
    mCamera = getCameraInstance();
    mCamera.setDisplayOrientation(90);

    // Create our Preview view and set it as the content of our activity.
    mPreview = new CameraPreview(this, mCamera);
    FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
    preview.addView(mPreview);

    thisActivity = this;
    // Add a listener to the Capture button
    Button captureButton = (Button) findViewById(R.id.button_capture);

    captureButton.setOnClickListener( new View.OnClickListener() {
      @Override
      public void onClick(View v) {
      // get an image from the camera
        mCamera.takePicture(null, null, mPicture);
      }
    });
  }

  @Override
  protected void onPause() {
      super.onPause();
      releaseCamera();              // release the camera immediately on pause event
  }

  private PictureCallback mPicture = new PictureCallback() {

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {

      // Claim storage place for Image
      tempTingDir = new File(myApp.getStorageDir(), ".temp");
      tempTingDir.mkdirs();
      File pictureFile = new File(tempTingDir, ".thumbnail.jpg");

      if (pictureFile == null){
        Logger.log("Error creating media file, check storage permissions");
        return;
      }

      
    // Write image data to bitmap and save in storage place
      try {
      // createa matrix for rotating the bitmap
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
      Bitmap imageBitmap = BitmapFactory.decodeByteArray(data, 0, data.length); // from bytes to bitmap
      imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, imageBitmap.getHeight(), imageBitmap.getHeight(), matrix, true); // crop to a square format
      imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_SIZE, THUMBNAIL_SIZE, false); // make smaller
      FileOutputStream fos = new FileOutputStream(pictureFile);
      imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
      fos.close();
    } 
    catch (FileNotFoundException e) {
      Logger.log( "File not found: " + e.getMessage());
    } catch (IOException e) {
      Logger.log("Error accessing file: " + e.getMessage());
    }

      // Forward the image to the create Ting activity 
    Intent intent = new Intent(thisActivity, CreateTingActivity.class);
    // intent.putExtra(EXTRA_IMAGE_PATH, pictureFile.getAbsolutePath())
    startActivity(intent);
    Logger.log("Starting CreateTingActivity");

    }
  };

  private void releaseCamera(){
    if (mCamera != null){
        mCamera.release();        // release the camera for other applications
        mCamera = null;
    }
  }

  /** A safe way to get an instance of the Camera object. */
  public static Camera getCameraInstance(){
    Camera c = null;
    try {
      c = Camera.open(); // attempt to get a Camera instance
    } catch (Exception e){
      Logger.log(e);
    }
    return c; // returns null if camera is unavailable
  }
}