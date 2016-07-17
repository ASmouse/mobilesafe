package com.example.mobliesafe.activity;

import java.util.List;

import com.example.mobliesafe.dao.ContactsDao;
import com.example.mobliesafe.domain.ContactBean;

public class SmsActivity extends BaseSmsTelFriendActivity {

	@Override
	public List<ContactBean> getDatas() {
		// TODO Auto-generated method stub
		return ContactsDao.getSmsContact(getApplicationContext());
	}
		
}
