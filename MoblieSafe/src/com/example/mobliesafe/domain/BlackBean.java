package com.example.mobliesafe.domain;

public class BlackBean {
	private String phone ;
	private int mode;
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	@Override
	public String toString() {
		return "BlackBean [phone=" + phone + ", mode=" + mode + "]";
	}
	
	
	
}
