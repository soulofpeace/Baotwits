package com.appspot.baotwits.server.data.dao.comment;

import com.appspot.baotwits.server.data.model.comment.CommentorWrapper;
import com.google.appengine.api.datastore.Key;


public interface CommentorDao {
	public CommentorWrapper getCommentorWrapper(Key key);

}
