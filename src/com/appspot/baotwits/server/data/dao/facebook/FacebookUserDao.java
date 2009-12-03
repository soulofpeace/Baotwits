package com.appspot.baotwits.server.data.dao.facebook;

import com.appspot.baotwits.server.data.model.facebook.FacebookUser;

public interface FacebookUserDao {
	public FacebookUser getFacebookUser(String keyString);
	public void saveFacebookUser(FacebookUser facebookUser);
	public FacebookUser getFacebookUserbyFID(String fid);

}
