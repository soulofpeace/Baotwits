package com.appspot.baotwits.server.data.model.comment;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

import java.util.Date;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
public class Comment {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String statusId;
	
	@Persistent
	private Date date;
	
	@Persistent
	private String text;
	
	@Persistent
	@Extension(vendorName="datanucleus", key="gae.parent-pk", value="true")
	private Key commentorWrapper;


	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getStatusId() {
		return statusId;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}


	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}

	public void setCommentorWrapper(Key commentorWrapper) {
		this.commentorWrapper = commentorWrapper;
	}

	public Key getCommentorWrapper() {
		return commentorWrapper;
	}
}
