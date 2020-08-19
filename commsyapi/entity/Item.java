package com.uhrenclan.commsyapi.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.uhrenclan.commsyapi.room.Room;

public class Item {
	
	public String ID;
	private String title, date, type, author, time_date, time_from, time_to;
	public Room room;
	
	public String PATH = "/material";
	
	public Item(Room _room) {
		room = _room;
	}
	
	public Item(Room _room, String id) {
		room = _room;
		ID = id;
	}
	
	public void setup(String html) {
		Matcher _id = Pattern.compile("<article.*?data-item-id=\"(.*?)\">").matcher(html);
		if(_id.find()) ID = _id.group(1);
		
		Matcher _title = Pattern.compile("<h4 class=\"uk-comment-title\">[^`]*?>[^`]*?([^ \\n\\r][^`]*?)[ \\n\\r]+<[^`]*?<\\/h4>").matcher(html);
		if(_title.find()) title = _title.group(1);
		
		Matcher _date = Pattern.compile("<span.*?class=\"uk-text-nowrap\">\\s+([^`]*?)\\s+<\\/span>").matcher(html);
		if(_date.find()) date = _date.group(1);
		
		Matcher _type = Pattern.compile("<i.*?class=\".*?uk-icon-([a-zA-Z]+)[^`]*?uk-icon-small\">").matcher(html);
		if(_type.find()) type = _type.group(1);
		
		switch(type) {
		case "calendar":
			Matcher _meta = Pattern.compile("<div class=\"uk-comment-meta\">[^`]*?([0-9]*?\\.[0-9]*?\\.[0-9]+)[^`]*?([0-9]*?\\:[0-9]+)[^`]*?([0-9]*?\\:[0-9]+)[^`]*?<\\/div>").matcher(html);
			if(_meta.find()) {
				time_date = _meta.group(1);
				time_from = _meta.group(2);
				time_to = _meta.group(3);
			}
			break;
		default:
			_meta = Pattern.compile("<div class=\"uk-comment-meta\">[^`]?Von: (?:<a.*?>|) *?([a-z,A-Z]+) ([a-z,A-Z]+)[^`]*?(?:\\(.*?\\)|)[^`]*?<\\/div>").matcher(html);
			if(_meta.find()) author = _meta.group(1)+" "+_meta.group(2);
			break;
		}
		
		//System.out.println(this);
	}
	
	public void setup() {
		if(ID==null) return;
	}
	
	public String toString() {
		return String.format("Title: %s\nAuthor: %s\nDate: %s\nType: %s\n", title, author, date, type);
	}
	
	public String title() 		{ if(title		== null)setup(); return title;		}
	public String date() 		{ if(date		== null)setup(); return date;		}
	public String type() 		{ if(type		== null)setup(); return type;		}
	public String author() 		{ if(author		== null)setup(); return author;		}
	public String time_date() 	{ if(time_date	== null)setup(); return time_date;	}
	public String time_from() 	{ if(time_from	== null)setup(); return time_from;	}
	public String time_to() 	{ if(time_to	== null)setup(); return time_to;	}
}
