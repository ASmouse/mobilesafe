package com.example.mobliesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {
	/**判断服务是否正在运行
	 * @param context
	 * @param serviceName
	 * @return
	 */
	public static boolean isServiceRunning(Context context,String serviceName){
		boolean res = false;
		//Activitymanager    Context.ACTIVITY_SERVICE
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取系统中所有运行的服务
		List<RunningServiceInfo> runningServices = am.getRunningServices(100);
		for (RunningServiceInfo rs : runningServices) {
			String name = rs.service.getClassName();
			if(name.equals(serviceName)){
				res = true;
				break;
			}
			
		}
		
		return res;
	}
}
