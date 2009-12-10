package com.appspot.baotwits.client.data.dto.facebook;

import java.util.ArrayList;

public class FacebookUserInfo {
	private String uid;
	private String name;
	private String pic_square;
	private ArrayList<FacebookUserInfo> friends;
	
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getUid() {
		return uid;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setPic_square(String pic_square) {
		this.pic_square = pic_square;
	}
	public String getPic_square() {
		return pic_square;
	}
	public void setFriends(ArrayList<FacebookUserInfo> friends) {
		this.friends = friends;
	}
	public ArrayList<FacebookUserInfo> getFriends() {
		return friends;
	}
}
