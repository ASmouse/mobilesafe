package com.example.mobliesafe.service;

import java.lang.reflect.Method;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.example.mobliesafe.R;
import com.example.mobliesafe.dao.BlackDao;
import com.example.mobliesafe.dao.ContactsDao;
import com.example.mobliesafe.db.BlackDB;

public class BlackService extends Service {

	private BlackDao mBlackDao;

	private SMSReceiver receiver;

	private TelephonyManager tm;

	private PhoneStateListener listener;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private class SMSReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 拦截短信
			Object[] smsObjects = (Object[]) intent.getExtras().get("pdus");
			for (Object sms : smsObjects) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms);
				String address = smsMessage.getDisplayOriginatingAddress();
				// 判断是否黑名单的号码:发来的短信号码查询返回非0就是黑名单中的

				int mode = mBlackDao.getMode(address);
				if ((mode & BlackDB.SMS_MODE) != 0) {
					// 是黑名单中的号码,并且是属于短信拦截模式
					// 拦截
					abortBroadcast();
				}
			}
		}
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		System.out.println("黑名单服务开启");
		//提升为前台进程
		upForeground();
		mBlackDao = new BlackDao(getApplicationContext());

		// 动态注册拦截短信的广播监听者
		registerSMS();
		// 注册电话拦截
		registerTel();

	}

	private void upForeground() {
		Notification notification = new Notification(R.drawable.heima, "这是手机安全卫士tips", 0);
		//传递包名,打开整个App
		Intent intent = new Intent("com.example.mobliesafe");
		PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 1, intent, 0);
		
		notification.setLatestEventInfo(getApplicationContext(), "手机卫士", "时刻保护您的手机安全", contentIntent);
		startForeground(0, notification);
	}

	/**
	 * 注册电话拦截
	 */
	private void registerTel() {
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		listener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				super.onCallStateChanged(state, incomingNumber);

				switch (state) {
				case TelephonyManager.CALL_STATE_RINGING:
					// 删除电话日志
					deleteLog(incomingNumber);

					// 判断和挂断的逻辑
					endCall(incomingNumber);

					break;

				}
			}

		};

		tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	private void deleteLog(final String phone) {
		// 利用内容提供者删除日志,避免不确定的日志生成事件导致删除错误
		Uri uri = Uri.parse("content://call_log/calls");
		getContentResolver().registerContentObserver(uri, true,
				new ContentObserver(new Handler()) {
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				//日志发生变化删除
				ContactsDao.deleteTelLog(getApplicationContext(), phone);
				//删除之后取消注册
				getContentResolver().unregisterContentObserver(this);
			}
		
		});


	}

	private void endCall(String phone) {
		// 是否是在黑名单中

		int mode = mBlackDao.getMode(phone);
		if ((mode & BlackDB.PHONE_MODE) != 0) {
			// 在黑名单中挂断电话
			System.out.println("挂断电话");
//			 sRegistry = ITelephonyRegistry.Stub.asInterface(ServiceManager.getService("telephony.registry"));
			try {
				// 1.calss
				Class clazz = Class.forName("android.os.ServiceManager");

				// 2.method
				Method method = clazz.getDeclaredMethod("getService",
						String.class);

				// 3.call
				IBinder iBinder = (IBinder) method.invoke(null,
						TELEPHONY_SERVICE);

				// 4.aidl ibinder
				ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
				iTelephony.endCall();// 挂断电话

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void registerSMS() {
		receiver = new SMSReceiver();
		IntentFilter filter = new IntentFilter();
		filter.setPriority(Integer.MAX_VALUE);
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		registerReceiver(receiver, filter);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);

		// 注销电话监听
		tm.listen(listener, PhoneStateListener.LISTEN_NONE);

	}

}
