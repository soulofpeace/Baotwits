package com.appspot.baotwits.client.data.dto;

import java.io.Serializable;
import java.util.Date;


public class Status implements Serializable, Comparable<Status>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date createdAt;
	private long id;
	private String text;
	private String source;
	private boolean isTruncated;
	private long inReplyToStatusId;
	private int inReplyToUserId;
	private boolean isFavorited;
	private String inReplyToScreenName;
	private int userId;
	private String screenName;
	private String imageProfileURL;
	
	
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getId() {
		return id;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getSource() {
		return source;
	}
	public void setTruncated(boolean isTruncated) {
		this.isTruncated = isTruncated;
	}
	public boolean isTruncated() {
		return isTruncated;
	}
	public void setInReplyToStatusId(long inReplyToStatusId) {
		this.inReplyToStatusId = inReplyToStatusId;
	}
	public long getInReplyToStatusId() {
		return inReplyToStatusId;
	}
	public void setInReplyToUserId(int inReplyToUserId) {
		this.inReplyToUserId = inReplyToUserId;
	}
	public int getInReplyToUserId() {
		return inReplyToUserId;
	}
	public void setFavorited(boolean isFavorited) {
		this.isFavorited = isFavorited;
	}
	public boolean isFavorited() {
		return isFavorited;
	}
	public void setInReplyToScreenName(String inReplyToScreenName) {
		this.inReplyToScreenName = inReplyToScreenName;
	}
	public String getInReplyToScreenName() {
		return inReplyToScreenName;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getUserId() {
		return userId;
	}
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setImageProfileURL(String imageProfileURL) {
		this.imageProfileURL = imageProfileURL;
	}
	public String getImageProfileURL() {
		return imageProfileURL;
	}
	
	public int compareTo(Status o) {
		// TODO Auto-generated method stub
		return this.createdAt.compareTo(o.getCreatedAt());
	}
	

}
