package com.example.mobliesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.mobliesafe.R;
import com.example.mobliesafe.service.BlackService;
import com.example.mobliesafe.utils.MyContains;
import com.example.mobliesafe.utils.SPUtils;
import com.example.mobliesafe.utils.ServiceUtils;
import com.example.mobliesafe.view.SettingCenterItem;
import com.example.mobliesafe.view.SettingCenterItem.OnToggleChangedListener;

public class SettingCenterActivity extends Activity {
	private RelativeLayout rl_autoupdata;
	private RelativeLayout rl_blackinterrupt;
	private ImageView iv_autoupdata;
	private ImageView iv_blackinterrupt;
	private SettingCenterItem view_blackinterrput;
	private SettingCenterItem view_autoupdata;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initView();
		initData();
		initEvent();
	}

	private void initEvent() {
		OnToggleChangedListener listener = new OnToggleChangedListener() {

			@Override
			public void onToggleChange(View v, boolean isOpen) {
		System.out.println(v.getId());
				switch (v.getId()) {
				case R.id.view_autoupdata:
					System.out.println("view_autoupdata");
					SPUtils.putBoolean(getApplicationContext(),
							MyContains.AUTOUPDATE, isOpen);

					break;
				case R.id.view_blackinterrput:
					System.out.println("view_blackinterrput");
					if (isOpen) {
						Intent service = new Intent(SettingCenterActivity.this,
								BlackService.class);
						startService(service);
					} else {
						Intent service = new Intent(SettingCenterActivity.this,
								BlackService.class);
						stopService(service);
					}

					break;

				}
			}

		
		};

		view_autoupdata.setOnToggleChangedListener(listener);
		view_blackinterrput.setOnToggleChangedListener(listener);
		
		
		/*view_autoupdata.setOnToggleChangedListener(new OnToggleChangedListener() {
		
			@Override
			public void onToggleChange(View v, boolean isOpen) {
				// TODO Auto-generated method stub
				SPUtils.putBoolean(getApplicationContext(),
						MyContains.AUTOUPDATE, isOpen);
			}
		});
		
		
		view_blackinterrput.setOnToggleChangedListener(new OnToggleChangedListener() {
			
			@Override
			public void onToggleChange(View v, boolean isOpen) {
				// TODO Auto-generated method stub
				if (isOpen) {
					Intent service = new Intent(SettingCenterActivity.this,
							BlackService.class);
					startService(service);
				} else {
					Intent service = new Intent(SettingCenterActivity.this,
							BlackService.class);
					stopService(service);
				}
			}

			
		});
		*/
		System.out.println("initEventSuccess");
	}

	private void initData() {

		view_autoupdata.setToggleOn(SPUtils.getBoolean(getApplicationContext(),
				MyContains.AUTOUPDATE, false));
		view_blackinterrput.setToggleOn(ServiceUtils.isServiceRunning(
				getApplicationContext(),
				"com.example.mobliesafe.service.BlackService"));
	
		System.out.println("initDataSuccess");
	}

	private void initView() {
		setContentView(R.layout.activity_centersetting);
		view_blackinterrput = (SettingCenterItem) findViewById(R.id.view_blackinterrput);
		view_autoupdata = (SettingCenterItem) findViewById(R.id.view_autoupdata);
	}
}
