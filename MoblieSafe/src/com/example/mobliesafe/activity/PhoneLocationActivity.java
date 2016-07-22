package com.example.mobliesafe.activity;

import javax.crypto.spec.IvParameterSpec;

import android.app.Activity;
import android.location.Address;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mobliesafe.R;
import com.example.mobliesafe.dao.AddressDao;

public class PhoneLocationActivity extends Activity {
	private EditText et_number;
	private TextView tv_result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initView();

		initData();

		initEvent();
	}

	private void initView() {
		setContentView(R.layout.activity_phonelocation);

		et_number = (EditText) findViewById(R.id.et_phonelocation_number);
		tv_result = (TextView) findViewById(R.id.tv_phonelocation_result);
	}

	public void query(View v) {
		String number = et_number.getText().toString().trim();
		if (TextUtils.isEmpty(number)) {
			tv_result.setText("未知号码");
			// 抖动
			Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
			et_number.startAnimation(shake);

			// 震动
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(new long[] { 100, 100, 200, 200, 300, 300, 400,
					400 }, 3);
			return;
		}
		
		try {
			String location = AddressDao.getLocation(number);
			tv_result.setText("归属地:\n\t" + location);
		} catch (Exception e) {
			// 可能发生数组越界异常!!!!!!
		}

	}

	private void initEvent() {
		// 随时监听
		et_number.addTextChangedListener(new TextWatcher() {
			// 写那个方法都无所谓,效果都一样,可用syso测试
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				query(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void initData() {

	}
}
