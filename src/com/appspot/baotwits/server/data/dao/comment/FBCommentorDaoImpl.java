package com.appspot.baotwits.server.data.dao.comment;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.jdo.Query;
import javax.jdo.Transaction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.appspot.baotwits.server.constants.comment.CommentConstants;
import com.appspot.baotwits.server.data.model.comment.CommentorWrapper;
import com.appspot.baotwits.server.data.model.comment.FBCommentor;

@Repository
public class FBCommentorDaoImpl implements FBCommentorDao{
	@Autowired
	private PersistenceManagerFactory pmf;
	private static final Logger logger = Logger.getLogger(FBCommentorDaoImpl.class.getCanonicalName());
	
	public void setPersistenceManagerFactory(PersistenceManagerFactory pmf){
		this.pmf = pmf;
	}
	
	@Override
	public CommentorWrapper createNewCommentorWrapper(FBCommentor fbCommentor) {
		// TODO Auto-generated method stub
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		fbCommentor = pm.makePersistent(fbCommentor);
		CommentorWrapper commentor = new CommentorWrapper();
		try{
			tx.begin();
			commentor.setType(CommentConstants.getFacebookType());
			commentor.setName(fbCommentor.getName());
			commentor.setProfileImageURL(fbCommentor.getProfileImageURL());
			commentor.setCommentorInstance(fbCommentor.getId());
			commentor = pm.makePersistent(commentor);
			fbCommentor.setCommentorWrapper(commentor.getCommenterId());
			fbCommentor=pm.makePersistent(fbCommentor);
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
	public FBCommentor getFBCommentor(int id) {
		// TODO Auto-generated method stub
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try{
			tx.begin();
			Query query = pm.newQuery(FBCommentor.class);
			query.setFilter("fbId==fbIdParam");
			query.declareParameters("int fbIdParam");
			List<FBCommentor> fbCommentors =(List<FBCommentor>)query.execute(id);
			FBCommentor fbCommentor = fbCommentors.isEmpty()?null:fbCommentors.get(0);
			tx.commit();
			return pm.detachCopy(fbCommentor);
		}
		finally{
			if(tx.isActive()){
				tx.rollback();
			}
			pm.close();
		}
		
	}

	@Override
	public CommentorWrapper updateFBCommentor(FBCommentor fbCommentor) {
		// TODO Auto-generated method stub
		PersistenceManager pm = pmf.getPersistenceManager();
		Transaction tx = pm.currentTransaction();
		try{
			tx.begin();
			fbCommentor =pm.makePersistent(fbCommentor);
			CommentorWrapper commentor = pm.getObjectById(CommentorWrapper.class, fbCommentor.getCommentorWrapper());
			tx.commit();
			return commentor;
		}finally{
			if(tx.isActive()){
				tx.rollback();
			}
			pm.close();
		}
		
		
	}

}
