package com.appspot.baotwits.client.proxy;

import java.util.ArrayList;

import com.appspot.baotwits.client.data.dto.Status;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TwitTwitServiceAsync {

	void getStatuses(String requestUri,
			AsyncCallback<ArrayList<Status>> callback);

	void updateStatus(String status, AsyncCallback<Void> callback);


}
