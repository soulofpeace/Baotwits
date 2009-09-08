package com.appspot.baotwits.client;


import java.util.ArrayList;


import com.appspot.baotwits.client.data.dto.LoginInfo;
import com.appspot.baotwits.client.data.dto.Status;
import com.appspot.baotwits.client.proxy.AuthService;
import com.appspot.baotwits.client.proxy.AuthServiceAsync;
import com.appspot.baotwits.client.proxy.TwitTwitService;
import com.appspot.baotwits.client.proxy.TwitTwitServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Index implements EntryPoint {
	
	private VerticalPanel mainPanel = new VerticalPanel();
	private VerticalPanel loginPanel = new VerticalPanel();
	private Label welcomeMsg = new Label();
	private LoginInfo loginInfo= null;
	private Label loginLabel = new Label();
	private Anchor signInLink = new Anchor("Sign In");
	private FlexTable statusesTable = new FlexTable();
	private ArrayList<Long> statuses = new ArrayList<Long>();
	private VerticalPanel tweetPanel = new VerticalPanel();
	private TextArea tweetBox = new TextArea();
	private Button tweetButton = new Button();
	private Label charRemain = new Label();
	private Timer timer;

	public void onModuleLoad() {
		// TODO Auto-generated method stub
		AuthServiceAsync authService = GWT.create(AuthService.class);
		authService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>(){
			
			public void onSuccess(LoginInfo result) {
				// TODO Auto-generated method stub
				loginInfo = result;
				if (loginInfo.isLoggedIn()){
					if(loginInfo.isTwitLogin()){
						loadWelcomeMsg();
						loadTweetPanel();
						loadStatuses();
						timer = new Timer() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								loadStatuses();
							}
						};
						timer.scheduleRepeating(300000);
		
					}
					else{
						loadTwitterLogin();
					}
				}
				else{
					loadLogin();
				}
					
			}
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
		RootPanel.get("twits").add(mainPanel);
	}
	
	private void loadLogin(){
		signInLink.setHref(loginInfo.getLoginUrl());
		loginLabel.setText("Please sign in to your Google Account to access the BaoTwits application.");
		loginPanel.add(loginLabel);
		loginPanel.add(signInLink);
		RootPanel.get("twits").add(loginPanel);
	}
	
	private void loadTwitterLogin(){
		signInLink.setHref(loginInfo.getTwitAuthURL());
		loginLabel.setText("Please grant access to Baotwits Account");
		loginPanel.add(loginLabel);
		loginPanel.add(signInLink);
		RootPanel.get("twits").add(loginPanel);
	}
	
	private void loadWelcomeMsg(){
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		welcomeMsg.getElement().setInnerHTML("Welcome "+loginInfo.getNickname() +"<a href=\""+loginInfo.getLogoutUrl()+"\"> (Sign out)</a>,");
		//signOutLink.setHref(loginInfo.getLogoutUrl());
		horizontalPanel.add(welcomeMsg);
		//horizontalPanel.add(signOutLink);
		mainPanel.add(horizontalPanel);
	}
	
	private void loadStatuses(){
		TwitTwitServiceAsync twittwitService = GWT.create(TwitTwitService.class);
		twittwitService.getStatuses(GWT.getHostPageBaseURL(), new AsyncCallback<ArrayList<Status>>() {
			
			public void onSuccess(ArrayList<Status> result) {
				// TODO Auto-generated method stub
				if (result!=null){
					/**
					statuses.setText(0, 0, "Time");
					statuses.setText(0,  1, "From");
					statuses.setText(0, 2, "Tweets");
					**/
					int count=0;
					for (Status status: result){
						if(!statuses.contains(status.getId())){
							VerticalPanel vp1=createScreenNamePanel(status);
							VerticalPanel vp2=createTweets(status);
							if(count==0){
								
								statusesTable.insertRow(0);
								statusesTable.setWidget(0, 0, vp1);
								statusesTable.setWidget(0, 1, vp2);
							}
							else{
								statusesTable.insertRow(count);
								statusesTable.setWidget(count, 0, vp1);
								statusesTable.setWidget(count, 1, vp2);
								
							}
							count++;
							statuses.add(status.getId());
						}
						else{
							break;
						}
					}
					
				}
				
			}
			
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mainPanel.add(statusesTable);
	
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
				// TODO Auto-generated method stub
				TwitTwitServiceAsync twittwitService = GWT.create(TwitTwitService.class);
				String tweets = tweetBox.getText();
				twittwitService.updateStatus(tweets, new AsyncCallback<Void>() {
					
					public void onSuccess(Void result) {
						// TODO Auto-generated method stub
						loadStatuses();
						tweetBox.setText("");
						String text = tweetBox.getText();
						int remain = 140- text.length();
						charRemain.setText(remain+ " Chars");
					}
					
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}
				});
				
			}
		});
		
		tweetBox.addKeyUpHandler(new KeyUpHandler() {
			
		
			public void onKeyUp(KeyUpEvent event) {
				// TODO Auto-generated method stub
				String text = tweetBox.getText();
				int remain = 140- text.length();
				charRemain.setText(remain+ " Chars");
			}
		});
		
		
		auxPanel.add(tweetBox);
		auxPanel.add(tweetButton);
		this.tweetPanel.add(auxPanel);
		this.tweetPanel.add(charRemain);
		this.mainPanel.add(tweetPanel);
		
	}
	
	private VerticalPanel createScreenNamePanel(Status status){
		VerticalPanel vp1 = new VerticalPanel();
		Image profileImage = new Image();
		Label screenName = new Label();
		profileImage.setUrl(status.getImageProfileURL());
		screenName.setText(status.getScreenName());
		vp1.add(profileImage);
		vp1.add(screenName);
		return vp1;
	}
	
	private VerticalPanel createTweets(Status status){
		VerticalPanel vp2 = new VerticalPanel();
		DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy.MM.dd 'at' HH:mm:ss");
		Label createdAt= new Label(dateTimeFormat.format(status.getCreatedAt()));
		Label tweets = new Label();
		tweets.getElement().setInnerHTML(status.getText());
		vp2.add(createdAt);
		vp2.add(tweets);
		return vp2;
	}
	
	

}
