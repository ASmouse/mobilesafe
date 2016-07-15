package com.example.mobliesafe.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * @author jacksonCao
 * @data 2016-7-14
 * @desc 土司的封装类

 * @version  $Rev: 9 $
 * @author  $Author: caojun $
 * @Date  $Date: 2016-07-15 13:16:31 +0800 (周五, 15 七月 2016) $
 * @Id	$ID$
 * @Url  $URL: https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example/mobliesafe/utils/ShowToast.java $
 */
public class ShowToast {
	/**上下文改成Activity类型, 为了使用runOnUIThread
	 * @param msg
	 * @param context 
	 */
	public static void show(final String msg, final Activity context){
		//是否是主线程
		/*if(Thread.currentThread().getName().equals("main")){
			Toast.makeText(context, msg, 1).show();
			
		}else{
			context.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Toast.makeText(context, msg, 1).show();
				}
			});
		}*/
		
		context.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(context, msg, 1).show();
			}
		});
	}
}
