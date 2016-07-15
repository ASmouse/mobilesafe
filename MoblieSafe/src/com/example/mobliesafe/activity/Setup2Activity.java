package com.example.mobliesafe.activity;

import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.mobliesafe.R;
import com.example.mobliesafe.utils.MyContains;
import com.example.mobliesafe.utils.SPUtils;

/**
 * @author jacksonCao
 * @data 2016-7-12
 * @desc 第一个设置向导界面
 * 
 * @version $Rev: 8 $
 * @author $Author: caojun $
 * @Date $Date: 2016-07-13 09:42:58 +0800 (周三, 13 七月 2016) $
 * @Id $ID$
 * @Url $URL:
 *      https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example
 *      /mobliesafe/activity/Setup2Activity.java $
 */
public class Setup2Activity extends BaseSetupActivity {
	private ImageView iv_bindSim;

	@Override
	protected void initView() {
		setContentView(R.layout.activity_setup2);
		iv_bindSim = (ImageView) findViewById(R.id.iv_setp2_bindSim);

	}
	//初始化数据,防止返回上一页再回到本页,图片不正确(每次跳转页面时都会销毁当前页面,若不设置则会显示默认的页面)
	@Override
	protected void initData() {
		if(isSimBinded()){
			iv_bindSim.setImageResource(R.drawable.lock);
		}else{
			iv_bindSim.setImageResource(R.drawable.unlock);
		}
	}
	
	
	public void bindSIM(View v){
		TelephonyManager tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		String number = tm.getSimSerialNumber();
		
		if(!isSimBinded()){
			//变成绑定
			SPUtils.putString(getApplicationContext(), MyContains.SIMNUMBER, number);
			iv_bindSim.setImageResource(R.drawable.lock);
			
			
		}else{
			//变成未绑定
			//这里只能用"",因为null也为字符串的一种值!!!!!!!!
			SPUtils.putString(getApplicationContext(), MyContains.SIMNUMBER,"");

			iv_bindSim.setImageResource(R.drawable.unlock);
		}
	}

	private boolean isSimBinded() {
		//null,"" 无所谓
		String simStr = SPUtils.getString(getApplicationContext(), MyContains.SIMNUMBER, "");
		if(TextUtils.isEmpty(simStr)){
			return false;
		}else{
			return true;
		}
	}
	//不绑定Sim卡不让进入下一页
	@Override
	protected void startNext() {

		if(isSimBinded()){
			startPage(Setup3Activity.class);
		}else{
			Toast.makeText(getApplicationContext(), "SIM必须绑定!", 0).show();
		}
	}

	@Override
	protected void startPrev() {
		startPage(Setup1Activity.class);
	}
}
