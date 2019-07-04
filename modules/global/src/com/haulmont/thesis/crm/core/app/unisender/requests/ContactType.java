package com.haulmont.thesis.crm.core.app.unisender.requests;

public enum ContactType {
	EMAIL, PHONE;
	
	@Override
	public String toString(){
		switch (this){
		case EMAIL:
			return "email";
		case PHONE:
			return "phone";
		default:
			return "email";
		}
	}
}
