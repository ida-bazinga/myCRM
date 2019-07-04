package com.haulmont.thesis.crm.core.app.unisender.entity;

public class Campaign {
	protected Integer id;
	protected String start_time;
	protected String status;
	protected Integer message_id;
	protected Integer list_id;
	protected String subject;
	protected String sender_name;
	protected String sender_email;
	protected String stats_url;



	//protected Integer count;

	public Campaign() {
		super();
	}

	public Campaign(Integer id) {
		super();
		this.id = id;
	}


	//public Campaign(Integer id, String status, Integer count) {
	public Campaign(Integer id, String status) {
		super();
		this.id = id;
		this.status = status;
		//this.count = count;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
/*
	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}
	*/

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public Integer getMessage_id() {
		return message_id;
	}

	public void setMessage_id(Integer message_id) {
		this.message_id = message_id;
	}

	public Integer getList_id() {
		return list_id;
	}

	public void setList_id(Integer list_id) {
		this.list_id = list_id;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getSender_name() {
		return sender_name;
	}

	public void setSender_name(String sender_name) {
		this.sender_name = sender_name;
	}

	public String getSender_email() {
		return sender_email;
	}

	public void setSender_email(String sender_email) {
		this.sender_email = sender_email;
	}

	public String getStats_url() {
		return stats_url;
	}

	public void setStats_url(String stats_url) {
		this.stats_url = stats_url;
	}

}
