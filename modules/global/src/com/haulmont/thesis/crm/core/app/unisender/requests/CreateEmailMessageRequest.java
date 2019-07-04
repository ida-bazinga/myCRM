package com.haulmont.thesis.crm.core.app.unisender.requests;

import com.haulmont.thesis.crm.core.app.unisender.entity.EmailMessage;
import com.haulmont.thesis.crm.core.app.unisender.entity.MailList;

public class CreateEmailMessageRequest extends CreateMessageRequest {
	private EmailMessage emailMessage;

	public CreateEmailMessageRequest(EmailMessage emailMessage, MailList listId) {
		this.emailMessage = emailMessage;
		this.listId = listId;
	}

	public EmailMessage getEmailMessage() {
		return emailMessage;
	}

	public void setEmailMessage(EmailMessage emailMessage) {
		this.emailMessage = emailMessage;
	}	
}
