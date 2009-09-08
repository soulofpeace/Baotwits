package com.appspot.baotwits.client.proxy;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.appspot.baotwits.client.data.dto.Status;

@RemoteServiceRelativePath("twittwit")
public interface TwitTwitService extends RemoteService{
	public ArrayList<Status> getStatuses(String requestUri);
	public void updateStatus(String status);

}
