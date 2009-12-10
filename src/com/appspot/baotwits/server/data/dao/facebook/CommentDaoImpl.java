package com.appspot.baotwits.server.data.dao.facebook;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.appspot.baotwits.server.data.model.facebook.Comment;
import com.appspot.baotwits.server.data.model.facebook.FacebookUser;
import com.google.appengine.api.datastore.KeyFactory;

@Repository
public class CommentDaoImpl implements CommentDao {
	
	private static Logger logger = Logger.getLogger(CommentDaoImpl.class.getName()); 
	
	@Autowired
	private PersistenceManagerFactory pmf;
	
	
	public void setPersistenceManagerFactory(PersistenceManagerFactory pmf){
		this.pmf = pmf;
	}

	@Override
	public void deleteComment(Comment comment) {
		// TODO Auto-generated method stub
		PersistenceManager pm = this.pmf.getPersistenceManager();
		pm.deletePersistent(comment);
	}

	@Override
	public Comment getCommentById(String keyString) {
		// TODO Auto-generated method stub
		PersistenceManager pm = this.pmf.getPersistenceManager();
		try{
			Comment comment = pm.getObjectById(Comment.class, KeyFactory.stringToKey(keyString));
			return pm.detachCopy(comment);
		}
		finally{
			pm.close();
		}
	}

	@Override
	public List<Comment> getCommentByStatusId(String statusId) {
		// TODO Auto-generated method stub
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tnx = pm.currentTransaction();
		try{
			tnx.begin();
			Query query = pm.newQuery(Comment.class);
			query.setFilter("statusId == statusIdParam");
			query.declareParameters("String statusIdParam");
			logger.info("Status id "+statusId);
			List<Comment> comments = (List<Comment>)query.execute(statusId);
			tnx.commit();
			return (List<Comment>)pm.detachCopyAll(comments);
		}
		finally{
			if(tnx.isActive()){
				tnx.rollback();
			}
			pm.close();
			
		}
	}

	@Override
	public void saveComment(Comment comment) {
		// TODO Auto-generated method stub
		PersistenceManager pm = pmf.getPersistenceManager();
		comment=pm.makePersistent(comment);
		Transaction tnx = pm.currentTransaction();
		try{
			tnx.begin();
			FacebookUser facebookUser = pm.getObjectById(FacebookUser.class, comment.getFacebookUserKey());
			facebookUser.addComment(comment.getKey());
			facebookUser = pm.makePersistent(facebookUser);
			tnx.commit();
		}
		finally{
			if(tnx.isActive()){
				tnx.rollback();
			}
			pm.close();
		}

	}

}
