package com.appspot.baotwits.client.proxy;

import com.appspot.baotwits.client.data.dto.LoginInfoDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface AuthServiceAsync {

	void login(String requestUri, AsyncCallback<LoginInfoDto> callback);



}
