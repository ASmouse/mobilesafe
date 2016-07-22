package com.example.mobliesafe.activity;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.IvParameterSpec;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mobliesafe.R;
import com.example.mobliesafe.dao.AddressDao;
import com.example.mobliesafe.domain.NumberAndName;
import com.example.mobliesafe.domain.ServiceNameAndType;

public class ServiceNumberActivity extends Activity {
	protected static final int LOADING = 1;
	protected static final int FINISH = 2;
	private ExpandableListView elv_showmess;
	// 保存elv的组数据 和 每组对应的子数据
	// private HashMap<ServiceNameAndType, List<NumberAndName>> mDatas = new
	// HashMap<ServiceNameAndType, List<NumberAndName>>();

	// hashmap不会用用别的, List是有序的怎么放怎去,不会乱套
	List<ServiceNameAndType> mServiceNameAndTypes = new ArrayList<ServiceNameAndType>();
	// 对应关系!!!!!!!!!
	List<List<NumberAndName>> mNumberAndName = new ArrayList<List<NumberAndName>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initView();

		initData();
		
		initEvent();
	}

	private void initEvent() {
		elv_showmess.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				
				//获取当前数据,拨打电话
				NumberAndName numberAndName = mNumberAndName.get(groupPosition).get(childPosition);
				String number = numberAndName.getNumber();
				
				Intent call = new Intent(Intent.ACTION_CALL);
				call.setData(Uri.parse("tel:"+ number));
				startActivity(call);
				//
				return true;
			}
		});
	}

	// Handler处理结果
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case LOADING:
System.out.println("LOADING");
				ll_loading.setVisibility(View.VISIBLE);
				elv_showmess.setVisibility(View.GONE);
				break;
			case FINISH:
System.out.println("FINISH");				
				elv_showmess.setVisibility(View.VISIBLE);
				ll_loading.setVisibility(View.GONE);

				// 刷新数据

				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}

		};
	};
	private LinearLayout ll_loading;
	private MyAdapter mAdapter;

	private void initData() {
		// 子线层取数据
		new Thread() {
			public void run() {
				// 1.取数据的消息

				mHandler.obtainMessage(LOADING).sendToTarget();
				// 2.取数据
				List<ServiceNameAndType> allServiceTypes = AddressDao
						.getAllServiceTypes();
				mServiceNameAndTypes = allServiceTypes;
				for (ServiceNameAndType serviceNameAndType : allServiceTypes) {
					List<NumberAndName> numberAndName = AddressDao
							.getNumberAndName(serviceNameAndType);
					// 添加数据
					// mDatas.put(serviceNameAndType, numberAndName);

					mNumberAndName.add(numberAndName);
				}

				// 3.取数据完成的消息
				mHandler.obtainMessage(FINISH).sendToTarget();

			};

		}.start();
	}

	private class MyAdapter extends BaseExpandableListAdapter {

		// 获取多少个组!!!!!!!
		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return mServiceNameAndTypes.size();
		}

		// 每个组有多少数据!!!!!!!!
		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return mNumberAndName.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public NumberAndName getChild(int groupPosition, int childPosition) {
			//
			return mNumberAndName.get(groupPosition).get(childPosition);

		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		// 组的显示!!!!!!!!!!!
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView tv = null;
			if (convertView == null) {
				tv = new TextView(getApplicationContext());
				tv.setTextSize(20);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.GRAY);

			} else {
				tv = (TextView) convertView;
			}

			// 赋值
			tv.setText(mServiceNameAndTypes.get(groupPosition).getName());

			return tv;
		}

		// 子View的显示!!!!!!!!!!!
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView tv = null;
			if (convertView == null) {
				tv = new TextView(getApplicationContext());
				tv.setTextSize(18);
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundColor(Color.BLUE);

			} else {
				tv = (TextView) convertView;
			}

			//添加状态选择器!!!!!!!!!!
			tv.setBackgroundResource(R.drawable.dialog_btn_seletor);
			
			// 赋值
			tv.setText(mNumberAndName.get(groupPosition).get(childPosition)
					.getName()
					+ "\t"
					+ mNumberAndName.get(groupPosition).get(childPosition)
							.getNumber());

			return tv;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// AUTO  true子view可以点击
			return true;
		}

	}

	private void initView() {
		setContentView(R.layout.activity_servicenumber);
		elv_showmess = (ExpandableListView) findViewById(R.id.elv_servicenumber_show);
		ll_loading = (LinearLayout) findViewById(R.id.ll_progressbar_root);
		
		
		mAdapter = new MyAdapter();
		elv_showmess.setAdapter(mAdapter);
	}
}
