package com.example.mobliesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.view.WindowId.FocusObserver;
import android.widget.TextView;

/**
 * @author jacksonCao
 * @data 2016-7-11
 * @desc 滚动文字 2.以类的方式  
 * 	前提: android:ellipsize="marquee"

 * @version  $Rev: 7 $
 * @author  $Author: caojun $
 * @Date  $Date: 2016-07-12 20:02:45 +0800 (周二, 12 七月 2016) $
 * @Id	$ID$
 * @Url  $URL: https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example/mobliesafe/view/FocusTextView.java $
 */
public class FocusTextView extends TextView{

	/**配置文件(Layout)反射调用
	 * @param context
	 * @param attrs
	 */
	public FocusTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	/**代码中实例化调用
	 * @param context
	 */
	public FocusTextView(Context context) {
		super(context);
	}

	//永远获取焦点
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		//永远获取焦点
		return true;
	}
	

}
