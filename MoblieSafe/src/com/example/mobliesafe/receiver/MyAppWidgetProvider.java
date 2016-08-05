package com.example.mobliesafe.receiver;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;

import com.example.mobliesafe.service.TaskWidgetService;

public class MyAppWidgetProvider extends  AppWidgetProvider {
	@Override
	public void onEnabled(Context context) {
		// 第一次创建执行
		// 服务监控进程状态
		Intent service = new Intent(context,TaskWidgetService.class);
		context.startService(service);
		super.onEnabled(context);
	}

	@Override
	public void onDisabled(Context context) {
		//删除最后一个执行
		Intent service = new Intent(context,TaskWidgetService.class);
		context.stopService(service);
		super.onDisabled(context);
	}
}
