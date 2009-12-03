package com.appspot.baotwits.client.data.dto.facebook;

import java.util.ArrayList;

public class FacebookUserDto {
	private String key;
	private String facebookId;
	private String twitterUser;
	private String twitterLoginURL;
	
	private ArrayList<StatusDto> statuses;
	
	public void setKey(String key) {
		this.key = key;
	}
	public String getKey() {
		return key;
	}
	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}
	public String getFacebookId() {
		return facebookId;
	}
	public void setTwitterUser(String twitterUser) {
		this.twitterUser = twitterUser;
	}
	public String getTwitterUser() {
		return twitterUser;
	}
	
	public void setStatuses(ArrayList<StatusDto> statuses) {
		this.statuses = statuses;
	}
	public ArrayList<StatusDto> getStatuses() {
		return statuses;
	}
	public void setTwitterLoginURL(String twitterLoginURL) {
		this.twitterLoginURL = twitterLoginURL;
	}
	public String getTwitterLoginURL() {
		return twitterLoginURL;
	}
}
