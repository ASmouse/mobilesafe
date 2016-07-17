package com.example.mobliesafe.activity;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.IvParameterSpec;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobliesafe.R;
import com.example.mobliesafe.dao.ContactsDao;
import com.example.mobliesafe.domain.ContactBean;
import com.example.mobliesafe.utils.MyContains;

/**
 * @author jacksonCao
 * @data 2016-7-14
 * @desc 显示所有好友的信息界面,耗时加载的标准结构[1]...[6]
 * 			ListActivity自动封装了ListView组件(不用findViewByid()了)

 * @version  $Rev: 12 $
 * @author  $Author: caojun $
 * @Date  $Date: 2016-07-17 19:50:22 +0800 (周日, 17 七月 2016) $
 * @Id	$ID$
 * @Url  $URL: https://192.168.56.250/svn/mobilesafesvn/trunk/MoblieSafe/src/com/example/mobliesafe/activity/FriendActivity.java $
 */
public class FriendActivity extends BaseSmsTelFriendActivity{

	@Override
	public List<ContactBean> getDatas() {
		
		return  ContactsDao.getContacts(getApplicationContext());
	}
	
}
