package com.appspot.baotwits.server.data.model.comment;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType=IdentityType.APPLICATION, detachable="true")
public class FBCommentor implements CommentorInterface{
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	@Persistent
	private String name;
	@Persistent
	private int fbId;
	@Persistent
	private String url;
	@Persistent
	private String profileImageURL;
	@Persistent(defaultFetchGroup = "true")
	private Key commentorWrapper;
	
	
	public void setId(Key id) {
		this.id = id;
	}
	public Key getId() {
		return id;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setFbId(int fbId) {
		this.fbId = fbId;
	}
	public int getFbId() {
		return fbId;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
	public void setProfileImageURL(String profileImageURL) {
		this.profileImageURL = profileImageURL;
	}
	public String getProfileImageURL() {
		return profileImageURL;
	}
	@Override
	public String getURL() {
		// TODO Auto-generated method stub
		return this.url;
	}
	public void setCommentorWrapper(Key commentorWrapper) {
		this.commentorWrapper = commentorWrapper;
	}
	public Key getCommentorWrapper() {
		return commentorWrapper;
	}

}
