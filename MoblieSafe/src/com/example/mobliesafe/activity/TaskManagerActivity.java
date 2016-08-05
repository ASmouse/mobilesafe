package com.example.mobliesafe.activity;

import java.util.List;
import java.util.Vector;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobliesafe.R;
import com.example.mobliesafe.domain.AppInfo;
import com.example.mobliesafe.service.ScreenOffClearService;
import com.example.mobliesafe.utils.AppInfoUtils;
import com.example.mobliesafe.utils.MyContains;
import com.example.mobliesafe.utils.SPUtils;
import com.example.mobliesafe.utils.ServiceUtils;
import com.example.mobliesafe.utils.TaskInfoUtils;
import com.example.mobliesafe.view.MessProgress;
import com.example.mobliesafe.view.SettingCenterItem;
import com.example.mobliesafe.view.SettingCenterItem.OnToggleChangedListener;

public class TaskManagerActivity extends Activity {
	protected static final int LOADING = 1;
	protected static final int FINISH = 2;
	private ListView lv_datas;
	private TextView tv_showInfo;
	private MessProgress mpb_mem;
	private MessProgress mpb_num;
	private LinearLayout ll_process;
	private SlidingDrawer sd_drawer;
	private ImageView iv_arrow1;
	private ImageView iv_arrow2;
	private SettingCenterItem sci_showsystem;
	private SettingCenterItem sci_screenoffclear;

	private List<AppInfo> allRunningTaskApp = new Vector<AppInfo>();
	private List<AppInfo> userRunningTaskApp = new Vector<AppInfo>();
	private List<AppInfo> systemRunningTaskApp = new Vector<AppInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initView();

		initData();

		initEvent();

		initAnimation();

		showUp();
	}

	private void showUp() {
		iv_arrow1.setImageResource(R.drawable.drawer_arrow_up);
		iv_arrow2.setImageResource(R.drawable.drawer_arrow_up);

		iv_arrow1.startAnimation(aa1);
		iv_arrow2.startAnimation(aa2);
	}

	private void showDown() {
		iv_arrow1.clearAnimation();
		iv_arrow2.clearAnimation();

		iv_arrow1.setImageResource(R.drawable.drawer_arrow_down);
		iv_arrow2.setImageResource(R.drawable.drawer_arrow_down);

	}

	private void initAnimation() {
		aa1 = new AlphaAnimation(1.0f, 0.5f);
		aa1.setDuration(500);
		aa1.setRepeatCount(Animation.INFINITE);

		aa2 = new AlphaAnimation(0.5f, 1.0f);
		aa2.setDuration(500);
		aa2.setRepeatCount(Animation.INFINITE);
	}

	private void initEvent() {

		sd_drawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {

			@Override
			public void onDrawerOpened() {
				// TODO Auto-generated method stub
				showDown();
			}
		});

		sd_drawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {

			@Override
			public void onDrawerClosed() {
				// TODO Auto-generated method stub
				showUp();
			}
		});

		sci_screenoffclear
				.setOnToggleChangedListener(new OnToggleChangedListener() {

					@Override
					public void onToggleChange(View v, boolean isOpen) {
						if (isOpen) {
							Intent service = new Intent(
									TaskManagerActivity.this,
									ScreenOffClearService.class);
							startService(service);

						} else {
							Intent service = new Intent(
									TaskManagerActivity.this,
									ScreenOffClearService.class);
							stopService(service);
						}
					}
				});

		sci_showsystem
				.setOnToggleChangedListener(new OnToggleChangedListener() {

					@Override
					public void onToggleChange(View v, boolean isOpen) {
						// TODO Auto-generated method stub
						SPUtils.putBoolean(getApplicationContext(),
								MyContains.SHOWSYSTEMTASK, isOpen);
						mAdapter.notifyDataSetChanged();
					}
				});

		// 点击条目
		lv_datas.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == 0 || position == userRunningTaskApp.size() + 1) {
					return;
				}
				AppInfo itemAtPosition = (AppInfo) lv_datas
						.getItemAtPosition(position);
				itemAtPosition.setChecked(!itemAtPosition.isChecked());

				if (itemAtPosition.getPackName().equals(getPackageName())) {
					itemAtPosition.setChecked(false);
				}

				mAdapter.notifyDataSetChanged();
			}
		});

		// 滑动更换textview
		lv_datas.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (firstVisibleItem <= userRunningTaskApp.size()) {
					tv_showInfo.setText("用户进程: (" + userRunningTaskApp.size()
							+ ")");
				} else {
					tv_showInfo.setText("系统进程: (" + systemRunningTaskApp.size()
							+ ")");
				}
			}
		});

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:
				ll_process.setVisibility(View.VISIBLE);
				lv_datas.setVisibility(View.GONE);
				tv_showInfo.setVisibility(View.GONE);

				break;
			case FINISH:
				ll_process.setVisibility(View.GONE);
				lv_datas.setVisibility(View.VISIBLE);
				tv_showInfo.setVisibility(View.VISIBLE);

				showMessageProgress();

				mAdapter.notifyDataSetChanged();

				break;

			}

		};
	};
	private MyAdapter mAdapter;

	private void showMessageProgress() {

		int totalTask = AppInfoUtils.getTotalAppNum(getApplicationContext());
		int runningTask = userRunningTaskApp.size()
				+ systemRunningTaskApp.size();
		mpb_num.setText("当前进程数: " + runningTask + " / " + totalTask);
		mpb_num.setProgress((totalTask - runningTask) * 1.0 / totalTask);

		mpb_mem.setText("当前RAM使用情况: "
				+ Formatter.formatFileSize(getApplicationContext(), availMem)
				+ " / "
				+ Formatter.formatFileSize(getApplicationContext(), totalMem));
		mpb_mem.setProgress((availMem) * 1.0 / totalMem);

	}

	private long totalMem;
	private long availMem;
	private AlphaAnimation aa1;
	private AlphaAnimation aa2;
	private ActivityManager mAM;

	private void initData() {

		sci_screenoffclear.setToggleOn(ServiceUtils.isServiceRunning(
				getApplicationContext(),
				"com.example.mobliesafe.service.ScreenOffClearService"));
		sci_showsystem.setToggleOn(SPUtils.getBoolean(getApplicationContext(),
				MyContains.SHOWSYSTEMTASK, true));

		new Thread() {

			public void run() {
				mHandler.obtainMessage(LOADING).sendToTarget();
				// 获取数据
				allRunningTaskApp.clear();
				userRunningTaskApp.clear();
				systemRunningTaskApp.clear();
				allRunningTaskApp = TaskInfoUtils
						.getAllRunningApps(getApplicationContext());
				for (AppInfo appInfo : allRunningTaskApp) {
					if (appInfo.isSystem()) {
						systemRunningTaskApp.add(appInfo);
					} else {
						userRunningTaskApp.add(appInfo);
					}
				}
				allRunningTaskApp.clear();
				allRunningTaskApp.addAll(userRunningTaskApp);
				allRunningTaskApp.addAll(systemRunningTaskApp);
				// 初始化内存使用情况
				totalMem = TaskInfoUtils.getTotalMem(getApplicationContext());
				availMem = TaskInfoUtils.getAvailMem(getApplicationContext());

				mHandler.obtainMessage(FINISH).sendToTarget();
			};

		}.start();
	}

	private class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {

			if (SPUtils.getBoolean(getApplicationContext(),
					MyContains.SHOWSYSTEMTASK, true)) {
				return userRunningTaskApp.size() + 1
						+ systemRunningTaskApp.size() + 1;
			} else {
				return userRunningTaskApp.size() + 1;
			}

		}

		@Override
		public AppInfo getItem(int position) {
			if (position <= userRunningTaskApp.size()) {
				return userRunningTaskApp.get(position - 1);
			} else {
				return systemRunningTaskApp.get(position
						- (userRunningTaskApp.size() + 2));
			}
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (position == 0) {
				TextView tv_tag = new TextView(getApplicationContext());
				tv_tag.setTextSize(16);
				tv_tag.setTextColor(Color.WHITE);
				tv_tag.setBackgroundColor(Color.GRAY);
				tv_tag.setText("用户进程: (" + userRunningTaskApp.size() + ")");
				return tv_tag;
			} else if (position == userRunningTaskApp.size() + 1) {

				TextView tv_tag = new TextView(getApplicationContext());
				tv_tag.setTextSize(16);
				tv_tag.setTextColor(Color.WHITE);
				tv_tag.setBackgroundColor(Color.GRAY);
				tv_tag.setText("系统进程: (" + systemRunningTaskApp.size() + ")");
				return tv_tag;
			} else {

				ViewHolder holder = null;
				if (convertView == null || convertView instanceof TextView) {
					convertView = View.inflate(getApplicationContext(),
							R.layout.item_taskmanager_lv, null);
					holder = new ViewHolder();
					holder.tv_taskmemsize = (TextView) convertView
							.findViewById(R.id.tv_taskmanager_lv_taskmemsize);
					holder.tv_taskname = (TextView) convertView
							.findViewById(R.id.tv_taskmanager_lv_taskname);
					holder.iv_icon = (ImageView) convertView
							.findViewById(R.id.iv_taskmanager_lv_icon);
					holder.cb_select = (CheckBox) convertView
							.findViewById(R.id.cb_taskmanager_lv_tasksize);

					convertView.setTag(holder);

				} else {
					holder = (ViewHolder) convertView.getTag();
				}
				// 设置数据

				final AppInfo bean = getItem(position);
				holder.tv_taskname.setText(bean.getAppName());
				holder.tv_taskmemsize.setText(Formatter.formatFileSize(
						getApplicationContext(), bean.getMemSize()));
				holder.iv_icon.setImageDrawable(bean.getIcon());
				// !!
				holder.cb_select.setChecked(bean.isChecked());

				holder.cb_select
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							@Override
							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {

								bean.setChecked(isChecked);
							}
						});

				// 设置自己隐藏
				if (bean.getPackName().equals(getPackageName())) {
					holder.cb_select.setVisibility(View.GONE);
				} else {
					holder.cb_select.setVisibility(View.VISIBLE);
				}

				return convertView;
			}
		}

		private class ViewHolder {
			TextView tv_taskname;
			TextView tv_taskmemsize;
			ImageView iv_icon;
			CheckBox cb_select;
		}

	}

	private void initView() {
		// TODO Auto-generated method stub
		setContentView(R.layout.activity_taskmanager);

		lv_datas = (ListView) findViewById(R.id.lv_taskmanager);
		tv_showInfo = (TextView) findViewById(R.id.tv_taskmanager_showInfo);

		mpb_mem = (MessProgress) findViewById(R.id.mpb_taskmanager_runningmem);
		mpb_num = (MessProgress) findViewById(R.id.mpb_taskmanager_runningnum);
		// progressbar
		ll_process = (LinearLayout) findViewById(R.id.ll_progressbar_root);

		sd_drawer = (SlidingDrawer) findViewById(R.id.slidingDrawer1);
		iv_arrow1 = (ImageView) findViewById(R.id.iv_drawer_arrow1);
		iv_arrow2 = (ImageView) findViewById(R.id.iv_drawer_arrow2);

		sci_showsystem = (SettingCenterItem) findViewById(R.id.sci_taskmanager_showsystem);
		sci_screenoffclear = (SettingCenterItem) findViewById(R.id.sci_taskmanager_screenoffclear);

		mAdapter = new MyAdapter();
		lv_datas.setAdapter(mAdapter);

		mAM = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
	}

	public void reverseSelect(View v) {
		for (AppInfo appInfo : userRunningTaskApp) {
			appInfo.setChecked(!appInfo.isChecked());
		}

		for (AppInfo appInfo : systemRunningTaskApp) {
			appInfo.setChecked(!appInfo.isChecked());
		}

		mAdapter.notifyDataSetChanged();
	}

	public void selectAll(View v) {
		for (AppInfo appInfo : userRunningTaskApp) {
			appInfo.setChecked(true);
		}

		for (AppInfo appInfo : systemRunningTaskApp) {
			appInfo.setChecked(true);
		}

		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 清理选中的进程
	 * 
	 * @param v
	 */
	public void clearTask(View v) {
		long memSize = 0;
		int taskNum = 0;
		for (int i = 0; i < userRunningTaskApp.size(); i++) {
			AppInfo appInfo = userRunningTaskApp.get(i);
			if (appInfo.isChecked()) {
				mAM.killBackgroundProcesses(appInfo.getPackName());
				taskNum++;
				userRunningTaskApp.remove(i--);
				memSize += appInfo.getMemSize();
			}
		}

		for (int i = 0; i < systemRunningTaskApp.size(); i++) {
			AppInfo appInfo = systemRunningTaskApp.get(i);
			if (appInfo.isChecked()) {
				mAM.killBackgroundProcesses(appInfo.getPackName());
				taskNum++;
				systemRunningTaskApp.remove(i--);
				memSize += appInfo.getMemSize();
			}
		}

		//
		mAdapter.notifyDataSetChanged();
		//
		
		availMem += memSize;
		showMessageProgress();
		
		//
		Toast.makeText(
				getApplicationContext(),
				"恭喜您! 成功清理了"
						+ taskNum
						+ "个进程,共释放内存:"
						+ Formatter.formatFileSize(getApplicationContext(),
								memSize), 0).show();
		//记录清理时间
		if ((systemRunningTaskApp.size() + userRunningTaskApp.size()) < 3){
			SPUtils.putLong(getApplicationContext(),MyContains.CLEARTIME, System.currentTimeMillis());
		}
	}

}
