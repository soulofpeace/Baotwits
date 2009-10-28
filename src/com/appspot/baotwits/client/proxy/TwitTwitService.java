package com.appspot.baotwits.client.proxy;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.appspot.baotwits.client.data.dto.StatusDto;

@RemoteServiceRelativePath("twittwit")
public interface TwitTwitService extends RemoteService{
	public ArrayList<StatusDto> getStatuses();
	public void updateStatus(String status);
	public ArrayList<StatusDto> getOwnStatuses(String userId);

}
