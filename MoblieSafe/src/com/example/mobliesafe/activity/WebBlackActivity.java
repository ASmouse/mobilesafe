package com.example.mobliesafe.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobliesafe.R;
import com.example.mobliesafe.dao.BlackDao;
import com.example.mobliesafe.db.BlackDB;
import com.example.mobliesafe.domain.BlackBean;
import com.example.mobliesafe.utils.MyContains;
import com.example.mobliesafe.utils.ShowToast;

public class WebBlackActivity extends Activity {

	private List<BlackBean> mBlackBeans = new ArrayList<BlackBean>();
	private MyAdapter mAdapter;
	protected static final int LOADING = 1;
	protected static final int FINISH = 2;
	private ImageView iv_add;
	private LinearLayout ll_loading;
	private ListView lv_showdata;
	private ImageView iv_noData;
	private BlackDao mBlackDao;
	private PopupWindow mPW;
	private View mContentView;
	private ScaleAnimation mPopupAnimation;

	private AlertDialog mAlertdialog;
	private EditText et_number;

	private static final int COUNTPERPAGE = 10;// 每一页显示多少条数据
	private int totalPages = 0;// 总页数
	private int currentPage = 0;// 當前页数

	// A.
	// private boolean mIsFirstShow ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initView(); // 界面

		initDate(); // 数据 可能执行多次

		initEvent(); // 事件

		initPopupWindow(); // 只初始化一次,没必要多次创建所以放在这
		initAddBlackDialog(); // 添加黑名单的对话框
	}

	private void initAddBlackDialog() {
		// TODO Auto-generated method stub
		// 通过兑黄狂添加黑名单数据

		AlertDialog.Builder builder = new Builder(this);
		View mDialogView = View.inflate(getApplicationContext(),
				R.layout.dialog_addblack, null);
		et_number = (EditText) mDialogView
				.findViewById(R.id.et_dialog_addblack_number);
		// 勾选框
		final CheckBox cb_phonemode = (CheckBox) mDialogView
				.findViewById(R.id.cb_dialog_addblack_phonemode);
		final CheckBox cb_smsmode = (CheckBox) mDialogView
				.findViewById(R.id.cb_dialog_addblack_smsmode);
		// 按钮
		Button btn_confirm = (Button) mDialogView
				.findViewById(R.id.btn_dialog_addblack_confirm);
		Button btn_cancel = (Button) mDialogView
				.findViewById(R.id.btn_dialog_addblack_cancel);

		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.btn_dialog_addblack_confirm:
					// 添加黑名单
					// 1.判断黑名单号码不能为空

					String phone = et_number.getText().toString().trim();
					if (TextUtils.isEmpty(phone)) {
						ShowToast.show("号码不能为空", WebBlackActivity.this);
						return;
					}
					// 2.拦截模式至少勾选一个

					if (!cb_phonemode.isChecked() && !cb_smsmode.isChecked()) {
						// 号码不能为空
						ShowToast.show("拦截模式至少够选一个", WebBlackActivity.this);
						return;
					}
					// 3.添加黑名单数据
					// 因为是按位与运算,不用判断两个都勾选的情况(已经包括)
					int mode = 0;
					if (cb_phonemode.isChecked()) {
						mode |= BlackDB.PHONE_MODE;
					}
					if (cb_smsmode.isChecked()) {
						mode |= BlackDB.SMS_MODE;
					}

					BlackBean bean = new BlackBean();
					bean.setPhone(phone);
					bean.setMode(mode);
					// 添加数据库 用updata 防止重复添加
					mBlackDao.updata(bean);
					// 4.显示最先添加的黑名单数据
					// A.
					// mIsFirstShow = true;
					initDate();
					// 避免了调用initDate 带来的显示缓冲对话框,减少了对数据库的多次读取
					// mBlackBeans.add(0, bean);
					// lv_showdata.smoothScrollToPosition(0); //这行代码这里无效
					// mAdapter.notifyDataSetChanged();

					// 5.关闭对话框
					mAlertdialog.dismiss();
					break;
				case R.id.btn_dialog_addblack_cancel:
					mAlertdialog.dismiss();
					break;
				default:
					break;
				}
			}
		};

		btn_cancel.setOnClickListener(listener);
		btn_confirm.setOnClickListener(listener);

		builder.setView(mDialogView);

		mAlertdialog = builder.create();

	}

	private void initPopupWindow() {

		mContentView = View.inflate(getApplicationContext(),
				R.layout.popupwindow_addblackdata, null);
		// 获取子组件
		TextView tv_manadd = (TextView) mContentView
				.findViewById(R.id.tv_manadd);
		TextView tv_phoneadd = (TextView) mContentView
				.findViewById(R.id.tv_phoneadd);
		TextView tv_smsadd = (TextView) mContentView
				.findViewById(R.id.tv_smsadd);
		TextView tv_friendadd = (TextView) mContentView
				.findViewById(R.id.tv_friendadd);

		OnClickListener mListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tv_manadd:// 手动
					manAdd();
					break;
				case R.id.tv_smsadd:
					smsAdd();
					break;
				case R.id.tv_phoneadd:
					phoneAdd();
					break;
				case R.id.tv_friendadd:
					friendAdd();
					break;

				}
			}

		};

		tv_friendadd.setOnClickListener(mListener);
		tv_phoneadd.setOnClickListener(mListener);
		tv_smsadd.setOnClickListener(mListener);
		tv_manadd.setOnClickListener(mListener);

		// 初始化弹出窗体
		// LayoutParams.WRAP_CONTENT == -2
		mPW = new PopupWindow(mContentView, 130, -2);
		// 设置焦点 子控件可以被点击
		mPW.setFocusable(true);
		// 设置背景 1.外部点击生效 2.可以播放动画
		mPW.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		// 外部点击
		mPW.setOutsideTouchable(true);

		mPopupAnimation = new ScaleAnimation(1.0f, 1.0f, 0.0f, 1.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.0f);

	}

	public void showDialog(String phone) {
		et_number.setText(phone);
		mAlertdialog.show();
	}

	private void friendAdd() {
		Intent friend = new Intent(this, FriendActivity.class);
		startActivityForResult(friend, 1);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			String phone = data.getStringExtra(MyContains.SAFENUMBER);
			// 显示对话框,自动显示号码
			showDialog(phone);

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void phoneAdd() {

		Intent phone = new Intent(this, TelActivity.class);
		startActivityForResult(phone, 1);
	}

	private void smsAdd() {
		Intent sms = new Intent(this, SmsActivity.class);
		startActivityForResult(sms, 1);
	}

	private void manAdd() {
		// ShowToast.show("manAdd", this);
		// 显示对话框
		mAlertdialog.show();
	}

	private void initEvent() {
		// 点击图片,添加黑名单数据
		// 监听事件不在主线程,所以initEvent(), 和initPopupWindow()顺序无关系
		iv_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 添加新的黑名单数据
				if (mPW != null && mPW.isShowing()) {
					mPW.dismiss();
				} else {
					// 显示动画
					mContentView.startAnimation(mPopupAnimation);
					// v是 onClick(View v)的 !!!!!!!!!
					mPW.showAsDropDown(v);
				}
			}
		});

	}

	private Handler mHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:
				// 加载数据 进度条显示
				ll_loading.setVisibility(View.VISIBLE);
				// 隐藏listview 和nodata
				lv_showdata.setVisibility(View.GONE);
				iv_noData.setVisibility(View.GONE);
				break;
			case FINISH:
				// 加载数据完成 隐藏进度条
				ll_loading.setVisibility(View.GONE);
				// 有数据就显示listview 要隐藏nodata
				if (mBlackBeans.isEmpty()) {
					// 没有数据
					iv_noData.setVisibility(View.VISIBLE);
					lv_showdata.setVisibility(View.GONE);

				} else {
					// 有数据
					lv_showdata.setVisibility(View.VISIBLE);
					iv_noData.setVisibility(View.GONE);

					// 刷新界面 适配器通知 !!!!! 取数据底层也是适配器做的

					mAdapter.notifyDataSetChanged();
					// A.
					/*
					 * if(mIsFirstShow){ lv_showdata.smoothScrollToPosition(0);
					 * mIsFirstShow = false; }
					 */
					
					
					//添加 页面 信息
					tv_pagemess.setText(currentPage +"/"+ totalPages);
				}

				// 没有数据显示nodata 隐藏listview

				break;
			default:
				break;
			}

		};
	};
	private EditText et_jumppage;
	private TextView tv_pagemess;

	private class MyAdapter extends BaseAdapter {

		private ViewHolder viewholder;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mBlackBeans.size();
		}

		@Override
		public BlackBean getItem(int position) {
			// TODO Auto-generated method stub
			return mBlackBeans.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		private class ViewHolder {
			TextView tv_phone;
			TextView tv_mode;
			ImageView iv_delete;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			if (convertView == null) {
				// 没有缓存
				convertView = View.inflate(getApplicationContext(),
						R.layout.item_black_lv, null);

				viewholder = new ViewHolder();
				viewholder.tv_phone = (TextView) convertView
						.findViewById(R.id.tv_item_black_lv_blackphone);
				viewholder.tv_mode = (TextView) convertView
						.findViewById(R.id.tv_item_black_lv_blackmode);
				viewholder.iv_delete = (ImageView) convertView
						.findViewById(R.id.iv_item_black_lv_delete);

				convertView.setTag(viewholder);
			} else {
				// 有缓存
				viewholder = (ViewHolder) convertView.getTag();
			}

			// 取值
			final BlackBean bean = getItem(position);

			// 显示值
			viewholder.tv_phone.setText(bean.getPhone());
			switch (bean.getMode()) {
			case BlackDB.SMS_MODE:// 短信拦截
				viewholder.tv_mode.setText("短信拦截");
				break;
			case BlackDB.PHONE_MODE:// 电话拦截
				viewholder.tv_mode.setText("电话拦截");
				break;
			case BlackDB.ALL_MODE:// 全部拦截
				viewholder.tv_mode.setText("全部拦截");
				break;

			}

			// 删除
			viewholder.iv_delete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 删除数据
					// 本地数据删除
					mBlackBeans.remove(bean);
					// 数据库删除
					mBlackDao.delete(bean.getPhone());
					// 更新界面
					mAdapter.notifyDataSetChanged();
				}
			});

			return convertView;
		}

	}

	private void initDate() {
		// 数据过大 子线程访问
		new Thread() {

			public void run() {
				// 获取数据
				// 1.正在加载数据
				mHandler.obtainMessage(LOADING).sendToTarget();

				// 2.加载数据
				
			
				// 从数据库中查询数据,查询的数据已经按添加的顺序排列(_id降序排列)!!!
				//
				// 获取总页数
				totalPages = (int) Math.ceil(mBlackDao.getTotalRows() * 1.0
						/ COUNTPERPAGE);
				// 获取当前页的数据
				mBlackBeans = mBlackDao.getPageData(currentPage, COUNTPERPAGE);

				// 3.加载数据完成
				mHandler.obtainMessage(FINISH).sendToTarget();

			};
		}.start();
	}

	public void shou(View v) {
		currentPage = 1;
		initDate();
	}

	public void wei(View v) {
		currentPage= totalPages;
		initDate();
	}

	public void shang(View v) {
		if(currentPage == 1){
			Toast.makeText(getBaseContext(), "已经是第一页了", 0).show();
			return;
		}
		currentPage--;
		initDate();
	}

	public void xia(View v) {
		if(currentPage == totalPages){
			Toast.makeText(getBaseContext(), "已经是最后一页了", 0).show();
			return;
		}
		currentPage++;
		initDate();
	}

	public void tiao(View v) {
		String pageStr = et_jumppage.getText().toString().trim();
		int page = Integer.parseInt(pageStr);
		if(page < 1 || page > totalPages){
			ShowToast.show("您输入的已经超出范围了", WebBlackActivity.this);
			return ;
		}
		currentPage = page;
		initDate();
	}

	private void initView() {
		setContentView(R.layout.activity_webblack);
		// 添加黑名单的按钮
		iv_add = (ImageView) findViewById(R.id.iv_black_add);
		// 加载数据
		ll_loading = (LinearLayout) findViewById(R.id.ll_progressbar_root);
		// 显示数据
		lv_showdata = (ListView) findViewById(R.id.lv_black_showdata);
		// 没有数据
		iv_noData = (ImageView) findViewById(R.id.iv_black_nodata);

		mAdapter = new MyAdapter();
		lv_showdata.setAdapter(mAdapter);
		// new一次,所以放在initView

		mBlackDao = new BlackDao(getApplicationContext());

		et_jumppage = (EditText) findViewById(R.id.et_webblack_topage);
		tv_pagemess = (TextView) findViewById(R.id.tv_web_black_pagemess);
	}
}
