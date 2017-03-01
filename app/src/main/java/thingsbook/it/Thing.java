package com.thingsbook.it;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.ByteArrayOutputStream;

import android.util.Log;
import android.content.Context;
import android.os.Parcel; 
import android.os.Parcelable; 
import android.util.Log;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.widget.ImageView;

import com.thingsbook.it.Logger;

public class Thing implements Parcelable
{

	private static final String TAG = "ItApplication";
	private static final int THUMBNAIL_SIZE = 612;

	private String path;
	private String name;
	private String thumbnailUrl;

	// A thing object is initiated by providing the directory
	// where its .git directory is located
	public Thing(File d) {

		path = d.getAbsolutePath();
		name = d.getName();

		// For later use: find all images of this Ting
		File imagefiles[] = d.listFiles(new FileFilter() {
			private final String[] okFileExtensions =  new String[] {"jpg", "png", "gif","jpeg"};
			public boolean accept(File file) {
				for (String extension : okFileExtensions) {
					if (file.getName().toLowerCase().endsWith(extension)) {               
						return true;
					}
				}
				return false;
			}
		});

		// Search through existing thumbnail image
		File thumbnailfiles[] = d.listFiles(new FileFilter() {
			private final String[] okFileNames =  new String[] {".thumbnail.jpg", ".thumbnail.png"};
			public boolean accept(File file) {
				for (String name : okFileNames) {
					if (file.getName().toLowerCase().equals(name)) {               
						return true;
					}
				}
				return false;
			}
		});
		// generate thumbnail image if none was found
		if (thumbnailfiles.length == 0) {
			Logger.log("couldn't find ting thumbnail");
			thumbnailUrl = (imagefiles.length != 0) ? makeThumbnail(imagefiles[0].getAbsolutePath()) : null;
		} else {
		thumbnailUrl = thumbnailfiles[0].getAbsolutePath(); // get path of found thumbnail file
		Logger.log("found thumbnailfile: " + thumbnailUrl);
		}
	}

	// constructor for the parcel interface
	public Thing(Parcel in) {
		readFromParcel(in);
	}

	// public get/set methods
	public String getThumbnailUrl() {
		return this.thumbnailUrl;
	}
	public String getName() {
		return this.name;
	}
	public String getFolderPath() {
		return this.path;
	}
	public File getDir() {
		return new File(this.path);
	}


	// METHODS NECCESSARY FOR THE PARCELABLE INTERFACE
	public static final Parcelable.Creator CREATOR =
    new Parcelable.Creator() {
      public Thing createFromParcel(Parcel in) {
        return new Thing(in);
      }

      public Thing[] newArray(int size) {
        return new Thing[size];
      }
    };
  

	public int describeContents() {
		return 0;
	}

	public void writeToParcel(Parcel parcel, int flags) { 
		parcel.writeString(path);
		parcel.writeString(thumbnailUrl);
		parcel.writeString(name);
  } 

  private void readFromParcel(Parcel in) {
		path = in.readString();
		thumbnailUrl = in.readString();
		name = in.readString();
	}

	public void setImageViewImage(ImageView iv) {
		if (thumbnailUrl != null) {
			Bitmap thumbnailBitmap = BitmapFactory.decodeFile(thumbnailUrl);
			if (iv == null) {
				Logger.log("iv is null!");
			}
			iv.setImageBitmap(thumbnailBitmap);
		}
		else {
			iv.setImageResource(R.drawable.placeholder);
		}
		iv.setBackgroundResource(android.R.color.transparent);
		iv.setAdjustViewBounds(true);
		iv.invalidate();
	}

	// Downsample larger image files to thumbnail bitmap and return URL
	public String makeThumbnail(String fileName) {

    try  {
      FileInputStream fis = new FileInputStream(fileName);
      Bitmap imageBitmap = BitmapFactory.decodeStream(fis);

      // transform non-square image to fit within a smaller square image
      final int w = imageBitmap.getWidth();
      final int h = imageBitmap.getHeight();
      double shrinkFactor = (w > h) ? (double) THUMBNAIL_SIZE / w : (double) THUMBNAIL_SIZE / h;
      int newWidth = (w > h) ? THUMBNAIL_SIZE : Math.round((float)shrinkFactor*w);
      int newHeight = (h > w) ? THUMBNAIL_SIZE : Math.round((float)shrinkFactor*h);
      imageBitmap = Bitmap.createScaledBitmap(imageBitmap, newWidth, newHeight, false);
      imageBitmap = Bitmap.createBitmap(imageBitmap, 0, 0, THUMBNAIL_SIZE, THUMBNAIL_SIZE);

      String outputPath = path + "/.thumbnail.png";
      File file = new File(outputPath);
      FileOutputStream fos = new FileOutputStream(file);
      imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

      return outputPath;
    }
    catch(Exception ex) {
    	Logger.log(ex);
    	return null;
    }

	}	


}
