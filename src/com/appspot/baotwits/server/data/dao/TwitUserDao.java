package com.appspot.baotwits.server.data.dao;

import com.appspot.baotwits.server.data.model.TwitUser;
import com.google.appengine.api.users.User;

public interface TwitUserDao{
	
	public TwitUser getTwitUser(User user);
	public void saveTwitUser(TwitUser twitUser);

}
