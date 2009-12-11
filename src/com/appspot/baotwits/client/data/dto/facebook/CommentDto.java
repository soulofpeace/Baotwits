package com.appspot.baotwits.client.data.dto.facebook;

import java.util.Date;

public class CommentDto {
	private String keyString;
	private Date dateCreated;
	private Date dateModified;
	private String statusId;
	private String text;
	private FacebookUserInfo facebookUserInfo;
	
	public void setKeyString(String keyString) {
		this.keyString = keyString;
	}
	public String getKeyString() {
		return keyString;
	}
	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}
	public Date getDateCreated() {
		return dateCreated;
	}
	public void setDateModified(Date dateModified) {
		this.dateModified = dateModified;
	}
	public Date getDateModified() {
		return dateModified;
	}
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}
	public String getStatusId() {
		return statusId;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	public void setFacebookUserInfo(FacebookUserInfo facebookUserInfo) {
		this.facebookUserInfo = facebookUserInfo;
	}
	public FacebookUserInfo getFacebookUserInfo() {
		return facebookUserInfo;
	}

}
