package com.appspot.baotwits.client.proxy.comment;

import java.util.ArrayList;

import com.appspot.baotwits.client.data.dto.comment.CommentDto;
import com.appspot.baotwits.client.data.dto.comment.CommentorDto;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommentPostingServiceAsync {

	public void addComment(CommentDto comment, AsyncCallback<Boolean> callback);

	public void getComments(String statusId,AsyncCallback<ArrayList<CommentDto>> callback);
	
	
}
