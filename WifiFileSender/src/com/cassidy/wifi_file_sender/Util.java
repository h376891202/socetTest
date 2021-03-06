package com.cassidy.wifi_file_sender;

import java.util.Random;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public final class Util {
	private static final String SEPARATOR = ":";
	private static final int RANDOM_LENGTH = 5;
	private static final String TAG = "WinfiFileSender";
	
	public static String getWifiIP(Context context) {
		WifiManager wifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int ipAddress = wifiInfo.getIpAddress();

		StringBuffer sb = new StringBuffer("");
		sb.append(String.valueOf((ipAddress & 0x000000FF)));
		sb.append(".");
		sb.append(String.valueOf((ipAddress & 0x0000FFFF) >>> 8));
		sb.append(".");
		sb.append(String.valueOf((ipAddress & 0x00FFFFFF) >>> 16));
		sb.append(".");
		sb.append(String.valueOf((ipAddress >>> 24)));
		return sb.toString();
	}
	
	public static int[] getPostion(String message){
		if(message.split("\\|").length != 4)
			return null;
		String postionStrs [] = message.split("\\|");
 		int [] postions = new int[4];
		try {
			for(int index = 0;index < 4;index++){
				postions[index] = Integer.parseInt(postionStrs[index]);
			}
		} catch (NumberFormatException e) {
			return null;
		}
		return postions;
	}
	
	public static String changeToString(int width, int height){
		return width+SEPARATOR+height;
	}
	
	public static int[] changeToInt(String size){
		String sizeStr[] = size.split(SEPARATOR);
		if(null == size || sizeStr.length != 2)
			return null;
		int sizeInt[] = new int[2];
		try {
			for(int i = 0;i < 2;i++){
				sizeInt[i] = Integer.parseInt(sizeStr[i]);
			}
			return sizeInt;
		} catch (NumberFormatException e) {
			return null;
		}
	}
	
	public static String createRandomId(){
		StringBuilder sb = new StringBuilder();
		for (int index = 0; index < RANDOM_LENGTH; index++) {
			int key = new Random().nextInt(36);
			char value;
			if (key <= 26) {
				value = (char)('a'+Math.random()*('z'-'a'+1));
			} else {
				value = (char)('0'+Math.random()*('9'-'0'+1));
			}
			sb.append(value);
		}
		return sb.toString();
		
	}
	
	public static void logv(String message){
		Log.v(TAG, message);
	}
	

}
