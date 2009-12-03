package com.appspot.baotwits.server.data.model;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import twitter4j.http.AccessToken;

import com.appspot.baotwits.server.data.model.facebook.FacebookUser;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
public class TwitterUser {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private String token;
	
	@Persistent
	private String tokenSecret;
	
	@Persistent
	private String twitUserId;
	
	@Persistent
	@Extension(vendorName="datanucleus", key="gae.parent-pk", value="true")
	private Key facebookUserKey;
	


	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return this.token;
	}
	
	public void setTokenSecret(String tokenSecret){
		this.tokenSecret = tokenSecret;
	}
	
	public String getTokenSecret(){
		return this.tokenSecret;
	}
	
	public AccessToken getAccessToken(){
		if (this.token!=null && this.tokenSecret!=null)
			return new AccessToken(this.token, this.tokenSecret);
		else
			return null;
	}
	
	public void setAccessToken(AccessToken at){
		this.token=at.getToken();
		this.tokenSecret=at.getTokenSecret();
	}

	public void setTwitUserId(String twitUserId) {
		this.twitUserId = twitUserId;
	}

	public String getTwitUserId() {
		return twitUserId;
	}


	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}

	public void setFacebookUserKey(Key facebookUserKey) {
		this.facebookUserKey = facebookUserKey;
	}

	public Key getFacebookUserKey() {
		return facebookUserKey;
	}


}
