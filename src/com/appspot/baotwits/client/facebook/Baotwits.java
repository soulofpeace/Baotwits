package com.appspot.baotwits.client.facebook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.core.client.EntryPoint;
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
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.http.client.*;

public class Baotwits implements EntryPoint {
	
	private FacebookUser facebookUser;
	
	private static Label debugLabel = new Label();
	
	private FlexTable statusesTable = new FlexTable();
	private ArrayList<String> statuses = new ArrayList<String>();
	private VerticalPanel tweetPanel = new VerticalPanel();
	private TextArea tweetBox = new TextArea();
	private Button tweetButton = new Button();
	private Label charRemain = new Label();
	private Label welcomeMsg = new Label();
	private Timer timer;
	

	
	public void onModuleLoad(){
		Baotwits.debug("start");
		RootPanel.get().add(this.debugLabel);
		String facebookUserId = Window.Location.getParameter("fb_sig_user");
		this.setFacebookUser(facebookUserId);
		
		
	}
	
	public void setFacebookUser(String facebookUserId){
		String url="http://baotwits.appspot.com/facebook/user/"+facebookUserId+".json";
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
						if(facebookUser.getStatuses().length()==0){
							VerticalPanel setupPanel = new VerticalPanel();
							Label setupLabel = new Label("Setup your twitter acoount");
							Anchor twitterOauthLink = new Anchor("Click Here");
							twitterOauthLink.setHref(facebookUser.getTwitterOauthLink());
							setupPanel.add(setupLabel);
							setupPanel.add(twitterOauthLink);
							RootPanel.get().add(setupPanel);
						}
						else{
							loadWelcomeMsg();
							loadTweetPanel();
							loadStatuses();
						}
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
	

	private void loadWelcomeMsg(){
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		welcomeMsg.getElement().setInnerHTML("Welcome "+facebookUser.getfacebookId());
		//signOutLink.setHref(loginInfo.getLogoutUrl());
		horizontalPanel.add(welcomeMsg);
		//horizontalPanel.add(signOutLink);
		RootPanel.get().add(horizontalPanel);
	}
	
	private void loadStatuses(){
					int count=0;
					JsArray<Status> result = this.facebookUser.getStatuses();
					for (int i =0; i<result.length();i++){
						Status status = result.get(i);
						if(!statuses.contains(status.getId())){
							VerticalPanel vp1=createScreenNamePanel(status);
							VerticalPanel vp2=createTweets(status);
							statusesTable.insertRow(count);
							statusesTable.setWidget(count, 0, vp1);
							statusesTable.setWidget(count, 1, vp2);
							statusesTable.getRowFormatter().addStyleName(count, "status");
							statusesTable.getCellFormatter().setWidth(count, 0, "84");
							statusesTable.getCellFormatter().setHeight(count, 0, "64");
							statusesTable.getCellFormatter().setWidth(count, 1, "864");
							statusesTable.getCellFormatter().setHeight (count, 1, "64");
							
							count++;
							statuses.add(status.getId());
						}
						else{
							break;
						}
					}
					
				
		statusesTable.setStyleName("tweets");
		RootPanel.get().add(statusesTable);
	
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
		RootPanel.get().add(tweetPanel);
		
	}
	
	private VerticalPanel createScreenNamePanel(Status status){
		VerticalPanel vp1 = new VerticalPanel();
		Image profileImage = new Image();
		Label screenName = new Label();
		profileImage.setUrl(status.getImageProfileURL());
		profileImage.setHeight("48");
		profileImage.setWidth("48");
		screenName.setText(status.getScreenName());
		screenName.addStyleName("screenName");
		vp1.add(profileImage);
		vp1.add(screenName);
		return vp1;
	}
	
	private VerticalPanel createTweets(Status status){
		VerticalPanel vp2 = new VerticalPanel();
		DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy.MM.dd 'at' HH:mm:ss");
		Label createdAt= new Label(dateTimeFormat.format(status.getCreatedAt()));
		createdAt.addStyleName("date");
		Label tweets = new Label();
		tweets.getElement().setInnerHTML(status.getText());
		tweets.addStyleName("tweets");
		vp2.add(createdAt);
		vp2.add(tweets);
		return vp2;
	}
	
	private static void debug(String text){
		String original=debugLabel.getText();
		original+=text;
		debugLabel.setText(original);
	}
	
}





class FacebookUser extends JavaScriptObject{
	protected FacebookUser(){}
	public static final native FacebookUser fromJson(String jsonString)/*-{
		alert(jsonString);
		return eval('('+jsonString+')');
	}-*/;
	public final native String getKey()/*-{ alert(this);return this.facebookUserDto.key;}-*/;
	public final native String getfacebookId()/*-{alert(this);return this.facebookUserDto.facebookId;}-*/;
	public final native String getTwitterUser()/*-{alert(this);return this.facebookUserDto.twitterUser;}-*/;
	public final native JsArray<Status> getStatuses()/*-{alert("hello"+this.facebookUserDto.statuses);return this.facebookUserDto.statuses;}-*/;
	public final native String getTwitterOauthLink()/*-{alert(this);return this.facebookUserDto.twitterLoginURL;}-*/;
}

class Status extends JavaScriptObject{
	protected Status(){}
	public final native Date getCreatedAt()/*-{ alert(this);return this.createdAt;}-*/;
	public final native String getId()/*-{ alert(this);return this.id+"";}-*/;
	public final native String getImageProfileURL()/*-{ alert(this);return this.imageProfileURL;}-*/;
	public final native String getInReplyToScreenName()/*-{ alert(this);return this.inReplyToScreenName;}-*/;
	public final native String getInReplyToStatusId()/*-{ alert(this);return this.inReplyToStatusId+"";}-*/;
	public final native String getInReplyToUserId()/*-{ alert(this);return this.inReplyToUserId+"";}-*/;
	public final native String getScreenName()/*-{ alert(this);return this.screenName;}-*/;
	public final native String getSource()/*-{ alert(this);return this.source;}-*/;
	public final native String getText()/*-{ alert(this);return this.text;}-*/;
	public final native String getUserId()/*-{ alert(this);return this.userId;}-*/;
	public final native boolean isFavorited()/*-{ alert(this);return this.isFavorited;}-*/;
	public final native boolean isTruncated()/*-{ alert(this);return this.isTruncated;}-*/;
}


