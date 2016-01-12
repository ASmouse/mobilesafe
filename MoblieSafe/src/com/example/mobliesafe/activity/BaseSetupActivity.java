package com.example.mobliesafe.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public abstract class BaseSetupActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initView();
		initData();
		initEvent();
		
	}
	
	/**跳转指定页面
	 * @param type 需要跳转的页面
	 */
	public void startPage(Class type){
		Intent intent = new Intent(this,type);
		startActivity(intent);
		finish(); //关闭自己,如果返回直接到主界面!!!!
	}
	
	/**
	 * 声明抽象,强制实现此方法
	 */
	protected abstract void startNext();

	
	/**跳转到下一个界面的按钮事件,内含跳转页面和跳转动画两个方法:
	 * 1.跳转动画由于都一样故子类直接调用(点击按钮调用,子类不用重写)
	 * 2.而跳转页面子类需求不一样,所以声明startNext()为抽象方法,强制子类重写实现自己的功能,并且提供了startPage(Class type)发发来方便子类调用,传入参数即可跳转到指定的页面
	 * @param v
	 */
	public void nextPage(View v){
		//下一个页面
		startNext();
		
		//跳转动画(位移动画)
		nextPageAnimation();
	}
	
	

	private void nextPageAnimation() {
		// TODO Auto-generated method stub
		
	}


	/**
	 * 子类覆盖此方法,完成数据的初始化
	 */
	private void initData() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 子类覆盖此方法,完成事件的初始化
	 */
	protected void initEvent() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 子类需要覆盖此方法,完成界面的显示
	 */
	protected void initView() {
		//界面显示
	}
}
