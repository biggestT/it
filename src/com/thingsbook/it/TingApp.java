package com.thingsbook.it;


import java.io.File;
import android.os.Environment;
import android.app.Application;

public class TingApp extends Application {

	// state variables of interest to multiple different activities
  private File storageDir;
  private boolean isExternalStorageWritable;

  @Override 
  public void onCreate() {
  	storageDir = findStorageDir();
  	isExternalStorageWritable = isExternalStorageWritable();
  }

  // Every time some configuration changes we need to check if external storage is still writeable
  // @Override
  // public void onConfigurationChanged(Configuration newConfig) {
  // 	isExternalStorageWritable = isExternalStorageWritable();
  // }

  private File findStorageDir() {
    // Get the directory for the user's public pictures directory.
    File itstorage = new File(Environment.getExternalStorageDirectory(), "it");
    if (!itstorage.isDirectory()){
      itstorage.mkdirs();
    }
    return itstorage;
  }

  private boolean isExternalStorageWritable() {
    String state = Environment.getExternalStorageState();
    if (Environment.MEDIA_MOUNTED.equals(state)) {
      return true;
    }
    return false;
  }


  public File getStorageDir() {
  	return storageDir;
  }
  public String getStoragePath() {
  	return storageDir.getAbsolutePath();
  }

  public boolean checkIfWritable() {
  	return isExternalStorageWritable;
  }

  // utilityfunction to delete directory
  public boolean deleteDirectory(File path) {
    if( path.exists() ) {
      File[] files = path.listFiles();
      for(int i=0; i<files.length; i++) {
         if(files[i].isDirectory()) {
           deleteDirectory(files[i]);
         }
         else {
           files[i].delete();
         }
      }
    }
    return( path.delete() );
  }
}