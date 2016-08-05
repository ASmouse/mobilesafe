package com.example.mobliesafe.activity;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobliesafe.R;
import com.example.mobliesafe.domain.AppInfo;
import com.example.mobliesafe.utils.AppInfoUtils;

public class CacheInfoActivity extends Activity {
	protected static final int STARTSCAN = 1;
	protected static final int SCANNING = 2;
	protected static final int FINISH = 0;
	private ImageView iv_scanner;
	private ProgressBar pb_scann;
	private TextView tv_result;
	private LinearLayout ll_cacheInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initAnimation();
		scanCache();
		
	}

	private void initAnimation() {

		mRa = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		mRa.setDuration(2);
		mRa.setRepeatCount(Animation.INFINITE);
	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case STARTSCAN:
				ll_cacheInfo.removeAllViews();
				ll_cacheInfo.setVisibility(View.VISIBLE);
				tv_result.setVisibility(View.GONE);
				iv_scanner.startAnimation(mRa);
				break;
			case SCANNING: {
				

				// 扫描中
				ScanInfo scanInfo = (ScanInfo) msg.obj;
				tv_name.setText("正在扫描" + scanInfo.appName);
				pb_scann.setMax(scanInfo.max);
				pb_scann.setProgress(scanInfo.progress);

				// 扫描过程添加view
				View view = View.inflate(getApplicationContext(),
						R.layout.item_antivirus_ll, null);
				TextView tv_name = (TextView) view
						.findViewById(R.id.tv_antivirus_ll_name);
				ImageView iv_icon = (ImageView) view
						.findViewById(R.id.iv_antivirus_ll_icon);
				ImageView iv_virus = (ImageView) view
						.findViewById(R.id.iv_antivirus_ll_isvirus);

				tv_name.setText(scanInfo.appName);
				iv_icon.setImageDrawable(scanInfo.icon);
				iv_virus.setVisibility(View.GONE);

				ll_cacheInfo.addView(view, 0);

				break;
			}
			case FINISH: {
				tv_name.setText("扫描完成");
				ll_cacheInfo.removeAllViews();
				if (infos.size() > 0) {
					Toast.makeText(getApplicationContext(),
							"扫描到缓存,点击右上方清理按钮进行清理", 0).show();
					// 有缓存显示确切的缓存信息
					for (final CacheInfo cacheInfo : infos) {
						View v = View.inflate(getApplicationContext(), R.layout.item_cacheinfo_ll, null);
						ImageView iv_icon = (ImageView) v.findViewById(R.id.iv_cahceinfo_ll_icon);
						TextView tv_name = (TextView) v.findViewById(R.id.tv_cacheinfo_ll_name);
						TextView tv_size = (TextView) v.findViewById(R.id.tv_cacheinfo_ll_size);
						
						iv_icon.setImageDrawable(cacheInfo.icon);
						tv_name.setText(cacheInfo.appName);
						tv_size.setText(Formatter.formatFileSize(getApplicationContext(), cacheInfo.cacheSize));
						
						ll_cacheInfo.addView(v,0);
						
						v.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
								intent.setData(Uri.parse("package:"+cacheInfo.packName));
								startActivity(intent);
							}
						});
					}
					

				} else {
					Toast.makeText(getApplicationContext(), "您的电脑暂无缓存", 0)
							.show();
					tv_result.setVisibility(View.VISIBLE);
					tv_result.setText("恭喜您,没有缓存需要清理");
				}

				break;
			}

			}
		}
	};

	private List<CacheInfo> infos = new Vector<CacheInfo>();

	class CacheInfo {
		long cacheSize;
		String appName;
		Drawable icon;
		String packName;
	}

	private RotateAnimation mRa;
	private TextView tv_name;

	class ScanInfo {
		Drawable icon;
		String appName;
		int max;
		int progress;
	}

	private void scanCache() {
		new Thread() {
			public void run() {
				mHandler.obtainMessage(STARTSCAN).sendToTarget();
				int progress = 0;
				List<AppInfo> allAppInfos = AppInfoUtils
						.getAllInstalledAppInfos(getApplicationContext());
				for (AppInfo appInfo : allAppInfos) {
					ScanInfo scanInfo = new ScanInfo();
					progress++;

					scanInfo.appName = appInfo.getAppName();
					scanInfo.icon = appInfo.getIcon();
					scanInfo.max = allAppInfos.size();
					scanInfo.progress = progress;
					// 发送信息
					Message msg = mHandler.obtainMessage(SCANNING);
					msg.obj = scanInfo;
					mHandler.sendMessage(msg);
					
					getCacheInfo(appInfo);
					
					SystemClock.sleep(200);

				}

				mHandler.obtainMessage(FINISH).sendToTarget();

			};
		}.start();

	}
	
	public void clearCache(View v){
		// public abstract void freeStorageAndNotify(long freeStorageSize, IPackageDataObserver observer);
		PackageManager mPm = getPackageManager();
		// 无法获取getPackageSizeInfo , 用反射
		class MyIPackageDataObserver extends IPackageDataObserver.Stub {

		

			@Override
			public void onRemoveCompleted(String packageName, boolean succeeded)
					throws RemoteException {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Toast.makeText(getApplicationContext(),	 "清理完毕", 1).show();
						ll_cacheInfo.removeAllViews();
						tv_result.setVisibility(View.VISIBLE);
						tv_result.setText("恭喜您,没有缓存需要清理");
					}
				});
			}
		};
		
		try {

			/*
			 * // 1.class Class type = mPM.getClass(); // 2.method // AUTO
			 * Method method = type.getDeclaredMethod("getPackageSizeInfo",
			 * String.class, IPackageStatsObserver.class); // 3.mPM // 4.invoke
			 * method.invoke(mPM, getPackageName(), mStatsObserver);
			 */
			Class type = mPm.getClass();
			// 2. method
			Method method = type.getDeclaredMethod("freeStorageAndNotify",
					long.class, IPackageDataObserver.class);
			// 3. mPm
			// 4. invoke
			method.invoke(mPm, Long.MAX_VALUE,
					new MyIPackageDataObserver());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("异常");
		}
		
		
	}
	
	
	
	private void initView() {
		setContentView(R.layout.activity_cacheinfo);
		iv_scanner = (ImageView) findViewById(R.id.iv_cacheinfo_scanner);
		tv_result = (TextView) findViewById(R.id.tv_cacheinfo_result);
		pb_scann = (ProgressBar) findViewById(R.id.pb_cacheinfo);

		ll_cacheInfo = (LinearLayout) findViewById(R.id.ll_cacheinfo_appinfo);
		tv_name = (TextView) findViewById(R.id.tv_cacheInfo_scanname);
	}

	private void getCacheInfo(final AppInfo bean) {
		//
		PackageManager mPm = getPackageManager();
		// 无法获取getPackageSizeInfo , 用反射
		class MyIPackageStatsObserver extends IPackageStatsObserver.Stub {

			@Override
			public void onGetStatsCompleted(PackageStats pStats,
					boolean succeeded) throws RemoteException {
				// 回调结果
				long cacheSize = pStats.cacheSize;
				if (pStats.cacheSize > 0) {
					// 查到缓存
					CacheInfo cacheInfo = new CacheInfo();
					cacheInfo.appName = bean.getAppName();
					cacheInfo.icon = bean.getIcon();
					cacheInfo.cacheSize = cacheSize;

					infos.add(cacheInfo);
				}
			}
		};
		
		try {

			/*
			 * // 1.class Class type = mPM.getClass(); // 2.method // AUTO
			 * Method method = type.getDeclaredMethod("getPackageSizeInfo",
			 * String.class, IPackageStatsObserver.class); // 3.mPM // 4.invoke
			 * method.invoke(mPM, getPackageName(), mStatsObserver);
			 */
			Class type = mPm.getClass();
			// 2. method
			Method method = type.getDeclaredMethod("getPackageSizeInfo",
					String.class, IPackageStatsObserver.class);
			// 3. mPm
			// 4. invoke
			method.invoke(mPm, bean.getPackName(),
					new MyIPackageStatsObserver());

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("异常");
		}
	}
}
