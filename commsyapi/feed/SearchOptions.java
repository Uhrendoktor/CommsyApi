package com.uhrenclan.commsyapi.feed;

public class SearchOptions {
	public String lastIndex, sort, lastId, search;
	public SearchOptions() {}
	public String toString() {
		return String.format("LastIndex: %s\nLastID: %s\nSort: %s\nSearch: %s\n", lastIndex, lastId, sort, search);
	}
}
