/**
 * Filename:
 * ---------
 * TimerTaskService.java
 *
 * Project:
 * --------
 * TabletInfo
 *
 * Description:
 * ------------
 * ,read the file at location /mnt/sdcard/Android/data/com.micromax.tabletinfo  and send the REG to server.
 *
 * Author:
 * -------
 @author dh.anant 
 * -------

 */

package com.micromax.salestrack;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Telephony.Mms;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

public class TimerTaskService extends Service {
	static Timer timer = null;
	String imei;
	// static int flagWifi = 0;
	String regMessageChecksum;
	String getMacAddress;
	String serialNO;
	String uri;
	String tabletRegs;
	String softwareVersion;
	Context mContext;
	long systemTime = System.currentTimeMillis();
	TabletInfoActivity tabInfo = new TabletInfoActivity();
	public int flagSim = 0;
	public int flagWiFi = 0;
	boolean success;
	boolean bFolderCreatesuccess;
	boolean timerTrue;
	private static String ACTION_START = "START";
	private static String ACTION_STOP = "STOP";
	private boolean sim1Ready;
	private boolean sim2Ready;
	private TelephonyInfo telephonyInfo;
	private static final String SENT = "MMX_SMS_SENT";
	private static final String DELIVERED = "MMX_SMS_DELIVERED";
	private SMSsentListener mSMSSentListener;
	int cid = 0, lac = 0;
	int mcc = 0, mnc = 0;
	int slotId;
	String messageTxt;
	String networkInfo;
	String Imei;

	final static String NETWORK_ERROR = "network not found";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static void actionStop(Context ctx) {

		// timer = new Timer();
		System.out.println("Inside actionStop of timer task ");
		// ctx.getSharedPreferences("timertask",0).edit().clear().putString("startservice","timerservicestarted").commit();
		Intent stopTimer = new Intent(ctx, TimerTaskService.class);
		stopTimer.setAction(ACTION_STOP);
		ctx.startService(stopTimer);
	}

	public int onStartCommand(final Intent intent, int flags, int startId) {
		try {

			mContext = this;
			telephonyInfo = TelephonyInfo.getInstance(TimerTaskService.this, 0);
			// android.os.SystemClock.sleep(5000*1);
			sim1Ready = telephonyInfo.isSIM1Ready();
			sim2Ready = telephonyInfo.isSIM2Ready();

			Imei = tabletImeiString();
			networkInfo = getNetworkInfo();

			System.out.println("In on Start commeand of service" + intent);
			if ((intent.getAction().equals(ACTION_STOP)) == true) {
				System.out.println("Inside acion stop flag");
				if (timer != null) {
					timer.cancel();
					timer = null;
				}

				stopSelf();
			}

			else {
				timer = new Timer();
				System.out.println("Inside onstart of timer task ");
				if (Setting.IS_TABLET) {
					if ((sim1Ready || sim2Ready)&& networkInfo != NETWORK_ERROR) {
						if (flagSim == 0) {
							tabletRegs = tabletReg(Imei, networkInfo);
							timerTask(tabletRegs);
						}

					} else {
						getSharedPreferences("timertask", 0).edit().putString("startservice","timerservicestarted").commit();
						timerTask();
					}
				} else {

					if (Setting.IS_RUSSIA) {// For Russia phone
						getSharedPreferences("timertask", 0).edit().putString("startservice","timerservicestarted").commit();
						timerTask();
					} else {// Other country phones
						if ((sim1Ready || sim2Ready)) {
							if (networkInfo == NETWORK_ERROR) {
								networkInfo = mcc + "" + mnc + ":"+ "0000:0000";
							}
							if (flagSim == 0) {
								tabletRegs = tabletReg(Imei, networkInfo);
								timerTask(tabletRegs);
							}
						} else {
							/*
							 * getSharedPreferences("timertask",0).edit().putString("startservice","timerservicestarted").commit();
							 * timerTask();
							 */
						}
					}
				}
			}

		} catch (Exception e) {
			System.out.println("There is an exception in Timer task service"+ e.toString());
		}
		return START_FLAG_REDELIVERY;
	}

	/**
	 * ----------------------------------------------------------------
	 * 
	 * 
	 * DESCRIPTION This method runs a timer task for 10 minutes before sending a
	 * message to the server
	 * 
	 * @return void
	 * @author dh.anant
	 * 
	 *         ----------------------------------------------------------------
	 */

	public void timerTask(final String message) {

		int delay = 1000 * 60 * 0;
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (flagSim == 0) {
					textMessageSend(message, slotId);
				}

			}
		}, delay);

	}

	public void timerTask() {
		System.out.println("flag:=" + flagSim);
		int delay = 1000 * 60 * 0;
		timer.schedule(new TimerTask() {
			public void run() {
				System.out.println("The value of shared preference is "+ getSharedPreferences("timertask", 0).getString("startservice", "").equals("timerservicestarted"));

				if (!getSharedPreferences("locValues", 0).getString("Serverloc", "").equals("message sent wifi")) {

					while (flagSim == 0 && flagWiFi == 0)
						try {

							{
								System.out.println("inside while loop of timer task");
								ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
								NetworkInfo mobileNwInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
								NetworkInfo wifiNwInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

								if ((wifiNwInfo.isAvailable() || mobileNwInfo.isAvailable())
										&& (mobileNwInfo.isConnected() || wifiNwInfo.isConnected())) {
									if (Setting.IS_TABLET) {

										java.util.Date systemDates = Calendar.getInstance().getTime();
										SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
										String currentTime = simpleDateFormat.format(systemDates);

										String Imei = tabletImeiString();
										if ((sim1Ready || sim2Ready) && !getNetworkInfo().equals(NETWORK_ERROR)) {
											networkInfo = getNetworkInfo();
										} else {
											networkInfo = "000000:0000:0000";
										}

										tabletRegs = tabletReg(Imei,networkInfo);
										// tabletRegs = tabletReg(Imei);
										uri = "http://sts.micromaxinfo.com/configureSms/msg.aspx?tim="+ currentTime+ "&Msg="+ tabletRegs;
										httpMessageSend(uri.toString().replace(" ", ""));

										System.out.println("inside Send timer task");

									} else {
										// Russia phone
										if (getNetworkInfo() != NETWORK_ERROR) {
											java.util.Date systemDates = Calendar.getInstance().getTime();
											SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
											String currentTime = simpleDateFormat.format(systemDates);

											String Imei = tabletImeiString();
											String networkInfo = getNetworkInfo();

											tabletRegs = tabletReg(Imei,networkInfo);
											// tabletRegs = tabletReg(Imei);
											uri = "http://sts.micromaxinfo.com/configureSms/msg.aspx?tim="+ currentTime+ "&Msg="+ tabletRegs;
											httpMessageSend(uri.toString().replace(" ", ""));
										} else {
											getSharedPreferences("locValues", 0).edit().putString("Server_error","elsecase").commit();
											SystemClock.sleep(1000 * 60 * 1);
											telephonyInfo = TelephonyInfo.getInstance(TimerTaskService.this,0);
											sim1Ready = telephonyInfo.isSIM1Ready();
											sim2Ready = telephonyInfo.isSIM2Ready();
										}
									}
								} else {
									getSharedPreferences("locValues", 0).edit().putString("Server_error","elsecase").commit();
									SystemClock.sleep(1000 * 60 * 1);
									telephonyInfo = TelephonyInfo.getInstance(TimerTaskService.this, 0);
									sim1Ready = telephonyInfo.isSIM1Ready();
									sim2Ready = telephonyInfo.isSIM2Ready();

								}
							}

						} catch (Exception e) {
							getSharedPreferences("timertask", 0).edit().clear().putString("startservice", "").commit();
						}

				} else {
					stopSelf();
				}
			}
		}, delay);
	}

	/**
	 * ----------------------------------------------------------------
	 * 
	 * 
	 * DESCRIPTION This method sends http message to server
	 * 
	 * @param final String
	 * @return void
	 * @author dh.anant
	 * 
	 *         ----------------------------------------------------------------
	 */

	public void httpMessageSend(final String gtMessage) {
		try {
			URL url = new URL(gtMessage);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

			InputStream in = new BufferedInputStream(urlConnection.getInputStream());

			String Ack = readStream(in);
			System.out.println("Inside sending message " + Ack);
			if (Ack.contains("OK")) {
				System.out.println("Message sent to server");
				flagWiFi = 1;
				getSharedPreferences("locValues", 0).edit().putString("Serverloc", "message sent wifi").commit();
				// Intent i = new Intent("com.package.MY_DIALOG");
				Intent i = new Intent(this, TabletInfoActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(i);
				stopSelf();
			} else {
				System.out.println("Ack not received");
			}
			urlConnection.disconnect();

		} catch (Exception e) {
			System.out.println("There is no network connection");
		}

	}

	private void registerSMSListener() {
		mSMSSentListener = new SMSsentListener();
		getApplicationContext().registerReceiver(mSMSSentListener,new IntentFilter(SENT));
	}

	private void sendSMS(String messageText, int slotId) {

		PendingIntent sentPI = PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0);
		PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,new Intent(DELIVERED), 0);

		IntentFilter sendReceiveFilter = new IntentFilter();
		sendReceiveFilter.addAction(SENT);

		SmsManager sms = SmsManager.getDefault();
		switch (mcc) {
		case 413:
			sendTxtMessage(mContext, slotId, new Intent(SENT), "+94114339003",messageText);
			// sms.sendTextMessage("+94114339003", null, messageText,sentPI,deliveredPI);
			break;
		case 470:
			sendTxtMessage(mContext, slotId, new Intent(SENT), "7464",messageText);
			// sms.sendTextMessage("7464", null, messageText,sentPI,deliveredPI);
			break;
		case 404:
			sendTxtMessage(mContext, slotId, new Intent(SENT), "+919773270001",messageText);
			// sms.sendTextMessage("+919212230707", null, messageText,sentPI,deliveredPI);
			break;
		case 405:
			sendTxtMessage(mContext, slotId, new Intent(SENT), "+919773270001",messageText);
			// sms.sendTextMessage("+919212230707", null, messageText,sentPI,deliveredPI);
			break;
		default:
			sendTxtMessage(mContext, slotId, new Intent(SENT), "+919773270001",messageText);
			// sms.sendTextMessage("+919212230707", null, messageText,sentPI,deliveredPI);

		}
	}

	/**
	 * ----------------------------------------------------------------
	 * 
	 * 
	 * DESCRIPTION This method sends text message to number
	 * 
	 * @param final String
	 * @return void
	 * @author dh.anant
	 * 
	 *         ----------------------------------------------------------------
	 */
	public void textMessageSend(final String gtMessage, int slotId) {
		try {
			messageTxt = gtMessage;
			registerSMSListener();
			sendSMS(gtMessage, slotId);

			System.out.println("Message sent to server");

		} catch (Exception e) {
			System.out.println("There is no balance");
		}

	}

	/**
	 * ----------------------------------------------------------------
	 * 
	 * 
	 * DESCRIPTION This method reads the response coming from the server
	 * 
	 * @param InputStream
	 * @return String
	 * @author dh.anant
	 * 
	 *         ----------------------------------------------------------------
	 */
	private String readStream(InputStream in) {
		String total = "";
		try {
			// TODO Auto-generated method stub
			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			String x = "";
			x = r.readLine();
			while (x != null) {
				total += x;
				x = r.readLine();
			}
		} catch (Exception ex) {

		}
		return total;
	}

	/**
	 * ----------------------------------------------------------------
	 * 
	 * 
	 * DESCRIPTION This method generates the REG String that is sent to the
	 * server .
	 * 
	 * @param String
	 * @return String
	 * @author dh.anant
	 * 
	 *         ----------------------------------------------------------------
	 */

	public String tabletReg(String serial_no, String netinfo) {
		try {

			if (Setting.IS_LOLLIPOP) {

				softwareVersion = telephonyInfo.getSotwareVersion();
				// TelephonyInfo.getBuild(TimerTaskService.this,"android.os.SystemProperties","ro.custom.build.version");softwareVersion = softwareVersion.replace(" ", "");
			} else {
				softwareVersion = Build.DISPLAY;
			}
			System.out.println("the Build NUmber is " + softwareVersion);
		} catch (Exception e) {
			System.out.println("Build Number Does not exist" + e.toString());
		}

		String netarray[] = netinfo.split(":");

		String regMessage = "REG:02:01" + netarray[0] + ":02" + netarray[1] + ":03" + netarray[2] + ":04" + "MOBHIG0333" + ":05"
				+ serial_no + ":06" + "V1.0" + ":07" + softwareVersion + ":";

		String checkSum = checkSumGeneratorNew(regMessage);
		regMessageChecksum = "REG:02:01" + netarray[0] + ":02" + netarray[1] + ":03" + netarray[2] + ":04" + "MOBHIG0333" + ":05"
				+ serial_no + ":06" + "V1.0" + ":07" + softwareVersion + ":" + checkSum;
		System.out.println("regMessageChecksum: " + softwareVersion + " $$"+ regMessageChecksum);
		return regMessageChecksum;
	}

	public String getNetworkInfo() {
		String signalInfo = null;

		if (sim1Ready) {
			slotId = 0;
			signalInfo = getSignalInfo(0);

		} else if (sim2Ready) {
			slotId = 1;
			signalInfo = getSignalInfo(1);
			// signalInfo = "";

		} else {
			signalInfo = NETWORK_ERROR;
		}

		return signalInfo;
	}

	public String getSignalInfo(int simId) {
		String netInfo = null;
		int cellPadding;
		String lacId_hex = null;
		String cellId_hex = null;

		telephonyInfo = TelephonyInfo.getInstance(TimerTaskService.this, simId);

		// getting Network mcc-mnc
		String networkOp = telephonyInfo.getNetworkOperator();

		if (networkOp.length() != 0) {
			mcc = Integer.parseInt(networkOp.substring(0, 3));
			mnc = Integer.parseInt(networkOp.substring(3));
		}

		int networkType1 = Integer.parseInt(telephonyInfo.getNetworkType());

		if (networkType1 == TelephonyManager.NETWORK_TYPE_UMTS) {
			cellPadding = 8;
		} else {
			cellPadding = 4;
		}

		// For A99
		String model = Build.MODEL;
		if (simId == 1 && model.contains("A99")) {
			cid = telephonyInfo.getcId();
			lac = telephonyInfo.getLacId();

			lacId_hex = getPaddedHex(lac, 4);
			cellId_hex = getPaddedHex(cid, cellPadding);

		} else {

			GsmCellLocation loc = (GsmCellLocation) telephonyInfo
					.getCellLocation();
			if (loc != null) {
				cid = loc.getCid();
				lac = loc.getLac();

				lacId_hex = getPaddedHex(lac, 4);
				cellId_hex = getPaddedHex(cid, cellPadding);
			} else {

				cid = 0;
				lac = 0;

				lacId_hex = getPaddedHex(lac, 4);
				cellId_hex = getPaddedHex(cid, cellPadding);

			}
		}

		if (mcc != 0 && mnc != 0 && cid != 0 & lac != 0)
		// if(mcc!=0 && mnc!=0)
		{
			netInfo = mcc + "" + mnc + ":" + cellId_hex + ":" + lacId_hex;

		} else {
			netInfo = NETWORK_ERROR;
		}

		Log.i("SALETRACK", "network signal data" + netInfo);
		System.out.println("signael" + netInfo);

		return netInfo;

	}

	private static void sendTxtMessage(Context context, int slotID,
			Intent intent, String number, String text) {

		try {

			Class<?> telephony = Class.forName("com.mediatek.telephony.SmsManagerEx");

			ArrayList<PendingIntent> sendIntent = new ArrayList<PendingIntent>();
			sendIntent.add(PendingIntent.getBroadcast(context, 0, intent, 0));

			Constructor<?> constructor = telephony.getDeclaredConstructor();
			constructor.setAccessible(true);
			Object obj = constructor.newInstance();

			Class<?>[] parameter1 = new Class[1];
			parameter1[0] = String.class;
			Method divMsg = telephony.getMethod("divideMessage", parameter1);

			Object[] obParameter1 = new Object[1];
			obParameter1[0] = text;

			Object divArr = divMsg.invoke(obj, obParameter1);

			Class<?>[] parameter = new Class[6];
			parameter[0] = String.class;
			parameter[1] = String.class;
			parameter[2] = ArrayList.class;
			parameter[3] = ArrayList.class;
			parameter[4] = ArrayList.class;
			parameter[5] = int.class;
			Method getSimID = telephony.getMethod("sendMultipartTextMessage",parameter);

			Object[] obParameter = new Object[6];
			obParameter[0] = number;
			obParameter[1] = null;
			obParameter[2] = divArr;
			obParameter[3] = sendIntent;
			obParameter[4] = null;
			obParameter[5] = slotID;

			getSimID.invoke(obj, obParameter);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	String getPaddedHex(int nr, int minLen) {
		String str = Integer.toHexString(nr).toUpperCase();
		if (str != null) {
			while (str.length() < minLen) {
				str = "0" + str;
			}
		}
		return str;
	}

	/*********************************************************************************************************
	 * FUNCTION : getPaddedHex PARMETER : integer , integer DESCRIPTION :
	 * Convert an integer to String and pad with 0's up to minLen.
	 *********************************************************************************************************/
	String getPaddedInt(int nr, int minLen) {
		String str = Integer.toString(nr);
		if (str != null) {
			while (str.length() < minLen) {
				str = "0" + str;
			}
		}
		return str;
	}

	public String checkSumGenerator(String s) {
		int sum = 0;
		for (int i = 0; i < s.length(); i++) {

			System.out.print((int) s.charAt(i) + "+");
			sum = sum + (int) s.charAt(i);

		}
		int mod;
		mod = sum % 255;

		String hex = Integer.toHexString(mod).toUpperCase();

		return hex;
	}

	public String checkSumGeneratorNew(String s) {
		int sum = 0;
		for (int i = 0; i < s.length(); i++) {

			System.out.print((int) s.charAt(i) + "+");
			sum = sum + reverse((int) s.charAt(i));

		}
		int multNum;
		multNum = sum * 52;

		int sumDigits = sumofDigits(multNum);

		String hex = Integer.toHexString(sumDigits).toUpperCase();

		return hex;
	}

	private int sumofDigits(int number) {

		int sum = 0;

		while (number != 0) {

			sum += number % 10;
			number /= 10;

		}
		return sum;
	}

	private int reverse(int number) {
		int reverse = 0;
		int remainder = 0;
		do {
			remainder = number % 10;
			reverse = reverse * 10 + remainder;
			number = number / 10;

		} while (number > 0);

		return reverse;
	}

	public String tabletImeiString() {
		System.out.println("Inside tabletRegString");

		String deviceIdImei = null;

		String imeiSIM1 = telephonyInfo.getImeiSIM1();
		String imeiSIM2 = telephonyInfo.getImeiSIM2();

		if (imeiSIM1 != null && imeiSIM2 != null) {
			if (imeiSIM1.length() != 0 && imeiSIM2.length() != 0) {
				deviceIdImei = imeiSIM1 + "," + imeiSIM2;
			} else {
				TelephonyManager tMgr = (TelephonyManager) getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
				if (tMgr != null) {
					deviceIdImei = tMgr.getDeviceId();
					System.out.println("the value of device imei is "+ deviceIdImei);
				}
			}
		} else {
			deviceIdImei = "";
		}

		return deviceIdImei;

	}

	private class SMSsentListener extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			switch (getResultCode()) {
			case Activity.RESULT_OK:
				try {
					flagSim = 1;
					getSharedPreferences("locValues", 0).edit().putString("Serverloc", "message sent sim").commit();
					// getContentResolver().delete(Uri.parse("content://sms/sent"),"body = ?",
					// new String[] { tabletRegs });
					if (!getSharedPreferences("locValues", 0).getString("Serverloc", "").equals("message sent wifi")) {
						Intent i = new Intent("com.package.MY_DIALOG");
						i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(i);
					}

					stopSelf();
				} catch (Exception e) {
					System.out.println(e);
				}

				break;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
			case SmsManager.RESULT_ERROR_NO_SERVICE:
			case SmsManager.RESULT_ERROR_NULL_PDU:
			case SmsManager.RESULT_ERROR_RADIO_OFF:

				if (slotId == 0 && sim2Ready) {
					// if sim1 is present and has no balance ..send from sim2
					slotId = 1;
					getApplicationContext().unregisterReceiver(mSMSSentListener);
					networkInfo = getSignalInfo(1);
					tabletRegs = tabletReg(Imei, networkInfo);
					timerTask(tabletRegs);
				} else {
					// if no balance in sim1 & sim2 not preset..send via url
					getSharedPreferences("timertask", 0).edit().putString("startservice", "timerservicestarted").commit();
					timerTask();
				}
				break;

			}
		}
	}
}
