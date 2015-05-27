package com.micromax.salestrac;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.util.Base64;

public class UnMountService extends Service{
	String imei;
	static int flagWifi=0;
	String regMessageChecksum;
	String getMacAddress ;
	String serialNO;
	String deviceIdImei;
	String uri;
	String tabletRegs;
	String softwareVersion;
	Context mContext;
	long systemTime = System.currentTimeMillis();
	TabletInfoActivity tabInfo = new TabletInfoActivity();
	public int flag = 0;
	boolean success;
	boolean bFolderCreatesuccess;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate()
	{
		
		System.out.println("Inside on Create of unmount service");
		super.onCreate();
	}
	@Override
	public int onStartCommand(final Intent intent, int flags, int startId) 
	{  
System.out.println("Inside on start of unmount service");
		sendCreateFile();
		return startId;
	}
	/**----------------------------------------------------------------


	 * DESCRIPTION
	 *    This method creates a file and write mac adress into it  
	 * @param
	 *  boolean
	 * @return
	 *  void
	 *  @author dh.anant

   ----------------------------------------------------------------*/
	public void sendCreateFile()
	{
		//String sFilePath="/Android/data/com.micromax.Tabletinfo";
		String sFile="macadressss.log";
		String exStorageDirectory=Environment.getExternalStorageDirectory().toString();
		File myFile=new File(exStorageDirectory+sFile);
		boolean autoWifi =false;
		TelephonyManager tMgr=(TelephonyManager)getApplicationContext().getSystemService(SendService.TELEPHONY_SERVICE);
		String networkOperator = tMgr.getNetworkOperator();

		System.out.println("the value of network operator is "+networkOperator);
		deviceIdImei=tMgr.getDeviceId();

		if(deviceIdImei.contains("23"))
		{
			writeInFile(deviceIdImei);
		}
		else{
			myFile = createFile(exStorageDirectory, myFile);
			try { 
				System.out.println("inside creating file");
				WifiManager wifiManager = (WifiManager)getSystemService(WIFI_SERVICE);
				System.out.println("Wifi status"+wifiManager.isWifiEnabled());

				if(!wifiManager.isWifiEnabled()){
					//System.out.println("inside is wifi enabled");
					wifiManager.setWifiEnabled(true);
					SystemClock.sleep(1000*20);
					System.out.println("inside auto wifi on");
					autoWifi=true;
				}

				if(wifiManager.isWifiEnabled()){
					wifiManager.setWifiEnabled(true);
					SystemClock.sleep(1000*20);
					System.out.println("turning on wifi");
					WifiInfo wifiInfo = wifiManager.getConnectionInfo();
					getMacAddress = wifiInfo.getMacAddress();
					encryptedSerialNo(getMacAddress);

					serialNO = serial_N0(getMacAddress);							
					System.out.println("getMacAdress"+serialNO);
					if(myFile.exists())
					{

						OutputStream fo = new FileOutputStream(myFile);              
						fo.write(serialNO.getBytes());
						fo.close();
					}
					else
					{
						System.out.println( "File not created ");	
					}
					if(autoWifi)
					{
						wifiManager.setWifiEnabled(false);
					}
				}
			}catch (Exception e) {
				System.out.println( "the exception is"+e.toString());	
			}
		}

		stopSelf();

	}
	public void writeInFile(String serialNumber)
	{
		System.out.println("creating file in unmount service");
		//String sFilePath="/Android/data/com.micromax.Tabletinfo";
		String sFile="/Android/data/com.micromax.Tabletinfo/macadressss.log";
		//String sFile="/macadressss.log";
		String exStorageDirectory=Environment.getExternalStorageDirectory().toString();
		File serialNumFile =new File(exStorageDirectory+sFile);


		serialNumFile = createFile(exStorageDirectory, serialNumFile);
		try { 
			System.out.println("writting file inside writeInFile");



			if(serialNumFile.exists())
			{

				OutputStream fo = new FileOutputStream(serialNumFile);              
				fo.write(serialNumber.getBytes());
				fo.close();
			}
			else
			{
				System.out.println( "File not created ");	
			}
		}
		catch (Exception e) {
			System.out.println("cannot write file "+e.toString());
		}
	}

	private File createFile(String exStorageDirectory, File myFile) {
		if(!myFile.exists())
		{
			try { 
				String sAndroidPath="/Android";
				File androidPath=new File(exStorageDirectory+sAndroidPath);
				if(!androidPath.exists())
					androidPath.mkdir();
				System.out.println("created /Android");
				String sAndroidDataPath="/Android/data";
				File androidDataPath=new File(exStorageDirectory+sAndroidDataPath);
				if(!androidDataPath.exists())
					androidDataPath.mkdir();
				System.out.println("created /Android/data");
				String sTabletInfoPath="/Android/data/com.micromax.Tabletinfo";
				File tabletInfoPath=new File(exStorageDirectory+sTabletInfoPath);
				bFolderCreatesuccess= tabletInfoPath.exists();

				if(!tabletInfoPath.exists())
				bFolderCreatesuccess=tabletInfoPath.mkdir();
				System.out.println("created /Android/data/com.micromax.Tabletinfo");
				if(bFolderCreatesuccess)
				{
					myFile= new File(Environment.getExternalStorageDirectory() + File.separator+"Android/data/com.micromax.Tabletinfo/macadressss.log");
				//	myFile= new File(Environment.getExternalStorageDirectory() + File.separator+"macadressss.log");
					myFile.createNewFile();
				}
			}catch (Exception e) {
				//ioe.printStackTrace();
			}
		}
		return myFile;
	}

	/**----------------------------------------------------------------


	 * DESCRIPTION
	 *    This method generates a integer val.
	 *    @param String
	 * @return
	 *  String
	 *  @author dh.anant

----------------------------------------------------------------*/
	public static int hextoInt(String inst)   {

		if(inst.equals("A")){

			return 10;
		}
		else if(inst.equals("B")){

			return 11;
		}
		else if(inst.equals("C")){

			return 12;
		}
		else if(inst.equals("D")){

			return 13;
		}
		else if(inst.equals("E")){

			return 14;
		}
		else if(inst.equals("F")){

			return 15;
		}
		else
			return Integer.parseInt(inst);      

	}

	/**----------------------------------------------------------------


	 * DESCRIPTION
	 *    This method generates a checksum for the 15 digit serial number.
	 *    @param String
	 * @return
	 *  String
	 *  @author dh.anant

----------------------------------------------------------------*/

	public static String generate_checkdigit(String Inst) 
	{
		String Inst1;
		int k = 0;
		int K = 0;

		// remove leading or trailing whitespace, convert to uppercase
		Inst = Inst.trim().toUpperCase();
		String LH;
		String RH;
		String SC;
		int TT;
		Inst1 = Inst;        
		LH = "";
		for (k=Inst1.length();(k>1); k=k-2)
		{        	         	
			LH = LH +   Integer.toHexString(hextoInt(Inst1.substring((k - 1), k)) *2);
			LH.toUpperCase();
		}
		RH = "";
		for (k = (Inst1.length() - 1); (k >= 1); k = (k-2))
		{
			RH = (RH + Inst1.substring((k - 1), k));
		}
		Inst1 = (LH + RH);
		TT = 0;
		for (K = 1; (K <= Inst1.length()); K++)
		{
			TT = (TT + hextoInt(Inst1.substring((K - 1), K).toUpperCase()));
		}
		TT = (TT % 16);
		TT = (16 - TT);
		if ((TT == 16))
		{
			TT = 0;
		}
		switch (TT)
		{
		case 10:
			SC = "A";
			break;
		case 11:
			SC = "B";
			break;
		case 12:
			SC = "C";
			break;
		case 13:
			SC = "D";
			break;
		case 14:
			SC = "E";
			break;
		case 15:
			SC = "F";
			break;
		default:
			SC = Integer.toString(TT);
			break;
		}
		return SC;
	}

	private final String characterEncoding = "UTF-8";
	private final String cipherTransformation = "AES/CBC/PKCS5Padding";
	private final String aesEncryptionAlgorithm = "AES";




	/**----------------------------------------------------------------


	 * DESCRIPTION
	 *    This method encrypt the string.
	 *    @param byte[], byte[], byte []
	 * @return
	 * byte[]
	 *  @author dh.anant

----------------------------------------------------------------*/
	public byte[] encrypt(byte[] plainText, byte[] key, byte []
			initialVector) throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException
			{
		Cipher cipher = Cipher.getInstance(cipherTransformation);
		SecretKeySpec secretKeySpec = new SecretKeySpec(key,
				aesEncryptionAlgorithm);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initialVector);
		cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		plainText = cipher.doFinal(plainText);
		return plainText;
			}
	/**----------------------------------------------------------------


	 * DESCRIPTION
	 *    This method to process the key specified for encryption and decryption.
	 *    @param String
	 * @return
	 * byte[]
	 *  @author dh.anant

----------------------------------------------------------------*/
	private byte[] getKeyBytes(String key) throws
	UnsupportedEncodingException{
		byte[] keyBytes= new byte[16];
		byte[] parameterKeyBytes= key.getBytes(characterEncoding);
		System.arraycopy(parameterKeyBytes, 0, keyBytes, 0,
				Math.min(parameterKeyBytes.length, keyBytes.length));
		return keyBytes;
	}
	/**----------------------------------------------------------------


	 * DESCRIPTION
	 *    This method encrypts a string with a key.
	 *    @param String
	 * @return
	 * String
	 *  @author dh.anant

----------------------------------------------------------------*/
	public String encrypt(String plainText, String key) throws
	UnsupportedEncodingException, InvalidKeyException,
	NoSuchAlgorithmException, NoSuchPaddingException,
	InvalidAlgorithmParameterException, IllegalBlockSizeException,
	BadPaddingException{
		byte[] plainTextbytes = plainText.getBytes(characterEncoding);
		byte[] keyBytes = getKeyBytes(key);
		return Base64.encodeToString(encrypt(plainTextbytes,keyBytes,
				keyBytes), Base64.DEFAULT);
	}

	/**----------------------------------------------------------------


	 * DESCRIPTION
	 *    This method concatenates 23 for every macadress.
	 *    @param String
	 * @return
	 * String
	 *  @author dh.anant

----------------------------------------------------------------*/

	public String serial_N0(String macAddress)
	{
		String n = macAddress.replace(":", "") ;
		n="23"+n;
		String enc="";
		if (n.length() ==14) {
			n=n + generate_checkdigit(n);
		}
		return n;
	}
	/**----------------------------------------------------------------


	 * DESCRIPTION
	 *    This method writes a encrypted string into a file .
	 *    @param String
	 * @return
	 * String
	 *  @author dh.anant

----------------------------------------------------------------*/

	public void encryptedSerialNo(String macAddress)
	{
		String n = null;
		try{
			n = macAddress.replace(":", "") ;
		}
		catch (Exception e) {
			// TODO: handle exception

		}
		n="23"+n;
		String enc="";
		if (n.length() ==14) {
			n=n + generate_checkdigit(n);
			try {
				enc=encrypt(n, "mlabs");

				try { 

//					String newFolder="/MMX/data";
//					String exStorageDirectory=Environment.getExternalStorageDirectory().toString();
//
//					File myNewFolder=new File(exStorageDirectory+newFolder);
//					myNewFolder.setReadOnly();
//					myNewFolder.mkdirs();
					File file = new File(Environment.getExternalStorageDirectory() + File.separator + "system.log");
					file.setReadOnly();
					file.createNewFile();
					OutputStream fo = new FileOutputStream(file);              
					fo.write(enc.getBytes());
					file.setReadOnly();
					fo.close();



				} catch (IOException ioe) 
				{ioe.printStackTrace();}


			} 
			catch (Exception e) {

			}


		}
	}

}
