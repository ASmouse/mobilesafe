package com.example.mobliesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.text.format.Formatter;
import android.widget.RemoteViews;

import com.example.mobliesafe.R;
import com.example.mobliesafe.receiver.MyAppWidgetProvider;
import com.example.mobliesafe.utils.TaskInfoUtils;

public class TaskWidgetService extends Service{
	private AppWidgetManager mAWM;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mAWM = AppWidgetManager.getInstance(getApplicationContext());
		System.out.println("widget  service create");
		
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
			
			@Override
			public void run() {
				updateWidgetMessage();
			}

		
		};
		timer.schedule(task, 0,2000);
	}
	private void updateWidgetMessage() {
		// TODO Auto-generated method stub
		ComponentName provider = new ComponentName(getApplicationContext(), MyAppWidgetProvider.class);
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.process_widget);
		views.setTextViewText(R.id.tv_process_count, "运行中的软件:" + TaskInfoUtils.getAllRunningApps(getApplicationContext()).size());
		views.setTextViewText(R.id.tv_process_memory, "可用内存:" + Formatter.formatFileSize(getApplicationContext(),
				TaskInfoUtils.getAvailMem(getApplicationContext())));
		
		Intent intent = new Intent();
		intent.setAction("widget.clear.task");
		PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent , 0);
		views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent );
		// 更新widget界面
		mAWM.updateAppWidget(provider, views);
	}
}
