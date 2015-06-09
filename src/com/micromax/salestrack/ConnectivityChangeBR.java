package com.micromax.salestrack;
/**
 * Filename:
 * ---------
 * ConnctivityChangeBR.java
 *
 * Project:
 * --------
 * TabletInfo
 *
 * Description:
 * ------------
 * This class starts at the time connectivity changed.
 *
 * Author:
 * -------
         @author dh.anant 
 * -------

 *//*


package com.micromax.salestrac;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityChangeBR extends BroadcastReceiver {

	static Intent registrationIntent=null;
	@Override
	public void onReceive(Context context, Intent intent) {

		System.out.println("Is in Monitor Connection");
		ConnectivityManager cm =
				(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected())
		{

			System.out.println("is connected");
			if(context.getSharedPreferences("locValues", 0).getString("Serverloc","").equals("") && (!context.getSharedPreferences("timertask", 0).getString("startservice", "").equals("timerservicestarted")))
			{
				//System.out.println("Moving to timer task service");
				//System.out.println("ip Has changed");
				registrationIntent = new Intent(context,TimerTaskService.class);
				registrationIntent.setAction("Start");
				context.startService(registrationIntent);

			}
		}

		else
		{
			System.out.println("is not connected");
			context.getSharedPreferences("timertask", 0).edit().clear().putString("startservice","").commit();
			TimerTaskService.actionStop(context);                    

		}



	}


}


*/