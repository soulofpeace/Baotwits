package com.appspot.baotwits.server.constants.comment;

public class CommentConstants {
	private static String commentTwitterConsumerKey="ZEF6WB3rAMnJmLshPiq3rw";
	private static String commentTwitterConsumerSecret="DcYvvzJcvsjJBlRiftLhgUcIfdlEJSkanazFIai0J8";
	private static String commentFacebookAPIKey="b090f11129b0d7dacc14d7e82f3b6618";
	private static String commentFacebookApplicationKey="82101ab62dc2b4dee0dcaa5db8a25bc9";
	private static String twitterType = "twitter";
	private static String facebookType= "facebook";
	
	public static void setCommentTwitterConsumerKey(
			String commentTwitterConsumerKey) {
		CommentConstants.commentTwitterConsumerKey = commentTwitterConsumerKey;
	}
	
	public static String getCommentTwitterConsumerKey() {
		return commentTwitterConsumerKey;
	}

	public static void setCommentTwitterConsumerSecret(
			String commentTwitterConsumerSecret) {
		CommentConstants.commentTwitterConsumerSecret = commentTwitterConsumerSecret;
	}

	public static String getCommentTwitterConsumerSecret() {
		return commentTwitterConsumerSecret;
	}

	public static void setCommentFacebookAPIKey(String commentFacebookAPIKey) {
		CommentConstants.commentFacebookAPIKey = commentFacebookAPIKey;
	}

	public static String getCommentFacebookAPIKey() {
		return commentFacebookAPIKey;
	}

	public static void setCommentFacebookApplicationKey(
			String commentFacebookApplicationKey) {
		CommentConstants.commentFacebookApplicationKey = commentFacebookApplicationKey;
	}

	public static String getCommentFacebookApplicationKey() {
		return commentFacebookApplicationKey;
	}

	public static void setTwitterType(String twitterType) {
		CommentConstants.twitterType = twitterType;
	}

	public static String getTwitterType() {
		return twitterType;
	}

	public static void setFacebookType(String facebookType) {
		CommentConstants.facebookType = facebookType;
	}

	public static String getFacebookType() {
		return facebookType;
	}
	
	
	

}
