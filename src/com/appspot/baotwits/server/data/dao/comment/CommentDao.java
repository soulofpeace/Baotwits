package com.appspot.baotwits.server.data.dao.comment;

import java.util.List;

import com.appspot.baotwits.server.data.model.comment.Comment;
import com.appspot.baotwits.server.data.model.comment.CommentorWrapper;


public interface CommentDao {
	public boolean addComment(Comment comment);
	public List<Comment> getComment(String statusId);
}
