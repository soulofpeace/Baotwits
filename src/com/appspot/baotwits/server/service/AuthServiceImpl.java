package com.appspot.baotwits.server.service;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.appspot.baotwits.client.data.dto.LoginInfoDto;
import com.appspot.baotwits.client.proxy.AuthService;
import com.appspot.baotwits.server.constants.Constants;
import com.appspot.baotwits.server.data.dao.TwitUserDao;
import com.appspot.baotwits.server.data.model.TwitUser;



public class AuthServiceImpl extends RemoteServiceServlet implements AuthService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3207237536025695746L;
	private static final Logger logger = Logger.getLogger(AuthServiceImpl.class.getName());
	private TwitUserDao twitUserDao;
	private static ThreadLocal<HttpServletRequest> servletRequest = new ThreadLocal<HttpServletRequest>();
	private static ThreadLocal<HttpServletResponse> servletResponse = new ThreadLocal<HttpServletResponse>();
	
	
	
	public void setRequest(HttpServletRequest request){
		logger.info("Setting Request in auth");
		servletRequest.set(request);
	}
	
	public HttpServletRequest getRequest(){
		return servletRequest.get();
	}
	
	public HttpServletResponse getResponse(){
		return servletResponse.get();
	}
	
	public void setResponse(HttpServletResponse response){
		logger.info("Setting Reponse in auth");
		servletResponse.set(response);
	}
	
	public void setTwitUserDao(TwitUserDao twitUserDao){
		this.twitUserDao =twitUserDao;
	}
	
	
	public LoginInfoDto login(String requestUri) {
		this.logger.info("In Login Method");
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		LoginInfoDto loginInfo = new LoginInfoDto();

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

	

	private void checkTwitterLogin(User user, LoginInfoDto loginInfo){
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
					String twitLoginUrl = this.getTwitAuthorizationLoginURL(twitter, user);
					loginInfo.setTwitLogin(false);
					loginInfo.setTwitAuthURL(twitLoginUrl);
				}
			}
			else{
				String twitLoginUrl = this.getTwitAuthorizationLoginURL(twitter, user);
				loginInfo.setTwitLogin(false);
				loginInfo.setTwitAuthURL(twitLoginUrl);
			}
		}
		else{
			String twitLoginUrl = this.getTwitAuthorizationLoginURL(twitter, user);
			loginInfo.setTwitLogin(false);
			loginInfo.setTwitAuthURL(twitLoginUrl);
		}
	}

	private String getTwitAuthorizationLoginURL(Twitter twitter, User user){
		
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
			logger.info("in get Session 2");
			this.getSession().setAttribute(user.getUserId(), requestToken);
			return requestToken.getAuthorizationURL();
		}
		else{
			return null;
		}
	}

	
	
	private HttpSession getSession() {
		// Get the current request and then return its session
		logger.info("in get Session");
		return this.getRequest().getSession();
	}
	
	
	


}
