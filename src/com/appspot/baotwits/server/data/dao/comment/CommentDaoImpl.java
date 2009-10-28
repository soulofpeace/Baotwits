package com.appspot.baotwits.server.data.dao.comment;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.appspot.baotwits.server.data.model.comment.Comment;
import com.appspot.baotwits.server.data.model.comment.CommentorWrapper;
import com.google.appengine.repackaged.com.google.common.collect.Lists;

import javax.jdo.Transaction;

import java.util.logging.Logger;

@Repository
public class CommentDaoImpl implements CommentDao{
	
	private static final Logger logger = Logger.getLogger(CommentDaoImpl.class.getName());
	@Autowired
	private PersistenceManagerFactory pmf;
	
	
	public void setPersistenceManagerFactory(PersistenceManagerFactory pmf){
		this.pmf = pmf;
	}

	@Override
	public boolean addComment(Comment comment) {
		// TODO Auto-generated method stub
		logger.info("Adding Comments");
		PersistenceManager pm = pmf.getPersistenceManager();
		comment = pm.makePersistent(comment);
		Transaction tx = pm.currentTransaction();
		try{
			tx.begin();
			logger.info("Comment is "+comment.getText());
			CommentorWrapper commentor = pm.getObjectById(CommentorWrapper.class, comment.getCommentorWrapper());
			logger.info("commntorWrapper key is "+comment.getCommentorWrapper());
			logger.info("Comment key is "+ comment.getKey());
			commentor.addComment(comment.getKey());
			commentor = pm.makePersistent(commentor);
			logger.info("comment size is "+commentor.getComments().size());
			tx.commit();
			return true;
		}
		catch(Exception ex){
			logger.info(ex.getMessage());
			return false;
		}
		finally{
			if(tx.isActive()){
				tx.rollback();
			}
			pm.close();
		}
	}

	@Override
	public List<Comment> getComment(String statusId) {
		PersistenceManager pm = pmf.getPersistenceManager();
		// TODO Auto-generated method stub
		Transaction tx = pm.currentTransaction();
		try{
			tx.begin();
			Query query = pm.newQuery(Comment.class);
			query.setFilter("statusId==statusIdParam");
			query.setOrdering("date asc");
			query.declareParameters("String statusIdParam");
			List<Comment>comments = (List<Comment>) query.execute(statusId);
			List<Comment> commentsList = Lists.newArrayList();
			for (Comment comment: comments){
				logger.info("hoho" +comment.getCommentorWrapper());
				commentsList.add(pm.detachCopy(comment));
			}
			tx.commit();
			return commentsList;
		}
		finally{
			if(tx.isActive()){
				tx.rollback();
			}
			pm.close();
		}
		
	}

}
