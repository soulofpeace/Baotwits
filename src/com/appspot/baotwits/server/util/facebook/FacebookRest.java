package com.appspot.baotwits.server.util.facebook;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.appspot.baotwits.client.data.dto.facebook.FacebookUserInfo;
import com.appspot.baotwits.server.constants.facebook.FacebookConstants;

@Service
public class FacebookRest {
	public static Logger logger = Logger.getLogger(FacebookRest.class.getName());
	
	@Autowired
	private RestTemplate restTemplate;
	
	public String getFacebookCanvasLoginURL(){
		try{
			String loginURL= "http://www.facebook.com/login.php?v=1.0&api_key="+FacebookConstants.getFacebookAPIKey()+"&next="+URLEncoder.encode(FacebookConstants.getCanvasURL(), "UTF-8")+"&canvas";
			return loginURL;
		}
		catch(UnsupportedEncodingException ex){
			return null;
		}
	}
	
	public boolean isCanvasAuthorised(HttpServletRequest request){
		logger.info("FacebookParam.ADDED is " +FacebookParam.ADDED);
		logger.info("FacebookParam ADDED value is "+request.getParameter(FacebookParam.ADDED.toString()));
		logger.info("Request URI is "+request.getRequestURI());
		logger.info("Request Parameter "+request.getParameterMap());
		String added = request.getParameter(FacebookParam.ADDED.toString());
		if (added!=null && added.equalsIgnoreCase("1")){
			return true;
		}
		else{
			return false;
		}
		
	}
	
	public boolean inFbCanvas(HttpServletRequest request){
		String canvas= request.getParameter(FacebookParam.IN_CANVAS.toString());
		if (canvas!=null && canvas.equalsIgnoreCase("1")){
			return true;
		}
		else{
			return false;
		}
	}
	
	public boolean inProfileTab(HttpServletRequest request){
		String tab = request.getParameter(FacebookParam.IN_PROFILE_TAB.toString());
		if(tab!=null && tab.equalsIgnoreCase("1")){
			return true;
		}
		else{
			return false;
		}
	}
	
	public void normalRedirect(HttpServletResponse response, String url){
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warning(e.getMessage());
		}
	}
	
	public boolean verifyFacebookSignature(HttpServletRequest request){
		Map<String, String[]> requestParams = this.getRequestParameterMap(request);
		Map<String, String>fbParam = FacebookSignatureUtil.pulloutFbSigParams(requestParams);
		return FacebookSignatureUtil.verifySignature(fbParam, FacebookConstants.getFacebookApplicationKey());
		
	}
	
	public FacebookUserInfo getFacebookUserInfo(String uid, String sessionId){
		
		String methodParam="method=users.getInfo";
		String apiKeyParam="api_key="+FacebookConstants.getFacebookAPIKey();
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
		
		String sigParam="sig="+FacebookSignatureUtil.generateSignature(params, FacebookConstants.getFacebookApplicationKey());
		params.add(sigParam);
		String userInfoURL = "http://api.facebook.com/restserver.php?";
		for(String param: params){
			userInfoURL+=param+"&";
		}
		userInfoURL=userInfoURL.substring(0, userInfoURL.length()-1);
		logger.info("URL is "+userInfoURL);
		ArrayList facebookUserInfos = restTemplate.getForObject(userInfoURL, ArrayList.class);
		/**
		try {
			JSONArray array = new JSONArray(jsonString);
			JSONObject jsonObject = array.getJSONObject(0);
			FacebookUserInfo facebookUserInfo = new FacebookUserInfo();
			facebookUserInfo.setName(jsonObject.getString("name"));
			facebookUserInfo.setPic_square(jsonObject.getString("pic_square"));
			facebookUserInfo.setUid(jsonObject.getString("uid"));
			return facebookUserInfo;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		**/
		LinkedHashMap facebookUserMap = (LinkedHashMap)facebookUserInfos.get(0);
		FacebookUserInfo facebookUserInfo = new FacebookUserInfo();
		facebookUserInfo.setName((String)facebookUserMap.get("name"));
		facebookUserInfo.setPic_square((String)facebookUserMap.get("pic_square"));
		facebookUserInfo.setUid(String.valueOf(facebookUserMap.get("uid")));
		
		return facebookUserInfo;
		
	}
	
	private Map<String,String[]> getRequestParameterMap( HttpServletRequest request ) {
		return (Map<String,String[]>) request.getParameterMap();
	}
}
