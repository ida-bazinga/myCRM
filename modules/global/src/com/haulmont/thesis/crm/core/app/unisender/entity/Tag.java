package com.haulmont.thesis.crm.core.app.unisender.entity;

public class Tag {
	private Integer id;
	private String name;

	public Tag(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Tag(Integer id) {
		this.id = id;
	}

	public Tag(String name) {
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
