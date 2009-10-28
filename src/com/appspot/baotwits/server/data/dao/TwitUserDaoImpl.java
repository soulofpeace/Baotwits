package com.appspot.baotwits.server.data.dao;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.appspot.baotwits.server.data.model.TwitUser;
import com.google.appengine.api.users.User;

@Repository
public class TwitUserDaoImpl implements TwitUserDao{
	private static final Logger logger = Logger.getLogger(TwitUserDaoImpl.class.getName());
	@Autowired
	private PersistenceManagerFactory pmf;
	
	public void setPersistenceManagerFactory(PersistenceManagerFactory pmf){
		this.pmf = pmf;
	}
	
	public TwitUser getTwitUser(User user) {
		// TODO Auto-generated method stub
		PersistenceManager pm = pmf.getPersistenceManager();
		Query query = pm.newQuery(TwitUser.class);
		query.setFilter("user==userParam");
		query.declareParameters("com.google.appengine.api.users.User userParam");
		logger.info("User is "+user.getEmail());
		logger.info("query is "+Query.JDOQL);
		List<TwitUser> twitUsers = (List<TwitUser>)query.execute(user);
		if(twitUsers.size()>0){
			logger.info("Found user");
			TwitUser twitUser = pm.detachCopy(twitUsers.get(0));
			pm.close();
			return twitUser;
		}
		else{
			logger.info("Cannot find User");
			pm.close();
			return null;
		}
		
	}
	
	public void saveTwitUser(TwitUser twitUser){
		PersistenceManager pm = pmf.getPersistenceManager();
		pm.makePersistent(twitUser);
		pm.close();
		
	}
	
	
	
	

}
