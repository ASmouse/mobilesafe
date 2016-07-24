package com.example.mobliesafe.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobliesafe.R;
import com.example.mobliesafe.utils.MyContains;
import com.example.mobliesafe.utils.SPUtils;

public class ShowLocationStyleDialog extends Dialog{

	SettingCenterItem mSci;
	
	public static final  String[] styleNames = new String[]{"半透明","活力橙","卫士蓝","金属灰","苹果绿"};
	public static final  int[]  bgColors = new int[]{R.drawable.call_locate_white,R.drawable.call_locate_orange
			,R.drawable.call_locate_blue,R.drawable.call_locate_gray,R.drawable.call_locate_green};
	
	private ListView lv_dialog;
	private MyAdapter adapter;

	public ShowLocationStyleDialog(Context context, int theme) {
		super(context, theme);
		//!!!!!!!!!设置样式
		Window mWindow = getWindow();
		
		LayoutParams attributes = mWindow.getAttributes();
		attributes.gravity= Gravity.BOTTOM;
		mWindow.setAttributes(attributes);
		
	}

	public ShowLocationStyleDialog(Context context,SettingCenterItem sci_show) {
		this(context,R.style.LocationStyle);
		mSci = sci_show;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initView();
		initData();
		initEvent();
		
	}

	private void initView() {
		setContentView(R.layout.dialog_locationstyle);
		lv_dialog = (ListView) findViewById(R.id.lv_dialog_locationstyle);
	}

	private void initData() {
		adapter = new MyAdapter();
		
		lv_dialog.setAdapter(adapter);
	}
	
	private class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return styleNames.length;
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
		class ViewHolder{
			TextView tv_content;
			View iv_icon;
			ImageView iv_select;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder= null;
			if(convertView == null){
				convertView = View.inflate(getContext(), R.layout.item_locationstyle_lv, null);
				
				holder = new ViewHolder();
				 holder.tv_content = (TextView) convertView.findViewById(R.id.tv_item_locationstyle_name);
				 holder.iv_icon = (View) convertView.findViewById(R.id.v_item_locationstyle_lv);
				 holder.iv_select = (ImageView) convertView.findViewById(R.id.iv_item_locationstyle_select);
				 
				 convertView.setTag(holder);
			}else{
				 holder = (ViewHolder) convertView.getTag();
			}
			
			//设置数据
			holder.tv_content.setText(styleNames[position]);
			holder.iv_icon.setBackgroundResource(bgColors[position]);
			
			if(SPUtils.getInt(getContext(), MyContains.LOCATIONSTYLEINDEX, 0)==position){
				holder.iv_select.setVisibility(View.VISIBLE);
			}else{
				holder.iv_select.setVisibility(View.GONE);

			}
			
			
			return convertView;
		}
		
	}
	
	
	private void initEvent() {
		// TODO Auto-generated method stub
		lv_dialog.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				SPUtils.putInt(getContext(), MyContains.LOCATIONSTYLEINDEX, position);
				mSci.setConText("归属地风格显示("+styleNames[position]+")");
				//这个类集成自Dialog
				dismiss();
			}
		});
	}
	
	



}
