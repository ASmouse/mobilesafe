package com.example.mobliesafe.activity;

import com.example.mobliesafe.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * @author jacksonCao
 * @data 2016-7-12
 * @desc 第一个设置向导界面

 * @version  $Rev: 7 $
 * @author  $Author: caojun $
 * @Date  $Date: 2016-07-12 20:02:45 +0800 (周二, 12 七月 2016) $
 * @Id	$ID$
 * @Url  $URL: https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example/mobliesafe/activity/Setup1Activity.java $
 */
public class Setup1Activity extends BaseSetupActivity{
	
	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_setup1);
	}

	@Override
	protected void startNext() {
		startPage(Setup2Activity.class);
	}
}
