package com.appspot.baotwits.client.proxy.comment;

import com.appspot.baotwits.client.data.dto.comment.CommentorDto;
import com.appspot.baotwits.client.dto.exception.BaoTwitsException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("comment/login")
public interface CommentLoginService extends RemoteService {
	public String twitterLogin();
	
	public String facebookLogin();
	
	public String googleConnectLogin();
	
	public CommentorDto getUserLogin();
	
	public String getLogout();
	

}
