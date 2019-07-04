package com.haulmont.thesis.crm.core.app.unisender.entity;

public enum FieldType {
	STRING, TEXT, NUMBER, BOOL;

	@Override
	public String toString() {
		switch (this) {
		case STRING:
			return "string";
		case TEXT:
			return "text";
		case NUMBER:
			return "number";
		case BOOL:
			return "bool";
		default:
			return "string";
		}
	}
}
