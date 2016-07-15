package com.example.mobliesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.mobliesafe.R;
import com.example.mobliesafe.utils.MyContains;
import com.example.mobliesafe.utils.SPUtils;

/**
 * @author jacksonCao
 * @data 2016-7-12
 * @desc 手机防盗界面
 
 * @version  $Rev: 9 $
 * @author  $Author: caojun $
 * @Date  $Date: 2016-07-15 13:16:31 +0800 (周五, 15 七月 2016) $
 * @Id	$ID$
 * @Url  $URL: https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example/mobliesafe/activity/LostFindActivity.java $
 */
public class LostFindActivity extends Activity{
	private TextView tv_safenumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		//直接进行逻辑判断 是否设置向导完成
		if (SPUtils.  getBoolean(getApplicationContext(), MyContains.ISSETUPFINISH, false)) {
			//设置向导完成
			initView();
			initData();
		} else {
			//设置向导未完成,进入第一个设置向导界面
			Intent intent = new Intent(this,Setup1Activity.class);
			startActivity(intent);
			//关闭自己
			finish();
			
		}
	}
	
	public void enterSetup1(View v){
		//进入第一个设置向导界面
		Intent setup1 = new Intent(this,Setup1Activity.class);
		startActivity(setup1);
		finish();
	}

	private void initData() {
		// 显示安全号码
		tv_safenumber.setText(SPUtils.getString(getApplicationContext(), MyContains.SAFENUMBER, ""));
		
	}

	/**
	 * 初始化界面
	 */
	private void initView() {
		setContentView(R.layout.activity_lostfind);
		
		tv_safenumber = (TextView) findViewById(R.id.tv_lostfind_safenumber);
		
	}
}
