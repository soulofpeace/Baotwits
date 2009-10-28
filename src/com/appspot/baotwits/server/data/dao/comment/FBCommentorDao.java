package com.appspot.baotwits.server.data.dao.comment;

import com.appspot.baotwits.server.data.model.comment.CommentorWrapper;
import com.appspot.baotwits.server.data.model.comment.FBCommentor;

public interface FBCommentorDao {
	public CommentorWrapper createNewCommentorWrapper(FBCommentor fbCommentor);
	public FBCommentor getFBCommentor(int id);
	public CommentorWrapper updateFBCommentor(FBCommentor fbCommentor);

}
