package com.example.mobliesafe.activity;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.example.mobliesafe.R;
import com.example.mobliesafe.receiver.MyDeviceAdminReceiverr;
import com.example.mobliesafe.service.LostFindService;
import com.example.mobliesafe.utils.MyContains;
import com.example.mobliesafe.utils.SPUtils;
import com.example.mobliesafe.utils.ServiceUtils;
import com.example.mobliesafe.utils.ShowToast;

/**
 * @author jacksonCao
 * @data 2016-7-12
 * @desc 第一个设置向导界面
 * 
 * @version $Rev: 10 $
 * @author $Author: caojun $
 * @Date $Date: 2016-07-15 17:32:25 +0800 (周五, 15 七月 2016) $
 * @Id $ID$
 * @Url $URL:
 *      https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example
 *      /mobliesafe/activity/Setup4Activity.java $
 */
public class Setup4Activity extends BaseSetupActivity {
	private CheckBox cb_isopenlostfind;
	private TextView tv_showstate;
	private DevicePolicyManager mPDM;
	private ComponentName mCN;

	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_setup4);
		cb_isopenlostfind = (CheckBox) findViewById(R.id.cb_setup4_isopenlostfind);
		tv_showstate = (TextView) findViewById(R.id.tv_setup4_showstate);

		mPDM = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
		mCN = new ComponentName(getApplicationContext(),
				MyDeviceAdminReceiverr.class);

	}

	@Override
	protected void initData() {
		// 初始化复选框的状态
		if (ServiceUtils.isServiceRunning(getApplicationContext(),
				"com.example.mobliesafe.service.LostFindService")) {
			cb_isopenlostfind.setChecked(true);
		} else {
			cb_isopenlostfind.setChecked(false);
		}

		super.initData();
	}

	@Override
	protected void initEvent() {
		// 添加复选框状态改变事件
		cb_isopenlostfind
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					private Intent service;

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						// 状态变化相应的结果
						// 1.文字
						if (isChecked) {

							// 是否已经激活设备管理器
							if (mPDM.isAdminActive(mCN)) {
								// 已经激活
								tv_showstate.setText("手机防盗已经开启");
								// 开启防盗服务
								service = new Intent(Setup4Activity.this,
										LostFindService.class);
								startService(service);

							} else {
								// 未激活
								// 开启设备管理器
								startDeviceManager();
							}

							// 初始化状态值不应该保存在sp中,而是应该动态判断服务是否运行(这样数据才能正确,不怕服务突然停止造成初始化错误)
							// 如果系统中正在运行的服务中有当前的服务,说明已经开启,复选框勾上,写个utils来实现此功能
							// 动态: ActivityManager 静态:PackageManager

						} else {
							tv_showstate.setText("手机防盗已经关闭");
							// 关闭防盗服务
							if(service != null){
								stopService(service);
							}
						}

					}

				});
		super.initEvent();
	}

	private void startDeviceManager() {
		Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
		intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mCN);
		intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
				"开启后便可启用防盗功能呦~");
		startActivityForResult(intent, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (mPDM.isAdminActive(mCN)) {

			// 已经激活
			tv_showstate.setText("手机防盗已经开启");
			// 开启防盗服务
			Intent service = new Intent(Setup4Activity.this,
					LostFindService.class);
			startService(service);
		} else {
			//未激活
		
			cb_isopenlostfind.setChecked(false);
			Toast.makeText(getApplicationContext(), "必须激活设备管理器才能继续!", 1).show();
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void startNext() {
		// 设置完成
		if (!cb_isopenlostfind.isChecked()) {
			// 提醒必须勾选
			ShowToast.show("请先勾选开启防盗", this);
		} else {
			// 设置完成
			// 保存设置完成的状态
			SPUtils.putBoolean(getApplicationContext(),
					MyContains.ISSETUPFINISH, true);
			// 添加系统启动 自动开启防盗服务
			SPUtils.putBoolean(getApplicationContext(),
					MyContains.BOOTCOMPLETE, true);
			startPage(LostFindActivity.class);
		}

	}

	@Override
	protected void startPrev() {
		startPage(Setup3Activity.class);
	}
}
