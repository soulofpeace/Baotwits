package com.appspot.baotwits.client.proxy.comment;

import com.appspot.baotwits.client.data.dto.comment.CommentorDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommentLoginServiceAsync {

	void facebookLogin(AsyncCallback<String> callback);

	void googleConnectLogin(AsyncCallback<String> callback);

	void twitterLogin(AsyncCallback<String> callback);

	void getUserLogin(AsyncCallback<CommentorDto> callback);

	void getLogout(AsyncCallback<String> callback);
	
	

}
