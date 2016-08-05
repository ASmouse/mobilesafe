package com.example.mobliesafe.receiver;



import java.util.List;

import com.example.mobliesafe.domain.AppInfo;
import com.example.mobliesafe.utils.TaskInfoUtils;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class WidgetClearTaskReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//清理进程
		List<AppInfo> allRunningAppInfos = TaskInfoUtils.getAllRunningApps(context);
		for (AppInfo appInfoBean : allRunningAppInfos) {
			am.killBackgroundProcesses(appInfoBean.getPackName());
		}
		System.out.println("清理进程widget");
	}
	

}
