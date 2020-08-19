package com.uhrenclan.commsyapi.room;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.uhrenclan.Https.HttpsRequest;
import com.uhrenclan.Https.Options;
import com.uhrenclan.commsyapi.CommsyClient;
import com.uhrenclan.commsyapi.feed.*;

public class Room {
	public String ID, SEARCH_TOKEN;
	public String PATH = "/room";
	public CommsyClient client;
	
	protected Feed feed;
	
	public Room(String _id, CommsyClient _client) {
		ID = _id;
		client = _client;
	}
	
	public Feed feed() {
		if(feed==null) setup();
		return feed;
	}
	
	public Feed search(SearchOptions options) {
		if(SEARCH_TOKEN==null) setup();
		return new Feed(this, options);
	}
	
	public void setup(String html) {
		Matcher searchToken = Pattern.compile("<input.*?name=\"search\\[_token\\]\".*?value=\"(.*?)\"").matcher(html);
        SEARCH_TOKEN = searchToken.group(1);
        feed = new Feed(this, new SearchOptions());
	}
	
	public void setup() {
		HttpsRequest request = new HttpsRequest(new Options() {{
			path=PATH+"/"+ID;
		}});
		setup(client.request(request).content);
	}
}
