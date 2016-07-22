package com.example.mobliesafe.domain;

public class ServiceNameAndType {
	private String name;
	private int outKey;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getOutKey() {
		return outKey;
	}
	public void setOutKey(int outKey) {
		this.outKey = outKey;
	}
	@Override
	public String toString() {
		return "ServiceNameAndType [name=" + name + ", outKey=" + outKey + "]";
	}
	
	
	
	
	
	
}
