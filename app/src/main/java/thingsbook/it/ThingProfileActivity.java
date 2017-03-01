package com.thingsbook.it;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.net.Uri;
import android.view.View;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.thingsbook.it.Thing;
import com.thingsbook.it.MainActivity;
import com.thingsbook.it.Logger;

public class ThingProfileActivity extends Activity {

	private static final String TAG = "ItApplication";
  private ImageView pictureView;
  private Thing thing;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.thing);

	// Get the message from the intent
		Bundle b = getIntent().getExtras();
		thing = b.getParcelable(MainActivity.EXTRA_THING);

		this.setTitle(thing.getName());

		pictureView = (ImageView) findViewById(R.id.thingimage);
    if (pictureView == null) {
      Logger.log("pview is null!");
    }
    thing.setImageViewImage(pictureView);

		File folder = new File(thing.getFolderPath());
		String filenames[] = folder.list();

    if (filenames != null) {
      Logger.log("lengt of filenames list" + filenames.length);
      ArrayAdapter<String> fileAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, filenames);
      ListView fileListView = (ListView) findViewById(R.id.filelist);
      fileListView.setAdapter(fileAdapter);
      final Context mContext = this;
      final String folderPath = thing.getFolderPath() + "/";

      fileListView.setOnItemClickListener(new OnItemClickListener() {
       public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        File myFile = new File((String) folderPath + parent.getItemAtPosition(position));
        try {
         Log.d(TAG, myFile.getAbsolutePath());
         openFile(mContext, myFile);
       }
       catch(IOException ex) {

       }
     } 
   });
    }


  }

  // Snippet from http://www.androidsnippets.com/open-any-type-of-file-with-default-intent
  private static void openFile(Context context, File url) throws IOException {
    // Create URI
  	File file=url;
  	Uri uri = Uri.fromFile(file);

  	Intent intent = new Intent(Intent.ACTION_VIEW);
      // Check what kind of file you are trying to open, by comparing the url with extensions.
      // When the if condition is matched, plugin sets the correct intent (mime) type, 
      // so Android knew what application to use to open the file
  	if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
          // Word document
  		intent.setDataAndType(uri, "application/msword");
  	} else if(url.toString().contains(".pdf")) {
          // PDF file
  		intent.setDataAndType(uri, "application/pdf");
  	} else if(url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
          // Powerpoint file
  		intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
  	} else if(url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
          // Excel file
  		intent.setDataAndType(uri, "application/vnd.ms-excel");
  	} else if(url.toString().contains(".zip") || url.toString().contains(".rar")) {
          // WAV audio file
  		intent.setDataAndType(uri, "application/x-wav");
  	} else if(url.toString().contains(".rtf")) {
          // RTF file
  		intent.setDataAndType(uri, "application/rtf");
  	} else if(url.toString().contains(".wav") || url.toString().contains(".mp3")) {
          // WAV audio file
  		intent.setDataAndType(uri, "audio/x-wav");
  	} else if(url.toString().contains(".gif")) {
          // GIF file
  		intent.setDataAndType(uri, "image/gif");
  	} else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
          // JPG file
  		intent.setDataAndType(uri, "image/jpeg");
  	} else if(url.toString().contains(".txt")) {
          // Text file
  		intent.setDataAndType(uri, "text/plain");
  	} else if(url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
          // Video files
  		intent.setDataAndType(uri, "video/*");
  	} else {
      //if you want you can also define the intent type for any other file

      //additionally use else clause below, to manage other unknown extensions
      //in this case, Android will show all applications installed on the device
      //so you can choose which application to use
  		intent.setDataAndType(uri, "*/*");
  	}
      
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
    context.startActivity(intent);
  }
}