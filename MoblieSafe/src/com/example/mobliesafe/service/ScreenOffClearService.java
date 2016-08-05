package com.example.mobliesafe.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

import com.example.mobliesafe.domain.AppInfo;
import com.example.mobliesafe.utils.TaskInfoUtils;

public class ScreenOffClearService extends Service{

	private ScreenOffReceiver mReceiver;
	private ActivityManager mAM;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		mReceiver = new ScreenOffReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(mReceiver, filter);
		
		mAM = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		
	}
	
	private class ScreenOffReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			
			List<AppInfo> allRunningApps = TaskInfoUtils.getAllRunningApps(getApplicationContext());
			for (AppInfo appInfo : allRunningApps) {
				mAM.killBackgroundProcesses(appInfo.getPackName());
			}
		}
		
		
	} 
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	
		unregisterReceiver(mReceiver);
	}
	

}
