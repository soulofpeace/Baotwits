package com.appspot.baotwits.client.facebook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.http.client.*;

public class Baotwits implements EntryPoint {
	
	private FacebookUser facebookUser;
	
	private static Label debugLabel = new Label();
	
	//profile pix dimension
	private double imageProfileSize;
	//tweets width
	private double tweetsWidth; 
	
	private FlexTable statusesTable = new FlexTable();
	private FlexTable ownStatusesTable = new FlexTable();
	private ArrayList<String> statuses = new ArrayList<String>();
	private ArrayList<String> ownStatus = new ArrayList<String>();
	private VerticalPanel tweetPanel = new VerticalPanel();
	private TextArea tweetBox = new TextArea();
	private Button tweetButton = new Button();
	private Label charRemain = new Label();
	private Label welcomeMsg = new Label();
	private VerticalPanel twitterPanel = new VerticalPanel();
	private VerticalPanel ownTweetsPanel = new VerticalPanel();
	private VerticalPanel friendsTweetsPanel = new VerticalPanel();
	private TabPanel mainPanel = new TabPanel();
	private Timer timer;
	
	private Map<String, List<String>> parameterMap;
	

	
	public void onModuleLoad(){
		Baotwits.debug("start");
		this.setDimension();
		if (this.isDebug()){
			RootPanel.get().add(debugLabel);
		}
		String facebookUserId = Window.Location.getParameter("fb_sig_user");
		parameterMap = Window.Location.getParameterMap();
		this.setFacebookUser(facebookUserId);
		this.mainPanel.setWidth(String.valueOf(this.tweetsWidth+this.imageProfileSize));
		this.mainPanel.add(this.twitterPanel, "Tweet Tweet");
		this.mainPanel.add(this.friendsTweetsPanel, "Friends Tweet");
		this.mainPanel.add(this.ownTweetsPanel, "Own Tweet");
		this.mainPanel.selectTab(0);
		RootPanel.get().add(this.mainPanel);
		
		
	}
	
	public void setFacebookUser(String facebookUserId){
		String url="http://baotwits.appspot.com/facebook/user/"+facebookUserId+".json";
		url=this.setfacebookRequestParameter(url);
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
		requestBuilder.setHeader("Content-Type", "application/json");
		try{
			Request request = requestBuilder.sendRequest(null, new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					Baotwits.debug("Hello receive some response here");
					// TODO Auto-generated method stub
					if(200==response.getStatusCode()){
						String json = response.getText();
						Baotwits.debug(json);
						facebookUser=FacebookUser.fromJson(json);
						Baotwits.debug("wahahaha"+facebookUser.getStatuses().length());
						setUpTweetsPanel();
					}	
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					// TODO Auto-generated method stub
					
				}
			} 
					
			);
		}
		catch(RequestException ex){
			
		}
	}
	
	private void setUpTweetsPanel(){
		if(facebookUser.getStatuses().length()==0){
			VerticalPanel setupPanel = new VerticalPanel();
			Label setupLabel = new Label("Setup your twitter acoount");
			Anchor twitterOauthLink = new Anchor("Click Here");
			twitterOauthLink.setHref(facebookUser.getTwitterOauthLink());
			setupPanel.add(setupLabel);
			setupPanel.add(twitterOauthLink);
			twitterPanel.add(setupPanel);
		}
		else{
			loadWelcomeMsg();
			loadTweetPanel();
			loadStatuses();
			timer = new Timer() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					updateFacebookUser();
				}
			};
			timer.scheduleRepeating(300000);
		}
	}
	
	private void setupOwnTweetPanel(){
		this.updateOwnStatuses();
		timer = new Timer() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateOwnStatuses();
			}
		};
		timer.scheduleRepeating(300000);
		
	}
	
	private void updateOwnStatuses(){
		String url="http://baotwits.appspot.com/facebook/user/"+facebookUser.getFacebookUserInfo().getUid()+"/ownStatuses.json";
		url=this.setfacebookRequestParameter(url);
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
		requestBuilder.setHeader("Content-Type", "application/json");
		requestBuilder.setHeader("Content-Type", "application/json");
		try{
			Request request = requestBuilder.sendRequest(null, new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					Baotwits.debug("Hello receive some response here");
					// TODO Auto-generated method stub
					if(200==response.getStatusCode()){
						String json = response.getText();
						Baotwits.debug(json);
						JsArray<Status> statuses = getOwnStatuses(json);
						
					}	
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					// TODO Auto-generated method stub
					
				}
			} 
					
			);
		}
		catch(RequestException ex){
			
		}
	}
	
	private void loadOwnStatuses(JsArray<Status> statuses){
		int rowCount=0;
		for(int i=0; i<statuses.length();i++){
			Status status = statuses.get(i);
			if(!this.ownStatus.contains(status.getId())){
				VerticalPanel vp1=createScreenNamePanel(status);
				VerticalPanel vp2=createTweets(status);
				ownStatusesTable.insertRow(rowCount);
				ownStatusesTable.setWidget(rowCount, 0, vp1);
				ownStatusesTable.setWidget(rowCount, 1, vp2);
				ownStatusesTable.getRowFormatter().addStyleName(rowCount, "status");
				ownStatusesTable.getCellFormatter().setWidth(rowCount, 0, String.valueOf(imageProfileSize));
				ownStatusesTable.getCellFormatter().setWidth(rowCount, 1, String.valueOf(tweetsWidth));
				rowCount++;
				CommentDropDown commentDropDown = new CommentDropDown(String.valueOf(status.getId()));
				ownStatusesTable.setWidget(rowCount, 1, commentDropDown );
				ownStatusesTable.getFlexCellFormatter().setColSpan(rowCount, 1, 2);
				rowCount++;
				ownStatus.add(status.getId());
			}
			else{
				break;
			}
			
		}
		statusesTable.setStyleName("tweets");
		this.ownTweetsPanel.add(this.ownStatusesTable);
	}
	
	private void updateFacebookUser(){
		String url="http://baotwits.appspot.com/facebook/user/"+this.facebookUser.getFacebookUserInfo().getUid()+".json";
		url=this.setfacebookRequestParameter(url);
		RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.GET, URL.encode(url));
		requestBuilder.setHeader("Content-Type", "application/json");
		try{
			Request request = requestBuilder.sendRequest(null, new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					Baotwits.debug("Hello receive some response here");
					// TODO Auto-generated method stub
					if(200==response.getStatusCode()){
						String json = response.getText();
						Baotwits.debug(json);
						facebookUser=FacebookUser.fromJson(json);
						Baotwits.debug("wahahaha"+facebookUser.getStatuses().length());
						loadStatuses();
						
					}	
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					// TODO Auto-generated method stub
					
				}
			} 
					
			);
		}
		catch(RequestException ex){
			
		}
	}

	
	
	private void setDimension(){
		//leaving width for scrollbar
		int width = Window.getClientWidth()-40;
		this.debug("width is "+width);
		this.imageProfileSize = ((width/4.0))<48?width/4.0:48;
		this.tweetsWidth=width-this.imageProfileSize;
	}
	

	private void loadWelcomeMsg(){
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		welcomeMsg.getElement().setInnerHTML("Welcome "+facebookUser.getFacebookUserInfo().getName());
		//signOutLink.setHref(loginInfo.getLogoutUrl());
		horizontalPanel.add(welcomeMsg);
		//horizontalPanel.add(signOutLink);
		twitterPanel.add(horizontalPanel);
	}
	
	private void loadStatuses(){
		int count=0;
		JsArray<Status> result = this.facebookUser.getStatuses();
		Baotwits.debug("Yoyo1 "+result.length());
		for (int i =0; i<result.length();i++){
			Status status = result.get(i);
			if(statuses.size()!=0){
				if(!statuses.contains(status.getId())){
					Baotwits.debug("yoyo "+ status.getText());
					//need to add in code to put in newStatus
					VerticalPanel vp1=createScreenNamePanel(status);
					VerticalPanel vp2=createTweets(status);
					statusesTable.insertRow(count);
					statusesTable.setWidget(count, 0, vp1);
					statusesTable.setWidget(count, 1, vp2);
					statusesTable.getRowFormatter().addStyleName(count, "status");
					statusesTable.getCellFormatter().setWidth(count, 0, String.valueOf(imageProfileSize));
					statusesTable.getCellFormatter().setWidth(count, 1, String.valueOf(tweetsWidth));
					
					count++;
					statuses.add(status.getId());
				}
				else{
					break;
				}
			}
			else{
				Baotwits.debug("yoyo "+ status.getText());
				VerticalPanel vp1=createScreenNamePanel(status);
				VerticalPanel vp2=createTweets(status);
				statusesTable.insertRow(count);
				statusesTable.setWidget(count, 0, vp1);
				statusesTable.setWidget(count, 1, vp2);
				statusesTable.getRowFormatter().addStyleName(count, "status");
				statusesTable.getCellFormatter().setWidth(count, 0, String.valueOf(imageProfileSize));
				statusesTable.getCellFormatter().setWidth(count, 1, String.valueOf(tweetsWidth));
				
				count++;
				statuses.add(status.getId());
			}
		
			
		}
					
				
		statusesTable.setStyleName("tweets");
		twitterPanel.add(statusesTable);
	
	}
	
	
	private void loadTweetPanel(){
		HorizontalPanel auxPanel = new HorizontalPanel();
		charRemain = new Label("140 Chars");
		charRemain.addStyleName("charRemain");
		tweetBox.setText("");
		tweetBox.setCharacterWidth(70);
		tweetBox.setVisibleLines(2);
		
		tweetButton = new Button("Tweet", new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				String facebookUserId = facebookUser.getFacebookUserInfo().getUid();
				String url = "http://baotwits.appspot.com/facebook/user/"+facebookUserId+"/"+tweetBox.getText()+".json";
				url=setfacebookRequestParameter(url);
				RequestBuilder requestBuilder = new RequestBuilder(RequestBuilder.POST, URL.encode(url));
				requestBuilder.setHeader("Content-Type", "application/json");
				try{
					Request request = requestBuilder.sendRequest(null, new RequestCallback() {
						
						@Override
						public void onResponseReceived(Request request, Response response) {
							Baotwits.debug("Hello receive some response here");
							// TODO Auto-generated method stub
							if(200==response.getStatusCode()){
								String json = response.getText();
								Baotwits.debug(json);
								facebookUser=FacebookUser.fromJson(json);
								Baotwits.debug("wahahaha"+facebookUser.getStatuses().length());
								loadStatuses();
								tweetBox.setText("");
							}	
						}
						
						@Override
						public void onError(Request request, Throwable exception) {
							// TODO Auto-generated method stub
							
						}
					}
					);
				}
				catch(RequestException ex){
				
				}
			}
				
		});
		
		tweetButton.addStyleName("tweetButton");
		tweetBox.addKeyDownHandler(new KeyDownHandler() {
		
		
			public void onKeyDown(KeyDownEvent event) {
				// TODO Auto-generated method stub
				String text = tweetBox.getText();
				int remain = 140- text.length();
				charRemain.setText(remain+ " Chars");
				if(remain<=0){
					tweetBox.addStyleName("tweetExceedLimit");
				}
				else{
					tweetBox.removeStyleName("tweetExceedLimit");
				}
			}
		});
		
		tweetBox.addKeyUpHandler(new KeyUpHandler() {
			
			@Override
			public void onKeyUp(KeyUpEvent event) {
				String text = tweetBox.getText();
				int remain = 140- text.length();
				charRemain.setText(remain+ " Chars");
				if(remain<=0){
					tweetBox.addStyleName("tweetExceedLimit");
				}
				else{
					tweetBox.removeStyleName("tweetExceedLimit");
				}
				
			}
		});
		
		auxPanel.add(tweetBox);
		auxPanel.add(tweetButton);
		this.tweetPanel.add(auxPanel);
		this.tweetPanel.add(charRemain);
		twitterPanel.add(tweetPanel);
		
	}
	
	private VerticalPanel createScreenNamePanel(Status status){
		VerticalPanel vp1 = new VerticalPanel();
		Image profileImage = new Image();
		profileImage.setUrl(status.getImageProfileURL());
		profileImage.setHeight(String.valueOf(this.imageProfileSize));
		profileImage.setWidth(String.valueOf(this.imageProfileSize));
		vp1.add(profileImage);
		return vp1;
	}
	
	private VerticalPanel createTweets(Status status){
		VerticalPanel vp2 = new VerticalPanel();
		
		Label screenName = new Label();
		screenName.setText(status.getScreenName());
		screenName.addStyleName("screenName");
		screenName.setWidth(String.valueOf(this.tweetsWidth));
		screenName.setWordWrap(true);
		
		DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy.MM.dd 'at' HH:mm:ss");
		Label createdAt= new Label(dateTimeFormat.format(status.getCreatedAt()));
		createdAt.setWidth(String.valueOf(this.tweetsWidth));
		createdAt.setWordWrap(true);
		createdAt.addStyleName("date");
		
		Label tweets = new Label();
		tweets.getElement().setInnerHTML(status.getText());
		tweets.addStyleName("tweets");
		tweets.setWidth(String.valueOf(this.tweetsWidth));
		tweets.setWordWrap(true);
		
		vp2.add(screenName);
		vp2.add(createdAt);
		vp2.add(tweets);
		return vp2;
	}
	
	private static void debug(String text){
		String original=debugLabel.getText();
		original+=text;
		debugLabel.setText(original);
	}
	
	private native String getById(String id) /*-{
 	var element = $doc.getElementById(id);
 	if (element == null)
     	return "";
 	return $doc.getElementById(id).value;
	}-*/;
	
	private boolean isDebug(){
		String value = this.getById("debug");
		return value.equals("true")?true:false;
	}
	
	private String setfacebookRequestParameter(String url){
		url+="?";
		int count= parameterMap.size();
		for(String key: this.parameterMap.keySet()){
			url+=key+"=";
			List<String> values = this.parameterMap.get(key);
			int valueCount= values.size();
			for (String value: values){
				if(valueCount!=1){
					url+=value+",";
				}
				else{
					url+=value;
				}
			}
			if (count!=1){
				url+="&";
			}
			count--;
			
		}
		return url;
		
	}
	
	public final native JsArray<Status> getOwnStatuses(String json)/*-{alert(json);eval('('+json+')').statuses}-*/;

	private class CommentDropDown extends VerticalPanel{
		
		private final DisclosurePanel commentDropDown = new DisclosurePanel("Click to Comment");
		private final VerticalPanel contentPanel = new VerticalPanel();
		private final FlexTable commentTable = new FlexTable();
		private final TextArea commentBox = new TextArea();
		private final HorizontalPanel commentorProfilePanel = new HorizontalPanel();
		private Button postButton = new Button("Post!");
		
		private double commentorProfilePixSize;
		private double commentsWidth;
		private double width;
		
		private String statusId;
		private final ArrayList<String> comments = new ArrayList<String>(); 
		
		public CommentDropDown(String statusId){
			this.setDimension();
			this.commentDropDown.setWidth(String.valueOf(tweetsWidth));
			this.statusId = statusId;
			this.retrieveCommentsForStatus();
			this.createCommentorProfilePanel(facebookUser);
			this.createCommentBox();
			this.createPostButton();
			
			
			this.commentDropDown.setContent(this.getContentPanel());
			this.add(this.commentDropDown);
			
		}
		
		
		public DisclosurePanel getCommentDropDown() {
			return commentDropDown;
		}
		
		public VerticalPanel getContentPanel() {
			return contentPanel;
		}
		
		public TextArea getCommentBox() {
			return commentBox;
		}
		
		public FlexTable getCommentTable() {
			return commentTable;
		}
		

		public String getStatusId(){
			return this.statusId;
		}
		
		public HorizontalPanel getCommentorProfilePanel() {
			return commentorProfilePanel;
		}		

		
		public double getCommentorProfilePixSize(){
			return this.commentorProfilePixSize;
		}
		
		public double getCommentsWidth(){
			return this.commentsWidth;
		}
		
		
		
		public void createCommentorProfilePanel(final FacebookUser facebookUser){
			this.commentorProfilePanel.setWidth(String.valueOf(tweetsWidth));
			VerticalPanel profilePixPanel = new VerticalPanel();
			profilePixPanel.setWidth(String.valueOf(this.commentorProfilePixSize));
			
			Label postAs = new Label(facebookUser.getFacebookUserInfo().getName()+" Says");
			postAs.setWidth(String.valueOf(this.commentsWidth));
			postAs.setWordWrap(true);
			
			Image profilePix = new Image();
			profilePix.setUrl(facebookUser.getFacebookUserInfo().getPicSquare());
			profilePix.setWidth(String.valueOf(this.commentorProfilePixSize));
			profilePix.setHeight(String.valueOf(this.commentorProfilePixSize));
			profilePixPanel.add(profilePix);
			
			
			
			
			
			commentorProfilePanel.insert(postAs, 0);
			commentorProfilePanel.insert(profilePixPanel, 0);
			commentorProfilePanel.addStyleName("postProfile");
			
			this.contentPanel.add(this.getCommentorProfilePanel());
			
		}
		
		

		private void createCommentBox(){
			this.commentBox.setWidth(String.valueOf(tweetsWidth));
			this.getCommentBox().setText("");
			this.getCommentBox().setVisibleLines(3);
			this.contentPanel.add(commentBox);
		}
		
		private void retrieveCommentsForStatus(){
			this.contentPanel.add(this.getCommentTable());
		}
		
		private void createPostButton(){
			postButton.addClickHandler(new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
				}
			});
			this.contentPanel.add(postButton);
		}
		
		private void setDimension(){
			this.width = tweetsWidth;	
			this.commentorProfilePixSize=(width/4.0>48)?48:width/4.0;
			this.commentsWidth=this.width-commentorProfilePixSize;
			
		}
		
		/**
		private VerticalPanel createCommentorProfilePixPanel(CommentDto commentDto){
			 VerticalPanel commentorProfilePixPanel = new VerticalPanel();
			 Image profilePix = new Image(commentDto.getCommentorDto().getProfileImageURL());
			 profilePix.setWidth(String.valueOf(this.commentorProfilePixSize));
			 commentorProfilePixPanel.add(profilePix);
			 return commentorProfilePixPanel;
			 
		}
		
		private VerticalPanel createCommentsTextPanel(CommentDto commentDto){
			VerticalPanel commentsPanel = new VerticalPanel();
			
			Label name = new Label(commentDto.getCommentorDto().getName());
			name.setWordWrap(true);
			name.setWidth(String.valueOf(this.commentsWidth));
			
			Label commentsText = new Label();
			commentsText.setText(commentDto.getText());
			commentsText.setWidth(String.valueOf(commentsWidth));
			commentsText.setWordWrap(true);
			
			
			DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy.MM.dd 'at' HH:mm:ss");
			Label dateCommented = new Label(dateTimeFormat.format(commentDto.getDate()));
			dateCommented.setWidth(String.valueOf(commentsWidth));
			dateCommented.setWordWrap(true);
			
			
			commentsPanel.add(name);
			commentsPanel.add(dateCommented);
			commentsPanel.add(commentsText);
			
			return commentsPanel;
		}
		
		**/
		
		
		
		
	}
	
}





class FacebookUser extends JavaScriptObject{
	protected FacebookUser(){}
	public static final native FacebookUser fromJson(String jsonString)/*-{
		return eval('('+jsonString+')');
	}-*/;
	public final native String getKey()/*-{return this.facebookUserDto.key;}-*/;
	//public final native String getfacebookId()/*-{return this.facebookUserDto.facebookId;}-*/;
	public final native String getTwitterUser()/*-{return this.facebookUserDto.twitterUser;}-*/;
	public final native JsArray<Status> getStatuses()/*-{return this.facebookUserDto.statuses;}-*/;
	public final native String getTwitterOauthLink()/*-{return this.facebookUserDto.twitterLoginURL;}-*/;
	public final  FacebookUserInfo getFacebookUserInfo(){
		return FacebookUserInfo.fromJson(this.getFacebookUserInfoString());
	}
	private final native String getFacebookUserInfoString()/*-{return this.facebookUserDto.facebookUserInfo;}-*/;
}

class Status extends JavaScriptObject{
	protected Status(){}
	public final native Date getCreatedAt()/*-{ return @com.appspot.baotwits.client.facebook.Status::toDate(D)(this.createdAt);}-*/;
	public final native String getId()/*-{ return this.id+"";}-*/;
	public final native String getImageProfileURL()/*-{ return this.imageProfileURL;}-*/;
	public final native String getInReplyToScreenName()/*-{return this.inReplyToScreenName;}-*/;
	public final native String getInReplyToStatusId()/*-{return this.inReplyToStatusId+"";}-*/;
	public final native String getInReplyToUserId()/*-{return this.inReplyToUserId+"";}-*/;
	public final native String getScreenName()/*-{return this.screenName;}-*/;
	public final native String getSource()/*-{return this.source;}-*/;
	public final native String getText()/*-{return this.text;}-*/;
	public final native String getUserId()/*-{return this.userId;}-*/;
	public final native boolean isFavorited()/*-{return this.isFavorited;}-*/;
	public final native boolean isTruncated()/*-{return this.isTruncated;}-*/;
	public static Date toDate(double millis){
		return new Date((long)millis);
	}
}

class FacebookUserInfo extends JavaScriptObject{
	protected FacebookUserInfo(){}
	public static final native FacebookUserInfo fromJson(String jsonString)/*-{
		return eval(jsonString);
	}-*/;
	public final native String getUid()/*-{return this.uid;}-*/;
	public final native String getName()/*-{return this.name;}-*/;
	public final native String getPicSquare()/*-{return this.pic_square;}-*/;
	private final native JsArray<FacebookUserInfo> getFriends()/*-{alert(this.friends);return this.friends;}-*/;
}


