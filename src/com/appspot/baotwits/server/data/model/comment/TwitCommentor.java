package com.appspot.baotwits.server.data.model.comment;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

import twitter4j.http.AccessToken;

@PersistenceCapable(identityType=IdentityType.APPLICATION, detachable="true")
public class TwitCommentor implements CommentorInterface{
    
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key id;
	@Persistent
	private String screenName;
	@Persistent
	private String profileImageURL;
	@Persistent
	private String name;
	@Persistent
	private String location;
	@Persistent
	private int followersCount;
    @Persistent
	private int friendCount;
    @Persistent
	private int statusesCount;
    @Persistent
	private String url;
    @Persistent
	private String token;
	@Persistent
	private String tokenSecret;
	@Persistent(defaultFetchGroup = "true")
	private Key commentorWrapper;
	@Persistent
	private int twitterId;
    

	
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}
	
	public void setProfileImageURL(String profileImageURL){
		this.profileImageURL = profileImageURL;
	}
	
	public String getProfileImageURL() {
		// TODO Auto-generated method stub
		return this.profileImageURL;
	}
	
	public void setURL(String url){
		this.url = url;
	}
	
	public String getURL() {
		// TODO Auto-generated method stub
		return this.url;
	}

	
	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}
	public String getScreenName() {
		return screenName;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getLocation() {
		return location;
	}
	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}
	public int getFollowersCount() {
		return followersCount;
	}
	public void setFriendCount(int friendCount) {
		this.friendCount = friendCount;
	}
	public int getFriendCount() {
		return friendCount;
	}
	public void setStatusesCount(int statusesCount) {
		this.statusesCount = statusesCount;
	}
	public int getStatusesCount() {
		return statusesCount;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public void setTokenSecret(String tokenSecret) {
		this.tokenSecret = tokenSecret;
	}

	public String getTokenSecret() {
		return tokenSecret;
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

	public void setCommentorWrapper(Key commentorWrapper) {
		this.commentorWrapper = commentorWrapper;
	}

	public Key getCommentorWrapper() {
		return commentorWrapper;
	}

	public void setId(Key id) {
		this.id = id;
	}

	public Key getId() {
		return id;
	}

	public void setTwitterId(int twitterId) {
		this.twitterId = twitterId;
	}

	public int getTwitterId() {
		return twitterId;
	}
    


}
