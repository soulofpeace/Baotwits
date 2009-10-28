package com.appspot.baotwits.client.data.dto.comment;

import java.util.Date;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CommentDto implements IsSerializable{
	private Date date;
	private String text;
	private CommentorDto commentorDto;
	private String statusId;
	private String id;
	
	public void setDate(Date date) {
		this.date = date;
	}
	public Date getDate() {
		return date;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getText() {
		return text;
	}
	public void setCommentorDto(CommentorDto commentorDto) {
		this.commentorDto = commentorDto;
	}
	public CommentorDto getCommentorDto() {
		return this.commentorDto;
	}
	public void setStatusId(String statusId) {
		this.statusId = statusId;
	}
	public String getStatusId() {
		return statusId;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getId() {
		return id;
	}
	
}
