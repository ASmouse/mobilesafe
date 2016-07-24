package com.example.mobliesafe.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mobliesafe.R;
import com.example.mobliesafe.utils.SmsUtils;
import com.example.mobliesafe.utils.SmsUtils.OnProgressListener;
import com.example.mobliesafe.view.SettingCenterItem;
import com.example.mobliesafe.view.SettingCenterItem.OnToggleChangedListener;

public class AToolActivity extends Activity {
	private SettingCenterItem sci_phonelocation;
	private SettingCenterItem sci_phoneservice;
	private SettingCenterItem sci_smsback;
	private SettingCenterItem sci_smsrestore;

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
					Intent intent = new Intent(AToolActivity.this,
							PhoneLocationActivity.class);
					startActivity(intent);

					break;

				case R.id.sci_atool_phoneservice:
					Intent numberService = new Intent(AToolActivity.this,
							ServiceNumberActivity.class);
					startActivity(numberService);

					break;

				case R.id.sci_atool_smsback:
					smsBack();
					break;

				case R.id.sci_atool_smsrestore:
					smsRestore();
					break;

				}
			}

		
		};

		sci_phonelocation.setOnToggleChangedListener(listener);
		sci_phoneservice.setOnToggleChangedListener(listener);
		sci_smsback.setOnToggleChangedListener(listener);
		sci_smsrestore.setOnToggleChangedListener(listener);

	}

	
	protected void smsRestore() {

		final ProgressDialog pb = new ProgressDialog(this);
		pb.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		SmsUtils.smsRestore(this,new OnProgressListener() {
			
			@Override
			public void show() {
				pb.show();
			}
			
			@Override
			public void setProgress(int progress) {
				// TODO Auto-generated method stub
				pb.setProgress(progress);
			}
			
			@Override
			public void setMax(int max) {
				// TODO Auto-generated method stub
				pb.setMax(max);
			}
			
			@Override
			public void dismiss() {
				// TODO Auto-generated method stub
				pb.dismiss();
			}
		});
		
	}

	private void smsBack() {
		
		final ProgressDialog pb = new ProgressDialog(this);
		pb.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		SmsUtils.smsBack(this,new OnProgressListener() {
			
			@Override
			public void show() {
				pb.show();
			}
			
			@Override
			public void setProgress(int progress) {
				// TODO Auto-generated method stub
				pb.setProgress(progress);
			}
			
			@Override
			public void setMax(int max) {
				// TODO Auto-generated method stub
				pb.setMax(max);
			}
			
			@Override
			public void dismiss() {
				// TODO Auto-generated method stub
				pb.dismiss();
			}
		});
		
	}
	
	private void initData() {

	}

	private void initView() {
		setContentView(R.layout.activity_atools);
		sci_phonelocation = (SettingCenterItem) findViewById(R.id.sci_atool_phonelocation);
		sci_phoneservice = (SettingCenterItem) findViewById(R.id.sci_atool_phoneservice);
		sci_smsback = (SettingCenterItem) findViewById(R.id.sci_atool_smsback);
		sci_smsrestore = (SettingCenterItem) findViewById(R.id.sci_atool_smsrestore);
	}
}
