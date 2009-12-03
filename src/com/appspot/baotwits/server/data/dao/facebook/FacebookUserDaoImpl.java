package com.appspot.baotwits.server.data.dao.facebook;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
		FacebookUser facebookUser = pm.getObjectById(FacebookUser.class, KeyFactory.stringToKey(keyString));
		pm.close();
		return pm.detachCopy(facebookUser);
		
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
