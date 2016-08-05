package com.example.mobliesafe.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AntiAruisDao {
	
	public static String PATH ="/data/data/com.example.mobliesafe/files/antivirus.db"; 
	
	/**传入MD5值判断是否是病毒
	 * 
	 * @param md5
	 * @return
	 */
	public static boolean isVirus(String md5){
		boolean isVirus = false;
		
		SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select 1 from datable where md5=?", new String[]{md5});
		
		if(cursor.moveToNext()){
			isVirus = true;
		}
		cursor.close();
		db.close();
		
		return isVirus;
	}
	
	
	/**得到当前将病毒数据库的版本
	 * @return
	 */
	public static int getCurrentVersion(){
		int version = -1;
		SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select subcnt from version", null);
		if(cursor.moveToNext()){
			version  = cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return version;
	}
	
	/**更新数据库的版本
	 * @param version
	 */
	public static void updateCurrentVersion(int version){
		SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READWRITE);
		ContentValues values = new ContentValues();
		values.put("subcnt", version);
		db.update("version", values, null, null);
		
		db.close();
	}
		
	/**更新病毒数据库
	 * @param md5
	 * @param desc
	 */
	public static void updateVirusDB(String md5 , String desc){
		SQLiteDatabase db = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READWRITE);
		ContentValues values =new  ContentValues();
		values.put("md5", md5);
		values.put("desc", desc);
		values.put("name", "Adnroid.Troj.GeminiReg.a");
		values.put("type", "6");
		db.insert("datable", null, values);
		db.close();
		
	}
	
}
