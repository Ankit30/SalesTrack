package com.micromax.salestrack;




import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager.LayoutParams;

public class TabletInfoActivity extends Activity {
	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		//setContentView(R.layout.main);
		// Make us non-modal, so that others can receive touch events.
	    getWindow().setFlags(LayoutParams.FLAG_NOT_TOUCH_MODAL, LayoutParams.FLAG_NOT_TOUCH_MODAL);

	    // ...but notify us that it happened.
	    getWindow().setFlags(LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH);

	    
		createDialog();

	}
	/**----------------------------------------------------------------


	 * DESCRIPTION
	 *    This method popups a alert dialog box on recieving an acknoledgement from server
	 * @return
	 * void
	 *  @author dh.anant

----------------------------------------------------------------*/
	public void createDialog()
	{
		String themessage;
		
		if(Setting.IS_TABLET){
			themessage = "Congratulations on purchasing Micromax Tablet.Press Continue to register your tablet with us for warranty services, updates and more.";
		}else{
			if(Setting.IS_RUSSIA){
				themessage = "Поздравляем с покупкой смартфона Micromax!";
			}else{
				themessage = "Congratulations on purchasing Micromax handset.Press Continue to register your phone with us for warranty services, updates and more.";
			}
			
//			
//			String themessage = "Congratulations on purchasing Micromax handset.Press Continue to register your phone with us for warranty services, updates and more.";
		}
		
		AlertDialog.Builder requestDenied = new AlertDialog.Builder (TabletInfoActivity.this); 

		if(Setting.IS_RUSSIA){
			requestDenied.setTitle("Регистрация в Micromax"); 
			requestDenied.setPositiveButton("Продолжить​", new DialogInterface.OnClickListener()  
			{ 
				@Override
				public void onClick(DialogInterface dialog, int which)  

				{
					finish();
					return;

				}
			});  

		}else{
			requestDenied.setTitle("Micromax Registration");
			requestDenied.setPositiveButton("Continue", new DialogInterface.OnClickListener()  
			{ 
				@Override
				public void onClick(DialogInterface dialog, int which)  

				{
					finish();
					return;

				}
			});  

		}
		
//			
		requestDenied.setMessage(themessage); 

				requestDenied.create();  
		requestDenied.show(); 


	}
	/**----------------------------------------------------------------


	 * DESCRIPTION
	 *    This method gets a context of this class.
	 * @return
	 * void
	 *  @author dh.anant

----------------------------------------------------------------*/
	public Context  getContext(Context context)
	{
		return context=this;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		return false;
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{    
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{     
			moveTaskToBack(true);
			return true;
		} 
		return super.onKeyDown(keyCode, event);  
	}
}