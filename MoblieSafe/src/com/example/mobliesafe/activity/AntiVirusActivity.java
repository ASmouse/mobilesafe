package com.example.mobliesafe.activity;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobliesafe.R;
import com.example.mobliesafe.dao.AntiAruisDao;
import com.example.mobliesafe.domain.AppInfo;
import com.example.mobliesafe.utils.AppInfoUtils;
import com.example.mobliesafe.utils.Md5Utils;
import com.github.lzyzsd.circleprogress.CircleProgress;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class AntiVirusActivity extends Activity {

	protected static final int STARTSCAN = 1;
	protected static final int SCANNING = 2;
	protected static final int FINISH = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		initView();

		checkVerson();
	}

	// 控制显示对话框的点点点
	boolean pointRunning = true;

	private void checkVerson() {
		// 上来先联网显示
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("联网更新");
		builder.setMessage("正在联网中......");
		final AlertDialog dialog = builder.create();
		dialog.show();
		// 动态显示点点点
		new Thread() {

			public void run() {
				pointRunning = true;
				class Data {
					int num = 1;
				}

				final Data data = new Data();
				data.num = 1;
				while (pointRunning) {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {

							dialog.setMessage("正在联网中"
									+ getPointNumber(data.num++ % 6));
							

						}
					});
					SystemClock.sleep(300);
				}
			}

		}.start();

		// 联网校验版本号

		HttpUtils http = new HttpUtils();
		http.configTimeout(5000);
		http.send(HttpMethod.GET,
				getResources().getString(R.string.virusversionurl),
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						dialog.dismiss();
						pointRunning = false;
						Toast.makeText(getApplicationContext(), "联网失败", 0)
								.show();
						startScan();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						dialog.dismiss();
						pointRunning = false;
						final int remoteVersion = Integer.parseInt(arg0.result);
						// 比较版本
						int currentVersion = AntiAruisDao.getCurrentVersion();

						if (remoteVersion != currentVersion) {
							// 版本不一致提示需要更新
							// 弹出对话框提示更新
							AlertDialog.Builder ab = new Builder(
									AntiVirusActivity.this);
							ab.setTitle("需要更新");
							ab.setMessage("有病毒库需要更新,是否更新");
							ab.setPositiveButton("立即更新",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											// 下载更新
											downLoadAirus(remoteVersion);
										}
									});
							ab.setNegativeButton("下次再说",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											startScan();
										}
									});

						} else {
							// 版本一致
							Toast.makeText(getApplicationContext(),
									"病毒库已经是最新的了", 0).show();
							startScan();
						}

					}
				});

	}

	/**
	 * 下载更新
	 */
	private void downLoadAirus(final int remoteVersion) {
		// TODO Auto-generated method stub
		HttpUtils http = new HttpUtils();
		http.configTimeout(3000);
		http.send(HttpMethod.GET,
				getResources().getString(R.string.virusdatas),
				new RequestCallBack<String>() {

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// 下载失败
						Toast.makeText(getApplicationContext(), "病毒库更新失败", 0)
								.show();
						startScan();
					}

					@Override
					public void onSuccess(ResponseInfo<String> arg0) {
						// 下载成功,解析
						String result = arg0.result;
						try {
							JSONArray jsonArray = new JSONArray(result);
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject jsonObject = jsonArray
										.getJSONObject(i);
								String md5 = jsonObject.getString("md5");
								String desc = jsonObject.getString("desc");
								AntiAruisDao.updateVirusDB(md5, desc);
							}

							AntiAruisDao.updateCurrentVersion(remoteVersion);
							Toast.makeText(getApplicationContext(), "病毒库更新成功",
									1).show();

							startScan();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				});

	}

	private String getPointNumber(int num) {
		String res = "";
		for (int i = 0; i < num; i++) {
			res += ".";
		}

		return res;
	};

	public class ScanInfo {
		String appName;
		Drawable appIcon;
		boolean isVirus;
		int max;
		int progress;
	}
	private boolean isInterruptScaning = false;
	/**
	 * 开始扫描
	 */
	private void startScan() {
		new Thread() {
			public void run() {
				mHandler.obtainMessage(STARTSCAN).sendToTarget();
				// 数据
				int progress = 0;
				List<AppInfo> allInstalledAppInfos = AppInfoUtils
						.getAllInstalledAppInfos(getApplicationContext());
				for (AppInfo appInfo : allInstalledAppInfos) {
					if(isInterruptScaning){
						return;
					}
					
					progress++;
					ScanInfo info = new ScanInfo();
					info.appIcon = appInfo.getIcon();
					info.appName = appInfo.getAppName();
					info.max = allInstalledAppInfos.size();
					info.progress = progress;
					// 是否是病毒
					String md5 = Md5Utils.encodeFile(appInfo.getSourceDir());
					info.isVirus = AntiAruisDao.isVirus(md5);

					Message msg = mHandler.obtainMessage(SCANNING);
					msg.obj = info;
					mHandler.sendMessage(msg);
					
					SystemClock.sleep(300);
				}

				// 结束
				mHandler.obtainMessage(FINISH).sendToTarget();

			};
		}.start();

	}

	private boolean mIsVirus = false;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case STARTSCAN:
				ll_anima.setVisibility(View.GONE);
				ll_progress.setVisibility(View.VISIBLE);
				ll_reslut.setVisibility(View.GONE);

				break;

			case SCANNING:
				// 扫描中
				// 先得到数据
				ScanInfo info = (ScanInfo) msg.obj;
				// 设置cp_progress
				cp_progress.setProgress(Math.round(info.progress*100.0f / info.max));
				tv_scan.setText("正在扫描 : " + info.appName);

				// 动态添加到下方LinearLayout的结果中
				View view = View.inflate(getApplicationContext(),
						R.layout.item_antivirus_ll, null);
				ImageView iv_icon = (ImageView) view
						.findViewById(R.id.iv_antivirus_ll_icon);
				TextView tv_name = (TextView) view
						.findViewById(R.id.tv_antivirus_ll_name);
				ImageView iv_isvirus = (ImageView) view
						.findViewById(R.id.iv_antivirus_ll_isvirus);

				iv_icon.setImageDrawable(info.appIcon);
				tv_name.setText(info.appName);
				if (info.isVirus) {
					mIsVirus = true;
					iv_isvirus.setImageResource(R.drawable.list_icon_risk);
				} else {
					iv_isvirus.setImageResource(R.drawable.list_icon_security);
				}

				ll_appInfo.addView(view, 0);

				break;

			case FINISH:
				// 完成扫描
				// 拍照
				ll_progress.setDrawingCacheEnabled(true);
				ll_progress
						.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
				Bitmap cache = ll_progress.getDrawingCache();

				Bitmap leftImage = getLeftBitmap(cache);
				Bitmap rightBitmap = getRightBitmap(cache);

				iv_left.setImageBitmap(leftImage);
				iv_right.setImageBitmap(rightBitmap);

				// !!!!!!
				if (isInitAnimationAndEvent) {
					initOpenShowResult();
					initCloseShowProgress();

					initEvent();
				}

				ll_anima.setVisibility(View.VISIBLE);
				ll_progress.setVisibility(View.GONE);
				ll_reslut.setVisibility(View.VISIBLE);
				
				if(mIsVirus){
					tv_reslutshow.setText("您的手机检测到病毒!");
					tv_reslutshow.setTextColor(Color.RED);
					
				}else{
					tv_reslutshow.setText("哎呦,手机保养的不错呦");
				}
				isInitAnimationAndEvent=false;
			
				startOpenShowResult();
				break;

			}

		}

	};

	private void initEvent() {
		btn_rescan.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startClosShowProgress();
				
			}
		});
		
		mASClose.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				// TODO Auto-generated method stub
				startScan();
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mASOpen.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator arg0) {
				// TODO Auto-generated method stub
				btn_rescan.setEnabled(false);
			}
			
			@Override
			public void onAnimationRepeat(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator arg0) {
				// TODO Auto-generated method stub
				btn_rescan.setEnabled(true);
			}
			
			@Override
			public void onAnimationCancel(Animator arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	
		
	}
	//!!!!!!!!!!!
	@Override
	protected void onDestroy() {
		isInterruptScaning =true;
		
		super.onDestroy();
	}
	private void initCloseShowProgress() {
		mASClose = new AnimatorSet();
		ll_progress.measure(0, 0);
		int width = ll_progress.getMeasuredWidth() / 2;
		int height = ll_progress.getMeasuredHeight();

		ObjectAnimator aaLeft = ObjectAnimator.ofFloat(iv_left, "alpha", 0.0f,
				1.0f);
		ObjectAnimator aaRight = ObjectAnimator.ofFloat(iv_right, "alpha",
				0.0f, 1.0f);
		ObjectAnimator taLeft = ObjectAnimator.ofFloat(iv_left, "translationX",
				-width, 0f);
		ObjectAnimator taRight = ObjectAnimator.ofFloat(iv_right,
				"translationX", width, 0f);
		ObjectAnimator aaResult = ObjectAnimator.ofFloat(ll_reslut, "alpha",
				 1.0f,0.0f);
		mASClose.playTogether(aaLeft, aaRight, taLeft, taRight,aaResult);
		mASClose.setDuration(2000);

	}

	public void startClosShowProgress() {
		mASClose.start();
	}

	private void initOpenShowResult() {
		mASOpen = new AnimatorSet();
		ll_progress.measure(0, 0);
		int width = ll_progress.getMeasuredWidth() / 2;
		int height = ll_progress.getMeasuredHeight();

		ObjectAnimator aaLeft = ObjectAnimator.ofFloat(iv_left, "alpha", 1.0f,
				0.0f);
		ObjectAnimator aaRight = ObjectAnimator.ofFloat(iv_right, "alpha",
				1.0f, 0.0f);
		ObjectAnimator taLeft = ObjectAnimator.ofFloat(iv_left, "translationX",
				0f, -width);
		ObjectAnimator taRight = ObjectAnimator.ofFloat(iv_right,
				"translationX", 0f, width);
		ObjectAnimator aaResult = ObjectAnimator.ofFloat(ll_reslut, "alpha",
				0.0f, 1.0f);
		mASOpen.playTogether(aaLeft, aaRight, taLeft, taRight, aaResult);
		mASOpen.setDuration(2000);
	}

	public void startOpenShowResult() {
		mASOpen.start();
	}

	private boolean isInitAnimationAndEvent = true;

	private Bitmap getRightBitmap(Bitmap cache) {
		// TODO Auto-generated method stub
		int width = cache.getWidth() / 2;
		int height = cache.getHeight();

		Bitmap bitmap = Bitmap.createBitmap(width, height, cache.getConfig());
		Canvas canvas = new Canvas(bitmap);
		Matrix matrix = new Matrix();
		matrix.setTranslate(-width, 0);
		Paint paint = new Paint();
		canvas.drawBitmap(cache, matrix, paint);
		return bitmap;

	}

	private Bitmap getLeftBitmap(Bitmap cache) {
		// TODO Auto-generated method stub
		int width = cache.getWidth() / 2;
		int height = cache.getHeight();

		Bitmap bitmap = Bitmap.createBitmap(width, height, cache.getConfig());
		Canvas canvas = new Canvas(bitmap);
		Matrix matrix = new Matrix();
		Paint paint = new Paint();
		canvas.drawBitmap(cache, matrix, paint);
		return bitmap;
	};

	private LinearLayout ll_reslut;
	private TextView tv_reslutshow;
	private Button btn_rescan;
	private LinearLayout ll_progress;
	private CircleProgress cp_progress;
	private TextView tv_scan;
	private LinearLayout ll_anima;
	private ImageView iv_right;
	private ImageView iv_left;
	private LinearLayout ll_appInfo;
	private AnimatorSet mASClose;
	private AnimatorSet mASOpen;

	private void initView() {
		setContentView(R.layout.activity_antivirus);
		// 结果显示
		ll_reslut = (LinearLayout) findViewById(R.id.ll_antivirus_reslut);
		tv_reslutshow = (TextView) findViewById(R.id.tv_antivirus_reslutshow);
		btn_rescan = (Button) findViewById(R.id.btn_antivirus_rescan);

		// 进度扫描

		ll_progress = (LinearLayout) findViewById(R.id.ll_antivirus_scanprogress);
		cp_progress = (CircleProgress) findViewById(R.id.cp_antivirus_circleprogress);

		tv_scan = (TextView) findViewById(R.id.tv_antivirus_scanning);

		// 动画切换

		ll_anima = (LinearLayout) findViewById(R.id.ll_antivirus_anima);
		iv_left = (ImageView) findViewById(R.id.iv_antivirus_left);
		iv_right = (ImageView) findViewById(R.id.iv_antivirus_right);
		// 显示的app信息
		ll_appInfo = (LinearLayout) findViewById(R.id.ll_antivirus_appinfo);

	}

}
