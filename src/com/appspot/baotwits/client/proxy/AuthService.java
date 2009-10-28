package com.appspot.baotwits.client.proxy;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.appspot.baotwits.client.data.dto.LoginInfoDto;


@RemoteServiceRelativePath("auth")
public interface AuthService extends RemoteService {
	public LoginInfoDto login(String requestUri);


}
