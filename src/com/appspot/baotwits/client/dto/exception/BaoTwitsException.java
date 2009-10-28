package com.appspot.baotwits.client.dto.exception;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.rpc.SerializableException;

public class BaoTwitsException extends SerializableException{
	private static final long serialVersionUID = -1443937578707739673L;

	public BaoTwitsException(String message){
		super(message);
	}
}
