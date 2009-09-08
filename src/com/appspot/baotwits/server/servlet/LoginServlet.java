package com.appspot.baotwits.server.servlet;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.*;

import twitter4j.TwitterException;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

import com.appspot.baotwits.server.data.dao.TwitUserDao;
import com.appspot.baotwits.server.data.dao.TwitUserDaoImpl;
import com.appspot.baotwits.server.data.model.TwitUser;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class LoginServlet extends HttpServlet {


	private static final long serialVersionUID = 2852513192367710765L;
	private static final Logger logger = Logger.getLogger(LoginServlet.class.getName());
	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		if (user != null) {
			this.setTwitAccessToken(req.getSession(), user);
			resp.sendRedirect("/");
		}
		else{
			resp.sendRedirect("/");
		}
	}
	
	private void setTwitAccessToken(HttpSession httpSession, User user) {
		// TODO Auto-generated method stub
		RequestToken requestToken = (RequestToken)httpSession.getAttribute(user.getUserId());
		logger.info("RequestToken is "+requestToken.getToken());
		try {
			AccessToken accessToken= requestToken.getAccessToken();
			logger.info("AccessToken is "+accessToken.getToken());
			TwitUserDao twitUserDao = new TwitUserDaoImpl();
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
