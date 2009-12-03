package com.appspot.baotwits.server.data.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import twitter4j.http.AccessToken;

import com.appspot.baotwits.server.data.model.facebook.FacebookUser;
import com.google.appengine.api.users.User;

@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class TwitUser {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	
	@Persistent
	private String token;
	
	@Persistent
	private String tokenSecret;
	
	@Persistent
	private User user;
	
	
	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

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

	public void setUser(User user) {
		this.user = user;
	}

	public User getUser() {
		return user;
	}



}
