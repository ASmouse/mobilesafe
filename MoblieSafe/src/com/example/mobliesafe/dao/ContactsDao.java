package com.example.mobliesafe.dao;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.example.mobliesafe.domain.ContactBean;

/**
 * @author jacksonCao
 * @data 2016-7-14
 * @desc 获取所有联系人的dao
 * 
 * @version $Rev: 12 $
 * @author $Author: caojun $
 * @Date $Date: 2016-07-17 19:50:22 +0800 (周日, 17 七月 2016) $
 * @Id $ID$
 * @Url $URL:
 *      https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example
 *      /mobliesafe/dao/ContactsDao.java $
 */
public class ContactsDao {
	public static List<ContactBean> getContacts(Context context) {
		List<ContactBean> dataLists = new ArrayList<ContactBean>();
		// 提高程序性能,优化:变量声明只用一次
		ContactBean cb = null;
		String data = null;
		String type = null;
		Cursor cursor2 = null;
		String r_c_id = null;
		// uri content://contacts/raw_contacts
		Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
		Uri uriData = Uri.parse("content://com.android.contacts/data");
		// 内容提供者
		Cursor cursor = context.getContentResolver().query(uri,
				new String[] { "contact_id" }, null, null, null);

		while (cursor.moveToNext()) {
			cb = new ContactBean();

			r_c_id = cursor.getString(0);
			cursor2 = context.getContentResolver().query(uriData,
					new String[] { "data1", "mimetype" }, "raw_contact_id=?",
					new String[] { r_c_id }, null);
			while (cursor2.moveToNext()) {

				data = cursor2.getString(0);
				type = cursor2.getString(1);

				if (type.equals("vnd.android.cursor.item/name")) {
					cb.setName(data);
				} else if (type.equals("vnd.android.cursor.item/phone_v2")) {
					cb.setPhone(data);
				}
			}

			// 添加数据
			dataLists.add(cb);
			cursor2.close();

		}
		cursor.close();

		return dataLists;
	}

	public static List<ContactBean> getSmsContact(Context context) {
		List<ContactBean> datas = new ArrayList<ContactBean>();
		
		Uri uri =Uri.parse("content://sms");
		Cursor cursor = context.getContentResolver().query(uri, new String[]{"address"}, null ,null, null);
		ContactBean bean = null;
		while(cursor.moveToNext()){
			bean = new ContactBean();
			bean.setName("sms:");
			bean.setPhone(cursor.getString(0));
			datas.add(bean);
		}
		cursor.close();
		
		return datas;

	}
	
	public static List<ContactBean> getTelContact(Context context) {
		List<ContactBean> datas = new ArrayList<ContactBean>();
		//contact2 表中的call 表 
		
		Uri uri =Uri.parse("content://call_log/calls");
		Cursor cursor = context.getContentResolver().query(uri, new String[]{"name","number"}, null ,null, null);
		ContactBean bean = null;
		while(cursor.moveToNext()){
			bean = new ContactBean();
			bean.setName(cursor.getString(0));
			bean.setPhone(cursor.getString(1));
			datas.add(bean);
		}
		cursor.close();
		return datas;

	}
}
