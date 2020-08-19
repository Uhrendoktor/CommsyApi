package com.uhrenclan.commsyapi.feed;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.uhrenclan.Https.HttpsRequest;
import com.uhrenclan.Https.Options;
import com.uhrenclan.commsyapi.entity.Item;
import com.uhrenclan.commsyapi.room.Room;

public class Feed {
	
	public Room room;
	public SearchOptions options;
	
	public Feed(Room _room, SearchOptions _options) {
		room = _room;
		options = _options;
	}
	
	public String generatePath(SearchOptions _options) {
		return String.format("%s/%s/feed/%s/%s%s", room.PATH, room.ID, _options.lastIndex==null?"0":_options.lastIndex, _options.sort==null?"date":_options.sort, _options.lastId==null?"":"?lastId="+_options.lastId);
	}
	
	public Item[] getItems(final String _path) {
		HttpsRequest request = new HttpsRequest(new Options() {{
			path=_path;
		}});
		System.out.println(_path);
		if(options.search!=null) {
			request.options.method = "POST";
			request.options.params.put("search[phrase]", options.search);
			request.options.params.put("search[_token]", room.SEARCH_TOKEN);
		}
		Item[] items = parseFeed(room.client.request(request).content);
		options.lastId = items[items.length-1].ID;
		return items;
	}
	
	public Item[] getItems() {
		return getItems(null, null);
	}
	
	public Item[] getItems(final String _lastIndex, final String _lastId) {
		return getItems(generatePath(new SearchOptions() {{
			lastIndex = _lastIndex;
			lastId = _lastId;
			search = options.search;
			sort = options.sort;
		}}));
	}
	
	public Item[] nextItems() {
		return getItems(generatePath(options));
	}
	
	public Item[] parseFeed(String html) {
		List<Item> items = new ArrayList<Item>();
		final Matcher articles = Pattern.compile("<article.*?>[^`]*?</article>").matcher(html);
		while(articles.find()) {
			items.add(new Item(this.room) {{
				setup(articles.group(1));
			}});
		}
		return (Item[])items.toArray();
	}
}
