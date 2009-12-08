package com.appspot.baotwits.server.controller.facebook.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.w3c.dom.Document;

import com.appspot.baotwits.server.constants.comment.CommentConstants;
import com.appspot.baotwits.server.util.facebook.FacebookRest;


@Component
public class FacebookUserFilter implements Filter{
	
	private String api_key;
	private String secret;
	
	private static final Logger logger = Logger.getLogger(FacebookUserFilter.class.getName());
	private static final String FACEBOOK_USER_CLIENT = "facebook.user.client";

	@Autowired
	private FacebookRest facebookRest;
	
	public void init(FilterConfig filterConfig) throws ServletException{
		ServletContext servletContext = filterConfig.getServletContext();
		WebApplicationContext webApplicationContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
		
		AutowireCapableBeanFactory autowireCapableBeanFactory = webApplicationContext.getAutowireCapableBeanFactory();
		
		autowireCapableBeanFactory.configureBean(this, "facebookRest");
		api_key=CommentConstants.getCommentFacebookAPIKey();
		secret= CommentConstants.getCommentFacebookApplicationKey();
		
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException{
		// TODO Auto-generated method stub
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp =(HttpServletResponse) response;
		if(this.facebookRest.isCanvasAuthorised(req)){
			logger.info("Has authorised");
			chain.doFilter(request, response);
		}
		else{
			resp.sendRedirect("/facebook/login");
		}
		
	}


	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
}
