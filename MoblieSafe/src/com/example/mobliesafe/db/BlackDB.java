package com.example.mobliesafe.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author jacksonCao
 * @data 2016-7-16
 * @desc 黑名单 数据库

 * @version  $Rev: 12 $
 * @author  $Author: caojun $
 * @Date  $Date: 2016-07-17 19:50:22 +0800 (周日, 17 七月 2016) $
 * @Id	$ID$
 * @Url  $URL: https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example/mobliesafe/db/BlackDB.java $
 */
public class BlackDB extends SQLiteOpenHelper{

	private  static final int VERSION = 1;
	//短信拦截
	public static final int  SMS_MODE= 1 << 0;  //01
	//电话拦截
	public static final int PHONE_MODE = 1 << 1;  //10
	//全部拦截
	public static final int ALL_MODE = SMS_MODE | PHONE_MODE; //11
	
	
	//表名
	public static final String TBNAME = "blacktb";
	//列名
	public static final String PHONE = "phone";
	public static final String MODE = "mode";

	
	
	public BlackDB(Context context) {
		super(context, "black.db", null	, VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table blacktb(_id integer primary key autoincrement,phone text,mode integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("drop table blacktb");
		onCreate(db);
		
	}

}
