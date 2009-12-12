package com.appspot.baotwits.server.controller.facebook;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

import com.appspot.baotwits.client.data.dto.facebook.CommentDto;
import com.appspot.baotwits.client.data.dto.facebook.FacebookUserInfo;
import com.appspot.baotwits.client.data.dto.facebook.StatusDto;
import com.appspot.baotwits.client.data.dto.facebook.FacebookUserDto;
import com.appspot.baotwits.server.constants.facebook.FacebookConstants;
import com.appspot.baotwits.server.constants.facebook.TwitterConstants;
import com.appspot.baotwits.server.data.dao.TwitterUserDao;
import com.appspot.baotwits.server.data.dao.facebook.CommentDao;
import com.appspot.baotwits.server.data.dao.facebook.FacebookUserDao;
import com.appspot.baotwits.server.data.model.TwitUser;
import com.appspot.baotwits.server.data.model.TwitterUser;
import com.appspot.baotwits.server.data.model.facebook.FBComment;
import com.appspot.baotwits.server.data.model.facebook.FacebookUser;
import com.appspot.baotwits.server.util.facebook.FacebookParam;
import com.appspot.baotwits.server.util.facebook.FacebookRest;
import com.appspot.baotwits.server.util.twitter.TwitterRest;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;


@Controller
public class BaotwitsFacebookController {
	private static final Logger logger = Logger.getLogger(BaotwitsFacebookController.class.getName());
	
	@Autowired
	private FacebookUserDao facebookUserDao;
	
	@Autowired
	private TwitterUserDao twitterUserDao;
	
	@Autowired
	private CommentDao facebookCommentDao;
	
	@Autowired
	private FacebookRest facebookRest;
	
	@Autowired
	private TwitterRest twitterRest;
	
	@RequestMapping(value="/facebook", method=RequestMethod.GET)
	public String show(HttpServletRequest request, Model model){
		Enumeration<String> parameterNames = request.getParameterNames();
		while(parameterNames.hasMoreElements()){
			logger.info("Parameter Name: "+parameterNames.nextElement());
		}
		String facebookUserId = request.getParameter("fb_sig_user");
		request.getSession().setAttribute("facebookUserId", facebookUserId);
		//facebookRest.getFriends(request.getParameter(FacebookParam.USER.toString()), request.getParameter(FacebookParam.SESSION_KEY.toString()));
		
		if(this.isFirstTimeUser(facebookUserId)){
			logger.info("adding " + facebookUserId);	
			this.saveUser(facebookUserId);
		}
		else{
			logger.info("Existing User");
		}
			
		return "facebook";
	}
	
	
	@RequestMapping(value="/facebook/twitter/authorise", method=RequestMethod.GET)
	public String authoriseTwitter(HttpServletRequest request, Model model){
		String facebookUserId = (String)request.getSession().getAttribute("facebookUserId");
		RequestToken requestToken = (RequestToken) request.getSession().getAttribute("twitterOauthToken");
		FacebookUser facebookUser = facebookUserDao.getFacebookUserbyFID(facebookUserId);
		this.linkTwitterUsertoFacebookUser(requestToken, facebookUser);
		logger.info("redirecting");
		model.addAttribute("url", FacebookConstants.getCanvasURL());
		return "afterTwitterLogin";
	}
	
	@RequestMapping(value="/facebook/login", method=RequestMethod.GET)
	public String login(Model model){
		model.addAttribute("url", facebookRest.getFacebookCanvasLoginURL());
		return "facebookLogin";
	}
	
	
	@RequestMapping(value="/facebook/user/{userId}", method=RequestMethod.GET)
	public FacebookUserDto getUser(@PathVariable String userId, Model model, HttpServletRequest request){
		
		FacebookUser facebookUser = facebookUserDao.getFacebookUserbyFID(userId);
		String sessionId = request.getParameter(FacebookParam.SESSION_KEY.toString());
		FacebookUserDto facebookUserDto = this.createFacebookUserDto(facebookUser, sessionId, request.getSession());
		logger.info("User found is "+facebookUser.getFacebookId());
		
		return facebookUserDto;
	}
	
	@RequestMapping(value="/facebook/user/{userId}/statuses", method=RequestMethod.GET)
	public ArrayList<StatusDto> getStatuses(@PathVariable String userId, Model model){
		FacebookUser facebookUser = facebookUserDao.getFacebookUserbyFID(userId);
		ArrayList<StatusDto> statuses = new ArrayList();
		if (facebookUser.getTwitterUserKey()!=null){
			TwitterUser twitterUser= twitterUserDao.getTwitterUser(KeyFactory.keyToString(facebookUser.getTwitterUserKey()));
			statuses = twitterRest.getFriendStatuses(twitterUser.getAccessToken());
		}
		return statuses;
			
	}
	
	@RequestMapping(value="/facebook/user/{userId}/ownStatuses", method=RequestMethod.GET)
	public ArrayList<StatusDto> getOwnStatuses(@PathVariable String userId, Model model){
		FacebookUser facebookUser = facebookUserDao.getFacebookUserbyFID(userId);
		ArrayList<StatusDto> statuses = new ArrayList();
		if (facebookUser.getTwitterUserKey()!=null){
			TwitterUser twitterUser= twitterUserDao.getTwitterUser(KeyFactory.keyToString(facebookUser.getTwitterUserKey()));
			statuses = twitterRest.getUserStatuses(twitterUser.getAccessToken());
		}
		return statuses;
	}
	
	@RequestMapping(value="/facebook/user/{userId}/{status}", method=RequestMethod.POST)
	public FacebookUserDto updateStatus(@PathVariable String status, @PathVariable String userId, HttpServletRequest request) {
		// TODO Auto-generated method stub
		FacebookUser facebookUser = this.facebookUserDao.getFacebookUserbyFID(userId);
		String sessionId =request.getParameter(FacebookParam.SESSION_KEY.toString());
		String twitterLoginUrl="";
		if (facebookUser.getTwitterUserKey()!=null){
			logger.info("Loading statuses");
			TwitterUser twitterUser= twitterUserDao.getTwitterUser(KeyFactory.keyToString(facebookUser.getTwitterUserKey()));
			twitterRest.updateStatus(twitterUser.getAccessToken(), status);
		}
		else{
			logger.info("Getting authenticationURL");
			twitterLoginUrl =this.getTwitAuthorizationLoginURL(request.getSession());
			
		}
		FacebookUserDto facebookUserDto = this.createFacebookUserDto(facebookUser, sessionId, request.getSession());
		facebookUserDto.setTwitterLoginURL(twitterLoginUrl);
		return facebookUserDto;
		
	}
	
	@RequestMapping(value="/facebook/status/{statusId}/comment", method =RequestMethod.GET)
	public ArrayList<CommentDto> getCommentsForStatus(@PathVariable String statusId, Model model, HttpServletRequest request){
		List<FBComment> comments = this.facebookCommentDao.getCommentByStatusId(statusId);
		ArrayList<CommentDto> commentsList = new ArrayList<CommentDto>();
		String sessionId = request.getParameter(FacebookParam.SESSION_KEY.toString());
		for(FBComment comment: comments){
			commentsList.add(this.createCommentDto(comment, sessionId, request.getSession()));
		}
		return commentsList;
		
	}
	
	@RequestMapping(value="/facebook/user/{userId}/status/{statusId}/comment/{text}")
	public ArrayList<CommentDto> addComment(@PathVariable String userId, @PathVariable String statusId, @PathVariable String text, Model model, HttpServletRequest request){
		FacebookUser facebookUser = facebookUserDao.getFacebookUserbyFID(userId);
		FBComment comment = new FBComment();
		Date date = new Date();
		comment.setDateCreated(date);
		comment.setDateModified(date);
		comment.setFacebookUserKey(facebookUser.getKey());
		comment.setStatusId(statusId);
		comment.setText(text);
		comment =this.facebookCommentDao.saveComment(comment);
		return this.getCommentsForStatus(statusId, model, request);
		
	}
	
	private CommentDto createCommentDto(FBComment comment, String sessionId, HttpSession session){
		CommentDto commentDto = new CommentDto();
		commentDto.setKeyString(KeyFactory.keyToString(comment.getKey()));
		commentDto.setDateCreated(comment.getDateCreated());
		commentDto.setDateModified(comment.getDateModified());
		commentDto.setStatusId(comment.getStatusId());
		commentDto.setText(comment.getText());
		FacebookUser facebookUser = facebookUserDao.getFacebookUser(KeyFactory.keyToString(comment.getFacebookUserKey()));
		FacebookUserInfo facebookUserInfo = this.facebookRest.getFacebookUserInfo(facebookUser.getFacebookId(), sessionId);
		commentDto.setFacebookUserInfo(facebookUserInfo);
		return commentDto;
	}

	private FacebookUserDto createFacebookUserDto(FacebookUser facebookUser, String sessionId , HttpSession session){
		FacebookUserDto facebookUserDto = new FacebookUserDto();
		facebookUserDto.setKey(KeyFactory.keyToString(facebookUser.getKey()));
		facebookUserDto.setTwitterUser(facebookUser.getTwitterUserKey()!=null?KeyFactory.keyToString(facebookUser.getTwitterUserKey()):null);
		facebookUserDto.setTwitterLoginURL(this.getTwitAuthorizationLoginURL(session));
		Key twitterUserKey = facebookUser.getTwitterUserKey();
		ArrayList<StatusDto> statuses = new ArrayList<StatusDto>();
		if (twitterUserKey!=null){
			TwitterUser twitterUser= twitterUserDao.getTwitterUser(KeyFactory.keyToString(twitterUserKey));
			statuses = twitterRest.getFriendStatuses(twitterUser.getAccessToken());
		}
		
		facebookUserDto.setStatuses(statuses); 
		FacebookUserInfo facebookUserInfo = this.facebookRest.getFacebookUserInfo(facebookUser.getFacebookId(), sessionId);
		logger.info("facebookUserInfo name "+facebookUserInfo.getName());
		facebookUserDto.setFacebookUserInfo(facebookUserInfo);
		return facebookUserDto;
	}

	private void saveUser(String facebookUserId){
		logger.info("adding "+ facebookUserId);
		FacebookUser facebookUser = new FacebookUser();
		facebookUser.setFacebookId(facebookUserId);
		facebookUserDao.saveFacebookUser(facebookUser);
	}
	
	private boolean isFirstTimeUser(String facebookUserId){
		logger.info("facebookuserId "+facebookUserId);
		FacebookUser facebookUser =facebookUserDao.getFacebookUserbyFID(facebookUserId);
		if (facebookUser ==null){
			return true;
		}
		else{
			return false;
		}
	}
	
	
	private String getTwitAuthorizationLoginURL(HttpSession session){
		RequestToken requestToken = twitterRest.getRequestToken();
		if (requestToken!=null){
			logger.info("in get Session 2");
			session.setAttribute("twitterOauthToken", requestToken);
			logger.info("RequestToken is"+requestToken.getAuthorizationURL());
			return requestToken.getAuthorizationURL();
		}
		else{
			return null;
		}
	}
	
	private void linkTwitterUsertoFacebookUser(RequestToken requestToken, FacebookUser facebookUser) {
		// TODO Auto-generated method stub
		logger.info("Setting Token");
		logger.info("RequestToken is "+requestToken.getToken());
		try {
			AccessToken accessToken= requestToken.getAccessToken();
			logger.info("AccessToken is "+accessToken.getToken());
			TwitterUser twitterUser = new TwitterUser();
			twitterUser.setAccessToken(accessToken);
			/**
			Twitter twitter = new Twitter();
			twitter.setOAuthConsumer(TwitterConstants.getConsumerKey(), TwitterConstants.getConsumerSecret());
			twitter.setOAuthAccessToken(accessToken);
			int twitterId=twitter.verifyCredentials().getId();
			logger.info("Twitter User Id "+twitterId);
			**/
			String twitterId = twitterRest.getTwitterId(accessToken);
			twitterUser.setTwitUserId(twitterId);
			facebookUserDao.setTwitterUser(facebookUser, twitterUser);
			
			//twitterUserDao.saveTwitterUser(twitterUser);
			
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
