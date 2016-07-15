package com.example.mobliesafe.domain;

public class ContactBean {
	private String name;
	private String phone;
	
	@Override
	public String toString() {
		return "ContactBean [name=" + name + ", phone=" + phone + "]";
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	
}
