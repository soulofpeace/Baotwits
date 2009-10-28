package com.appspot.baotwits.server.controller.comment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.appspot.baotwits.server.constants.comment.CommentConstants;
import com.appspot.baotwits.server.data.dao.comment.CommentorDao;
import com.appspot.baotwits.server.data.dao.comment.FBCommentorDao;
import com.appspot.baotwits.server.data.dao.comment.TwitCommentorDao;
import com.appspot.baotwits.server.data.model.comment.CommentorWrapper;
import com.appspot.baotwits.server.data.model.comment.FBCommentor;
import com.appspot.baotwits.server.data.model.comment.TwitCommentor;
import com.appspot.baotwits.server.util.facebook.FacebookSignatureUtil;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.http.AccessToken;
import twitter4j.http.RequestToken;


@Controller
public class CommentLoginController {
	private static final Logger logger = Logger.getLogger(CommentLoginController.class.getName());
	
	//@Autowired
	//private RestTemplate restTemplate; 
	
	@Autowired
	private TwitCommentorDao twitCommentorDao;
	
	@Autowired
	private FBCommentorDao fbCommentorDao;
	
	@RequestMapping(value="/comment/login/twitter", method=RequestMethod.GET)
	public String twitterCallBackLogin(HttpSession session){
		RequestToken requestToken = (RequestToken)session.getAttribute("twitterRequestToken");
		try {
			AccessToken accessToken= requestToken.getAccessToken();
			logger.info("AccessToken is "+accessToken.getToken());
			Twitter twitter = new Twitter();
			twitter.setOAuthConsumer(CommentConstants.getCommentTwitterConsumerKey(), CommentConstants.getCommentTwitterConsumerSecret());
			twitter.setOAuthAccessToken(accessToken);
			User user = twitter.verifyCredentials();
			TwitCommentor twitCommentor = twitCommentorDao.getTwitCommentor(user.getId());
			
			CommentorWrapper commentor;
			if (twitCommentor ==null){
				logger.info("New TwitUser");
				twitCommentor = new TwitCommentor();
				this.updateTwitCommentorFields(twitCommentor, user, accessToken);
				commentor = twitCommentorDao.createNewCommentorWrapper(twitCommentor);
			}
			else{
				logger.info("Existing twitUser");
				logger.info("twit Commentor CommentorWrapper Key 3"+ twitCommentor.getCommentorWrapper());
				this.updateTwitCommentorFields(twitCommentor, user, accessToken);
				logger.info("twit Commentor CommentorWrapper Key "+ twitCommentor.getCommentorWrapper());
				commentor = twitCommentorDao.updateTwitCommentor(twitCommentor);
			}
			logger.info("Commentor is "+commentor);
			logger.info("CommentorId is "+KeyFactory.keyToString(commentor.getCommenterId()));
			//twitCommentor.setURL(user.getURL().toString());
			session.setAttribute("commentorId", KeyFactory.keyToString(commentor.getCommenterId()));
		}catch (TwitterException e) {
			// TODO Auto-generated catch block
			 if(401 == e.getStatusCode()){
		          logger.warning("Unable to get AccessToken");
		       }
			 else{
		          logger.warning(e.getMessage());
			 }
		}
		return "SuccessfulAuthentication";
	}
	
	@RequestMapping(value="/comment/login/facebook", method=RequestMethod.GET)
	public String facebookCallBackLogin(HttpServletRequest request, HttpSession session){
		String sessionJSON = request.getParameter("session");
		logger.info("Session is"+sessionJSON);
		try {
			JSONObject jsonObject = new JSONObject(sessionJSON);
			String sessionId= jsonObject.getString("session_key");
			String uid= jsonObject.getString("uid");
			String secret = jsonObject.getString("secret");
			logger.info("result is "+sessionId+" "+uid+" "+secret);
			
			String methodParam="method=users.getInfo";
			String apiKeyParam="api_key="+CommentConstants.getCommentFacebookAPIKey();
			Date currentDate = new Date();
			logger.info("Current time is"+currentDate.getTime());
			String callIdParam="call_id="+currentDate.getTime();
			String versionParam="v=1.0";
			String uidsParam ="uids="+uid;
			String fieldsParam="fields=pic_square, name";
			String sessionKeyParam="session_key="+sessionId;
			String formatParam="format=JSON";
			
			ArrayList<String>params=new ArrayList(); 
			params.add(methodParam);
			params.add(apiKeyParam);
			params.add(callIdParam);
			params.add(versionParam);
			params.add(uidsParam);
			params.add(fieldsParam);
			params.add(sessionKeyParam);
			params.add(formatParam);
			
			String sigParam="sig="+FacebookSignatureUtil.generateSignature(params, CommentConstants.getCommentFacebookApplicationKey());
			params.add(sigParam);
			String userInfoURL = "http://api.facebook.com/restserver.php?";
			for(String param: params){
				String[] paramTokens=param.split("=");
				userInfoURL+=apiKeyParam=paramTokens[0]+"="+URLEncoder.encode(paramTokens[1], "UTF-8")+"&";
			}
			userInfoURL=userInfoURL.substring(0, userInfoURL.length()-1);
			logger.info("UserInfoURL is"+userInfoURL);
			String userInfo = this.getJSONURLResponse(userInfoURL);
			JSONArray userInfoJsonArray = new JSONArray(userInfo);
			JSONObject userInfoJson = userInfoJsonArray.getJSONObject(0);
			CommentorWrapper commentor;
			FBCommentor fbCommentor = fbCommentorDao.getFBCommentor(Integer.parseInt(uid));
			if(fbCommentor==null){
				fbCommentor = new FBCommentor();
				fbCommentor.setFbId(userInfoJson.getInt("uid"));
				fbCommentor.setName(userInfoJson.getString("name"));
				fbCommentor.setProfileImageURL(userInfoJson.getString("pic_square"));
				commentor = this.fbCommentorDao.createNewCommentorWrapper(fbCommentor);
			}
			else{
				fbCommentor.setFbId(userInfoJson.getInt(("uid")));
				fbCommentor.setName(userInfoJson.getString("name"));
				fbCommentor.setProfileImageURL(userInfoJson.getString("pic_square"));
				commentor = this.fbCommentorDao.updateFBCommentor(fbCommentor);
			}
			session.setAttribute("commentorId", KeyFactory.keyToString(commentor.getCommenterId()));
			
			logger.info("Source is "+userInfo);
			//String userInfoURL = "http://api.facebook.com/restserver.php?method=users.getInfo&api_key={apiKey}&call_id={callId}&sig={sig}&v=1.0&uids={uid}&fields={field}&session_key={sessionKey}";
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			logger.warning(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.warning(e.getMessage());
		}
		return "SuccessfulAuthentication";
	}
	
	private void updateTwitCommentorFields(final TwitCommentor twitCommentor, User user, AccessToken accessToken){
	    twitCommentor.setTwitterId(user.getId());
		twitCommentor.setAccessToken(accessToken);
		twitCommentor.setFollowersCount(user.getFollowersCount());
		twitCommentor.setFriendCount(user.getFriendsCount());
		twitCommentor.setLocation(user.getLocation());
		twitCommentor.setName(user.getName());
		twitCommentor.setScreenName(user.getScreenName());
		twitCommentor.setStatusesCount(user.getStatusesCount());
		twitCommentor.setProfileImageURL(user.getProfileImageURL().toString());
		logger.info("User url is "+user.getURL());
	}
	
	
	private String getJSONURLResponse(String urlString){
		try {
			URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            String line;
            String output="";
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = reader.readLine()) != null) {
            	output+=line;
            }
            return output;
        } catch (MalformedURLException e) {
            logger.warning(e.getMessage());
            return null;
        } catch (IOException e) {
            logger.warning(e.getMessage());
            return null;
        }
		
	}
}
