package com.example.mobliesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mobliesafe.R;
import com.example.mobliesafe.view.SettingCenterItem;
import com.example.mobliesafe.view.SettingCenterItem.OnToggleChangedListener;

public class AToolActivity extends Activity {
	private SettingCenterItem sci_phonelocation;
	private SettingCenterItem sci_phoneservice;
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
				switch (v.getId()) {
				case R.id.sci_atool_phonelocation:
					Intent intent = new Intent(AToolActivity.this,PhoneLocationActivity.class);
					startActivity(intent);
					
					break;
					
					
				case R.id.sci_atool_phoneservice:
					Intent numberService = new Intent(AToolActivity.this,ServiceNumberActivity.class);
					startActivity(numberService);
					
					break;
				
				}
			}
		};
		
		sci_phonelocation.setOnToggleChangedListener(listener);
		sci_phoneservice.setOnToggleChangedListener(listener);
		
	}

	private void initData() {
		// TODO Auto-generated method stub
		
	}

	private void initView() {
		setContentView(R.layout.activity_atools);
		sci_phonelocation = (SettingCenterItem) findViewById(R.id.sci_atool_phonelocation);
		sci_phoneservice = (SettingCenterItem) findViewById(R.id.sci_atool_phoneservice);

	}
}
