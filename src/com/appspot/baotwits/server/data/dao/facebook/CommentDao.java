package com.appspot.baotwits.server.data.dao.facebook;

import java.util.List;

import com.appspot.baotwits.server.data.model.facebook.Comment;

public interface CommentDao {
	
	public Comment getCommentById(String keyString);
	public void saveComment(Comment comment);
	public List<Comment> getCommentByStatusId(String statusId);
	public void deleteComment(Comment comment);

}
