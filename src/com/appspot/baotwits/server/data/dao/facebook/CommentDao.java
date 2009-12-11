package com.appspot.baotwits.server.data.dao.facebook;

import java.util.List;

import com.appspot.baotwits.server.data.model.facebook.FBComment;

public interface CommentDao {
	
	public FBComment getCommentById(String keyString);
	public FBComment saveComment(FBComment comment);
	public List<FBComment> getCommentByStatusId(String statusId);
	public void deleteComment(FBComment comment);

}
