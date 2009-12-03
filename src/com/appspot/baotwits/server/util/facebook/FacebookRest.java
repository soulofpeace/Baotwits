package com.appspot.baotwits.server.util.facebook;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.appspot.baotwits.server.constants.comment.CommentConstants;
import com.appspot.baotwits.server.constants.facebook.FacebookConstants;

public class FacebookRest {
	public static Logger logger = Logger.getLogger(FacebookRest.class.getName());
	
	public static String getFacebookCanvasLoginURL(){
		try{
			String loginURL= "http://www.facebook.com/login.php?v=1.0&api_key="+FacebookConstants.getFacebookAPIKey()+"&next="+URLEncoder.encode(FacebookConstants.getCanvasURL(), "UTF-8")+"&canvas";
			return loginURL;
		}
		catch(UnsupportedEncodingException ex){
			return null;
		}
	}
	
	public static boolean isCanvasAuthorised(HttpServletRequest request){
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
	
	public static boolean inFbCanvas(HttpServletRequest request){
		String canvas= request.getParameter(FacebookParam.IN_CANVAS.toString());
		if (canvas!=null && canvas.equalsIgnoreCase("1")){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static boolean inProfileTab(HttpServletRequest request){
		String tab = request.getParameter(FacebookParam.IN_PROFILE_TAB.toString());
		if(tab!=null && tab.equalsIgnoreCase("1")){
			return true;
		}
		else{
			return false;
		}
	}
	
	public static void normalRedirect(HttpServletResponse response, String url){
		try {
			response.sendRedirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.warning(e.getMessage());
		}
	}
}
