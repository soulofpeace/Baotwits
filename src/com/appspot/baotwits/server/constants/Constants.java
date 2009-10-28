package com.appspot.baotwits.server.constants;

public class Constants {
	private static String consumerKey="2plHs9yMlDbSxhG3vGBJg";
	
	private static String consumerSecret="VJy789ALmQ99VYFc5ASRquH7VpYxoq3QTXwsaB8tUU";
	

	public static void setConsumerKey(String consumerKey) {
		Constants.consumerKey = consumerKey;
	}

	public static String getConsumerKey() {
		return consumerKey;
	}

	public static void setConsumerSecret(String consumerSecret) {
		Constants.consumerSecret = consumerSecret;
	}

	public static String getConsumerSecret() {
		return consumerSecret;
	}

}
