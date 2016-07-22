package com.example.mobliesafe.service;

import com.example.mobliesafe.dao.AddressDao;
import com.example.mobliesafe.view.MyToast;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

public class IncomingShowLocationService extends Service {

	private TelephonyManager mTM;
	private PhoneStateListener listener;
	private MyToast myToast;
	private IntentFilter filter;
	private MyOutgoingCall mReceiver;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		System.out.println(" 服务已经启动 ");
		//注册来电显示
		registerService();
		
		//注册外拨电话显示
		registerOutgoing();
		
		myToast = new MyToast(getApplicationContext());
		
		super.onCreate();
	}

	
	private class MyOutgoingCall extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			String location = AddressDao.getLocation(number);
			
			showLocation(location);
System.out.println("success");
		}
	}
	
	
	private void registerOutgoing() {
		mReceiver = new MyOutgoingCall();
		filter = new IntentFilter();
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		
		registerReceiver(mReceiver, filter);
System.out.println("registerOutgoing");	
		
	}

	private void registerService() {
		mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		listener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				
				switch (state) {
				case TelephonyManager.CALL_STATE_IDLE:
					myToast.hiden();
					
					break;
				case TelephonyManager.CALL_STATE_OFFHOOK:
					myToast.hiden();
					break;
				case TelephonyManager.CALL_STATE_RINGING:
					showLocation(incomingNumber);
					break;
		
				}

				super.onCallStateChanged(state, incomingNumber);
			}

		};

		mTM.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

	}
	
	
	
	@Override
	public void onDestroy() {
		mTM.listen(listener, PhoneStateListener.LISTEN_NONE);
		
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	protected void showLocation(String incomingNumber) {
		String location = AddressDao.getLocation(incomingNumber);
		myToast.show(location);
	}

}
