package com.example.mobliesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author jacksonCao
 * @data 2016-7-10
 * @desc 对SharedPreferences功能的封装

 * @version  $Rev: 16 $
 * @author  $Author: caojun $
 * @Date  $Date: 2016-08-05 17:55:44 +0800 (周五, 05 八月 2016) $
 * @Id	$ID$
 * @Url  $URL: https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example/mobliesafe/utils/SPUtils.java $
 */
public class SPUtils {
	
	/**
	 *
	 * @param context 
	 * @param key
	 * @param value
	 */
	public static void putBoolean(Context context , String key, boolean value){
		 SharedPreferences sp = context.getSharedPreferences(MyContains.SPFILENAME, Context.MODE_PRIVATE);  
		 sp.edit().putBoolean(key, value).commit();
	}
	
	public static boolean getBoolean(Context context ,String key ,boolean defValue){
		SharedPreferences sp = context.getSharedPreferences(MyContains.SPFILENAME, Context.MODE_PRIVATE);
		return sp.getBoolean(key, defValue);
	}
	public static void putString(Context context , String key, String value){
		 SharedPreferences sp = context.getSharedPreferences(MyContains.SPFILENAME, Context.MODE_PRIVATE);  
		 sp.edit().putString(key, value).commit();
	}
	
	public static String getString(Context context ,String key ,String defValue){
		SharedPreferences sp = context.getSharedPreferences(MyContains.SPFILENAME, Context.MODE_PRIVATE);
		return sp.getString(key, defValue);
	}
	
	
	public static void putInt(Context context , String key, int value){
		 SharedPreferences sp = context.getSharedPreferences(MyContains.SPFILENAME, Context.MODE_PRIVATE);  
		 sp.edit().putInt(key, value).commit();
	}
	
	public static int getInt(Context context ,String key ,int defValue){
		SharedPreferences sp = context.getSharedPreferences(MyContains.SPFILENAME, Context.MODE_PRIVATE);
		return sp.getInt(key, defValue);
	}
	public static void putLong(Context context , String key, long value){
		 SharedPreferences sp = context.getSharedPreferences(MyContains.SPFILENAME, Context.MODE_PRIVATE);  
		 sp.edit().putLong(key, value).commit();
	}
	
	public static long getLong(Context context ,String key ,long defValue){
		SharedPreferences sp = context.getSharedPreferences(MyContains.SPFILENAME, Context.MODE_PRIVATE);
		return sp.getLong(key, defValue);
	}
}
