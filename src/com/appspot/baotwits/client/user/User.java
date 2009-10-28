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
	private static HashMap<Long, CommentDropDown> statusCommentHash = new HashMap();
	private String commentorWrapperId;
	private double imageProfileWidth;
	private double leftColumnWidth;
	private double rightColumnWidth;
	private Label debugLabel = new Label();

	public void onModuleLoad() {
		// TODO Auto-generated method stub
		this.defineUpdateLogin();
		this.dimensionElements();
		String userId = this.getById("userId");
		this.loadOwnStatuses(userId);
		Label height = new Label("Client Height is "+Window.getClientHeight());
		Label width = new Label("Client width is "+(Window.getClientWidth()/4.0));
		mainPanel.add(height);
		mainPanel.add(width);
		mainPanel.add(debugLabel);
		RootPanel.get("displayTwits").add(mainPanel);
		
		
		updateLogin();
	}
	
	public double getLeftColumnWidth(){
		return this.leftColumnWidth;
	}
	
	public double getRightColumnWidth(){
		return this.rightColumnWidth;
	}
	
	private void dimensionElements(){
		int height = Window.getClientHeight();
		int width = Window.getClientWidth()-40;
		this.leftColumnWidth=(width/4.0);
		String text=this.debugLabel.getText();
		text+="Left column width: "+leftColumnWidth+"\n";
		this.imageProfileWidth = this.leftColumnWidth<48?this.leftColumnWidth:48.0;
		this.rightColumnWidth=width/4.0 * 3;
		text+="right column width: "+rightColumnWidth+"\n";
		this.debugLabel.setText(text);
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
	 
	 
	private void loadOwnStatuses(String userId){
			TwitTwitServiceAsync twittwitService = GWT.create(TwitTwitService.class);
			twittwitService.getOwnStatuses(userId, new AsyncCallback<ArrayList<StatusDto>>() {
				public void onSuccess(ArrayList<StatusDto> result) {
					// TODO Auto-generated method stub
					if (result!=null){
						/**
						statuses.setText(0, 0, "Time");
						statuses.setText(0,  1, "From");
						statuses.setText(0, 2, "Tweets");
						**/
						int count=0;
						for (StatusDto status: result){
							if(!statuses.contains(status.getId())){
								VerticalPanel vp1=createScreenNamePanel(status);
								VerticalPanel vp2=createTweets(status);
								statusesTable.insertRow(count);
								statusesTable.setWidget(count, 0, vp1);
								statusesTable.setWidget(count, 1, vp2);
								statusesTable.getRowFormatter().addStyleName(count, "status");
								//statusesTable.getCellFormatter().setWidth(count, 0, "84");
								//statusesTable.getCellFormatter().setHeight(count, 0, "64");
								//statusesTable.getCellFormatter().setWidth(count, 1, "864");
								//statusesTable.getCellFormatter().setHeight (count, 1, "64");
								statusesTable.getCellFormatter().setWidth(count, 0, String.valueOf(leftColumnWidth));
								statusesTable.getCellFormatter().setHeight(count, 0, "64");
								statusesTable.getCellFormatter().setWidth(count, 1, String.valueOf(rightColumnWidth));
								statusesTable.getCellFormatter().setHeight(count, 1, "64");
								count++;
								CommentDropDown commentDropDown = new CommentDropDown(String.valueOf(status.getId()));
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
			profileImage.setHeight(String.valueOf(this.imageProfileWidth));
			profileImage.setWidth(String.valueOf(this.imageProfileWidth));
			vp1.add(profileImage);
			return vp1;
		}
		
		private VerticalPanel createTweets(StatusDto status){
			VerticalPanel vp2 = new VerticalPanel();
			Label screenName = new Label();
			screenName.setText(status.getScreenName());
			screenName.addStyleName("screenName");
			screenName.setWidth(String.valueOf(leftColumnWidth));
			screenName.setWordWrap(true);
			DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy.MM.dd 'at' HH:mm:ss");
			Label createdAt= new Label(dateTimeFormat.format(status.getCreatedAt()));
			createdAt.setWidth(String.valueOf(this.rightColumnWidth));
			createdAt.setWordWrap(true);
			createdAt.addStyleName("date");
			Label tweets = new Label();
			tweets.getElement().setInnerHTML(status.getText());
			tweets.addStyleName("tweets");
			tweets.setWidth(String.valueOf(this.rightColumnWidth));
			tweets.setWordWrap(true);
			vp2.add(screenName);
			vp2.add(createdAt);
			vp2.add(tweets);
			return vp2;
		}
		
		
		public static void updateLogin(){
			CommentLoginServiceAsync  commentLoginService = GWT.create(CommentLoginService.class);
			commentLoginService.getUserLogin(new AsyncCallback<CommentorDto>(){

				@Override
				public void onFailure(Throwable caught) {
					// TODO Auto-generated method stub
				}

				public void onSuccess(final CommentorDto commentor){
					// TODO Auto-generated method stub
					if (commentor==null){
						return;
					}
					Collection<CommentDropDown> c = statusCommentHash.values();
					Iterator<CommentDropDown> iter = c.iterator();
					while(iter.hasNext()){
						final CommentDropDown commentDropDown = iter.next();
						VerticalPanel profilePanel = new VerticalPanel();
						profilePanel.setWidth(String.valueOf(commentDropDown.getPostingPanelLeftColumnWidth()));
						Label postAs = new Label("Says");
						postAs.setWidth(String.valueOf(commentDropDown.getPostingPanelLeftColumnWidth()));
						postAs.setWordWrap(true);
						Image profilePix = new Image();
						Label name = new Label(commentor.getName());
						name.setWordWrap(true);
						name.setWidth(String.valueOf(commentDropDown.getPostingPanelLeftColumnWidth()));
						profilePix.setUrl(commentor.getProfileImageURL());
						profilePix.setWidth(String.valueOf(commentDropDown.getPostingPanelImageProfileWidth()));
						profilePix.setHeight(String.valueOf(commentDropDown.getPostingPanelImageProfileWidth()));
						profilePanel.add(profilePix);
						profilePanel.add(name);
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
								commentDto.setStatusId(commentDropDown.getStatusId());
								commentDto.setText(commentDropDown.getCommentBox().getText());
								commentPostingService.addComment(commentDto, new AsyncCallback<Boolean>() {
									
									@Override
									public void onSuccess(Boolean result) {
										// TODO Auto-generated method stub
										commentDropDown.getCommentBox().setText("");
										commentDropDown.update();
									}
									
									@Override
									public void onFailure(Throwable caught) {
										// TODO Auto-generated method stub
										
									}
								});
								
							}
						});
						commentDropDown.getPostingArea().insert(postAs, 0);
						commentDropDown.getPostingArea().insert(profilePanel, 0);
						commentDropDown.getPostingArea().addStyleName("postProfile");
						//commentDropDown.getLoginPanel().add(postAs);
						commentDropDown.getDropDown().remove(commentDropDown.getSignInPanel());
						//commentDropDown.getDropDown().insert(commentDropDown.getLoginPanel(), 1);
						commentDropDown.getDropDown().add(postButton);
					}
					
				}
				
			});
		}
		
		private class CommentDropDown extends VerticalPanel{
			private final DisclosurePanel commentPanel = new DisclosurePanel("Click to Comment");
			private final VerticalPanel dropDown = new VerticalPanel();
			private final FlexTable commentTable = new FlexTable();
			private final ArrayList<String> comments = new ArrayList<String>(); 
			private final TextArea commentBox = new TextArea();
			private final HorizontalPanel postingArea = new HorizontalPanel();
			private final HorizontalPanel signInPanel = new HorizontalPanel();
			private final int noTypeLogOn=2;
			private double logonWidth;
			private double commentLeftColumnWidth;
			private double commentRightColumnWidth;
			private double postingPanelLeftColumnWidth;
			private double postingPanelImageProfileWidth;
			private double postingPanelMiddleColumnWidth;
			private double postingPanelRightColumnWidth;
			private double commentImageProfilePixWidth;
			private String statusId;
			
			public CommentDropDown(String statusId){
				this.setDimension();
				this.postingArea.setVerticalAlignment(ALIGN_MIDDLE);
				this.statusId = statusId;
				this.getDropDown().add(this.getCommentTable());
				//this.getCommentTable().addStyleName("comments");
				this.update();
				this.createCommentBox();
				this.getDropDown().add(this.getPostingArea());
				this.getDropDown().add(commentBox);
				this.createSignInPanel();
				this.getDropDown().add(this.getSignInPanel());
				this.getCommentPanel().setContent(this.getDropDown());
				this.add(this.commentPanel);
				
			}
			
			public double getCommentLeftColumnWidth(){
				return this.commentLeftColumnWidth;
			}
			
			public double getCommentRightColumnWidth(){
				return this.commentRightColumnWidth;
			}
			
			private void setDimension(){
				this.commentPanel.setWidth(String.valueOf(rightColumnWidth));
				this.commentBox.setWidth(String.valueOf(rightColumnWidth));
				this.signInPanel.setWidth(String.valueOf(rightColumnWidth));
				this.postingArea.setWidth(String.valueOf(rightColumnWidth));
				this.commentLeftColumnWidth=rightColumnWidth/4.0;
				this.commentImageProfilePixWidth=(commentLeftColumnWidth>48)?48:commentLeftColumnWidth;
				this.commentRightColumnWidth=rightColumnWidth/4.0*3;
				this.postingPanelLeftColumnWidth=rightColumnWidth/6.0;
				this.postingPanelMiddleColumnWidth=rightColumnWidth/6.0;
				this.postingPanelRightColumnWidth=rightColumnWidth/3.0*2;
				this.setPostingPanelImageProfileWidth((postingPanelLeftColumnWidth>48)?48:postingPanelLeftColumnWidth);
				
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
							commentPanel.getHeaderTextAccessor().setText("Be the first to comment!");
						}
						else{
							commentPanel.getHeaderTextAccessor().setText(result.size()+" Comments!");
						}
						for (final CommentDto commentDto: result){
							if(!comments.contains(commentDto.getId())){
								final HorizontalPanel horizontalPanel = new HorizontalPanel();
								final VerticalPanel namePanel = new VerticalPanel();
								final VerticalPanel textPanel = new VerticalPanel();
								final Label commentsText = new Label();
								commentsText.setWordWrap(true);
								Image profilePix = new Image(commentDto.getCommentorDto().getProfileImageURL());
								profilePix.setWidth(String.valueOf(commentImageProfilePixWidth));
								Label name = new Label(commentDto.getCommentorDto().getName());
								name.setWordWrap(true);
								name.setWidth(String.valueOf(commentLeftColumnWidth));
								namePanel.add(profilePix);
								DateTimeFormat dateTimeFormat = DateTimeFormat.getFormat("yyyy.MM.dd 'at' HH:mm:ss");
								final Label dateCommented = new Label(dateTimeFormat.format(commentDto.getDate()));
								dateCommented.setWidth(String.valueOf(commentRightColumnWidth));
								dateCommented.setWordWrap(true);
								commentsText.setText(commentDto.getText());
								commentsText.setWidth(String.valueOf(commentRightColumnWidth));
								commentsText.setWordWrap(true);
								textPanel.add(name);
								textPanel.add(dateCommented);
								textPanel.add(commentsText);
								
								horizontalPanel.add(namePanel);
								horizontalPanel.add(textPanel);
								
								getCommentTable().insertRow(i);
								getCommentTable().setWidget(i, 0, horizontalPanel);
								//getCommentTable().setWidget(i, 1, textPanel);
								getCommentTable().getCellFormatter().addStyleName(i, 0, "commentBox");
								getCommentTable().getCellFormatter().setWidth(i, 0, String.valueOf(rightColumnWidth));
								//getCommentTable().getCellFormatter().addStyleName(i, 1, "commentBox");
								//getCommentTable().getCellFormatter().setWidth(i, 1, "864");
								//getCommentTable().getCellFormatter().setHeight (i, 1, "64");
								comments.add(commentDto.getId());
								i++;
							}
							
							
						}
						
					}

					
				});
				
			}
			
			public DisclosurePanel getCommentPanel() {
				return commentPanel;
			}
			
			public VerticalPanel getDropDown() {
				return dropDown;
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
			
			private void createCommentBox(){
				this.getCommentBox().setText("");
				this.getCommentBox().setVisibleLines(3);
				
			}
			
			private void createSignInPanel(){
				PushButton twitterLoginButton = new PushButton(new Image("/images/twitterSignIn.png"));
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
				PushButton facebookLoginButton = new PushButton(new Image("/images/facebookSignIn.gif"));
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


			public HorizontalPanel getPostingArea() {
				return postingArea;
			}

			public void setPostingPanelLeftColumnWidth(
					int postingPanelLeftColumnWidth) {
				this.postingPanelLeftColumnWidth = postingPanelLeftColumnWidth;
			}

			public double getPostingPanelLeftColumnWidth() {
				return postingPanelLeftColumnWidth;
			}

			public void setPostingPanelMiddleColumnWidth(
					double postingPanelMiddleColumnWidth) {
				this.postingPanelMiddleColumnWidth = postingPanelMiddleColumnWidth;
			}

			public double getPostingPanelMiddleColumnWidth() {
				return postingPanelMiddleColumnWidth;
			}

			public void setPostingPanelRightColumnWidth(
					double postingPanelRightColumnWidth) {
				this.postingPanelRightColumnWidth = postingPanelRightColumnWidth;
			}

			public double getPostingPanelRightColumnWidth() {
				return postingPanelRightColumnWidth;
			}

			public void setLogonWidth(double logonWidth) {
				this.logonWidth = logonWidth;
			}

			public double getLogonWidth() {
				return logonWidth;
			}

			public void setPostingPanelImageProfileWidth(
					double postingPanelImageProfileWidth) {
				this.postingPanelImageProfileWidth = postingPanelImageProfileWidth;
			}
			
			public double getPostingPanelImageProfileWidth() {
				return postingPanelImageProfileWidth;
			}
			
			public double getCommentImageProfilePixWidth(){
				return this.commentImageProfilePixWidth;
			}
			
			public void  setCommentImageProfilePixWidth(double commentImageProfilePixWidth){
				this.commentImageProfilePixWidth=commentImageProfilePixWidth;
			}

			
		}

}



