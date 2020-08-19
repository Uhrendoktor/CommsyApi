package com.uhrenclan.commsyapi.room;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.uhrenclan.Https.HttpsRequest;
import com.uhrenclan.Https.Options;
import com.uhrenclan.commsyapi.CommsyClient;
import com.uhrenclan.commsyapi.feed.DashboardFeed;
import com.uhrenclan.commsyapi.feed.Feed;
import com.uhrenclan.commsyapi.feed.SearchOptions;

public class DashboardRoom extends Room{
	
	public DashboardRoom(CommsyClient _client) {
		super(_client.UID, _client);	
		super.PATH = "/dashboard";
	}

	public Feed search(SearchOptions options) {
		if(SEARCH_TOKEN==null) setup();
		return new DashboardFeed(this, options);
	}
	
	public void setup(String html) {
		Matcher searchToken = Pattern.compile("<input.*?name=\"search\\[_token\\]\".*?value=\"(.*?)\"").matcher(html);
		searchToken.find();
        SEARCH_TOKEN = searchToken.group(1);
        feed = new DashboardFeed(this, new SearchOptions());
	}
}
