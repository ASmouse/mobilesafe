package com.example.mobliesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mobliesafe.R;

public class MessProgress extends RelativeLayout{

	private RelativeLayout rl_root;
	private ProgressBar pb_progress;
	private TextView tv_mess;



	public MessProgress(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		initView();
	}
	
	public void setText(String text){
		tv_mess.setText(text);
	}
	
	public void setProgress(double progress){
		pb_progress.setProgress((int) Math.round(progress*100));
	}
	
	private void initView() {
		//!!!!!!!!!!!!this啊啊啊啊啊啊啊啊啊啊啊啊啊啊
		
		rl_root = (RelativeLayout) View.inflate(getContext(), R.layout.view_appmanager_progressbar, this);
		pb_progress = (ProgressBar) rl_root.findViewById(R.id.pb_appmanager_progressBar);
		tv_mess = (TextView) rl_root.findViewById(R.id.tv_appmanager_mess);
		pb_progress.setMax(100);
	}

	
	
	public MessProgress(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}

	
	
}
