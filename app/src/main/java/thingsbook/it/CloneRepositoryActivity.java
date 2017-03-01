package com.thingsbook.it;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import android.widget.ProgressBar;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.content.Intent;
import android.widget.TextView;
import android.view.View;
import android.os.Message;
import android.graphics.drawable.Drawable;

// NFC stuff
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;

import com.thingsbook.it.NativeGit;
import com.thingsbook.it.Logger;
import com.thingsbook.it.TingApp;

public class CloneRepositoryActivity extends Activity implements Runnable
{
  public AtomicBoolean isCloning = new AtomicBoolean(false);

  private String tingPath, storagePath;
  private TextView progressText;
  private ProgressBar progressBar;
  private TingApp myApp;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.clone);
    Logger.log("cloneRepositoryActivity onCreate");
    // Get the message from the intent
    progressText = (TextView) findViewById(R.id.clone_progress_text);
    progressBar = (ProgressBar) findViewById(R.id.clone_progress_bar);
    
    Drawable progressBarStyle = getResources().getDrawable(R.drawable.progressbar);
    progressBar.setProgressDrawable(progressBarStyle);

    myApp = ((TingApp)getApplicationContext());
    storagePath = myApp.getStoragePath();

  }

  @Override 
  protected void onStart() {
    super.onStart();
  
    Intent intent = getIntent();
    
    // if started by detection of a tag we need to read its data
    if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
      readDataFromTag(intent);
      Logger.log("got nfc tag object");
    }

    tingPath = storagePath + "/OD-11";
    
    Thread currentThread = new Thread(this);
    isCloning.set(true);
    currentThread.start();
  
    Logger.log("cloneRepositoryActivity onStart");
  }

  @Override 
  protected void onResume() {
    super.onResume();
    Logger.log("cloneRepositoryActivity onResume");
  }
  @Override
  protected void onStop() {
    super.onStop();
    isCloning.set(false);
    Logger.log("cloneRepositoryActivity onStop");
  }

  @Override
  public void run() {
    deleteDirectory(new File(tingPath));
    NativeGit.cloneWithProgress("https://github.com/biggestT/example-product.git", tingPath, threadHandler);
    finish();
  }

  private Handler threadHandler = new Handler() {
    public void handleMessage(Message msg) {
      progressText.setText(msg.getData().getString("progressText"));
      progressBar.setProgress(msg.getData().getInt("progressPercent"));
    }
  };

  static public boolean deleteDirectory(File path) {
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

  // get remote repository address from detected tag
  private void readDataFromTag(Intent intent) {
    Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
    MifareClassic mfc = MifareClassic.get(tag);
    byte[] data;
    // try {
    //   mfc.connect();

    // }
    
  }
}