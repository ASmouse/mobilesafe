package com.example.mobliesafe.unittest;

import java.util.List;

import com.example.mobliesafe.dao.AddressDao;
import com.example.mobliesafe.domain.ServiceNameAndType;
import com.example.mobliesafe.utils.TaskInfoUtils;

import android.test.AndroidTestCase;

public class TestAddress extends AndroidTestCase{
	/*public void testGetMobileLocation(){
		String location = AddressDao.getMobileLocation("1352303");
		System.out.println(location);
	}
	public void testGetPhoneLocation(){
		String location = AddressDao.getPhoneLocation("0371");
		System.out.println(location);
	}*/
	
	public void testGetLocation(){

		System.out.println(AddressDao.getLocation("13523032378"));
		System.out.println(AddressDao.getLocation("037163917012"));
		System.out.println(AddressDao.getLocation("059156996565"));
	}
	
	public void testGetNumberAndName(){
		List<ServiceNameAndType> allServiceTypes = AddressDao.getAllServiceTypes();
		for (ServiceNameAndType serviceNameAndType : allServiceTypes) {
			
			System.out.println(	AddressDao.getNumberAndName(serviceNameAndType));
		
		}
	}
	
	public void testRunningApp(){
		System.out.println("可用RAM:"+TaskInfoUtils.getAvailMem(getContext()));
		System.out.println("总RAM:"+ TaskInfoUtils.getTotalMem(getContext()));
		System.out.println("运行中的内存:"+TaskInfoUtils.getAllRunningApps(getContext()));
		
	}
}
