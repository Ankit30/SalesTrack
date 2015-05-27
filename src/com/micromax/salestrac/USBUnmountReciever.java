package com.micromax.salestrac;

import java.io.File;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.widget.Toast;

public class USBUnmountReciever extends BroadcastReceiver{
@Override
public void onReceive(Context context, Intent intent) {
	// TODO Auto-generated method stub
	
	System.out.println("Starting on UnMounted Complete");

	//Toast.makeText(context, "Trying to mount SD Card", Toast.LENGTH_LONG).show();
	String filepath="/Android/data/com.micromax.Tabletinfo/macadressss.log";
	String exStorageDirectory=Environment.getExternalStorageDirectory().toString();
	File logFile=new File(exStorageDirectory+filepath);

	if(!logFile.exists())
	{

  if(intent.getAction().equals(Intent.ACTION_MEDIA_BAD_REMOVAL)||intent.getAction().equals(Intent.ACTION_MEDIA_EJECT)||intent.getAction().equals(Intent.ACTION_MEDIA_REMOVED)||intent.getAction().equals(Intent.ACTION_MEDIA_UNMOUNTED))
		{
			//Toast.makeText(context, "SDCard Removed", Toast.LENGTH_LONG).show();
			System.out.println("power connected");
			System.out.println("inside UNMountBroadCastReciever");
			Intent registrationIntent = new
					Intent(context,UnMountService.class);
			registrationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			context.startService(registrationIntent);
		}
	}

}
	

}
