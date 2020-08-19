package com.uhrenclan.commsyapi.feed;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.uhrenclan.commsyapi.entity.Item;
import com.uhrenclan.commsyapi.room.Room;

public class DashboardFeed extends Feed{

	public DashboardFeed(Room _room, SearchOptions _options) {
		super(_room, _options);
	}
	
	public Item[] parseFeed(String html) {
		List<Item> items = new ArrayList<Item>();
		final Matcher articles = Pattern.compile("<article.*?>[^`]*?</article>").matcher(html);
		while(articles.find()) {
			Matcher mroom = Pattern.compile("<a href=\"/room/([0-9]*?)\">").matcher(html);
			mroom.find();
			Room _room = new Room(mroom.group(1), room.client);
			items.add(new Item(_room) {{
				setup(articles.group(0));
			}});
		}
		Item[] ret = new Item[items.size()];
		return items.toArray(ret);
	}
}
