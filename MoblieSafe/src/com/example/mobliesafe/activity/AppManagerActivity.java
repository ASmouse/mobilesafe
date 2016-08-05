package com.example.mobliesafe.activity;

import java.util.ArrayList;
import java.util.List;
/*import com.stericson.RootTools.RootTools;
import com.stericson.RootTools.RootToolsException;*/
import se.emilsjolander.stickylistheaders.StickyListHeadersAdapter;
import se.emilsjolander.stickylistheaders.StickyListHeadersListView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.example.mobliesafe.R;
import com.example.mobliesafe.domain.AppInfo;
import com.example.mobliesafe.utils.AppInfoUtils;
import com.example.mobliesafe.view.MessProgress;

public class AppManagerActivity extends Activity {
	protected static final int LOADING = 1;
	protected static final int FINISH = 2;
	private StickyListHeadersListView lv_datas;
	private TextView tv_romInfo;
	private MyAdapter mAdapter;
	private AppInfo clickedAppInfo;
	// 初始化数据
	private List<AppInfo> allInstalledAppInfos = new ArrayList<AppInfo>();
	// 系统App
	private List<AppInfo> systemAllInstalledAppInfos = new ArrayList<AppInfo>();
	// 用户App
	private List<AppInfo> userAllInstalledAppInfos = new ArrayList<AppInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initView();
		// initData();
		initEvent();
		initPopupWindow();

		registRemoveAppReceiver();

	}

	class RemovedApp extends BroadcastReceiver {

		@SuppressLint("ShowToast")
		@Override
		public void onReceive(Context context, Intent intent) {
			Toast.makeText(getApplicationContext(), clickedAppInfo.getAppName()
					+ "已卸载!", 1);
		}

	}

	private void registRemoveAppReceiver() {
		mReceiver = new RemovedApp();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
		registerReceiver(mReceiver, filter);

	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	private void initPopupWindow() {
		// TODO Auto-generated method stub
		View mPopupViewRoot = View.inflate(getApplicationContext(),
				R.layout.popupwindow_appmanager, null);

		LinearLayout ll_uninstall = (LinearLayout) mPopupViewRoot
				.findViewById(R.id.ll_appmanager_uninstall);
		LinearLayout ll_start = (LinearLayout) mPopupViewRoot
				.findViewById(R.id.ll_appmanager_start);
		LinearLayout ll_share = (LinearLayout) mPopupViewRoot
				.findViewById(R.id.ll_appmanager_share);
		LinearLayout ll_setting = (LinearLayout) mPopupViewRoot
				.findViewById(R.id.ll_appmanager_setting);
		// 点击事件
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.ll_appmanager_uninstall:
					// 卸载
					uninstall();
					break;
				case R.id.ll_appmanager_setting:
					// 设置
					setting();
					break;
				case R.id.ll_appmanager_share:
					// 分享
					share();
					break;
				case R.id.ll_appmanager_start:
					// 启动
					startApp();
					break;
				default:
					break;
				}
				mPW.dismiss();

			}
		};
		ll_uninstall.setOnClickListener(listener);
		ll_start.setOnClickListener(listener);
		ll_share.setOnClickListener(listener);
		ll_setting.setOnClickListener(listener);
		//

		mPW = new PopupWindow(mPopupViewRoot, -2, -2);
		// !!!!!!
		mPW.setFocusable(true);
		mPW.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mPW.setOutsideTouchable(true);
		// 设置动画

		mPW.setAnimationStyle(R.style.popAnimation);
	}

	private void initEvent() {
		lv_datas.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 记录点击的内容
				clickedAppInfo = allInstalledAppInfos.get(position);

				mPW.showAsDropDown(view, 50, -view.getHeight());

			}
		});
	}

	private void startApp() {
		// TODO Auto-generated method stub
		Intent launchIntentForPackage = getPackageManager()
				.getLaunchIntentForPackage(clickedAppInfo.getPackName());
		startActivity(launchIntentForPackage);
	}

	private void share() {
		ShareSDK.initSDK(this);
		 OnekeyShare oks = new OnekeyShare();
		 //关闭sso授权
		 oks.disableSSOWhenAuthorize(); 

		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		 oks.setTitle(getString(R.string.share));
		 // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		 oks.setTitleUrl("http://sharesdk.cn");
		 // text是分享文本，所有平台都需要这个字段
		 oks.setText("我是分享文本");
		 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		 //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		 // url仅在微信（包括好友和朋友圈）中使用
		 oks.setUrl("http://sharesdk.cn");
		 // comment是我对这条分享的评论，仅在人人网和QQ空间使用
		 oks.setComment("我是测试评论文本");
		 // site是分享此内容的网站名称，仅在QQ空间使用
		 oks.setSite(getString(R.string.app_name));
		 // siteUrl是分享此内容的网站地址，仅在QQ空间使用
		 oks.setSiteUrl("http://sharesdk.cn");

		// 启动分享GUI
		 oks.show(this);

	}

	private void setting() {
		// TODO Auto-generated method stub
		Intent setting = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
		setting.setData(Uri.parse("package:"+clickedAppInfo.getPackName()));
		startActivity(setting);
	}

	private void uninstall() {

		if (clickedAppInfo.isSystem()) {
			// 系统app
/*
			try {
				RootTools.sendShell("mount -o remount rw /system", 5000);
				RootTools.sendShell(
						"rm -r " + clickedAppInfo.getSourceDir(), 5000);
				RootTools.sendShell("mount -o remount r /system", 5000);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
		} else {
			// 用户app
			/*
			 * <intent-filter> <action android:name="android.intent.action.VIEW"
			 * /> <action android:name="android.intent.action.DELETE" />
			 * <category android:name="android.intent.category.DEFAULT" /> <data
			 * android:scheme="package" /> </intent-filter>
			 */

			Intent uninstall = new Intent("android.intent.action.DELETE");
			uninstall.addCategory("android.intent.category.DEFAULT");
			uninstall.setData(Uri.parse("package:"
					+ clickedAppInfo.getPackName()));
			startActivityForResult(uninstall, 0);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		initData();
		super.onResume();
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:
				ll_progress.setVisibility(View.VISIBLE);
				lv_datas.setVisibility(View.GONE);

				mpb_rom.setVisibility(View.GONE);
				mpb_sd.setVisibility(View.GONE);
				break;
			case FINISH:
				mpb_rom.setVisibility(View.VISIBLE);
				mpb_sd.setVisibility(View.VISIBLE);

				ll_progress.setVisibility(View.GONE);
				lv_datas.setVisibility(View.VISIBLE);

				long phoneAvialMem = AppInfoUtils.getPhoneAvailMem();
				long phoneTotalMem = AppInfoUtils.getPhoneTotalMem();

				long sdAvialMem = AppInfoUtils.getSDAvailSpace();
				long sdTotalMem = AppInfoUtils.getSDTotalSpace();

				String phoneAvial = Formatter.formatFileSize(
						getApplicationContext(), phoneAvialMem);
				String phoneTotal = Formatter.formatFileSize(
						getApplicationContext(), phoneTotalMem);

				String sdAvail = Formatter.formatFileSize(
						getApplicationContext(), sdAvialMem);
				String sdTotal = Formatter.formatFileSize(
						getApplicationContext(), sdTotalMem);
				/*
				 * tv_romInfo.setText(phoneTotal); tv_sdInfo.setText(sdTotal);
				 */
				// progressBar
				mpb_rom.setText(phoneAvial);
				mpb_sd.setText(sdAvail);

				mpb_sd.setProgress((sdTotalMem - sdAvialMem) * 1.0 / sdTotalMem);
				mpb_rom.setProgress((phoneTotalMem - phoneAvialMem) * 1.0
						/ phoneTotalMem);

				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		};
	};

	private LinearLayout ll_progress;
	private TextView tv_sdInfo;
	private MessProgress mpb_rom;
	private MessProgress mpb_sd;
	private PopupWindow mPW;
	private RemovedApp mReceiver;

	private void initData() {
		new Thread() {
			public void run() {
				mHandler.obtainMessage(LOADING).sendToTarget();

				// 数据
				SystemClock.sleep(1000);
				
				
				allInstalledAppInfos.clear();
				allInstalledAppInfos = AppInfoUtils
						.getAllInstalledAppInfos(getApplicationContext());
				userAllInstalledAppInfos.clear();
				systemAllInstalledAppInfos.clear();
				for (AppInfo appInfo : allInstalledAppInfos) {
					if (appInfo.isSystem()) {
						systemAllInstalledAppInfos.add(appInfo);
					} else {
						userAllInstalledAppInfos.add(appInfo);
					}
				}
				// 排序
				allInstalledAppInfos.clear();
				allInstalledAppInfos.addAll(userAllInstalledAppInfos);
				allInstalledAppInfos.addAll(systemAllInstalledAppInfos);

				mHandler.obtainMessage(FINISH).sendToTarget();

			};
		}.start();
	}

	private class MyAdapter extends BaseAdapter implements
			StickyListHeadersAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return allInstalledAppInfos.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		class ViewHolder {
			TextView tv_appname;
			TextView tv_applocation;
			TextView tv_appsize;
			ImageView iv_icon;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			/*
			 * //user TextView if(position == 0){ TextView tv_user = new
			 * TextView(getApplicationContext()); tv_user.setText("用户软件");
			 * tv_user.setBackgroundColor(Color.GRAY); tv_user.setTextSize(18);
			 * tv_user.setTextColor(Color.WHITE); //!!!!!!!十分重要的返回 return
			 * tv_user; } //system TextView if(position ==
			 * (userAllInstalledAppInfos.size()+1)){ TextView tv_system = new
			 * TextView(getApplicationContext()); tv_system.setText("系统软件");
			 * tv_system.setBackgroundColor(Color.GRAY);
			 * tv_system.setTextSize(18); tv_system.setTextColor(Color.WHITE);
			 * //!!!!!!!十分重要的返回 return tv_system; }
			 */

			// 正常的数据
			ViewHolder holder = null;
			// !!!!!!! -----> !(convertView instanceof TextView)
			if ((convertView != null) /* && !(convertView instanceof TextView) */) {
				holder = (ViewHolder) convertView.getTag();
			} else {
				// !!!! convertView
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_appmanager_lv, null);

				holder = new ViewHolder();
				holder.tv_applocation = (TextView) convertView
						.findViewById(R.id.tv_appmanager_lv_applocation);
				holder.tv_appname = (TextView) convertView
						.findViewById(R.id.tv_appmanager_lv_appname);
				holder.tv_appsize = (TextView) convertView
						.findViewById(R.id.tv_appmanager_lv_appsize);
				holder.iv_icon = (ImageView) convertView
						.findViewById(R.id.iv_appmanager_lv_icon);

				convertView.setTag(holder);
			}
			// 得到数据 suer ,system
			// !!!!!!不要单独一个一个的申请空间,直接申请AppInfo
			AppInfo appInfo = null;
			/*
			 * if(position <=userAllInstalledAppInfos.size()){ //用户的信息 appInfo =
			 * userAllInstalledAppInfos.get(position-1); }else { //因为当为TextView时
			 * 已经返回,故没有TextView这种情况 //系统软件的信息 appInfo =
			 * systemAllInstalledAppInfos
			 * .get(position-(userAllInstalledAppInfos.size()+2));
			 * 
			 * }
			 */

			appInfo = allInstalledAppInfos.get(position);

			// 设置数据
			holder.tv_appname.setText(appInfo.getAppName());
			holder.tv_applocation.setText(appInfo.isSD() ? "SD存储" : "ROM存储");
			holder.tv_appsize.setText(Formatter.formatFileSize(
					getApplicationContext(), appInfo.getSize()));
			holder.iv_icon.setImageDrawable(appInfo.getIcon());

			return convertView;
		}

		@Override
		public View getHeaderView(int position, View convertView,
				ViewGroup parent) {
			AppInfo appInfoBean = allInstalledAppInfos.get(position);
			TextView tv_usertag = new TextView(getApplicationContext());
			tv_usertag.setBackgroundColor(Color.GRAY);// 背景灰色
			tv_usertag.setTextColor(Color.WHITE);// 字体白色
			if (!appInfoBean.isSystem()) {
				tv_usertag.setText("用户软件(" + userAllInstalledAppInfos.size()
						+ ")");

			} else {
				tv_usertag.setText("系统软件(" + systemAllInstalledAppInfos.size()
						+ ")");
			}
			return tv_usertag;

		}

		@Override
		public long getHeaderId(int position) {
			AppInfo appInfoBean = allInstalledAppInfos.get(position);
			if (!appInfoBean.isSystem()) {

				return 1;
			} else {
				return 2;
			}
		}

	}

	private void initView() {
		setContentView(R.layout.activity_appmanager);
		/*
		 * tv_romInfo = (TextView) findViewById(R.id.tv_appmanager_rommeminfo);
		 * tv_sdInfo = (TextView) findViewById(R.id.tv_appmanager_sdmeminfo);
		 */
		lv_datas = (StickyListHeadersListView) findViewById(R.id.lv_appmanager_viewdatas);
		ll_progress = (LinearLayout) findViewById(R.id.ll_progressbar_root);

		mpb_rom = (MessProgress) findViewById(R.id.mpb_appmanager_rom);
		mpb_sd = (MessProgress) findViewById(R.id.mpb_appmanager_sd);

		mAdapter = new MyAdapter();
		lv_datas.setAdapter(mAdapter);
	}
}
