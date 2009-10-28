package com.appspot.baotwits.client.data.dto.comment;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CommentorDto implements IsSerializable{
	private String profileImageURL;
	private String name;
	private String id;
	
	public void setProfileImageURL(String profileImageURL) {
		this.profileImageURL = profileImageURL;
	}
	public String getProfileImageURL() {
		return profileImageURL;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}

}
