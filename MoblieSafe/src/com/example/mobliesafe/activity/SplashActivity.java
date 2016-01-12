package com.example.mobliesafe.activity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobliesafe.R;
import com.example.mobliesafe.utils.MyContains;
import com.example.mobliesafe.utils.SPUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

/**
 * @author jacksonCao
 * @data 2016-7-9
 * @desc splash界面
 * 
 * @version $Rev: 7 $
 * @author $Author: caojun $
 * @Date $Date: 2016-07-12 20:02:45 +0800 (周二, 12 七月 2016) $
 * @Id $ID$
 * @Url $URL:
 *      https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example
 *      /mobliesafe/activity/SplashActivity.java $
 */
public class SplashActivity extends Activity {

	protected static final int LOADMAIN = 1;
	private static final int NEWVERSION = 2;
	private RelativeLayout rl_root;
	private TextView tv_verionname;
	private TextView tv_versionnumber;
	private PackageManager mPm;
	private AnimationSet mAs;
	private int mVersionCode;
	private String mVersionName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除标题,界面显示之前
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		// TODO 模拟数据
		SPUtils.putBoolean(getApplicationContext(), MyContains.AUTOUPDATE, true);

		// 界面
		initView();
		// 数据
		initData();

		// 动画
		initAnnimation();
		// 事件
		initEvent();
	}

	private void initAnnimation() {
		
		// 补间动画 duration ,repeatCount,repeatMode
		// 1.旋转动画
		RotateAnimation ra = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		// 设置动画的参数
		ra.setDuration(1000);
		// 补间动画是一个影子动画,停留在结束的位置
		ra.setFillAfter(true);

		// 2.比例动画
		ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		sa.setDuration(1000);
		sa.setFillAfter(true);

		// 3.透明动画
		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		aa.setDuration(1000);
		aa.setFillAfter(true);

		// 4.平移动画
		TranslateAnimation ta = new TranslateAnimation(
				Animation.RELATIVE_TO_PARENT, 0.2f,
				Animation.RELATIVE_TO_PARENT, 0.2f,
				Animation.RELATIVE_TO_PARENT, 0.2f,
				Animation.RELATIVE_TO_PARENT, 0.2f);
		ta.setDuration(1000);
		ta.setFillAfter(true);

		// 5.动画集,每种动画用自己的动画插入器(数学函数),线性
		mAs = new AnimationSet(false);
		mAs.addAnimation(aa);

		mAs.addAnimation(ra);
		mAs.addAnimation(sa);
		// 开始动画
		rl_root.startAnimation(mAs);
	}

	private void initView() {
		// 初始化界面

		setContentView(R.layout.activity_splash);
		// 获取splash界面的根布局
		rl_root = (RelativeLayout) findViewById(R.id.rl_splash_root);
		// 版本名
		tv_verionname = (TextView) findViewById(R.id.tv_splash_versionname);
		// 版本号
		tv_versionnumber = (TextView) findViewById(R.id.tv_splash_versionnumber);
	}

	private void initData() {
		// 组织数据
		// 版本名从清单文件中进行性配置
		// api 两大功能类 PackageManager 静态数据 || ActivityManager 动态数据:内存使用

		try {
			mPm = getPackageManager();
			// 可选项基本都为0
			PackageInfo packageInfo = mPm.getPackageInfo(getPackageName(), 0);
			mVersionCode = packageInfo.versionCode;
			mVersionName = packageInfo.versionName;

			// 显示数据
			tv_verionname.setText(mVersionName);
			tv_versionnumber.setText("V " + mVersionCode + "");

		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		// 显示数据

	}

	// 进入主界面
	private void startHome() {
		Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
		startActivity(intent);
		// 关闭自己
		finish();
	}

	// 版本更新检测
	private void checkVision() {
		// 检测版本(访问网络放在子线程,否则ANR)

		new Thread() {
			public void run() {
				// 访问网路 url,数据格式json
				readUrlData();

			}

		}.start();

		// 安装
		// 跳转
	}

	// 在子线程中执行
	private void readUrlData() {
		//Message实现方式:1.new 2.Message.btain();  3.handler.obtainMessage();
		Message msg = mHandler.obtainMessage();

		long startTime = SystemClock.currentThreadTimeMillis();
		// 产品,异常要详细
		try {
			URL uri = new URL(getResources().getString(R.string.versionurl));
			// 连接
			HttpURLConnection conn = (HttpURLConnection) uri.openConnection();
			// 设置属性
			conn.setRequestMethod("GET"); // get请求
			conn.setConnectTimeout(5000); // 超时时间
			int code = conn.getResponseCode(); // 200success ,
												// 404notfound, 500内部错误
			if (code == 200) {
				// 请求成功
				// 把流转成json数据
				String json = stream2json(conn.getInputStream());
				versionInfo = parseJson(json);
				// 检测版本
				if (versionInfo.versionCode == mVersionCode) {
					// 版本一致,进入主界面
					msg.what = LOADMAIN;

				} else {
					// 新版本
					Log.d("syso", "有新版本需要更新啊");
					// 提示用户是否更新,如果实否进入主界面,是:下载并提醒安装,对话框交给handler更新,因为这里是子线程
					msg.what = NEWVERSION;
				}

			} else {
				// 错误 非200,如404,500
				msg.what = code;

			}

		} catch (MalformedURLException e) {
			// url

			msg.what = 10087;
			e.printStackTrace();
		} catch (NotFoundException e) {
			// notfound
			msg.what = 10088;
			e.printStackTrace();
		} catch (IOException e) {
			// io
			msg.what = 10089;
			e.printStackTrace();
		} catch (JSONException e) {
			msg.what = 10090;
			// json解析
			e.printStackTrace();
		} finally {
			// 延时处理
			long endTime = SystemClock.currentThreadTimeMillis();
			if (endTime - startTime < 2000) {
				// 动画没播完
				SystemClock.sleep(2000 - (endTime - startTime));
			}
			mHandler.sendMessage(msg);
		}

	};

	/**
	 * 下载新的APK
	 * 
	 * @param versionInfo
	 */

	private void downloadNewApk() {
		HttpUtils httpUtils = new HttpUtils();
		// 放到缓存目录下
		// !!!!!!apk放到存目录下,系统调用私人apk文件安装会缺少权限,故改到其他目录下
		String path = Environment.getExternalStorageDirectory().getPath()
				+ "/newsMobilePhone.apk";
		httpUtils.download(versionInfo.downloadUrl, path,
				new RequestCallBack<File>() {

					@Override
					public void onSuccess(ResponseInfo<File> arg0) {
						// 主线程
						Toast.makeText(getApplicationContext(), "更新完毕",
								Toast.LENGTH_SHORT).show();
						// 安装apk!!!!!!
						/*
						 * <intent-filter> <action
						 * android:name="android.intent.action.VIEW" />
						 * <category
						 * android:name="android.intent.category.DEFAULT" />
						 * <data android:scheme="content" /> <data
						 * android:scheme="file" /> <data android:mimeType=
						 * "application/vnd.android.package-archive" />
						 * </intent-filter>
						 */
						Intent intent = new Intent();
						intent.setAction("android.intent.action.VIEW");
						intent.addCategory("android.intent.category.DEFAULT");
						// 内容提供者是Uri.parse();
						// 因为要制定位置,所以用Uri.fromFile();!!!!

						intent.setDataAndType(Uri.fromFile(new File(Environment
								.getExternalStorageDirectory(),
								"newsMobilePhone.apk")),
								"application/vnd.android.package-archive");
						startActivityForResult(intent, 1);
						// 调用系统的安装需要知道返回值(是否安装成功)
						// !!!!缺少权限,提示不明确:查找系统这个清单文件给出的权限 <uses-permission
						// android:name="android.permission.INSTALL_PACKAGES" />
						// 但是这里是不需要这个权限的,这个仅提供给系统应用(A安装B,A需要权限并且必须为系统级)这里是调用系统的Activity,在由系统Activity来安装
						// !!!!!!apk放到存目录下,系统调用私人apk文件安装会缺少权限,故改到其他目录下

					}

					@Override
					public void onFailure(HttpException arg0, String arg1) {
						// 主线程,下载失败进入主界面
						Toast.makeText(getApplicationContext(),
								"下载失败:\n" + arg0.toString(), 1).show();
						startHome();
					}
				});

	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//进入主界面,不管返回值是什么????安装成功后点击"完成",打开还是老版本
		startHome();
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	
	
	private void showpIsDownloadDialog() {
		// TODO Auto-generated method stub
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("TIPS:")
				.setMessage("检测到新版本,是否进行更新?\n新增功能:\n" + versionInfo.desc)
				//.setCancelable(false)	//[1].防止点击其他地方取消对话框造成的程序不能继续向前执行的bug
				.setPositiveButton("下载", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 确定,下载更新,使用XUTILS
						downloadNewApk();

					}

				}).setNegativeButton("取消", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// 取消进入主界面
						startHome();

					}
				})
				.setOnCancelListener(new OnCancelListener() {	
									//[2].或者是设置点击取消时间,不管用什么方式取消,只要取消了就进入主界面
					@Override
					public void onCancel(DialogInterface dialog) {
						// 点击取消,进入主界面
						startHome();
					}
				})
				.show();

	}

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADMAIN:

				Toast.makeText(getApplicationContext(), "已经是最新版本",
						Toast.LENGTH_LONG).show();
				startHome();
				break;
			case NEWVERSION:
				// 显示对话框
				showpIsDownloadDialog();

				// !!!防止对话框一闪而过!!!!下面不能直接startHome();

				break;

			default:
				switch (msg.what) {
				case 404:
					Toast.makeText(getApplicationContext(), "网络资源不存在",
							Toast.LENGTH_LONG).show();
					break;
				case 500:
					Toast.makeText(getApplicationContext(), "服务器内部错误",
							Toast.LENGTH_LONG).show();
					break;
				case 10087:
					Toast.makeText(getApplicationContext(), "URL错误",
							Toast.LENGTH_LONG).show();
					break;
				case 10088:
					Toast.makeText(getApplicationContext(), "NOT Found ",
							Toast.LENGTH_LONG).show();
					break;
				case 10089:
					Toast.makeText(getApplicationContext(), "IO 错误",
							Toast.LENGTH_LONG).show();
				case 10090:
					Toast.makeText(getApplicationContext(), "JSON格式错误",
							Toast.LENGTH_LONG).show();
					break;

				default:

					Toast.makeText(getApplicationContext(), "未知错误",
							Toast.LENGTH_LONG).show();
					break;

				}
				// 进入主界面
				startHome();
				break;
			}

		};
	};
	private VersionInfo versionInfo;

	// 只用于这个类的信息封装
	class VersionInfo {
		String versionName;
		int versionCode;
		String downloadUrl;
		String desc;
	}

	/**
	 * 解析json数据
	 * 
	 * @param json
	 * @throws JSONException
	 */
	private VersionInfo parseJson(String json) throws JSONException {
		VersionInfo info = new VersionInfo();
		// 解析json数据
		JSONObject jsonObject = new JSONObject(json);
		info.desc = jsonObject.getString("desc");
		info.versionName = jsonObject.getString("versionname");
		info.versionCode = jsonObject.getInt("versioncode");
		info.downloadUrl = jsonObject.getString("downloadurl");

		return info;
	}

	/**
	 * 把IO流转成字符串
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private String stream2json(InputStream is) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = reader.readLine()) != null) {

			sb.append(line);
		}

		return sb.toString();
	}

	private void initEvent() {
		// 监听动画开始和结束的事件
		mAs.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// 动画开始 ,初始化数据,初始化网络(子线程)
				// 版本更新
				if (SPUtils.getBoolean(getApplicationContext(),
						MyContains.AUTOUPDATE, false)) {
					// 需要进行版本更新
					Log.d(MyContains.AUTOUPDATE, "更新");
					checkVision();
				} else {

				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// 动画重复

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 动画结束
				// 是否进行版本更新 ,1.不进行版本更新,动画播完直接进入主界面 2.
				if (SPUtils.getBoolean(getApplicationContext(),
						MyContains.AUTOUPDATE, false)) {

				} else {
					// 不更新,直接进入主界面
					Log.d(MyContains.AUTOUPDATE, "不更新");
					startHome();
				}
			}

		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
