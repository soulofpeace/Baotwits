package com.appspot.baotwits.client.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.appspot.baotwits.client.data.dto.StatusDto;
import com.appspot.baotwits.client.data.dto.comment.CommentDto;
import com.appspot.baotwits.client.data.dto.comment.CommentorDto;
import com.appspot.baotwits.client.proxy.TwitTwitService;
import com.appspot.baotwits.client.proxy.TwitTwitServiceAsync;
import com.appspot.baotwits.client.proxy.comment.CommentLoginService;
import com.appspot.baotwits.client.proxy.comment.CommentLoginServiceAsync;
import com.appspot.baotwits.client.proxy.comment.CommentPostingService;
import com.appspot.baotwits.client.proxy.comment.CommentPostingServiceAsync;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class User implements EntryPoint {
	
	private FlexTable statusesTable = new FlexTable();
	private VerticalPanel mainPanel = new VerticalPanel();
	private ArrayList<Long> statuses = new ArrayList<Long>();
	
	private static HashMap<Long, CommentDropDown> statusCommentHash = new HashMap<Long, CommentDropDown>();
	
	//profile pix dimension
	private double imageProfileSize;
	//tweets width
	private double tweetsWidth;
	private static Label debugLabel = new Label();
	
	private CommentorDto loginCommentor;

	public double getImageProfileSize(){
		return this.imageProfileSize;
	}
	
	public double getTweetsWidth(){
		return this.tweetsWidth;
	}
	
	public void onModuleLoad() {
		// TODO Auto-generated method stub
		this.defineUpdateLogin();
		this.setDimension();
		
		if(isDebug()){
			debugLabel.setWidth(String.valueOf(imageProfileSize+tweetsWidth));
			mainPanel.add(debugLabel);
		}
		this.initialiseStatuses();
		RootPanel.get("displayTwits").add(mainPanel);	
		
	}
	
	
	
	public static void updateLogin(){
		User.debug("in Update login method");
		CommentLoginServiceAsync  commentLoginService = GWT.create(CommentLoginService.class);
		commentLoginService.getUserLogin(new AsyncCallback<CommentorDto>(){

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
			}

			public void onSuccess(final CommentorDto commentor){
				// TODO Auto-generated method stub
				if (commentor==null){
					User.debug("Commentor is null");
					return;
				}
				User.debug("Commentor is "+commentor.getName());
				Collection<CommentDropDown> c = statusCommentHash.values();
				Iterator<CommentDropDown> iter = c.iterator();
				while(iter.hasNext()){
					CommentDropDown commentDropDown = iter.next();
					commentDropDown.createCommentorProfilePanel(commentor);
				}
									
			}
			
		});
	}
	
	
	public void setLoginCommentor(CommentorDto loginCommentor) {
		this.loginCommentor = loginCommentor;
	}

	public CommentorDto getLoginCommentor() {
		return loginCommentor;
	}

	
	private void initialiseStatuses(){
		User.debug("In Initialise Status");
		CommentLoginServiceAsync  commentLoginService = GWT.create(CommentLoginService.class);
		commentLoginService.getUserLogin(new AsyncCallback<CommentorDto>(){

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				String userId = getById("userId");
				loadOwnStatuses(userId);
			}

			public void onSuccess(final CommentorDto commentor){
				// TODO Auto-generated method stub
				if (commentor==null){
					User.debug("Commentor is null");
				}
				else{
					User.debug("COmmentor is "+commentor.getName());
					loginCommentor = commentor;
				}
				String userId = getById("userId");
				loadOwnStatuses(userId);
			}
		});
		
	}

	private void setDimension(){
		//leaving width for scrollbar
		int width = Window.getClientWidth()-40;
		this.debug("width is "+width);
		this.imageProfileSize = ((width/4.0))<48?width/4.0:48;
		this.tweetsWidth=width-this.imageProfileSize;
	}
	
	private native void defineUpdateLogin() /*-{
		$wnd.updateLogin = @com.appspot.baotwits.client.user.User::updateLogin();
	}-*/;
	
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
	
	private void loadOwnStatuses(String userId){
			User.debug("In load own status");
			TwitTwitServiceAsync twittwitService = GWT.create(TwitTwitService.class);
			twittwitService.getOwnStatuses(userId, new AsyncCallback<ArrayList<StatusDto>>() {
				public void onSuccess(ArrayList<StatusDto> result) {
					// TODO Auto-generated method stub
					if (result!=null){
						int count=0;
						for (StatusDto status: result){
							if(!statuses.contains(status.getId())){
								VerticalPanel vp1=createScreenNamePanel(status);
								VerticalPanel vp2=createTweets(status);
								statusesTable.insertRow(count);
								statusesTable.setWidget(count, 0, vp1);
								statusesTable.setWidget(count, 1, vp2);
								statusesTable.getRowFormatter().addStyleName(count, "status");
								statusesTable.getCellFormatter().setWidth(count, 0, String.valueOf(imageProfileSize));
								statusesTable.getCellFormatter().setWidth(count, 1, String.valueOf(tweetsWidth));
								count++;
								CommentDropDown commentDropDown = new CommentDropDown(String.valueOf(status.getId()));
								if(loginCommentor!=null){
									commentDropDown.createCommentorProfilePanel(loginCommentor);
								}
								statusCommentHash.put(status.getId(), commentDropDown);
								statusesTable.setWidget(count, 1, commentDropDown );
								statusesTable.getFlexCellFormatter().setColSpan(count, 1, 2);
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
			statusesTable.setStyleName("tweets");
			mainPanel.add(statusesTable);
	 }
		
	 
	 
	 private VerticalPanel createScreenNamePanel(StatusDto status){
			VerticalPanel vp1 = new VerticalPanel();
			Image profileImage = new Image();
			profileImage.setUrl(status.getImageProfileURL());
			profileImage.setHeight(String.valueOf(this.imageProfileSize));
			profileImage.setWidth(String.valueOf(this.imageProfileSize));
			vp1.add(profileImage);
			return vp1;
		}
		
		private VerticalPanel createTweets(StatusDto status){
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
		
		
		
	



		private class CommentDropDown extends VerticalPanel{
			
			private final DisclosurePanel commentDropDown = new DisclosurePanel("Click to Comment");
			private final VerticalPanel contentPanel = new VerticalPanel();
			private final FlexTable commentTable = new FlexTable();
			private final TextArea commentBox = new TextArea();
			private final HorizontalPanel commentorProfilePanel = new HorizontalPanel();
			private final HorizontalPanel signInPanel = new HorizontalPanel();
			
			private final ArrayList<String> comments = new ArrayList<String>(); 
			
			//dimensioning variable
			private final int noTypeLogOn=2;
			private double logonWidth;
			private double commentorProfilePixSize;
			private double commentsWidth;
			private double width;
			
			private String statusId;
			
			public CommentDropDown(String statusId){
				this.setDimension();
				
				this.commentDropDown.setWidth(String.valueOf(tweetsWidth));
				
				this.statusId = statusId;
				
				this.update();
				this.contentPanel.add(this.getCommentTable());
					
				this.contentPanel.add(this.getCommentorProfilePanel());
				
				this.createCommentBox();
				this.contentPanel.add(commentBox);
				
				this.createSignInPanel();
				this.contentPanel.add(this.getSignInPanel());
				
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
			
			public HorizontalPanel getSignInPanel() {
				return signInPanel;
			}

			public String getStatusId(){
				return this.statusId;
			}
			
			public HorizontalPanel getCommentorProfilePanel() {
				return commentorProfilePanel;
			}		

			public void setLogonWidth(double logonWidth) {
				this.logonWidth = logonWidth;
			}

			public double getLogonWidth() {
				return logonWidth;
			}
			
			public double getCommentorProfilePixSize(){
				return this.commentorProfilePixSize;
			}
			
			public double getCommentsWidth(){
				return this.commentsWidth;
			}
			
			
			
			public void createCommentorProfilePanel(final CommentorDto commentor){
				this.commentorProfilePanel.setWidth(String.valueOf(tweetsWidth));
				
				VerticalPanel profilePixPanel = new VerticalPanel();
				profilePixPanel.setWidth(String.valueOf(this.commentorProfilePixSize));
				
				Label postAs = new Label(commentor.getName()+" Says");
				postAs.setWidth(String.valueOf(this.commentsWidth));
				postAs.setWordWrap(true);
				
				Image profilePix = new Image();
				profilePix.setUrl(commentor.getProfileImageURL());
				profilePix.setWidth(String.valueOf(this.commentorProfilePixSize));
				profilePix.setHeight(String.valueOf(this.commentorProfilePixSize));
				profilePixPanel.add(profilePix);
				
				
				Button postButton = new Button("Post!");
				postButton.addClickHandler(new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						// TODO Auto-generated method stub
						CommentPostingServiceAsync commentPostingService = GWT.create(CommentPostingService.class);
						CommentDto commentDto = new CommentDto();
						commentDto.setCommentorDto(commentor);
						Date date = new Date();
						commentDto.setDate(date);
						commentDto.setStatusId(getStatusId());
						commentDto.setText(commentBox.getText());
						commentPostingService.addComment(commentDto, new AsyncCallback<Boolean>() {
							
							@Override
							public void onSuccess(Boolean result) {
								// TODO Auto-generated method stub
								commentBox.setText("");
								update();
							}
							
							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
								
							}
						});
					
					}
				});
				
				commentorProfilePanel.insert(postAs, 0);
				commentorProfilePanel.insert(profilePixPanel, 0);
				commentorProfilePanel.addStyleName("postProfile");
				//commentDropDown.getLoginPanel().add(postAs);
				contentPanel.remove(signInPanel);
				//commentDropDown.getDropDown().insert(commentDropDown.getLoginPanel(), 1);
				contentPanel.add(postButton);
			}

			
			

			private void setDimension(){
				this.width = tweetsWidth;	
				
				this.commentorProfilePixSize=(width/4.0>48)?48:width/4.0;
				this.commentsWidth=this.width-commentorProfilePixSize;
				
				this.logonWidth=(this.width/this.noTypeLogOn>90)?90:this.width/this.noTypeLogOn;
			}
			
			private VerticalPanel createCommentorProfilePixPanel(CommentDto commentDto){
				 VerticalPanel commentorProfilePixPanel = new VerticalPanel();
				 Image profilePix = new Image(commentDto.getCommentorDto().getProfileImageURL());
				 profilePix.setWidth(String.valueOf(this.commentorProfilePixSize));
				 commentorProfilePixPanel.add(profilePix);
				 return commentorProfilePixPanel;
				 
			}
			
			private VerticalPanel createCommentsPanel(CommentDto commentDto){
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
			
						
			private void createCommentBox(){
				this.commentBox.setWidth(String.valueOf(tweetsWidth));
				this.getCommentBox().setText("");
				this.getCommentBox().setVisibleLines(3);
				
			}
			
			private void createSignInPanel(){
				//this.signInPanel.setWidth(String.valueOf(tweetsWidth));
				Image twitterSignInImage = new Image("/images/twitterSignIn.png");
				twitterSignInImage.setWidth(String.valueOf(this.logonWidth));
				PushButton twitterLoginButton = new PushButton(twitterSignInImage);
				twitterLoginButton.addClickHandler(new ClickHandler(){

					public void onClick(ClickEvent event) {
						// TODO Auto-generated method stub
						CommentLoginServiceAsync  commentLoginService = GWT.create(CommentLoginService.class);
						commentLoginService.twitterLogin(new AsyncCallback<String>(){

						
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
								
							}

							public void onSuccess(String result) {
								// TODO Auto-generated method stub
								Window.open(result, "Twitter Login", "menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes");
								
							}
							
						});
					}
					
				}
				
				);
				Image facebookSignInImage = new Image("/images/facebookSignIn.gif");
				facebookSignInImage.setWidth(String.valueOf(this.logonWidth));
				PushButton facebookLoginButton = new PushButton(facebookSignInImage);
				facebookLoginButton.addClickHandler(new ClickHandler(){

					@Override
					public void onClick(ClickEvent event) {
						// TODO Auto-generated method stub
						CommentLoginServiceAsync  commentLoginService = GWT.create(CommentLoginService.class);
						commentLoginService.facebookLogin(new AsyncCallback<String>(){

							@Override
							public void onFailure(Throwable caught) {
								// TODO Auto-generated method stub
								
							}

							@Override
							public void onSuccess(String result) {
								// TODO Auto-generated method stub
								Window.open(result, "Facebook Login", "menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes");
							}
						
						});
					}
				});
				this.getSignInPanel().add(twitterLoginButton);
				this.getSignInPanel().add(facebookLoginButton);
				
				
			}


			public void update(){
				CommentPostingServiceAsync commentPostingService =GWT.create(CommentPostingService.class);
				commentPostingService.getComments(statusId, new AsyncCallback<ArrayList<CommentDto>>(){

					@Override
					public void onFailure(Throwable caught) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onSuccess(ArrayList<CommentDto> result) {
						// TODO Auto-generated method stub
						int i=comments.size();
						if(result.size()==0){
							commentDropDown.getHeaderTextAccessor().setText("Be the first to comment!");
						}
						else{
							commentDropDown.getHeaderTextAccessor().setText(result.size()+" Comments!");
						}
						for (final CommentDto commentDto: result){
							if(!comments.contains(commentDto.getId())){
								HorizontalPanel commentContainerPanel = new HorizontalPanel();
								VerticalPanel commentorProfilePixPanel = createCommentorProfilePixPanel(commentDto);
								VerticalPanel commentsPanel = createCommentsPanel(commentDto);
								
								commentContainerPanel.add(commentorProfilePixPanel);
								commentContainerPanel.add(commentsPanel);
								
								getCommentTable().insertRow(i);
								getCommentTable().setWidget(i, 0, commentContainerPanel);
								getCommentTable().getCellFormatter().addStyleName(i, 0, "commentBox");
								getCommentTable().getCellFormatter().setWidth(i, 0, String.valueOf(tweetsWidth));
								comments.add(commentDto.getId());
								i++;
							}
							
							
						}
						
					}

					
				});
				
			}
			
			
		}

}



