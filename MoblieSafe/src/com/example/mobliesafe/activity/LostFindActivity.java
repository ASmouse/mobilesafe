package com.example.mobliesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.example.mobliesafe.utils.MyContains;
import com.example.mobliesafe.utils.SPUtils;

/**
 * @author jacksonCao
 * @data 2016-7-12
 * @desc 手机防盗界面
 
 * @version  $Rev: 7 $
 * @author  $Author: caojun $
 * @Date  $Date: 2016-07-12 20:02:45 +0800 (周二, 12 七月 2016) $
 * @Id	$ID$
 * @Url  $URL: https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example/mobliesafe/activity/LostFindActivity.java $
 */
public class LostFindActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		//进来先判断是否设置向导完成
		if(SPUtils.getBoolean(getApplicationContext(), MyContains.ISSETUPFINISH, false)){
			//设置向导完成
			initView();
		}else{
			//设置向导未完成,进入第一个设置向导界面
			Intent intent = new Intent(this,Setup1Activity.class);
			startActivity(intent);
			
			//关闭自己,防止利用任务栈来返回到这个界面
			finish();
		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		
	}
}
