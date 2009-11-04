package com.appspot.baotwits.client.widget;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

public class CommentWidget implements EntryPoint {
	private Frame statusFrame;
	private int width;
	private int height;
	
	@Override
	public void onModuleLoad() {
		// TODO Auto-generated method stub
		this.initialiseDimension();
		RootPanel root = RootPanel.get();
		this.initialiseFrame(this.getUserId());
		root.add(statusFrame);
	}
	
	private void initialiseDimension(){
		this.width= Window.getClientWidth()-20;
		this.height = Window.getClientHeight()-80;
	}
	
	private void initialiseFrame(String userId){
		statusFrame = new Frame("http://baotwits.appspot.com/user/"+userId);
		statusFrame.setWidth(String.valueOf(width));
		statusFrame.setHeight(String.valueOf(height));
	}
	
	private String getUserId(){
		return this.getById("userId");
	}
	
	private native String getById(String id) /*-{
 	var element = $doc.getElementById(id);
 	if (element == null)
     	return "";
 	return $doc.getElementById(id).value;
	}-*/;
 

}
