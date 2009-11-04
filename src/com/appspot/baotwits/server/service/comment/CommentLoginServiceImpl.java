package com.appspot.baotwits.server.service.comment;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.http.RequestToken;

import com.appspot.baotwits.client.data.dto.comment.CommentorDto;
import com.appspot.baotwits.client.dto.exception.BaoTwitsException;
import com.appspot.baotwits.client.proxy.comment.CommentLoginService;
import com.appspot.baotwits.server.constants.comment.CommentConstants;
import com.appspot.baotwits.server.data.dao.comment.CommentorDao;
import com.appspot.baotwits.server.data.model.comment.CommentorWrapper;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CommentLoginServiceImpl extends RemoteServiceServlet implements CommentLoginService {
	
	private static ThreadLocal<HttpServletRequest> servletRequest = new ThreadLocal<HttpServletRequest>();
	private static ThreadLocal<HttpServletResponse> servletResponse = new ThreadLocal<HttpServletResponse>();
	private static final Logger logger = Logger.getLogger(CommentLoginServiceImpl.class.getName());
	@Autowired
	private CommentorDao commentorDao;
	
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
	
	@Override
	public String facebookLogin() {
		// TODO Auto-generated method stub
		String authenticationURL="http://www.facebook.com/login.php?api_key="+CommentConstants.getCommentFacebookAPIKey()+"&connect_display=popup&v=1.0&next=http://baotwits.appspot.com/comment/login/facebook&cancel_url=http://www.facebook.com/connect/login_failure.html&fbconnect=true&return_session=true&req_perms=read_stream,publish_stream,offline_access";
		return authenticationURL;
	}

	@Override
	public String googleConnectLogin() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String twitterLogin() {
		// TODO Auto-generated method stub
		Twitter twitter = new Twitter();
		twitter.setOAuthConsumer(CommentConstants.getCommentTwitterConsumerKey(), CommentConstants.getCommentTwitterConsumerSecret());
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
			this.getSession().setAttribute("twitterRequestToken", requestToken);
			return requestToken.getAuthenticationURL();
		}
		else{
			return null;
		}
	}
	
	@Override
	public CommentorDto getUserLogin() {
		// TODO Auto-generated method stub
		logger.info("Choon Kee CommentorId is "+(String)this.getSession().getAttribute("commentorId"));
		if(this.getSession().getAttribute("commentorId")!=null){
			String commentorId = (String)this.getSession().getAttribute("commentorId");
			return this.getCommentor(KeyFactory.stringToKey(commentorId));
		}
		else{
			return null;
		}
	}
	
	private CommentorDto getCommentor(Key id) {
		// TODO Auto-generated method stub
		CommentorDto commentorDto = new CommentorDto();
		CommentorWrapper commentor = commentorDao.getCommentorWrapper(id);
		if (commentor==null){
			logger.info("Commentor is null");
			return null;
		}
		commentorDto.setId(KeyFactory.keyToString(id));
		commentorDto.setName(commentor.getName());
		logger.info("profile Pix is "+commentor.getProfileImageURL());
		commentorDto.setProfileImageURL(commentor.getProfileImageURL());
		return commentorDto;
	}
	
	private HttpSession getSession() {
			// Get the current request and then return its session
		logger.info("in get Session");
		return this.getRequest().getSession();
	}

	@Override
	public String getLogout() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
