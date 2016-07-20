package com.example.mobliesafe.unittest;

import android.test.AndroidTestCase;

import com.example.mobliesafe.dao.BlackDao;
import com.example.mobliesafe.db.BlackDB;

public class TestBlack extends AndroidTestCase{
	public void testAdd(){
		BlackDao dao = new BlackDao(getContext());
		
		for(int i = 0; i<100;i++){
			
			dao.add("1200"+i, BlackDB.PHONE_MODE);
		}
	}
	
	
	public void  testFindAll(){
		BlackDao dao = new BlackDao(getContext());
		System.out.println(dao.findAll());
	}
	
	public void testCount(){
		BlackDao dao = new BlackDao(getContext());
		System.out.println(dao.getTotalRows());

	}

	public void testgetPageData(){
		BlackDao dao = new BlackDao(getContext());
		System.out.println(dao.getPageData(1,10));

	}
	
	public void testGetMode(){
		BlackDao dao = new BlackDao(getContext());
		int mode = dao.getMode("312");
		System.out.println(mode);
	}

}
