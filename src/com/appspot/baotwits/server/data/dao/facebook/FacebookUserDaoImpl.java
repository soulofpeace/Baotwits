package com.appspot.baotwits.server.data.dao.facebook;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.appspot.baotwits.server.data.model.TwitterUser;
import com.appspot.baotwits.server.data.model.facebook.FacebookUser;
import com.google.appengine.api.datastore.KeyFactory;

@Repository
public class FacebookUserDaoImpl implements FacebookUserDao {
	
	private static Logger logger = Logger.getLogger(FacebookUserDao.class.getName());
	
	
	@Autowired
	private PersistenceManagerFactory pmf;
	
	
	public void setPersistenceManagerFactory(PersistenceManagerFactory pmf){
		this.pmf = pmf;
	}

	@Override
	public FacebookUser getFacebookUser(String keyString) {
		// TODO Auto-generated method stub
		PersistenceManager pm = pmf.getPersistenceManager();
		try{
			
			FacebookUser facebookUser = pm.getObjectById(FacebookUser.class, KeyFactory.stringToKey(keyString));
			return pm.detachCopy(facebookUser);
		}
		finally{
			pm.close();
		}
		
	}

	@Override
	public void saveFacebookUser(FacebookUser facebookUser) {
		// TODO Auto-generated method stub
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		 try {  
		        tx.begin();
		        pm.makePersistent(facebookUser);
		        tx.commit();
		    } finally {
		        if (tx.isActive()) {
		            tx.rollback();
		        }
		        pm.close();
		    }
	}
	
	public void setTwitterUser(FacebookUser facebookUser, TwitterUser twitterUser){
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tnx =pm.currentTransaction();
		try{
			tnx.begin();
			logger.info(KeyFactory.keyToString(facebookUser.getKey()));
			twitterUser.setFacebookUserKey(facebookUser.getKey());
			twitterUser = pm.makePersistent(twitterUser);
			facebookUser.setTwitterUserKey(twitterUser.getKey());
			pm.makePersistent(facebookUser);
			tnx.commit();
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.warning(ex.getMessage());
			for(StackTraceElement element: ex.getStackTrace()){
				logger.warning(element.getFileName()+"::"+element.getClassName()+"::"+element.getMethodName()+"::"+element.getLineNumber());
			}
		}finally{
			if(tnx.isActive()){
				tnx.rollback();
			}
			pm.close();
		}
	}

	@Override
	public FacebookUser getFacebookUserbyFID(String fid) {
		// TODO Auto-generated method stub
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tnx = pm.currentTransaction();
		try{
			tnx.begin();
			Query query = pm.newQuery(FacebookUser.class);
			query.setFilter("facebookId == facebookIdParam");
			query.declareParameters("String facebookIdParam");
			logger.info("fid "+fid);
			List<FacebookUser> facebookUsers = (List<FacebookUser>) query.execute(fid);
			FacebookUser facebookUser = facebookUsers.isEmpty()?null:facebookUsers.get(0);
			tnx.commit();
			return pm.detachCopy(facebookUser);
		}
		finally{
			if(tnx.isActive()){
				tnx.rollback();
			}
			pm.close();
			
		}
	}
	
	

}
