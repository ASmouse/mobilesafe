package com.example.mobliesafe.activity;

import com.example.mobliesafe.R;

import android.app.Activity;

/**
 * @author jacksonCao
 * @data 2016-7-12
 * @desc 第一个设置向导界面

 * @version  $Rev: 7 $
 * @author  $Author: caojun $
 * @Date  $Date: 2016-07-12 20:02:45 +0800 (周二, 12 七月 2016) $
 * @Id	$ID$
 * @Url  $URL: https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example/mobliesafe/activity/Setup3Activity.java $
 */
public class Setup3Activity extends BaseSetupActivity{
	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_setup3);
	}

	@Override
	protected void startNext() {
		startPage(Setup4Activity.class);
	}
}
