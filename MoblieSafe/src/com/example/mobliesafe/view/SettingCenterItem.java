package com.example.mobliesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.mobliesafe.R;

public class SettingCenterItem extends LinearLayout {

	private TextView tv_desc;
	private View rootView;
	private ImageView iv_toggle;

	private boolean isOpen = false;

	public SettingCenterItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView();
		initDate(attrs);
		initEvent();

	}

	//暴露接口
	public interface OnToggleChangedListener {
		void onToggleChange(View v,boolean isOpen);

//		void onToggleChange(boolean isOpen);
	}
	//
	private OnToggleChangedListener mOnToggleChangedListener;
	//不用static 调用findViewById已经获取到了实例
	public void setOnToggleChangedListener(OnToggleChangedListener listener){
		this.mOnToggleChangedListener=listener;
	}  
	
	public void setToggleOn(boolean isOpen){
		this.isOpen = isOpen;
		if(isOpen){
			iv_toggle.setImageResource(R.drawable.on);
		}else{
			iv_toggle.setImageResource(R.drawable.off);
		}
	}
	private void initEvent() {
		rootView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				isOpen = !isOpen;
				
				if(isOpen){
					iv_toggle.setImageResource(R.drawable.on);
				}else{
					iv_toggle.setImageResource(R.drawable.off);
				}
				
				//!!!!!!一定是放在这里!!!!!!!
				if(mOnToggleChangedListener !=null){
					
					mOnToggleChangedListener.onToggleChange(SettingCenterItem.this, isOpen);
				}
			}
		});
		
	
		
	}

	

	
	
	private void initDate(AttributeSet attrs) {
		// 取出属性
		String desc = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.example.mobliesafe",
				"desc");
		// 设置属性
		tv_desc.setText(desc);

		
		String bgselectors = attrs.getAttributeValue(
				"http://schemas.android.com/apk/res/com.example.mobliesafe",
				"bgselector");
		switch (Integer.parseInt(bgselectors)) {
		case 0:
			rootView.setBackgroundResource(R.drawable.iv_first_selector);
			break;
		case 1:
			rootView.setBackgroundResource(R.drawable.iv_middle_selector);
			break;
		case 2:
			rootView.setBackgroundResource(R.drawable.iv_last_selector);
			break;
	
		}
		
		
		
	}

	private void initView() {
		rootView = View.inflate(getContext(), R.layout.view_setting_item_view,
				this);
		tv_desc = (TextView) rootView
				.findViewById(R.id.tv_view_settingview_item_desc);
		iv_toggle = (ImageView) rootView
				.findViewById(R.id.iv_view_settingview_item_taggle);
	}

	public SettingCenterItem(Context context) {
		this(context, null);

	}

}

