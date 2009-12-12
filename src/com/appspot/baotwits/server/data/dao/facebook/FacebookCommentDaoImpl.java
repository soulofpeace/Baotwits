package com.appspot.baotwits.server.data.dao.facebook;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.appspot.baotwits.server.data.model.facebook.FBComment;
import com.appspot.baotwits.server.data.model.facebook.FacebookUser;
import com.google.appengine.api.datastore.KeyFactory;

@Repository
public class FacebookCommentDaoImpl implements CommentDao {
	
	private static Logger logger = Logger.getLogger(FacebookCommentDaoImpl.class.getName()); 
	
	@Autowired
	private PersistenceManagerFactory pmf;
	
	
	public void setPersistenceManagerFactory(PersistenceManagerFactory pmf){
		this.pmf = pmf;
	}

	@Override
	public void deleteComment(FBComment comment) {
		// TODO Auto-generated method stub
		PersistenceManager pm = this.pmf.getPersistenceManager();
		pm.deletePersistent(comment);
	}

	@Override
	public FBComment getCommentById(String keyString) {
		// TODO Auto-generated method stub
		PersistenceManager pm = this.pmf.getPersistenceManager();
		try{
			FBComment comment = pm.getObjectById(FBComment.class, KeyFactory.stringToKey(keyString));
			return pm.detachCopy(comment);
		}
		finally{
			pm.close();
		}
	}

	@Override
	public List<FBComment> getCommentByStatusId(String statusId) {
		// TODO Auto-generated method stub
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tnx = pm.currentTransaction();
		try{
			tnx.begin();
			Query query = pm.newQuery(FBComment.class);
			query.setFilter("statusId == statusIdParam");
			query.setOrdering("dateCreated asc");
			query.declareParameters("String statusIdParam");
			logger.info("Status id "+statusId);
			List<FBComment> comments = (List<FBComment>)query.execute(statusId);
			tnx.commit();
			return (List<FBComment>)pm.detachCopyAll(comments);
		}
		finally{
			if(tnx.isActive()){
				tnx.rollback();
			}
			pm.close();
			
		}
	}

	@Override
	public FBComment saveComment(FBComment comment) {
		// TODO Auto-generated method stub
		PersistenceManager pm = pmf.getPersistenceManager();
		comment=pm.makePersistent(comment);
		Transaction tnx = pm.currentTransaction();
		try{
			tnx.begin();
			FacebookUser facebookUser = pm.getObjectById(FacebookUser.class, comment.getFacebookUserKey());
			logger.info("key is "+comment.getFacebookUserKey());
			logger.info("hello"+comment.getKey());
			logger.info("hello2"+facebookUser);
			facebookUser.addComment(comment.getKey());
			facebookUser = pm.makePersistent(facebookUser);
			tnx.commit();
			return pm.detachCopy(comment);
		}
		finally{
			if(tnx.isActive()){
				tnx.rollback();
			}
			pm.close();
		}

	}

}
