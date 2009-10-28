package com.appspot.baotwits.client.proxy;

import java.util.ArrayList;

import com.appspot.baotwits.client.data.dto.StatusDto;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TwitTwitServiceAsync {

	void getStatuses(AsyncCallback<ArrayList<StatusDto>> callback);

	void updateStatus(String status, AsyncCallback<Void> callback);

	void getOwnStatuses(String userId, AsyncCallback<ArrayList<StatusDto>> callback);


}
