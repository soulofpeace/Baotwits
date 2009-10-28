package com.appspot.baotwits.client.proxy.comment;

import java.util.ArrayList;

import com.appspot.baotwits.client.data.dto.comment.CommentDto;
import com.appspot.baotwits.client.data.dto.comment.CommentorDto;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.gwt.user.client.rpc.RemoteService;

@RemoteServiceRelativePath("comment/post")
public interface CommentPostingService extends RemoteService{
	
	public boolean addComment(CommentDto comment);
	public ArrayList<CommentDto> getComments(String statusId);

}
