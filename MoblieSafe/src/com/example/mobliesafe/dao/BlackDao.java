package com.example.mobliesafe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.mobliesafe.db.BlackDB;
import com.example.mobliesafe.domain.BlackBean;

/**
 * @author jacksonCao
 * @data 2016-7-16
 * @desc 对黑名单数据的操作
 * 
 * @version $Rev: 13 $
 * @author $Author: caojun $
 * @Date $Date: 2016-07-20 19:56:24 +0800 (周三, 20 七月 2016) $
 * @Id $ID$
 * @Url $URL:
 *      https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example
 *      /mobliesafe/dao/BlackDao.java $
 */
public class BlackDao {

	private BlackDB mBlackDB;

	public BlackDao(Context context) {
		mBlackDB = new BlackDB(context);
	}

	/**
	 * @param phone要删除的黑名单号码
	 */
	public boolean delete(String phone) {
		SQLiteDatabase database = mBlackDB.getWritableDatabase();
		int delete = database.delete(BlackDB.TBNAME, BlackDB.PHONE + "=?",
				new String[] { phone });
		database.close();

		if (delete > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 如果有 先删除再添加 如果没有 直接添加
	 * 
	 * @param phone
	 * @param mode
	 */
	public void updata(String phone, int mode) {
		// 不管有没有,先删除再添加
		delete(phone);
		add(phone, mode);
	}

	public void updata(BlackBean bean) {
		updata(bean.getPhone(), bean.getMode());
	}

	/**
	 * @param bean
	 *            黑名单数据的对象
	 */
	public void add(BlackBean bean) {
		add(bean.getPhone(), bean.getMode());
	}

	/**
	 * 添加黑名单数据
	 * 
	 * @param phone
	 * @param mode
	 *            拦截模式 SMS_MODE, PHONE_MODE , ALL_MODE
	 */
	public void add(String phone, int mode) {
		// 1.获取数据库
		SQLiteDatabase database = mBlackDB.getWritableDatabase();
		// 2.设置数据
		ContentValues values = new ContentValues();
		values.put(BlackDB.PHONE, phone);
		values.put(BlackDB.MODE, mode);
		// 添加数据
		database.insert(BlackDB.TBNAME, null, values);
		// 关闭数据库
		database.close();
	}

	/**
	 * @return 所有的黑名单数据
	 */
	public List<BlackBean> findAll() {
		List<BlackBean> datas = new ArrayList<BlackBean>();

		SQLiteDatabase db = mBlackDB.getWritableDatabase();
		// Cursor cursor = db.query(BlackDB.TBNAME, null, null, null, null,
		// null, null);
		Cursor cursor = db.rawQuery("select " + BlackDB.PHONE + ","
				+ BlackDB.MODE + " from " + BlackDB.TBNAME
				+ " order by _id desc", null);

		BlackBean data = null;
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				// 1.封装数据
				data = new BlackBean();
				data.setPhone(cursor.getString(0));
				data.setMode(cursor.getInt(1));

				// 2.添加数据
				datas.add(data);
			}
		}
		return datas;

	}

	/**
	 * @param pageNumber
	 *            第几页
	 * @param countPerPage
	 *            一夜显示多少条数据
	 * @return
	 */
	public List<BlackBean> getPageData(int pageNumber, int countPerPage) {
		List<BlackBean> datas = new ArrayList<BlackBean>();

		int startIndex = (pageNumber - 1) * countPerPage;
		SQLiteDatabase database = mBlackDB.getWritableDatabase();
		// !!!!!!!!!!!!!!!添加数据后显示在第一条的关键: " order by _id desc"
		Cursor cursor = database.rawQuery("select " + BlackDB.PHONE + ","
				+ BlackDB.MODE + " from " + BlackDB.TBNAME
				+ " order by _id desc" + " limit ?,?", new String[] {
				startIndex + "", countPerPage + "" });

		BlackBean data = null;
		if (cursor != null && cursor.getCount() > 0) {
			while (cursor.moveToNext()) {
				// 1.封装数据
				data = new BlackBean();
				data.setPhone(cursor.getString(0));
				data.setMode(cursor.getInt(1));

				// 2.添加数据
				datas.add(data);
			}
		}
		return datas;

	}

	/**
	 * 返回数据的条数
	 * 
	 * @return
	 */
	public int getTotalRows() {
		int toatalRows = 0;
		SQLiteDatabase database = mBlackDB.getWritableDatabase();
		Cursor cursor = database.rawQuery("select count(1) from blacktb", null);
		// 只有一列
		if (cursor.moveToNext()) {
			toatalRows = cursor.getInt(0);
		}

		return toatalRows;

	}

	/**
	 * 根据电话号码查询黑名单数据库中号码的拦截类型,没有查到返回0
	 * 
	 * @param phone
	 * @return
	 */
	public  int getMode(String phone) {
		int mode = 0;
		SQLiteDatabase database = mBlackDB.getWritableDatabase();
		Cursor cursor = database.rawQuery("select "+BlackDB.MODE+" from "+BlackDB.TBNAME+" where "+BlackDB.PHONE+"=?"  , new String[]{phone});
		
		if(cursor.moveToNext()){
			mode = cursor.getInt(0);
		}
		return mode;

	}

}
