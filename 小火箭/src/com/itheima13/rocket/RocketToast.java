package com.itheima13.rocket;


import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.SystemClock;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

/**
 * @author Administrator
 * @desc 自定义Toast
 * @date 2015-9-18
 * @Author $Author: goudan $ Id Rev URL
 * @Date $Date: 2015-09-20 11:31:40 +0800 (Sun, 20 Sep 2015) $
 * @Id $Id: RocketToast.java 63 2015-09-20 03:31:40Z goudan $
 * @Rev $Rev: 63 $
 * @URL $URL: https://188.188.4.100/svn/mobilesafeserver/%E5%B0%8F%E7%81%AB%E7%AE%AD/src/com/itheima13/rocket/RocketToast.java $
 * 
 */
public class RocketToast implements OnTouchListener {

	private WindowManager mWM;
	private WindowManager.LayoutParams mParams;
	private View mView;
	private RocketService mContext ;
	private float downX;
	private float downY;

	public RocketToast(RocketService context) {
		// 0. WindowManager
		mContext = context;
		mWM = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

		// 1. params
		mParams = new WindowManager.LayoutParams();
		mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		mParams.format = PixelFormat.TRANSLUCENT;
		//去掉toast原装动画
		mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
		mParams.setTitle("Toast");
		mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				/*| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE*/;
		//设置gravity
		mParams.gravity = Gravity.LEFT | Gravity.TOP;//坐标原点为坐上角
		
		// 2. View
		
		
	}
	
	
	public void show() {
		 hiden();
		 mView = View.inflate(mContext, R.layout.rocket_view, null);
		
		 //帧动画播放
		 AnimationDrawable background = (AnimationDrawable) mView.getBackground();
		 background.start();//开始动画
		 mView.setOnTouchListener(this);//设置touch事件
		 //添加到窗体 mView parrent  mWM
		 mWM.addView(mView, mParams);
	}
	
	public void hiden(){
		if (mView != null) {
            // note: checking parent() just to make sure the view has
            // been added...  i have seen cases where we get here when
            // the view isn't yet added, so let's try not to crash.
            if (mView.getParent() != null) {
                //mView赋值 并且加到mWM   没有mWM.addView(mView, mParams);
                mWM.removeView(mView);
            }

            mView = null;
        }
		
	}



	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
		// 触摸事件
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downX = event.getRawX();
			downY = event.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			
			float moveX = event.getRawX();
			float moveY = event.getRawY();
			
			float dx = moveX - downX;
			float dy = moveY - downY;
			
			//改变toast的参�?
			mParams.x += dx;
			mParams.y += dy;
			
			// x y新的坐标
			//判断越界
			if(mParams.x  < 0 ) {
				mParams.x = 0;
			} else if (mParams.x > mWM.getDefaultDisplay().getWidth() - mView.getWidth()) {
				mParams.x = mWM.getDefaultDisplay().getWidth() - mView.getWidth();
			}
			
			if (mParams.y < 0) {
				mParams.y = 0;
			} else if (mParams.y > mWM.getDefaultDisplay().getHeight() - mView.getHeight()) {
				mParams.y = mWM.getDefaultDisplay().getHeight() - mView.getHeight();
			}
			
			//改变view的位置
			mWM.updateViewLayout(mView, mParams);
			
			//该位置变为新的起始位置
			downX = moveX;
			downY = moveY;
			
			
			break;
		case MotionEvent.ACTION_UP:
			
			//是否发射
			//判断位置
			int height = mWM.getDefaultDisplay().getHeight();
			if (mParams.y > height * 5 / 8) {
				//超过高度的5/8发射
				System.out.println("发射");
				//烟的显示
				smokeView();
				
				//移动小火箭
				rocketMove();
			}
			break;

		default:
			break;
		}
		return true;//自己消费�?
	}

    private Handler mHandler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case 1:
				//更新小火箭位置
	    		mWM.updateViewLayout(mView, mParams);
				break;
			case 2:
				
				mContext.stopSelf();//关闭服务
				break;
			default:
				break;
			}
    		
    	};
    };
	private void rocketMove() {
		//从当前位置往上跑
		//中间 x坐标  屏幕宽度一半 - 小火箭宽度一半
		mParams.x = Math.round((mWM.getDefaultDisplay().getWidth() - mView.getWidth()) / 2);
		final int time = 2000 / mParams.y;
		
		new Thread(){
			public void run() {
				for (int y = mParams.y ; y >= 0; y = y - 5 ) {
					//改变mParams.y的坐标
					mParams.y = y;
					//休眠
					SystemClock.sleep(time);
					//更新view
					mHandler.obtainMessage(1).sendToTarget();
				}
				
				//发射完毕
				//关闭小火箭
				//关闭服务
				mHandler.obtainMessage(2).sendToTarget();
				
			};
		}.start();
		
		
		
	}


	private void smokeView() {
		//启动烟的Activity
		//广播或服务中启动Activity
		Intent smoke = new Intent(mContext,SmokeActivity.class);
		//设置数据//广播或服务中启动Activity
		smoke.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(smoke);
	}
}
