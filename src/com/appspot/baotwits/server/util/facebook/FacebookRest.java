package com.appspot.baotwits.server.util.facebook;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	
	public void getFriends(String uid, String sessionId){
		ParamsMap paramsMap = new ParamsMap();
		paramsMap.put("method", "FQL.multiquery" );
		paramsMap.put("api_key", FacebookConstants.getFacebookAPIKey());
		paramsMap.put("call_id", String.valueOf(new Date().getTime()));
		paramsMap.put("v","1.0");
		paramsMap.put("session_key", sessionId);
		paramsMap.put("format", "JSON");
		paramsMap.put("queries", "{\"friends\":\"SELECT uid, name, pic_square FROM user WHERE uid in (SELECT uid2 FROM friend where uid1="+uid+") and is_app_user=1\", \"user\": \"select uid, name, pic_square from user where uid ="+uid+"\"}");
		/*String methodParam="method::FQL.multiquery";
		String apiKeyParam="api_key::"+FacebookConstants.getFacebookAPIKey();
		Date currentDate = new Date();
		logger.info("Current time is"+currentDate.getTime());
		String callIdParam="call_id::"+currentDate.getTime();
		String versionParam="v::1.0";
		String sessionKeyParam="session_key::"+sessionId;
		String formatParam="format::JSON";
		String queryParam="queries::{\"friends\":\"SELECT uid, name, pic_square FROM user WHERE uid in (SELECT uid2 FROM friend where uid1="+uid+")\", \"user\": \"select uid, name, pic_square from user where uid ="+uid+"\"}";
		ArrayList<String> params = new ArrayList();
		params.add(methodParam);
		params.add(apiKeyParam);
		params.add(callIdParam);
		params.add(versionParam);
		params.add(sessionKeyParam);
		params.add(formatParam);
		params.add(queryParam);
		*/
		
		String sigParam="sig::"+FacebookSignatureUtil.generateSignature(paramsMap.toSignatureArray(), FacebookConstants.getFacebookApplicationKey());
		paramsMap.put("sig",FacebookSignatureUtil.generateSignature(paramsMap.toSignatureArray(), FacebookConstants.getFacebookApplicationKey()));
		String urlString = "http://api.facebook.com/restserver.php?"+paramsMap.toURLString();
		logger.info("UserInfoURL is"+urlString);
		String result = this.getJSONURLResponse(urlString);
		logger.info("result: "+result);
		
		
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

class ParamsMap extends HashMap<String, String>{
	
	public ParamsMap(){
		super();
	}
	
	public ArrayList<String> toSignatureArray(){
		Set<String> keys=this.keySet();
		ArrayList<String> params = new ArrayList<String>();
		for (String key: keys){
			String param = key+"="+this.get(key);
			params.add(param);
		}
		return params;
	}
	
	public String toURLString(){
		try{
			Set<String> keys=this.keySet();
			String params = "";
			int count = this.size();
			for (String key: keys){
				String param = key+"="+URLEncoder.encode(this.get(key), "UTF-8");
				if(count>1){
					params+=param+"&";
				}
				else{
					params+= param;
				}
			}
			return params;
		}
		catch(UnsupportedEncodingException e){
			e.printStackTrace();
			return null;
		}
		
	}
}
