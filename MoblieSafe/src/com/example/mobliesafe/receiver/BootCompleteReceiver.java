package com.example.mobliesafe.receiver;

import com.example.mobliesafe.service.LostFindService;
import com.example.mobliesafe.utils.MyContains;
import com.example.mobliesafe.utils.SPUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.TelephonyManager;
import android.telephony.SmsManager;


/**
 * @author jacksonCao
 * @data 2016-7-14
 * @desc 系统启动完成的广播监听

 * @version  $Rev: 9 $
 * @author  $Author: caojun $
 * @Date  $Date: 2016-07-15 13:16:31 +0800 (周五, 15 七月 2016) $
 * @Id	$ID$
 * @Url  $URL: https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example/mobliesafe/receiver/BootCompleteReceiver.java $
 */
public class BootCompleteReceiver extends BroadcastReceiver{

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		//系统启动完成
		
		//1.检测SIM是否变更
		//获取当前的SIM卡和保存的SIM卡比较
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String simSerialNumber = tm.getSimSerialNumber();
		if(simSerialNumber.equals(SPUtils.getString(context, MyContains.SIMNUMBER, ""))){
			
		} else{
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(SPUtils.getString(context, MyContains.SAFENUMBER, ""), null, "SIM Info had been changed!", null, null);
		}
		
		//2.若果打开了防盗保护,开启时启动防盗保护的服务
		if(SPUtils.getBoolean(context, MyContains.BOOTCOMPLETE, false)){
			//启动服务
			Intent service = new Intent(context,LostFindService.class);
			context.startService(service);
		}
		
	}
	
	
}

