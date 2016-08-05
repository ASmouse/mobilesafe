package com.example.mobliesafe.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;

import com.example.mobliesafe.domain.AppInfo;

/**
 * @author jacksonCao
 * @data 2016-7-26
 * @desc 软件管家的工具类  所有APP信息 sd可用内存 手机可用内存 

 * @version  $Rev: 16 $
 * @author  $Author: caojun $
 * @Date  $Date: 2016-08-05 17:55:44 +0800 (周五, 05 八月 2016) $
 * @Id	$ID$
 * @Url  $URL: https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example/mobliesafe/utils/AppInfoUtils.java $
 */
public class AppInfoUtils {
	
	public static int getTotalAppNum(Context context){
		PackageManager mPM = context.getPackageManager();
		List<ApplicationInfo> installedApplications = mPM.getInstalledApplications(0);
		return installedApplications.size();
	
	}
	
	/**
	 * @return 手机总空间
	 */
	public static long getPhoneTotalMem(){
		File file = Environment.getDataDirectory();
		return file.getTotalSpace();
		
	}
	
	/**
	 * @return  手机可用空间
	 */
	public static long getPhoneAvailMem(){
		File file = Environment.getDataDirectory();
		return file.getFreeSpace();
	}
	
	/**
	 * @return sd卡总空间
	 */
	public static long getSDTotalSpace(){
		File file = Environment.getExternalStorageDirectory();
		
		return file.getTotalSpace();
	}
	
	
	public static long getSDAvailSpace(){
		File  file = Environment.getExternalStorageDirectory();
		return file.getFreeSpace();
	}
	

	/**前提已经设置好包名
	 * @param context
	 * @param bean 
	 * @throws NameNotFoundException 
	 */
	public static void getOthersAppInfo(Context context,AppInfo appInfo) throws NameNotFoundException{
		PackageManager mPM = context.getPackageManager();
		PackageInfo packageInfo = mPM.getPackageInfo(appInfo.getPackName(), 0);
		ApplicationInfo applicationInfo = packageInfo.applicationInfo;
		//名字
		appInfo.setAppName(applicationInfo.loadLabel(mPM)+"");
		

		//路径
		appInfo.setSourceDir(applicationInfo.sourceDir);
		//图标
		appInfo.setIcon(applicationInfo.loadIcon(mPM));
		//安装文件的大小
		appInfo.setSize(new File(applicationInfo.sourceDir).length());
		//是否是系统App
		if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM)!=0){
			//系统APP
			appInfo.setSystem(true);
			
		}
		
		//安装位置
		if((applicationInfo.flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE)!=0){
			//安装在sd卡中
			appInfo.setSD(true);
		}
		
		
	}
	
	
	
	
	
	/**
	 * @param context
	 * @return 获取所有安装的app信息 
	 */
	public static List<AppInfo> getAllInstalledAppInfos(Context context){
		List<AppInfo> datas = new ArrayList<AppInfo>();
		
		PackageManager pm = context.getPackageManager();
		List<ApplicationInfo> installedApplications = pm.getInstalledApplications(0);
		AppInfo appInfo = null;
		for (ApplicationInfo applicationInfo : installedApplications) {
			appInfo = new AppInfo();
			//包名
			appInfo.setPackName(applicationInfo.packageName);
			
			try {
				getOthersAppInfo(context, appInfo);
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			datas.add(appInfo);
			
		}
		
		
		return datas;
	}
}
