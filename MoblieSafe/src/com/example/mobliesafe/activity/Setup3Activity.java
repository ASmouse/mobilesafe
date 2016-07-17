package com.example.mobliesafe.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.example.mobliesafe.R;
import com.example.mobliesafe.utils.MyContains;
import com.example.mobliesafe.utils.SPUtils;
import com.example.mobliesafe.utils.ShowToast;

/**
 * @author jacksonCao
 * @data 2016-7-12
 * @desc 第一个设置向导界面
 * 
 * @version $Rev: 12 $
 * @author $Author: caojun $
 * @Date $Date: 2016-07-17 19:50:22 +0800 (周日, 17 七月 2016) $
 * @Id $ID$
 * @Url $URL:
 *      https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example
 *      /mobliesafe/activity/Setup3Activity.java $
 */
public class Setup3Activity extends BaseSetupActivity {
	private EditText et_safenumber;

	@Override
	protected void initView() {
		super.initView();
		setContentView(R.layout.activity_setup3);
		// 安全号码的编辑框
		et_safenumber = (EditText) findViewById(R.id.et_setup3_safenumber);

	}

	@Override
	protected void initData() {
		super.initData();
		// 回显安全号码
		et_safenumber.setText(SPUtils.getString(getApplicationContext(),
				MyContains.SAFENUMBER, ""));
		// 设置光标停留的位置
		et_safenumber.setSelection(et_safenumber.getText().toString().length());
	}

	/**
	 * 设置安全号码的事件
	 * 
	 * @param v
	 */
	public void selectSafenumber(View v) {
		// 启动新的界面来显示所有好友信息
		Intent intent = new Intent(this, FriendActivity.class);
		// 点击某个好友,关闭界面,在安全号码编辑框显示选择的好友
		startActivityForResult(intent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 获取选择的好友
		// !!!!!!!!!防止空指针异常,加判断(直接关闭FriendActivity界面导致没有数据传递)!!!!!!!!!!!
		if (data != null) {
			String safeNumber = data.getStringExtra(MyContains.SAFENUMBER);
			// 显示在编辑框
			et_safenumber.setText(safeNumber);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void startNext() {
		// 添加获取保存安全号码
		String safenumber = et_safenumber.getText().toString().trim();
		if (TextUtils.isEmpty(safenumber)) {
			// 空
			ShowToast.show("安全号码不能为空", this);

		} else {
			// 有安全号码
			// 保存安全号码到sp中
			SPUtils.putString(getApplicationContext(), MyContains.SAFENUMBER,
					safenumber);
			startPage(Setup4Activity.class);
		}

	}

	@Override
	protected void startPrev() {
		startPage(Setup2Activity.class);
	}
}
