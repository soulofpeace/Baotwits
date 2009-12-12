package com.appspot.baotwits.server.data.model.facebook;

import java.util.Date;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
public class FBComment {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String statusId;
	
	@Persistent
	private Date dateCreated;
	
	@Persistent
	private Date dateModified;
	
	@Persistent
	private String text;
	
	@Persistent(defaultFetchGroup = "true")
	@Extension(vendorName="datanucleus", key="gae.parent-pk", value="true")
	private Key facebookUserKey;

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}

	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}

	public String getStatusId() {
		return statusId;
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

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setFacebookUserKey(Key facebookUserKey) {
		this.facebookUserKey = facebookUserKey;
	}

	public Key getFacebookUserKey() {
		return facebookUserKey;
	}
	

}
