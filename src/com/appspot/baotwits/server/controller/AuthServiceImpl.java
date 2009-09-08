package com.appspot.baotwits.server.controller;

import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.appspot.baotwits.client.data.dto.LoginInfo;
import com.appspot.baotwits.client.proxy.AuthService;
import com.appspot.baotwits.server.constants.Constants;
import com.appspot.baotwits.server.data.dao.TwitUserDao;
import com.appspot.baotwits.server.data.dao.TwitUserDaoImpl;
import com.appspot.baotwits.server.data.model.TwitUser;

public class AuthServiceImpl extends RemoteServiceServlet implements AuthService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3207237536025695746L;
	private static final Logger logger = Logger.getLogger(AuthServiceImpl.class.getName());
	private TwitUserDao twitUserDao = new TwitUserDaoImpl();

	public LoginInfo login(String requestUri) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		LoginInfo loginInfo = new LoginInfo();

		if (user != null) {
			loginInfo.setLoggedIn(true);
			loginInfo.setEmailAddress(user.getEmail());
			loginInfo.setNickname(user.getNickname());
			loginInfo.setLogoutUrl(userService.createLogoutURL(requestUri));
			this.checkTwitterLogin(user, loginInfo);


		} else {
			loginInfo.setLoggedIn(false);
			loginInfo.setLoginUrl(userService.createLoginURL(requestUri));
		}
		return loginInfo;
	}

	

	private void checkTwitterLogin(User user, LoginInfo loginInfo){
		Twitter twitter = new Twitter();
		twitter.setOAuthConsumer(Constants.getConsumerKey(), Constants.getConsumerSecret());
		TwitUser twitUser = twitUserDao.getTwitUser(user);
		if (twitUser !=null){
			if (twitUser.getAccessToken()!=null){
				AccessToken accessToken = twitUser.getAccessToken();
				twitter.setOAuthAccessToken(accessToken);
				try{
					twitter4j.User twitU = twitter.verifyCredentials();
					loginInfo.setTwitUid(twitU.getId());
					loginInfo.setTwitLogin(true);
				}
				catch(TwitterException te){
					String twitLoginUrl = this.getTwitLoginURL(twitter, user);
					loginInfo.setTwitLogin(false);
					loginInfo.setTwitAuthURL(twitLoginUrl);
				}
			}
			else{
				String twitLoginUrl = this.getTwitLoginURL(twitter, user);
				loginInfo.setTwitLogin(false);
				loginInfo.setTwitAuthURL(twitLoginUrl);
			}
		}
		else{
			String twitLoginUrl = this.getTwitLoginURL(twitter, user);
			loginInfo.setTwitLogin(false);
			loginInfo.setTwitAuthURL(twitLoginUrl);
		}
	}

	private String getTwitLoginURL(Twitter twitter, User user){
		
		RequestToken requestToken=null;
		int count= 1000;
		while(requestToken==null){
			if(count <0){
				break;
			}
			try {
				requestToken = twitter.getOAuthRequestToken();
				count--;

			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				logger.warning(e.getMessage());
			}
		}
		if (requestToken!=null){
			this.getSession().setAttribute(user.getUserId(), requestToken);
			return requestToken.getAuthorizationURL();
		}
		else{
			return null;
		}
	}

	
	
	private HttpSession getSession() {
		// Get the current request and then return its session
		return this.getThreadLocalRequest().getSession();
	}
	


}
