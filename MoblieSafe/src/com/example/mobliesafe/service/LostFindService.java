package com.example.mobliesafe.service;

import java.util.List;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.VideoView;


import com.example.mobliesafe.R;
import com.example.mobliesafe.location.CaculateRealPosition;
import com.example.mobliesafe.location.ModifyOffset;
import com.example.mobliesafe.receiver.MyDeviceAdminReceiverr;
import com.example.mobliesafe.utils.MyContains;

public class LostFindService extends Service{

	private SmsReceiver receiver;
	private DevicePolicyManager mPDM;
	private ComponentName mCN;
	private LocationManager lm;
	private String type;

	

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	//!!!!!!!!!!
	//短信的广播接受者,动态注册的可以写成内部类
	private class SmsReceiver extends BroadcastReceiver{
		private boolean isPlaying = false; //音乐播放标记
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//获取短信
			Object[]  smsDatas= (Object[]) intent.getExtras().get("pdus");
			for (Object data : smsDatas) {
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) data);
				String body = smsMessage.getDisplayMessageBody();
				//根据短信内容进行拦截
				if(body.equals("#*music*#")){
					abortBroadcast();//停止广播传递
					//防止多重播放混音
					if(!isPlaying){
					
						MediaPlayer mp =MediaPlayer.create(getApplicationContext(), R.raw.qqqg);
							//监听音乐播放完毕事件
							mp.setOnCompletionListener(new OnCompletionListener() {
								
								@Override
								public void onCompletion(MediaPlayer mp) {
									//播放完毕
									isPlaying = false;
								}
							});
							
							mp.start();
							isPlaying =true;
					
					
					}
					
				}else if(body.equals("#*gps*#")){
					
					getLocation();
					
					
					abortBroadcast();//停止广播传递
				}else if(body.equals("#*wipedata*#")){
					//清除sd卡上的数据
					mPDM.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
					
					abortBroadcast();//停止广播传递
				}else if(body.equals("#*lockscreen*#")){
					mPDM.lockNow();
					//重设密码
					mPDM.resetPassword("63917012", DevicePolicyManager.RESET_PASSWORD_REQUIRE_ENTRY);
					abortBroadcast();//停止广播传递
					
			
			}
		}
		
	}
	
	}
	
	
	private void getLocation() {
		String provider = null;	
		type = null;
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> allProviders = lm.getAllProviders();
		
		for (String prov : allProviders) {
			System.out.println(prov);
		}
		if(allProviders.contains(LocationManager.GPS_PROVIDER)){
			//gps
			provider = LocationManager.GPS_PROVIDER;
			type = "GPS";
		}else if(allProviders.contains(LocationManager.NETWORK_PROVIDER)){
			//wifi
			provider = LocationManager.NETWORK_PROVIDER;
			type = "wifi";
		}else if (allProviders.contains(LocationManager.PASSIVE_PROVIDER)){
			//数据
			type = "移动数据";
			provider = LocationManager.PASSIVE_PROVIDER;
		}
		
	/*	Location  lastKnowLocation= lm.getLastKnownLocation(provider);
		
		sendLocationInfo(lastKnowLocation);*/
		
		lm.requestLocationUpdates(provider, 0, 0, new LocationListener() {
			
			@Override
			public void onStatusChanged(String provider, int status, Bundle extras) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderEnabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProviderDisabled(String provider) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onLocationChanged(Location location) {
				//位置改变调用
				
				sendLocationInfo(location);
				
				lm.removeUpdates(this);
			
			}
			
			
		});	
	}
	private void sendLocationInfo(Location location) {
		StringBuffer sb = new StringBuffer(); 
		float accuracy = location.getAccuracy();
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		double altitude = location.getAltitude();
		sb.append("定位方式"+type+"\n").append("火星坐标:\n").append("精度值:"+accuracy+"\n").append("纬度值:"+latitude+"\n").append("精度值:"+longitude+"\n").append("海拔:"+altitude+"\n");
	
		//转换坐标为真实坐标
		String realLocation = CaculateRealPosition.getRealLocation(latitude, longitude);
		sb.append(realLocation);
		
		//发送短信
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendTextMessage(MyContains.SAFENUMBER, null, sb+"", null, null);
		
		
	}
	@Override
	public void onCreate() {
		// 第一次初始化
		super.onCreate();
		// 服务开启注册短信的拦截广播,代码注册广播优先级高,(另外:开启放到保护包含了拦截短信读取内容)
		receiver = new SmsReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
		//优先级,发送短信最先拦截
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(receiver, filter);
		
		mPDM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mCN = new ComponentName(this, MyDeviceAdminReceiverr.class);
		
	}
	
	
	@Override
	public void onDestroy() {
		// 服务销毁
		unregisterReceiver(receiver);
		super.onDestroy();
	}

}
