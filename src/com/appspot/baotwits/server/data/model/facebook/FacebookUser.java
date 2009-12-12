package com.appspot.baotwits.server.data.model.facebook;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.appspot.baotwits.server.data.model.TwitterUser;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
public class FacebookUser {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private String facebookId;
	
	@Persistent(defaultFetchGroup = "true")
	private Key twitterUserKey;
	
	@Persistent(defaultFetchGroup = "true")
	private Set<Key> comments = new HashSet<Key>();

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setTwitterUserKey(Key twitterUserKey) {
		this.twitterUserKey = twitterUserKey;
	}

	public Key getTwitterUserKey() {
		return twitterUserKey;
	}

	public void setComments(Set<Key> comments) {
		this.comments = comments;
	}

	public Set<Key> getComments() {
		return comments;
	}
	
	public void addComment(Key commentKey){
		if(comments!=null){
			comments.size();
			this.comments.add(commentKey);
		}
		else{
			comments = new HashSet<Key>();
		}
	}
	
	
	

}
