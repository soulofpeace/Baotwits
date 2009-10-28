package com.appspot.baotwits.server.data.dao.comment;

import java.util.List;

import com.appspot.baotwits.server.data.model.comment.CommentorWrapper;
import com.appspot.baotwits.server.data.model.comment.TwitCommentor;

public interface TwitCommentorDao {
	public CommentorWrapper createNewCommentorWrapper(TwitCommentor twitCommentor);
	public TwitCommentor getTwitCommentor(int id);
	public CommentorWrapper updateTwitCommentor(TwitCommentor twitCommentor);

}
