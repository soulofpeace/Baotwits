package com.appspot.baotwits.server.data.dao;

import com.appspot.baotwits.server.data.model.TwitterUser;

public interface TwitterUserDao {
	public TwitterUser getTwitterUser(String keyString);
	public void saveTwitterUser(TwitterUser twitterUser);

}
