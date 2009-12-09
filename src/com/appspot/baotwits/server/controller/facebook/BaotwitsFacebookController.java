package com.appspot.baotwits.server.controller.facebook;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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

import com.appspot.baotwits.client.data.dto.facebook.FacebookUserInfo;
import com.appspot.baotwits.client.data.dto.facebook.StatusDto;
import com.appspot.baotwits.client.data.dto.facebook.FacebookUserDto;
import com.appspot.baotwits.server.constants.facebook.FacebookConstants;
import com.appspot.baotwits.server.constants.facebook.TwitterConstants;
import com.appspot.baotwits.server.data.dao.TwitterUserDao;
import com.appspot.baotwits.server.data.dao.facebook.FacebookUserDao;
import com.appspot.baotwits.server.data.model.TwitUser;
import com.appspot.baotwits.server.data.model.TwitterUser;
import com.appspot.baotwits.server.data.model.facebook.FacebookUser;
import com.appspot.baotwits.server.util.facebook.FacebookParam;
import com.appspot.baotwits.server.util.facebook.FacebookRest;
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
	private FacebookRest facebookRest;
	
	@RequestMapping(value="/facebook", method=RequestMethod.GET)
	public String show(HttpServletRequest request, Model model){
		Enumeration<String> parameterNames = request.getParameterNames();
		while(parameterNames.hasMoreElements()){
			logger.info("Parameter Name: "+parameterNames.nextElement());
		}
		String facebookUserId = request.getParameter("fb_sig_user");
		facebookRest.getFriends(request.getParameter(FacebookParam.USER.toString()), request.getParameter(FacebookParam.SESSION_KEY.toString()));
		
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
		/**
		FacebookUser facebookUser = facebookUserDao.getFacebookUserbyFID(userId);
		logger.info("User found is "+facebookUser.getFacebookId());
	
		FacebookUserDto facebookUserDto = new FacebookUserDto();
		facebookUserDto.setKey(KeyFactory.keyToString(facebookUser.getKey()));
		facebookUserDto.setTwitterUser(facebookUser.getTwitterUserKey()!=null?KeyFactory.keyToString(facebookUser.getTwitterUserKey()):null);
		facebookUserDto.getTwitterLoginURL();
		request.getSession().setAttribute("facebookUserId", userId);
		ArrayList<StatusDto> statuses = new ArrayList();
		if (facebookUser.getTwitterUserKey()!=null){
			logger.info("Loading statuses");
			statuses = this.getStatuses(facebookUser);
		}
		else{
			logger.info("Getting authenticationURL");
			facebookUserDto.setTwitterLoginURL(this.getTwitAuthorizationLoginURL(request.getSession()));
			
		}
			
		facebookUserDto.setStatuses(statuses); 
		FacebookUserInfo facebookUserInfo = this.facebookRest.getFacebookUserInfo(request.getParameter(FacebookParam.USER.toString()), request.getParameter(FacebookParam.SESSION_KEY.toString()));
		
		logger.info("facebookUserInfo name "+facebookUserInfo.getName());
		facebookUserDto.setFacebookUserInfo(facebookUserInfo);
		**/
		FacebookUserDto facebookUserDto = this.facebookRest.getFacebookUserDto(userId,  request.getParameter(FacebookParam.SESSION_KEY.toString()));
		return facebookUserDto;
	}
	
	@RequestMapping(value="/facebook/user/{userId}/statuses", method=RequestMethod.GET)
	public ArrayList<StatusDto> getStatuses(@PathVariable String userId, Model model){
		FacebookUser facebookUser = facebookUserDao.getFacebookUserbyFID(userId);
		ArrayList<StatusDto> statuses = new ArrayList();
		if (facebookUser.getTwitterUserKey()!=null){
			statuses = this.getStatuses(facebookUser);
		}
		return statuses;
			
	}
	
	@RequestMapping(value="/facebook/user/{userId}/{status}", method=RequestMethod.POST)
	public FacebookUserDto updateStatus(@PathVariable String status, @PathVariable String userId, HttpServletRequest request) {
		// TODO Auto-generated method stub
		FacebookUser facebookUser = this.facebookUserDao.getFacebookUserbyFID(userId);
		Twitter twitter = this.getTwitterUser(twitterUserDao.getTwitterUser(KeyFactory.keyToString(facebookUser.getTwitterUserKey())));
		try {
			twitter.updateStatus(status);
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			logger.warning("Unable to update status for "+ facebookUser.getFacebookId());
		}
		FacebookUserDto facebookUserDto = new FacebookUserDto();
		facebookUserDto.setKey(KeyFactory.keyToString(facebookUser.getKey()));
		facebookUserDto.setTwitterUser(facebookUser.getTwitterUserKey()!=null?KeyFactory.keyToString(facebookUser.getTwitterUserKey()):null);
		facebookUserDto.getTwitterLoginURL();
		request.getSession().setAttribute("facebookUserId", userId);
		ArrayList<StatusDto> statuses = new ArrayList();
		if (facebookUser.getTwitterUserKey()!=null){
			logger.info("Loading statuses");
			statuses = this.getStatuses(facebookUser);
		}
		else{
			logger.info("Getting authenticationURL");
			facebookUserDto.setTwitterLoginURL(this.getTwitAuthorizationLoginURL(request.getSession()));
			
		}
			
		facebookUserDto.setStatuses(statuses); 
		FacebookUserInfo facebookUserInfo = this.facebookRest.getFacebookUserInfo(request.getParameter(FacebookParam.USER.toString()), request.getParameter(FacebookParam.SESSION_KEY.toString()));
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
	
	private ArrayList<StatusDto> getStatuses(FacebookUser facebookUser) {
		// TODO Auto-generated method stub
		
		Twitter twitter = this.getTwitterUser(twitterUserDao.getTwitterUser(KeyFactory.keyToString(facebookUser.getTwitterUserKey())));
		try {
			List<twitter4j.Status> statuses = twitter.getFriendsTimeline();
			return this.getStatusDto(statuses);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			logger.warning("Unable to retrieve tweets for "+ facebookUser.getFacebookId());
			
		}
		return null;
		
	}
	
	
	private ArrayList<StatusDto> getStatusDto(List<twitter4j.Status> statuses){
		ArrayList<StatusDto> statusDtos = new ArrayList<StatusDto>();
		for (twitter4j.Status status: statuses){
			StatusDto statusDto = new StatusDto();
			statusDto.setCreatedAt(status.getCreatedAt());
			statusDto.setFavorited(status.isFavorited());
			statusDto.setId(status.getId());
			statusDto.setInReplyToScreenName(status.getInReplyToScreenName());
			statusDto.setInReplyToStatusId(status.getInReplyToStatusId());
			statusDto.setInReplyToUserId(status.getInReplyToUserId());
			statusDto.setSource(status.getSource());
			statusDto.setText(constructStatus(status.getText()));
			statusDto.setTruncated(status.isTruncated());
			statusDto.setScreenName(status.getUser().getScreenName());
			statusDto.setImageProfileURL(status.getUser().getProfileImageURL().toString());
			statusDto.setUserId(status.getUser().getId());
			statusDtos.add(statusDto);
			
		}
		return statusDtos;
	}


	private String constructStatus(String status){
        String [] parts = status.split("\\s");
        String outputString="";
        // Attempt to convert each item into an URL.   
        for( String item : parts ) try {
            URL url = new URL(item);
            // If possible then replace with anchor...
            outputString+="<a href=\"" + url + "\" target=\"_blank\">"+ url + "</a> " ;    
        } catch (MalformedURLException e) {
            // If there was an URL that was not it!...
            outputString+=item + " " ;
        }
        return outputString;
	}
	
	
	
	private Twitter getTwitterUser(TwitterUser twitterUser){
		Twitter twitter = new Twitter();
		twitter.setOAuthConsumer(TwitterConstants.getConsumerKey(), TwitterConstants.getConsumerSecret());
		twitter.setOAuthAccessToken(twitterUser.getAccessToken());
		return twitter;
	}
	
	private String getTwitAuthorizationLoginURL(HttpSession session){
		Twitter twitter = new Twitter();
		twitter.setOAuthConsumer(TwitterConstants.getConsumerKey(), TwitterConstants.getConsumerSecret());
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
			Twitter twitter = new Twitter();
			twitter.setOAuthConsumer(TwitterConstants.getConsumerKey(), TwitterConstants.getConsumerSecret());
			twitter.setOAuthAccessToken(accessToken);
			int twitterId=twitter.verifyCredentials().getId();
			logger.info("Twitter User Id "+twitterId);
			twitterUser.setTwitUserId(String.valueOf(twitterId));
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
