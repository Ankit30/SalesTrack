package com.micromax.salestrack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.CellLocation;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;

public final class TelephonyInfo {

	private static TelephonyInfo telephonyInfo;
	private String imeiSIM1;
	private String imeiSIM2;
	private boolean isSIM1Ready;
	private boolean isSIM2Ready;
	private String networkOperator;
	private String networkType;
	private GsmCellLocation cellLocation;
	private int cId;
	private int lacId;
	private String sotwareVersion;
	private static long subscriptionId;
	private static final Class<?> mTelephonyManagerClass = TelephonyManager.class;
	private static TelephonyManager mTelephonyManager = null,tmSim1, tmSim2, tm;

	public long getSubscriptionId() {
		return subscriptionId;
	}

	public String getSotwareVersion() {
		return sotwareVersion;
	}
	
	public int getLacId() {
		return lacId;
	}
	
	public int getcId() {
		return cId;
	}
	
	public String getNetworkOperator() {
		return networkOperator;
	}
	
	public String getNetworkType() {
		return networkType;
	}

	public GsmCellLocation getCellLocation() {
		return cellLocation;
	}

	public String getImeiSIM1() {
		return imeiSIM1;
	}

	public String getImeiSIM2() {
		return imeiSIM2;
	}

	public boolean isSIM1Ready() {
		return isSIM1Ready;
	}

	public boolean isSIM2Ready() {
		return isSIM2Ready;
	}

	public boolean isDualSIM() {
		return imeiSIM2 != null;
	}

	private TelephonyInfo() {
	}

	public static TelephonyInfo getInstance(Context context, int simId) {

		telephonyInfo = new TelephonyInfo();

		mTelephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));

		try {

			/*
			 * telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context,"getDeviceIdGemini", 0); 
			 * telephonyInfo.imeiSIM1 =getDeviceIdBySlot(context, "getDeviceIdGemini", 1);
			 * 
			 * telephonyInfo.networkOperator = getDeviceIdBySlot(context,"getNetworkOperatorGemini", simId);
			 * 
			 * telephonyInfo.cellLocation =(GsmCellLocation)getCellLocation(context,"getCellLocationGemini", simId);
			 * 
			 * telephonyInfo.networkType = getDeviceIdBySlot(context,"getNetworkTypeGemini", simId);
			 */
			/*
			 * telephonyInfo.imeiSIM1 = getDeviceIdBySlot(context,"getDeviceId", 0); 
			 * telephonyInfo.imeiSIM2 = getDeviceIdBySlot(context, "getDeviceId", 1);
			 */

			if(Setting.IS_WINGTECH){
				 telephonyInfo.imeiSIM1 = getTMinstancebysimId(0).getDeviceId();
				 telephonyInfo.imeiSIM2 = getTMinstancebysimId(1).getDeviceId();
				 telephonyInfo.networkOperator = getTMinstancebysimId(simId).getNetworkOperator();
				 telephonyInfo.cellLocation = (GsmCellLocation) getTMinstancebysimId(simId).getCellLocation();
				 telephonyInfo.networkType = String.valueOf(getTMinstancebysimId(simId).getNetworkType());
				 telephonyInfo.isSIM1Ready = getTMinstancebysimId(0).getSimState()== TelephonyManager.SIM_STATE_READY; 
				 telephonyInfo.isSIM2Ready = getTMinstancebysimId(1).getSimState()== TelephonyManager.SIM_STATE_READY;;
			 }else if(Setting.IS_TOPWEIZ){
				 tmSim1 = (TelephonyManager)context.getSystemService(getTelephonyService(0));
				 tmSim2 = (TelephonyManager)context.getSystemService(getTelephonyService(1));
				 tm = (TelephonyManager)context.getSystemService(getTelephonyService(simId));
				 telephonyInfo.imeiSIM1 = tmSim1.getDeviceId();
				 telephonyInfo.imeiSIM2 = tmSim2.getDeviceId();

				 telephonyInfo.networkOperator = tm.getNetworkOperator();

				 telephonyInfo.cellLocation = (GsmCellLocation) tm.getCellLocation();

				 telephonyInfo.networkType = String.valueOf(tm.getNetworkType());
					
				 telephonyInfo.isSIM1Ready = tmSim1.getSimState() == TelephonyManager.SIM_STATE_READY;
				 telephonyInfo.isSIM2Ready = tmSim2.getSimState() == TelephonyManager.SIM_STATE_READY;;
			 }else if(Setting.IS_SINGLESIM){
				 	telephonyInfo.imeiSIM1 = mTelephonyManager.getDeviceId();
					telephonyInfo.imeiSIM2 = "";

					telephonyInfo.networkOperator = mTelephonyManager.getNetworkOperator();

					telephonyInfo.cellLocation = (GsmCellLocation) mTelephonyManager.getCellLocation();

					telephonyInfo.networkType = String.valueOf(mTelephonyManager.getNetworkType());
					
					telephonyInfo.isSIM1Ready = mTelephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
					telephonyInfo.isSIM2Ready = false;
			 }
			else{
				 telephonyInfo.imeiSIM1 = getDeviceInfoBySlotNew(context,"getDeviceId", 0); 
				 telephonyInfo.imeiSIM2 =getDeviceInfoBySlotNew(context, "getDeviceId", 1);
				 if(Setting.IS_LOLLIPOP){
					 subscriptionId = getSubIdBySlot(simId);
					 telephonyInfo.networkOperator = getNetworkOperatorBySubId(subscriptionId);
					 telephonyInfo.cellLocation = (GsmCellLocation) getCellLocationBySubId(subscriptionId);
					 telephonyInfo.networkType = ""+getNetworkTypeBySubId(subscriptionId);
					 telephonyInfo.sotwareVersion = getSoftwareVersion(context, "get", "ro.mediatek.version.release", "unknown");
				 }else{
					 telephonyInfo.networkOperator = getDeviceInfoBySlotNew(context,"getSimOperator", simId);
					 telephonyInfo.cellLocation = (GsmCellLocation)getCellLocationNew(context, "getCellLocation",simId);
					 telephonyInfo.networkType = getDeviceInfoBySlotNew(context,"getNetworkType", simId);

				 }
				 
				 telephonyInfo.isSIM1Ready = getSIMStateBySlotNew(context,"getSimState", 0); 
				 telephonyInfo.isSIM2Ready = getSIMStateBySlotNew(context, "getSimState", 1);

			 }
			 			 
			 
			 
			 
			 
//			 telephonyInfo.networkOperator = getDeviceIdBySlotNew(context,"getNetworkOperator", simId);
			 
//			 if (simId==1) {
//				 telephonyInfo.cId = getCellLocationNewA99(context, "getInt", "persist.sys.cid", simId);
//				 telephonyInfo.lacId = getCellLocationNewA99(context, "getInt", "persist.sys.lac", simId);
//			 }

		} catch (Exception e) {
//			e.printStackTrace();
			telephonyInfo.imeiSIM1 = mTelephonyManager.getDeviceId();
			telephonyInfo.imeiSIM2 = "";

			telephonyInfo.networkOperator = mTelephonyManager.getNetworkOperator();

			telephonyInfo.cellLocation = (GsmCellLocation) mTelephonyManager.getCellLocation();

			telephonyInfo.networkType = String.valueOf(mTelephonyManager.getNetworkType());
			
			telephonyInfo.isSIM1Ready = mTelephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
			telephonyInfo.isSIM2Ready = false;
		}

		return telephonyInfo;
	}
	
	
	private static String getTelephonyService(int simId) {
		Method method;
		String telService = null;
		try {
			method = mTelephonyManagerClass.getMethod("getServiceName",String.class,int.class);
			telService =  (String) method.invoke(null, Context.TELEPHONY_SERVICE,simId);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return telService;
	}

	private static TelephonyManager getTMinstancebysimId(int simId) {
		Class<?> telephonyClass;
		try {
			telephonyClass = Class.forName(mTelephonyManager.getClass().getName());
			Method method = telephonyClass.getMethod("getDefault", int.class);
			mTelephonyManager =  (TelephonyManager) method.invoke(null, simId);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mTelephonyManager;
	}

	private static String getSoftwareVersion(Context context,String predictedMethodName, String param1, String param2)
			throws GeminiMethodNotFoundException
	{
		Object ob_phone = null;
		String softwareV = null;
		
		try {

			Class<?> telephony = Class.forName("android.os.SystemProperties");

			Constructor<?> constructor = telephony.getDeclaredConstructor();
			constructor.setAccessible(true);
			Object obj = constructor.newInstance();

			Class<?>[] parameter = new Class[2];
			parameter[0] = String.class;
			parameter[1] = String.class;
			Method getSimID = telephony.getMethod(predictedMethodName,parameter);

			Object[] obParameter = new Object[2];
			obParameter[0] = param1;
			obParameter[1] = param2;
			ob_phone = getSimID.invoke(obj, obParameter);
			
			if (ob_phone != null) {
				softwareV = ob_phone.toString();
				System.out.println("res:"+softwareV);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeminiMethodNotFoundException(predictedMethodName);
		}
		return softwareV;
	}
	
	
	
	

	private static String getDeviceInfoBySlotNew(Context context,String predictedMethodName, int slotID)
			throws GeminiMethodNotFoundException {

		String imei = null;

		try {

			Class<?> telephony = Class.forName("com.mediatek.telephony.TelephonyManagerEx");

			Constructor<?> constructor = telephony.getDeclaredConstructor();
			constructor.setAccessible(true);
			Object obj = constructor.newInstance();

			Class<?>[] parameter = new Class[1];
			parameter[0] = int.class;
			Method getSimID = telephony.getMethod(predictedMethodName,parameter);

			Object[] obParameter = new Object[1];
			obParameter[0] = slotID;
			Object ob_phone = getSimID.invoke(obj, obParameter);

			if (ob_phone != null) {
				imei = ob_phone.toString();

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeminiMethodNotFoundException(predictedMethodName);
		}

		return imei;

	}

	private static String getDeviceIdBySlot(Context context,String predictedMethodName, int slotID)
			throws GeminiMethodNotFoundException {

		String imei = null;

		TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		try {

			Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

			Class<?>[] parameter = new Class[1];
			parameter[0] = int.class;
			Method getSimID = telephonyClass.getMethod(predictedMethodName,parameter);

			Object[] obParameter = new Object[1];
			obParameter[0] = slotID;
			Object ob_phone = getSimID.invoke(telephony, obParameter);

			if (ob_phone != null) {
				imei = ob_phone.toString();

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeminiMethodNotFoundException(predictedMethodName);
		}

		return imei;
	}

	private static Object getCellLocationNew(Context context,String predictedMethodName, int slotID)
			throws GeminiMethodNotFoundException {

		Object ob_phone = null;

		try {

			Class<?> telephony = Class.forName("com.mediatek.telephony.TelephonyManagerEx");

			Constructor<?> constructor = telephony.getDeclaredConstructor();
			constructor.setAccessible(true);
			Object obj = constructor.newInstance();

			Class<?>[] parameter = new Class[1];
			parameter[0] = int.class;
			Method getSimID = telephony.getMethod(predictedMethodName,parameter);

			Object[] obParameter = new Object[1];
			obParameter[0] = slotID;
			ob_phone = getSimID.invoke(obj, obParameter);

			/*
			 * if(ob_phone != null){ imei = ob_phone.toString();
			 * 
			 * }
			 */
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeminiMethodNotFoundException(predictedMethodName);
		}

		return ob_phone;
	}
	
	
	private static Integer getCellLocationNewA99(Context context,String predictedMethodName, String cellLac,int slotID)
			throws GeminiMethodNotFoundException {

		Object ob_phone = null;
		int cellLacId = 0;

		try {

			Class<?> telephony = Class.forName("android.os.SystemProperties");

			Constructor<?> constructor = telephony.getDeclaredConstructor();
			constructor.setAccessible(true);
			Object obj = constructor.newInstance();

			Class<?>[] parameter = new Class[2];
			parameter[0] = String.class;
			parameter[1] = int.class;
			Method getSimID = telephony.getMethod(predictedMethodName,parameter);

			Object[] obParameter = new Object[2];
			obParameter[0] = cellLac;
			obParameter[0] = slotID;
			ob_phone = getSimID.invoke(obj, obParameter);

			if (ob_phone != null) {
				cellLacId = Integer.parseInt(ob_phone.toString());

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeminiMethodNotFoundException(predictedMethodName);
		}

		return cellLacId;
	}

	private static Object getCellLocation(Context context,String predictedMethodName, int slotID)
			throws GeminiMethodNotFoundException {

		Object ob_phone = null;

		TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		try {

			Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

			Class<?>[] parameter = new Class[1];
			parameter[0] = int.class;
			Method getSimID = telephonyClass.getMethod(predictedMethodName,parameter);

			Object[] obParameter = new Object[1];
			obParameter[0] = slotID;
			ob_phone = getSimID.invoke(telephony, obParameter);

		} catch (Exception e) {
			e.printStackTrace();
			throw new GeminiMethodNotFoundException(predictedMethodName);
		}

		return ob_phone;
	}

	private static boolean getSIMStateBySlotNew(Context context,String predictedMethodName, int slotID)
			throws GeminiMethodNotFoundException {

		boolean isReady = false;

		try {

			Class<?> telephony = Class.forName("com.mediatek.telephony.TelephonyManagerEx");

			Constructor<?> constructor = telephony.getDeclaredConstructor();
			constructor.setAccessible(true);
			Object obj = constructor.newInstance();

			Class<?>[] parameter = new Class[1];
			parameter[0] = int.class;
			Method getSimState = telephony.getMethod(predictedMethodName,parameter);

			Object[] obParameter = new Object[1];
			obParameter[0] = slotID;
			Object ob_phone = getSimState.invoke(obj, obParameter);
			if (ob_phone != null) {
				int simState = Integer.parseInt(ob_phone.toString());
				if (simState == TelephonyManager.SIM_STATE_READY) {
					isReady = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeminiMethodNotFoundException(predictedMethodName);
		}

		return isReady;
	}

	private static boolean getSIMStateBySlot(Context context,String predictedMethodName, int slotID)
			throws GeminiMethodNotFoundException {

		boolean isReady = false;

		TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

		try {

			Class<?> telephonyClass = Class.forName(telephony.getClass().getName());

			Class<?>[] parameter = new Class[1];
			parameter[0] = int.class;
			Method getSimStateGemini = telephonyClass.getMethod(predictedMethodName, parameter);

			Object[] obParameter = new Object[1];
			obParameter[0] = slotID;
			Object ob_phone = getSimStateGemini.invoke(telephony, obParameter);

			if (ob_phone != null) {
				int simState = Integer.parseInt(ob_phone.toString());
				if (simState == TelephonyManager.SIM_STATE_READY) {
					isReady = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new GeminiMethodNotFoundException(predictedMethodName);
		}

		return isReady;
	}

	public static String getBuild(Context context, String className, String key)
			throws IllegalArgumentException {

		String ret = "";

		try {

			ClassLoader cl = context.getClassLoader();
			Class<?> SystemProperties = cl.loadClass(className);

			// Parameters Types
			Class<?>[] paramTypes = new Class[1];
			paramTypes[0] = String.class;

			Method get = SystemProperties.getMethod("get", paramTypes);

			// Parameters
			Object[] params = new Object[1];
			params[0] = new String(key);

			ret = (String) get.invoke(SystemProperties, params);

		} catch (IllegalArgumentException iAE) {
			throw iAE;
		} catch (Exception e) {
			ret = "";
			// TODO
		}

		return ret;

	}
	
	private static long getSubIdBySlot(int slot) {
		Class<?> subscriptionManager = null;
		try {
			subscriptionManager = Class.forName("android.telephony.SubscriptionManager");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		if (subscriptionManager == null) {
			return -1;
		}

		try {
			Method method = subscriptionManager.getMethod("getSubId", int.class);
			try {
				long[] subId = (long[]) method.invoke(null, slot);
				return subId[0];
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return -1;
	}
	
	private static int getNetworkTypeBySubId(long subId) {
		int type = -1;
		try {
			Method method = mTelephonyManagerClass.getMethod("getNetworkType",long.class);
			try {
				type = (Integer) method.invoke(mTelephonyManager, subId);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return type;
	}

	private static String getNetworkOperatorBySubId(long subId) {
		String name = null;
		try {
			Method method = mTelephonyManagerClass.getMethod("getNetworkOperator", long.class);
			try {
				name = (String) method.invoke(mTelephonyManager, subId);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return name;
	}

	private static CellLocation getCellLocationBySubId(long subId) {
		CellLocation cellLocation = null;
		try {
			Method method = mTelephonyManagerClass.getMethod("getCellLocationBySubId", long.class);
			try {
				cellLocation = (CellLocation) method.invoke(mTelephonyManager,subId);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return cellLocation;
	}

	private static class GeminiMethodNotFoundException extends Exception {

		private static final long serialVersionUID = -996812356902545308L;

		public GeminiMethodNotFoundException(String info) {
			super(info);
		}
	}
}