package com.appspot.baotwits.client.data.dto;


import com.google.gwt.user.client.rpc.IsSerializable;

public class LoginInfoDto implements IsSerializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6253654388997574454L;
	/**
	 * 
	 */
	
	private boolean loggedIn = false;
	private String loginUrl;
	private String logoutUrl;
	private String emailAddress;
	private String nickname;
	private int twitUid;
	private boolean twitLogin = false;
	private String twitAuthURL;

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}

	public String getLoginUrl() {
		return loginUrl;
	}

	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public String getLogoutUrl() {
		return logoutUrl;
	}

	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	public int getTwitUid(){
		return this.twitUid;
	}
	
	public void setTwitUid(int twitUid){
		this.twitUid = twitUid;
		
	}

	public void setTwitLogin(boolean twitLogin) {
		this.twitLogin = twitLogin;
	}

	public boolean isTwitLogin() {
		return twitLogin;
	}

	public void setTwitAuthURL(String twitAuthURL) {
		this.twitAuthURL = twitAuthURL;
	}
	

	public String getTwitAuthURL() {
		return twitAuthURL;
	}
}
