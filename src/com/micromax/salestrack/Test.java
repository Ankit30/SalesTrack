package com.micromax.salestrack;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Test extends Activity{

	Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.main);
		Button action =(Button)findViewById(R.id.button);

		action.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.out.println("the value of shared preference is "+getSharedPreferences("locValues", 0).getString("Serverloc",""));
				if( !getSharedPreferences("locValues", 0).getString("Serverloc","").equals("message sent sim"))
				{
					System.out.println("Starting sendService from Broadcast reciever");

					System.out.println("Send Service Started"+getSharedPreferences("locValues", 0).getString("Serverloc",""));
					
					AlarmManager service = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
					 
					 Intent registrationIntent = new Intent(context,SendService.class);
					 registrationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					 PendingIntent pending = PendingIntent.getService(context, 0, registrationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

					 long delay = 0;
					 long totalDelay = SystemClock.elapsedRealtime()+ delay;
					 service.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, totalDelay, pending);	
						
					 
				}


			}
		});

	}

}
