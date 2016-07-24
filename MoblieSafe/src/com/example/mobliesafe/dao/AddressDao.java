package com.example.mobliesafe.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobliesafe.domain.NumberAndName;
import com.example.mobliesafe.domain.ServiceNameAndType;

public class AddressDao {
	private static final String DBPATHPHONE = "/data/data/com.example.mobliesafe/files/address.db";
	private static final String DBPATHSERVICE = "/data/data/com.example.mobliesafe/files/commonnum.db";

	
	/**得到所有的服务名和id
	 * @return
	 */
	public static List<ServiceNameAndType>  getAllServiceTypes(){
		List<ServiceNameAndType> mServiceNameAndType = new ArrayList<ServiceNameAndType>();
		
		SQLiteDatabase database = SQLiteDatabase.openDatabase(DBPATHSERVICE, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = database.rawQuery("select idx,name from classlist", null);
		ServiceNameAndType type = null;
		while(cursor.moveToNext()){
			type = new ServiceNameAndType();
			type.setName(cursor.getString(1));
			type.setOutKey(cursor.getInt(0));
			mServiceNameAndType.add(type);
		}
		cursor.close();
		return mServiceNameAndType;
	}
	
	
	
	/**
	 * @param type
	 * @return 具体服务类型的具体数据
	 */
	public static List<NumberAndName> getNumberAndName(ServiceNameAndType type){
		List<NumberAndName> mNumberAndName = new ArrayList<NumberAndName>();
		
		SQLiteDatabase database = SQLiteDatabase.openDatabase(DBPATHSERVICE, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = database.rawQuery("select number,name from table"+type.getOutKey(), null);
		NumberAndName Info =null;
		while(cursor.moveToNext()){
			Info = new NumberAndName();
			Info.setName(cursor.getString(1));
			Info.setNumber(cursor.getString(0));
			mNumberAndName.add(Info);
		}
		cursor.close();
		return mNumberAndName;
		
	}
	
	
	
	
	
	
	
	/**根据输入的号码得到其所在的位置,不区分固话和手机
	 * @param number
	 * @return
	 */
	public static String getLocation(String number){
		String location="未知号码";
		 
		
		Pattern p = Pattern.compile("1[34578]{1}[0-9]{9}");
		 Matcher m = p.matcher(number);
		 boolean b = m.matches();
		//判断是固话还是手机号
		if(b){
			//手机号
			 location = getMobileLocation(number.substring(0,7));
		}else{
			
		
		
				//固话
				if(number.charAt(1)=='1' || number.charAt(1)=='2'){
					//区号,截取两位
					location=getPhoneLocation(number.substring(1, 3));
				}else{
					//区号,截取三位
					 location = getPhoneLocation(number.substring(1, 4));
				}
		
		
			
		}
		
		return location;
	}
	
	
	
	
	/**查询手机号码的归属地
	 * @param mobileNumber
	 * 	手机号码的前七位!!!!!!!!!
	 * @return
	 */
	private static String getMobileLocation(String mobileNumber){
		String res="未知";
		SQLiteDatabase database = SQLiteDatabase.openDatabase(DBPATHPHONE, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = database.rawQuery("select location from data2 where id=(select outkey from data1 where id=?)", new String[]{mobileNumber});
		
		if(cursor.moveToNext()){
			 res = cursor.getString(0);
		}
		
		cursor.close();
		return res;
		
	}
	

	/**查询固话的位置
	 * @param phoneNumber
	 * 	固话的区号
	 * @return
	 */
	private static String getPhoneLocation(String phoneNumber){
		String res="未知";
		SQLiteDatabase database = SQLiteDatabase.openDatabase(DBPATHPHONE, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = database.rawQuery("select location from data2 where area=?", new String[]{phoneNumber});
		
		if(cursor.moveToNext()){
			 res = cursor.getString(0);
		}
		
		cursor.close();
		return res;
		
	}
}
