package com.example.mobliesafe.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

import com.example.mobliesafe.domain.AppInfo;

/**
 * @author jacksonCao
 * @data 2016-7-31
 * @desc 进程管理的工具类

 * @version  $Rev: 16 $
 * @author  $Author: caojun $
 * @Date  $Date: 2016-08-05 17:55:44 +0800 (周五, 05 八月 2016) $
 * @Id	$ID$
 * @Url  $URL: https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example/mobliesafe/utils/TaskInfoUtils.java $
 */
public class TaskInfoUtils {
	
	public static long getAvailMem(Context context){
		ActivityManager mAM = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		mAM.getMemoryInfo(outInfo);
		return outInfo.availMem;
	}
	
	
	public static long getTotalMem(Context context) {
		File file = new File("/proc/meminfo");
		try {
			BufferedReader bf = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String readLine = bf.readLine();
			String trim = readLine.substring(readLine.indexOf(':')+1, readLine.length()-2).trim();
			long result   =  Long.parseLong(trim)*1024;
			return result;
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	
	public static List<AppInfo> getAllRunningApps(Context context){
		List<AppInfo>  datas = new ArrayList<AppInfo>();
		
		ActivityManager mAM = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = mAM.getRunningAppProcesses();
		AppInfo bean = null;
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			bean = new AppInfo();
			//获取运行进程占用ram的大小
			android.os.Debug.MemoryInfo[] processMemoryInfo = mAM.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
			int privateDirty = processMemoryInfo[0].getTotalPrivateDirty();
			bean.setMemSize(privateDirty*1024);
			//获取包名
			bean.setPackName(runningAppProcessInfo.processName);
			//获取其他所有属性
			try {
				AppInfoUtils.getOthersAppInfo(context, bean);
				//出现异常不添加
				datas.add(bean);
				
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			
			
		}
		return datas;
	}
	
}
