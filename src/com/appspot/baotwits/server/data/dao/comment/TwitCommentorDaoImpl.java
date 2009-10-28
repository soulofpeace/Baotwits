package com.appspot.baotwits.server.data.dao.comment;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;
import javax.jdo.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.appspot.baotwits.server.constants.comment.CommentConstants;
import com.appspot.baotwits.server.data.model.comment.CommentorWrapper;
import com.appspot.baotwits.server.data.model.comment.TwitCommentor;

@Repository
public class TwitCommentorDaoImpl implements TwitCommentorDao {
	private static final Logger logger = Logger.getLogger(TwitCommentorDaoImpl.class.getName());
	@Autowired
	private PersistenceManagerFactory pmf;
	
	
	public void setPersistenceManagerFactory(PersistenceManagerFactory pmf){
		this.pmf = pmf;
	}
	public CommentorWrapper createNewCommentorWrapper(TwitCommentor twitCommentor) {
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		twitCommentor= pm.makePersistent(twitCommentor);
		CommentorWrapper commentor = new CommentorWrapper();
		try{
			tx.begin();
			commentor.setType(CommentConstants.getTwitterType());
			commentor.setCommentorInstance(twitCommentor.getId());
			commentor.setName(twitCommentor.getName());
			commentor.setUrl(twitCommentor.getURL());
			commentor.setProfileImageURL(twitCommentor.getProfileImageURL());
			commentor=pm.makePersistent(commentor);
			twitCommentor.setCommentorWrapper(commentor.getCommenterId());
			twitCommentor=pm.makePersistent(twitCommentor);
			logger.info("Commenter is "+ commentor);
			logger.info("Commentor id is "+commentor.getCommenterId());
			tx.commit();
		}
		catch(Exception ex){
			ex.printStackTrace();
			logger.warning(ex.getMessage());
			for(StackTraceElement element: ex.getStackTrace()){
				logger.warning(element.getFileName()+"::"+element.getClassName()+"::"+element.getMethodName()+"::"+element.getLineNumber());
			}
			commentor= null;
		}finally{
			if(tx.isActive()){
				tx.rollback();
			}
			pm.close();
		}
		
		return commentor;
	}

	@Override
	public TwitCommentor getTwitCommentor(int id) {
		// TODO Auto-generated method stub
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try{
			tx.begin();
			Query query = pm.newQuery(TwitCommentor.class);
			query.setFilter("twitterId==twitterIdParam");
			query.declareParameters("int twitterIdParam");
			List<TwitCommentor> list=(List<TwitCommentor>) query.execute(id);
			TwitCommentor twitCommentor =list.isEmpty()?null:list.get(0);
			if (twitCommentor!=null){
				logger.info("commentor key is"+twitCommentor.getCommentorWrapper());
			}
			return pm.detachCopy(twitCommentor); 
		}finally{
			if(tx.isActive()){
				tx.rollback();
			}
			pm.close();
		}
		
	}

	@Override
	public CommentorWrapper updateTwitCommentor(TwitCommentor twitCommentor) {
		// TODO Auto-generated method stub
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try{
			tx.begin();
			twitCommentor = pm.makePersistent(twitCommentor);
			logger.info("twitCommentor key is "+twitCommentor.getCommentorWrapper());
			CommentorWrapper commentor = pm.getObjectById(CommentorWrapper.class, twitCommentor.getCommentorWrapper());
			return commentor;
		}finally{
			if(tx.isActive()){
				tx.rollback();
			}
			pm.close();
		}
		
	}

}
