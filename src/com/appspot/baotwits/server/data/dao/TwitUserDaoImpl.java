package com.appspot.baotwits.server.data.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.appspot.baotwits.server.data.model.TwitUser;
import com.google.appengine.api.users.User;

public class TwitUserDaoImpl implements TwitUserDao{
	private static final Logger logger = Logger.getLogger(TwitUserDaoImpl.class.getName());

	public TwitUser getTwitUser(User user) {
		// TODO Auto-generated method stub
		PersistenceManager pm= PMF.get().getPersistenceManager();
		try{
			Query query = pm.newQuery(TwitUser.class);
			query.setFilter("user==userParam");
			query.declareParameters("com.google.appengine.api.users.User userParam");
			logger.info("User is "+user.getEmail());
			logger.info("query is "+Query.JDOQL);
			List<TwitUser> twitUsers = (List<TwitUser>)query.execute(user);
			if(twitUsers.size()>0){
				logger.info("Found user");
				TwitUser twitUser = pm.detachCopy(twitUsers.get(0));
				return twitUser;
			}
			else{
				logger.info("Cannot find User");
				return null;
			}
		}
		finally{
			pm.close();
		}
	}
	
	public void saveTwitUser(TwitUser twitUser){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try{
			pm.makePersistent(twitUser);
		}
		finally{
			pm.close();
		}
	}
	
	
	
	

}
