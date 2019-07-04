package com.haulmont.thesis.crm.core.app.unisender.requests;

import com.haulmont.thesis.crm.core.app.unisender.entity.MailList;
import com.haulmont.thesis.crm.core.app.unisender.entity.SmsMessage;

public class CreateSmsMessageRequest extends CreateMessageRequest {
	private SmsMessage smsMessage;

	public CreateSmsMessageRequest(SmsMessage smsMessage, MailList listId) {
		this.smsMessage = smsMessage;
		this.listId = listId;
	}

	public SmsMessage getSmsMessage() {
		return smsMessage;
	}

	public void setSmsMessage(SmsMessage smsMessage) {
		this.smsMessage = smsMessage;
	}
}
