package com.itheima13.rocket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class RocketService extends Service {

	private RocketToast mRocketToast;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//¿ªÆôÐ¡»ð¼ý
		mRocketToast = new RocketToast(this);
		mRocketToast.show();//ÏÔÊ¾Ð¡»ð¼ý
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		//ÒÆ³ýÐ¡»ð¼ý
		mRocketToast.hiden();
		super.onDestroy();
	}

}
