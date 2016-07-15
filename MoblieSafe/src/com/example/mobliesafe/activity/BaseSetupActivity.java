package com.example.mobliesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.example.mobliesafe.R;

public abstract class BaseSetupActivity extends Activity {
	private GestureDetector mGD;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		initData();
		initEvent();

		initGuesture();
	}

	/**
	 * 添加手势识别器
	 */
	@SuppressWarnings("deprecation")
	private void initGuesture() {
		// 需要绑定Touch事件,onTouchEvent
		mGD = new GestureDetector(new MyOnGestureListener() {
			// 重写onFling()
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// e1 事件,可得到按下的点
				// e2事件,可得到 松开的点
				// velocityX x方向速度 单位px/s
				// velocityY y方向的速度

				// 是否是X轴方向的滑动,dx > dy 说明x轴偏移较大
				if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY() - e2.getY())) {
					// 横向滑动
					// 判断速度 >50算有效滑动
					if (Math.abs(velocityX) > 50) {
						// 判断方向
						if (velocityX < 0) {
							// 从左往右滑
							//不用参数,传递null(不存在方法重载可以这样用)
							prevPage(null);
							
						} else {
							// 从右往左滑
							nextPage(null);
							
						}
					}
				}

				// 返回true
				return true;
			}
		});
	}

	//事件的传递机制
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if(mGD != null){
			mGD.onTouchEvent(event);
			return true; 
					
					 
		}
		
		return super.onTouchEvent(event);
	}
	
	
	
	/**
	 * 跳转指定页面
	 * 
	 * @param type
	 *            需要跳转的页面
	 */
	public void startPage(Class type) {
		Intent intent = new Intent(this, type);
		startActivity(intent);
		finish(); // 关闭自己,如果返回直接到主界面!!!!
	}

	/**
	 * 声明抽象,强制实现此方法
	 */
	protected abstract void startNext();

	/**
	 * 跳转到下一个界面的按钮事件,内含跳转页面和跳转动画两个方法: 1.跳转动画由于都一样故子类直接调用(点击按钮调用,子类不用重写)
	 * 2.而跳转页面子类需求不一样,所以声明startNext()为抽象方法,强制子类重写实现自己的功能,并且提供了startPage(Class
	 * type)发发来方便子类调用,传入参数即可跳转到指定的页面
	 * 
	 * @param v
	 */
	public void nextPage(View v) {
		// 下一个页面
		startNext();

		// 跳转动画(位移动画),动画仅限于页面切换时执行,页面不切换就不执行
		nextPageAnimation();
	}

	/**
	 * 翻页的动画,一个API就行了
	 */
	public void nextPageAnimation() {
		// !!!!!!!!翻页动画!!!!!!!
		overridePendingTransition(R.anim.next_enter_anim, R.anim.next_exit_anim);

	}

	/**
	 * 子类覆盖此方法,完成数据的初始化
	 */
	protected void initData() {
	}

	/**
	 * 子类覆盖此方法,完成事件的初始化
	 */
	protected void initEvent() {
		

	}

	/**
	 * 子类需要覆盖此方法,完成界面的显示
	 */
	protected void initView() {
		// 界面显示
	}

	public void prevPage(View v) {
		// 上一个页面
		startPrev();

		// 跳转动画(位移动画)
		prePageAnimation();
	}

	protected abstract void startPrev();
	
	private void prePageAnimation() {
		// !!!!!!!!翻页动画!!!!!!!
		overridePendingTransition(R.anim.prev_exit_anim, R.anim.prev_enter_anim);

	}

	// 为了简化手势监听器,直接new 重写的方法太多..import
	// android.view.GestureDetector.OnGestureListener;
	// 上面想覆盖哪个方法就覆盖那个
	private class MyOnGestureListener implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			return false;
		}

	}

}
