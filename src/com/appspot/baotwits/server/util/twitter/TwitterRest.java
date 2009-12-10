package com.appspot.baotwits.server.util.twitter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;

import com.appspot.baotwits.client.data.dto.facebook.StatusDto;
import com.appspot.baotwits.server.constants.facebook.TwitterConstants;

@Service
public class TwitterRest {
	
	private static Logger logger = Logger.getLogger(TwitterRest.class.getName());
	
	
	public ArrayList<StatusDto> getFriendStatuses(AccessToken accessToken) {
		// TODO Auto-generated method stub
		
		Twitter twitter = this.getTwitterUser(accessToken);
		try {
			List<twitter4j.Status> statuses = twitter.getFriendsTimeline();
			return this.getStatusDto(statuses);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			logger.warning("Unable to retrieve tweets for "+ accessToken.toString());
			
		}
		return null;
		
	}
	
	public RequestToken getRequestToken(){
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
		return requestToken;
	}
	
	public boolean updateStatus(AccessToken accessToken, String status){
		Twitter twitter = this.getTwitterUser(accessToken);
		try {
			twitter.updateStatus(status);
			return true;
			
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			logger.warning("Unable to update status for "+ accessToken.toString());
			return false;
		}
	}
	
	public String getTwitterId(AccessToken accessToken){
		Twitter twitter = this.getTwitterUser(accessToken);
		try {
			int twitterId = twitter.verifyCredentials().getId();
			logger.info("Twitter User Id "+twitterId);
			return String.valueOf(twitterId);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
	}
	
	public ArrayList<StatusDto> getUserStatuses(AccessToken accessToken){
		Twitter twitter = this.getTwitterUser(accessToken);
		try {
			List<twitter4j.Status> statuses = twitter.getUserTimeline();
			return this.getStatusDto(statuses);
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			logger.warning("Unable to retrieve tweets for "+ accessToken.toString());
		}
		return null;
	}
	
	private Twitter getTwitterUser(AccessToken accessToken){
		Twitter twitter = new Twitter();
		twitter.setOAuthConsumer(TwitterConstants.getConsumerKey(), TwitterConstants.getConsumerSecret());
		twitter.setOAuthAccessToken(accessToken);
		return twitter;
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
}
