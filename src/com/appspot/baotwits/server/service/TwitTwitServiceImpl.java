package com.appspot.baotwits.server.service;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.net.MalformedURLException;
import java.net.URL;



import twitter4j.Twitter;
import twitter4j.TwitterException;

import com.appspot.baotwits.client.data.dto.StatusDto;
import com.appspot.baotwits.client.proxy.TwitTwitService;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.appspot.baotwits.server.constants.Constants;
import com.appspot.baotwits.server.data.dao.TwitUserDao;
import com.appspot.baotwits.server.data.model.TwitUser;

public class TwitTwitServiceImpl extends RemoteServiceServlet implements TwitTwitService{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2841977258354860234L;
	
	private static final Logger logger = Logger.getLogger(TwitTwitService.class.getName());
	private TwitUserDao twitUserDao;
	

	public void setTwitUserDao(TwitUserDao twitUserDao){
		this.twitUserDao = twitUserDao;
	}
	
	@Override
	public ArrayList<StatusDto> getOwnStatuses(String userId) {
		// TODO Auto-generated method stub
		String emailAddress=userId+"@gmail.com";
		User user = new User(emailAddress, "gmail.com");
		Twitter twitter = this.getTwitter(user);
		try {
			List<twitter4j.Status> statuses = twitter.getUserTimeline();
			return this.getStatusDto(statuses);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			logger.warning("Unable to retrieve tweets for "+ user.getNickname());
			
		}
		return null;
	}

	public ArrayList<StatusDto> getStatuses() {
		// TODO Auto-generated method stub
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		Twitter twitter = this.getTwitter(user);
		try {
			List<twitter4j.Status> statuses = twitter.getFriendsTimeline();
			return this.getStatusDto(statuses);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			logger.warning("Unable to retrieve tweets for "+ user.getNickname());
			
		}
		return null;
		
	}
	public void updateStatus(String status) {
		// TODO Auto-generated method stub
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		Twitter twitter = this.getTwitter(user);
		try {
			twitter.updateStatus(status);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			logger.warning("Unable to update status for "+ user.getNickname());
		}
		
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
	
	
	
	private Twitter getTwitter(User user){
		TwitUser twitUser = twitUserDao.getTwitUser(user);
		Twitter twitter = new Twitter();
		twitter.setOAuthConsumer(Constants.getConsumerKey(), Constants.getConsumerSecret());
		twitter.setOAuthAccessToken(twitUser.getAccessToken());
		return twitter;
	}

		
	
	

}
