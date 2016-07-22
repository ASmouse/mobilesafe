package com.example.mobliesafe.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.mobliesafe.R;
import com.example.mobliesafe.utils.MyContains;
import com.example.mobliesafe.utils.SPUtils;

public class MyToast implements OnTouchListener{
	private WindowManager mWM;
	private WindowManager.LayoutParams mParams;
	private View mView;
	private Context mContext;
	private float downX;
	private float downY;
	
	
	
	public MyToast(Context context){
		
		mContext = context;
		//1.mWM  WindowManager

		mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		
		//2.params 
		
		
		mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.format = PixelFormat.TRANSLUCENT;
        
        mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        mParams.setTitle("Toast");
        mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
              /*  | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE*/;
		
        mParams.gravity = Gravity.TOP | Gravity.LEFT;
        mParams.x = SPUtils.getInt(mContext, MyContains.TOASTX, 0);
        mParams.y = SPUtils.getInt(mContext, MyContains.TOASTY, 0);
	
	
	}
	
	
	
	public void show(String location){
		//每次动态添加view

		//3.view
	        
	       mView = View.inflate(mContext, R.layout.sys_toast, null);
	      
	       //添加注册事件!!!!!!!!!!!!!!!
	       mView.setOnTouchListener(this);
	       
	       TextView tv_toast= (TextView) mView.findViewById(R.id.tv_toast_text);
	       tv_toast.setText(location);
	       
		mWM.addView(mView, mParams);
	}
	
	
	public void hiden(){
		if(mView != null){
			if(mView.getParent() != null){
				//mView的父 就是mWM,防止mView虽然赋值了,但是mWM没有值也是无意义的
				mWM.removeView(mView);
			}
			//每次view要动态获取 , 防止再次进来
			mView = null;
		}
		
	
	}



	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getRawX();
			downY = event.getRawY();
			
			break;
			
		case MotionEvent.ACTION_MOVE:
			float moveX = event.getRawX();
			float moveY = event.getRawY();
			
			float dx = moveX - downY;
			float dy = moveY - downY;
			
			mParams.x +=dx;
			mParams.y +=dy;
			
			//越界判断
			if(mParams.x<0){
				mParams.x = 0;
			}else if(mParams.x > mWM.getDefaultDisplay().getWidth()-mView.getWidth()){
				mParams.x = mWM.getDefaultDisplay().getWidth()-mView.getWidth();
			}
			
			if(mParams.y<0){
				mParams.y = 0;
			}else if(mParams.y > mWM.getDefaultDisplay().getHeight()-mView.getHeight()){
				mParams.y = mWM.getDefaultDisplay().getHeight()-mView.getHeight();
			}
			//更新
			mWM.updateViewLayout(mView, mParams);
			
			downX = moveX;
			downY = moveY;
			
			break;
			
		case MotionEvent.ACTION_UP:
			
			//保存位置
			SPUtils.putInt(mContext, MyContains.TOASTX, (int)downX);
			SPUtils.putInt(mContext, MyContains.TOASTY, (int)downY);
			break;
			
		default:
			break;
		}
		
		return true;
	}
}
