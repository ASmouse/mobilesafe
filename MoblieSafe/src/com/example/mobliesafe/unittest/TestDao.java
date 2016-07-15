package com.example.mobliesafe.unittest;

import com.example.mobliesafe.dao.ContactsDao;
import com.example.mobliesafe.utils.ServiceUtils;

import android.test.AndroidTestCase;

public class TestDao extends AndroidTestCase{
	public void testgetContacts(){
//		ContactsDao.getContacts(getContext());
		
		ServiceUtils.isServiceRunning(getContext(), "");
	}
}
