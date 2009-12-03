package com.appspot.baotwits.server.data.dao;

import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.appspot.baotwits.server.data.model.TwitterUser;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@Repository
public class TwitterUserDaoImpl implements TwitterUserDao {

	public static Logger logger = Logger.getLogger(TwitterUserDaoImpl.class.getName());
	
	@Autowired
	private PersistenceManagerFactory pmf;
	
	
	public void setPersistenceManagerFactory(PersistenceManagerFactory pmf){
		this.pmf = pmf;
	}
	
	@Override
	public TwitterUser getTwitterUser(String keyString) {
		// TODO Auto-generated method stub
		PersistenceManager pm = pmf.getPersistenceManager();
		try{
			TwitterUser twitterUser =pm.getObjectById(TwitterUser.class, KeyFactory.stringToKey(keyString));
			return pm.detachCopy(twitterUser);
		}
		finally{
			pm.close();
		}
	}

	@Override
	public void saveTwitterUser(TwitterUser twitterUser) {
		// TODO Auto-generated method stub
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try{
			tx.begin();
			pm.makePersistent(twitterUser);
			tx.commit();
		}
		finally{
			if (tx.isActive()) {
	            tx.rollback();
	        }
			pm.close();
		}
		


	}

}
