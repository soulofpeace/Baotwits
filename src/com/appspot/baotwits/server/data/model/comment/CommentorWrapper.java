package com.appspot.baotwits.server.data.model.comment;

import java.util.Set;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType = IdentityType.APPLICATION, detachable="true")
public class CommentorWrapper {
	
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key commenterId;
	
	@Persistent
	private String type;
	
	@Persistent(defaultFetchGroup = "true")
	private Set<Key> comments;
	
	@Persistent
	private String name;
	
	@Persistent
	private String url;
	
	@Persistent
	private String profileImageURL; 
	
	@Persistent
	@Extension(vendorName="datanucleus", key="gae.parent-pk", value="true")
    private Key commentorInstance;
	
	
	public void setCommenterId(Key commenterId) {
		this.commenterId = commenterId;
	}

	public Key getCommenterId() {
		return commenterId;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}


	public void setCommentorInstance(Key commentorInstance) {
		this.commentorInstance = commentorInstance;
	}

	public Key getCommentorInstance() {
		return commentorInstance;
	}
	
	public void setComments(Set<Key> comments){
		this.comments = comments;
	}
	
	public Set<Key> getComments(){
		return this.comments;
	}
	
	public void addComment(Key comment){
		this.comments.add(comment);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
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

}
