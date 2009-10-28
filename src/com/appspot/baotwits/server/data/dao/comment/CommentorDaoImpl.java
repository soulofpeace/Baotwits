package com.appspot.baotwits.server.data.dao.comment;

import java.util.logging.Logger;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.appspot.baotwits.server.constants.comment.CommentConstants;
import com.appspot.baotwits.server.data.model.comment.CommentorWrapper;
import com.appspot.baotwits.server.data.model.comment.CommentorInterface;
import com.appspot.baotwits.server.data.model.comment.TwitCommentor;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;

@Repository
public class CommentorDaoImpl implements CommentorDao{
	private static final Logger logger = Logger.getLogger(CommentorDaoImpl.class.getName());
	@Autowired
	private PersistenceManagerFactory pmf;
	
	
	public void setPersistenceManagerFactory(PersistenceManagerFactory pmf){
		this.pmf = pmf;
	}

	public CommentorWrapper getCommentorWrapper(Key key) {
		// TODO Auto-generated method stub
		logger.info("KeyString is "+key);
		PersistenceManager pm = pmf.getPersistenceManager();
		try{
			CommentorWrapper commentorWrapper =pm.getObjectById(CommentorWrapper.class, key);
			
			return pm.detachCopy(commentorWrapper);
		}
		catch(JDOObjectNotFoundException ex){
			logger.info("Cannot find?");
			return null;
		}
		finally{
			pm.close();
		}
	}
	
	


}
