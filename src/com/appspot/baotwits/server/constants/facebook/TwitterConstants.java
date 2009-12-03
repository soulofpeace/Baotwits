package com.appspot.baotwits.server.constants.facebook;

public class TwitterConstants {
	private static String consumerKey="cmkh9lVV2f4rSH6qVLa5Mg";
	private static String consumerSecret="82pWkFVXYhx8P28OlvNno5wI9cBm8QkguENqXefdbBQ";
	public static void setConsumerKey(String consumerKey) {
		TwitterConstants.consumerKey = consumerKey;
	}
	public static String getConsumerKey() {
		return consumerKey;
	}
	public static void setConsumerSecret(String consumerSecret) {
		TwitterConstants.consumerSecret = consumerSecret;
	}
	public static String getConsumerSecret() {
		return consumerSecret;
	}

}
