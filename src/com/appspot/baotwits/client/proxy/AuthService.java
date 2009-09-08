package com.appspot.baotwits.client.proxy;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.appspot.baotwits.client.data.dto.LoginInfo;


@RemoteServiceRelativePath("auth")
public interface AuthService extends RemoteService {
	public LoginInfo login(String requestUri);


}
