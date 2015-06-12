/**
 * Filename:
 * ---------
 * BootCompleteReciever.java
 *
 * Project:
 * --------
 * TabletInfo
 *
 * Description:
 * ------------
 * This class starts upa at the time of boot complete of a device and starts a service.
 *
 * Author:
 * -------
         @author dh.anant 
 * -------

 */



package com.micromax.salestrack;



import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

public class BootCompleteReceiver extends BroadcastReceiver{
	@Override
	public void onReceive(Context context, Intent intent) {
		
		System.out.println("Starting on Boot Complete");
		/*String filepath="/Android/data/com.micromax.Tabletinfo/macadressss.log";
		String exStorageDirectory=Environment.getExternalStorageDirectory().toString();
		File logFile=new File(exStorageDirectory+filepath);
*/
//		if(!logFile.exists())
//		if(!context.getSharedPreferences("locValues", 0).getString("Serverloc", "").equals("message sent")){
			
			if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
			{
				
//				 String model = Build.MODEL;
				 AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				 
				 Intent registrationIntent = new Intent(context,SendService.class);
				 registrationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				 PendingIntent pending = PendingIntent.getService(context, 0, registrationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

				 long delay = 1000*60*10;
				 long totalDelay = SystemClock.elapsedRealtime()+ delay;
				 service.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, totalDelay, pending);	
				 
				 
				 
					
			}
//		}
		
	}

}
