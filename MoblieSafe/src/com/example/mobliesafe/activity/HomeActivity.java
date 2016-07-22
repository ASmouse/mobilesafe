package com.example.mobliesafe.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobliesafe.R;
import com.example.mobliesafe.utils.Md5Utils;
import com.example.mobliesafe.utils.MyContains;
import com.example.mobliesafe.utils.SPUtils;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * @author jacksonCao
 * @data 2016-7-10
 * @desc 手机卫士的主界面
 * 
 * @version $Rev: 14 $
 * @author $Author: caojun $
 * @Date $Date: 2016-07-22 19:23:04 +0800 (周五, 22 七月 2016) $
 * @Id $ID$
 * @Url $URL:
 *      https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example
 *      /mobliesafe/activity/HomeActivity.java $
 */
public class HomeActivity extends Activity {
	private ImageView iv_logo;
	private ImageView iv_setting;
	private GridView gv_tool;

	private static final String[] names = new String[] { "手机防盗", "通讯卫士",
			"软件管家", "进程管理", "流量统计", "病毒查杀", "缓存清理", "高级工具" };
	private static final String[] descs = new String[] { "手机丢失好找", "防骚扰防监听",
			"方便管理软件", "保持手机流程", "统计流量信息", "手機安全保障", "手機快步運行", "特性處理更好" };
	private static final int[] icons = new int[] { R.drawable.sjfd,
			R.drawable.srlj, R.drawable.rjgj, R.drawable.jcgl, R.drawable.lltj,
			R.drawable.sjsd, R.drawable.hcql, R.drawable.szzx };
	private AlertDialog mAd;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		initView();
		// 开始动画
		initAnimaton();
		// 设置数据
		initDate();

		initEvent();

	}

	private void initEvent() {
		// 给gridView添加单击事件,注意是ItemClickListener
		gv_tool.setOnItemClickListener(new OnItemClickListener() {
			// arg2 == position

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				switch (arg2) {
				case 0:// 手机防盗
						// 首次使用显示设置密码的对话框,已经设置过密码显示输入密码的对话框
					String password = SPUtils.getString(
							getApplicationContext(), MyContains.PASSWORD, "");
					// TextUtils.isEmpty()已经判断了是null 或 "";
					if (TextUtils.isEmpty(password)) {
						// 未设置过密码
						// 显示设置密码的对话框
						showSetPasswordDialog();
					} else {
						// 输入密码的对话框
						showEnterPasswodDialog();
					}

					break;

				case 1:// 黑名单管理
					Intent intent = new Intent(HomeActivity.this,
							WebBlackActivity.class);
					startActivity(intent);

					break;

				case 7:// 高级工具
					Intent intent1= new Intent(HomeActivity.this,
							AToolActivity.class);
					startActivity(intent1);

					break;

				default:
					break;
				}
			}

		});

		iv_setting.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this,
						SettingCenterActivity.class);
				startActivity(intent);
			}
		});

	}

	/**
	 * 输入密码的对话框
	 * 
	 */
	private void showEnterPasswodDialog() {
		showMyDialog(false);
	}

	/**
	 * 设置密码的对话框
	 * 
	 */
	private void showSetPasswordDialog() {
		showMyDialog(true);
	}

	/**
	 * @param isSetPassword
	 *            true设置密码, false输入密码
	 */
	private void showMyDialog(final boolean isSetPassword) {

		AlertDialog.Builder ab = new Builder(this);
		View layout = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.dialog_setpassword, null);

		// 获取view子控件,一定要有layout.findViewById(),否则空指针异常
		final EditText et_password1 = (EditText) layout
				.findViewById(R.id.et_dialog_password1);
		final EditText et_password2 = (EditText) layout
				.findViewById(R.id.et_dialog_password2);
		final TextView tv_title = (TextView) layout
				.findViewById(R.id.tv_dialog_title);

		final Button btn_confirm = (Button) layout
				.findViewById(R.id.btn_dialog_confirm);
		final Button btn_cancel = (Button) layout
				.findViewById(R.id.btn_dialog_cancel);

		if (!isSetPassword) {
			// 输入密码对话框
			et_password2.setVisibility(View.GONE);
			tv_title.setText("输入密码");
		}

		// 需要的是View里的OnClickListener,而不是dialog的
		View.OnClickListener listener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 可以switch判断id,也可以判断对象
				if (v == btn_confirm) {
					// 先取2个密码
					String pass1 = et_password1.getText().toString().trim();
					String pass2 = et_password2.getText().toString().trim();

					if (!isSetPassword) {
						// 输入密码,小技巧
						pass2 = "aaa";
					}

					// 判断是否为空,已经包含了两种对话框
					if (TextUtils.isEmpty(pass2) || TextUtils.isEmpty(pass1)) {
						// 密码为空
						Toast.makeText(getApplicationContext(), "密码不能为空",
								Toast.LENGTH_SHORT).show();
						// !!!防止直接被dismiss掉
						return;
					} else {
						// 密码非空,再继续判断是哪个对话框,是否正确
						if (!isSetPassword) {
							// 输入密码对话框
							pass1 = Md5Utils.encode(pass1);
							pass2 = SPUtils.getString(getApplicationContext(),
									MyContains.PASSWORD, "");
							// MD5比較
							if (pass1.equals(pass2)) {
								// 输入密码密码一致

								Toast.makeText(getApplicationContext(), "登陆成功",
										Toast.LENGTH_SHORT).show();
								mAd.dismiss();
								// TODO 跳转界面
								Intent intent = new Intent(HomeActivity.this,
										LostFindActivity.class);
								startActivity(intent);
							} else {
								// 输入面膜密码不一致
								Toast.makeText(getApplicationContext(),
										"登录密码错误,请重新输入", Toast.LENGTH_SHORT)
										.show();
								et_password1.setText("");
							}
						} else {
							// 设置密码对话框
							if (pass1.equals(pass2)) {
								// 设置密码密码一致
								Toast.makeText(getApplicationContext(),
										"密码设置成功", Toast.LENGTH_SHORT).show();
								// 保存密码!!!!MD5
								SPUtils.putString(getApplicationContext(),
										MyContains.PASSWORD,
										Md5Utils.encode(pass1));
								// TODO 跳转界面
								Intent intent = new Intent(HomeActivity.this,
										LostFindActivity.class);
								startActivity(intent);
								mAd.dismiss();

							} else {
								// 设置密码密码不一致

								Toast.makeText(getApplicationContext(),
										"两次密码不一致,请重新输入", Toast.LENGTH_SHORT)
										.show();
								et_password1.setText("");
								et_password2.setText("");
							}

						}

					}
				} else if (v == btn_cancel) {
					// 取消,关闭对话框
					mAd.dismiss();
				}
			}
		};

		// 给两个按钮设置同一个监听事件
		btn_cancel.setOnClickListener(listener);
		btn_confirm.setOnClickListener(listener);

		ab.setView(layout); // 对话框设置自己的风格!!!!!!!
		/*
		 * Creates a AlertDialog with the arguments supplied to this builder. It
		 * does not Dialog.show() the dialog. This allows the user to do any
		 * extra processing before displaying the dialog. Use show() if you
		 * don't have any other processing to do and want this to be created and
		 * displayed.
		 */
		mAd = ab.create();
		mAd.show();
	}

	private void initDate() {
		// gridview显示数据
		MyAdapter adapter = new MyAdapter();
		gv_tool.setAdapter(adapter);
	}

	/**
	 * 对 logo添加旋转的动画,立体的旋转智能用属性动画(>11),为了兼容低版本,导入第三方JAR包---nineoldandroid(
	 * 2q1API与系统用法完全一样)
	 */
	private void initAnimaton() {
		// 属性动画完成logo旋转,第二个参数是利用反射的机制
		ObjectAnimator oa = ObjectAnimator.ofFloat(iv_logo, "rotationY", 0, 60,
				90, 120, 180, 240, 270, 300, 360);
		// 属性动画:对属性变化过程一系列操作组成的动画
		oa.setDuration(1000); // 一次完成动画的时间
		oa.setRepeatCount(ObjectAnimator.INFINITE); // -1
		// 播放动画
		oa.start();

	}

	private void initView() {
		setContentView(R.layout.activity_home);
		// 获取logo
		iv_logo = (ImageView) findViewById(R.id.iv_home_logo);
		// 设置的按钮
		iv_setting = (ImageView) findViewById(R.id.iv_home_setting);
		// gridview
		gv_tool = (GridView) findViewById(R.id.gv_home_tools);
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// 返回数据的个数
			return names.length;
		}

		@Override
		public Object getItem(int position) {

			return null;
		}

		@Override
		public long getItemId(int position) {

			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 界面转换成View
			View view = View.inflate(getApplicationContext(),
					R.layout.item_home_gv, null);
			// icon,注意R.id.~ 别写错,写错空指针异常.尤其是在数据多的时候不好检查,尤其注意
			ImageView iv_icon = (ImageView) view
					.findViewById(R.id.iv_item_home_gv_icon);
			// 标题
			TextView tv_title = (TextView) view
					.findViewById(R.id.tv_item_home_gv_title);
			// 描述信息
			TextView tv_desc = (TextView) view
					.findViewById(R.id.tv_item_home_gv_desc);

			// 赋值!!!
			iv_icon.setImageResource(icons[position]);
			tv_desc.setText(descs[position]);
			tv_title.setText(names[position]);
			return view;
		}

	}
}
