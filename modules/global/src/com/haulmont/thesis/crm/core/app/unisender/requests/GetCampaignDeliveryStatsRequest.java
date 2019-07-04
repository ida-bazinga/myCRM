package com.haulmont.thesis.crm.core.app.unisender.requests;

import com.haulmont.thesis.crm.core.app.unisender.entity.Campaign;

public class GetCampaignDeliveryStatsRequest {
	private Campaign campaign;
	private String changed_since;
	private boolean group;



	public GetCampaignDeliveryStatsRequest(Campaign campaign) {
		super();
		this.campaign = campaign;
	}

	public GetCampaignDeliveryStatsRequest(Campaign campaign,
			String changedSince) {
		this.campaign = campaign;
		changed_since = changedSince;
	}

	public GetCampaignDeliveryStatsRequest(Campaign campaign,
										   boolean group) {
		this.campaign = campaign;
		this.group = group;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public String getChanged_since() {
		return changed_since;
	}

	public void setChanged_since(String changedSince) {
		changed_since = changedSince;
	}

	public void setGroup(boolean group) {
		this.group = group;
	}

	public boolean isGroup() {
		return group;
	}
}
