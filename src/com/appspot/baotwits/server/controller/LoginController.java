package com.appspot.baotwits.server.controller;

import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import twitter4j.TwitterException;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

import com.appspot.baotwits.server.data.dao.TwitUserDao;
import com.appspot.baotwits.server.data.dao.TwitUserDaoImpl;
import com.appspot.baotwits.server.data.model.TwitUser;
import com.appspot.baotwits.server.servlet.LoginServlet;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@Controller
public class LoginController {
	
	private static final Logger logger = Logger.getLogger(LoginController.class.getName());
	@Autowired
	private TwitUserDao twitUserDao;
	
	
	@RequestMapping(value="/login", method=RequestMethod.GET)
	public String login(HttpSession session){
		logger.info("In login method");
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user != null) {
			logger.info("Able to get User in login Service");
			this.setTwitAccessToken(session, user);
		}
		return "redirect:/";
	}
	
	
	private void setTwitAccessToken(HttpSession httpSession, User user) {
		// TODO Auto-generated method stub
		logger.info("Setting Token");
		RequestToken requestToken = (RequestToken)httpSession.getAttribute(user.getUserId());
		logger.info("RequestToken is "+requestToken.getToken());
		try {
			AccessToken accessToken= requestToken.getAccessToken();
			logger.info("AccessToken is "+accessToken.getToken());
			TwitUser twitUser = twitUserDao.getTwitUser(user);
			if (twitUser ==null){
				logger.info("How come no user?");
				twitUser = new TwitUser();
				twitUser.setUser(user);
				twitUser.setAccessToken(accessToken);
				twitUserDao.saveTwitUser(twitUser);
			}
			else{
				twitUser.setAccessToken(accessToken);
				twitUserDao.saveTwitUser(twitUser);
			}
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			 if(401 == e.getStatusCode()){
		          logger.warning("Unable to get AccessToken");
		       }
			 else{
		          logger.warning(e.getMessage());
			 }
		}

		
	}
	
}
