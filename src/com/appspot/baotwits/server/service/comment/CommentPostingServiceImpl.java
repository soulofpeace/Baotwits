package com.appspot.baotwits.server.service.comment;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appspot.baotwits.client.data.dto.comment.CommentDto;
import com.appspot.baotwits.client.data.dto.comment.CommentorDto;
import com.appspot.baotwits.client.proxy.comment.CommentPostingService;
import com.appspot.baotwits.server.data.dao.comment.CommentDao;
import com.appspot.baotwits.server.data.dao.comment.CommentorDao;
import com.appspot.baotwits.server.data.model.comment.Comment;
import com.appspot.baotwits.server.data.model.comment.CommentorWrapper;
import com.google.appengine.api.datastore.KeyFactory;

@Service
public class CommentPostingServiceImpl implements CommentPostingService {
	
	private static final Logger logger = Logger.getLogger(CommentPostingServiceImpl.class.getName());
	
	@Autowired
	private CommentDao commentDao;
	
	@Autowired
	private CommentorDao commentorDao;
	

	@Override
	public boolean addComment(CommentDto commentDto) {
		// TODO Auto-generated method stub
		Comment comment =this.createComment(commentDto);
		if (comment==null){
			return false;
		}
		return commentDao.addComment(comment);
		
	}

	@Override
	public ArrayList<CommentDto> getComments(String statusId) {
		// TODO Auto-generated method stub
		List<Comment> commentsList = commentDao.getComment(statusId);
		ArrayList<CommentDto> comments = new ArrayList<CommentDto>();
		for (Comment comment: commentsList){
			CommentDto commentDto =this.createCommentDto(comment);
			comments.add(commentDto);
		}
		return comments;
	}

	public void setCommentDao(CommentDao commentDao) {
		this.commentDao = commentDao;
	}

	public CommentDao getCommentDao() {
		return commentDao;
	}
	
	private CommentorDto createCommentorDto(CommentorWrapper commentorWrapper){
		CommentorDto commentorDto = new CommentorDto();
		commentorDto.setId(KeyFactory.keyToString(commentorWrapper.getCommenterId()));
		commentorDto.setName(commentorWrapper.getName());
		commentorDto.setProfileImageURL(commentorWrapper.getProfileImageURL());
		return commentorDto;
	}
	
	private CommentDto createCommentDto(Comment comment){
		CommentDto commentDto = new CommentDto();
		commentDto.setId(KeyFactory.keyToString(comment.getKey()));
		commentDto.setDate(comment.getDate());
		commentDto.setStatusId(comment.getStatusId());
		commentDto.setText(comment.getText());
		logger.info("KAKA" +comment.getCommentorWrapper());
		CommentorWrapper commentorWrapper =commentorDao.getCommentorWrapper(comment.getCommentorWrapper());
		CommentorDto commentorDto = this.createCommentorDto(commentorWrapper);
		commentDto.setCommentorDto(commentorDto);
		return commentDto;
		
	}
	
	private Comment createComment(CommentDto commentDto){
		CommentorWrapper commentor =commentorDao.getCommentorWrapper(KeyFactory.stringToKey(commentDto.getCommentorDto().getId()));
		if (commentor==null){
			return null;
		}
		Comment comment = new Comment();
		logger.info("Text is "+commentDto.getText());
		comment.setDate(commentDto.getDate());
		comment.setStatusId(commentDto.getStatusId());
		comment.setText(commentDto.getText());
		comment.setCommentorWrapper(commentor.getCommenterId());
		return comment;
	}

	

}
