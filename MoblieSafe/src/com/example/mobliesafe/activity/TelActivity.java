package com.example.mobliesafe.activity;

import java.util.List;

import com.example.mobliesafe.dao.ContactsDao;
import com.example.mobliesafe.domain.ContactBean;

public class TelActivity extends BaseSmsTelFriendActivity {

	@Override
	public List<ContactBean> getDatas() {
		return ContactsDao.getTelContact(getApplicationContext());
	}
		
}
