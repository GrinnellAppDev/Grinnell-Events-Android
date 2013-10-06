package edu.grinnell.events.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;
import edu.grinnell.events.data.EventContent.Event;

//Note: Much of the code for this program was copied directly from 
//http://developer.android.com/training/basics/network-ops/xml.html
public class EventParser {
	// We don't use namespaces
	private static final String ns = null;

	public List<Event> parse(InputStream in) throws XmlPullParserException,
			IOException {
		
		Log.i("Pull", "parse begun");

		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);
			parser.nextTag();
			return readFeed(parser);
		} finally {
			in.close();
		}
	}

	private List<Event> readFeed(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		
		List<Event> events = new ArrayList<Event>();

		parser.require(XmlPullParser.START_TAG, ns, "rss");
		parser.nextTag();
		parser.require(XmlPullParser.START_TAG, ns, "channel");

		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			// Starts by looking for the entry tag
			if (name.equals("item")) {
				events.add(readEntry(parser));
			} else {
				skip(parser);
			}
		}
		return events;
	}

	public static class Entry {
		public final String title;
		public final String link;
		public final String summary;

		private Entry(String title, String summary, String link) {
			this.title = title;
			this.summary = summary;
			this.link = link;
		}
	}

	// Parses the contents of an entry. If it encounters a title, summary, or
	// link tag, hands them off
	// to their respective "read" methods for processing. Otherwise, skips the
	// tag.
	private Event readEntry(XmlPullParser parser)
			throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "item");

		String title = null;
		String summary = null;
		String link = null;
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equals("title")) {
				title = readTitle(parser);
			} else if (name.equals("description")) {
				summary = readDescription(parser);
			} else if (name.equals("link")) {
				link = readLink(parser);
			} else {
				skip(parser);
			}
		}
		
		String id = null;
		String date = null;
		String location = null;
		String details = null;
		
		id = link.substring(link.lastIndexOf("=")+1);
		String[] parts = summary.split(" -- ");
		if (!summary.startsWith("-")) {
			details = parts[0];
			date = parts[1];
			if (parts.length == 3) {
				location = parts[2];
			}
		} else {
			date = parts[0];
			if (parts.length == 2) {
				location = parts[1];
			}
		}
		
		Log.i("parser", title);
		//return new EventContent.Event(id, title, date, location, details);
		return new EventContent.Event(id, title, new Date(2013, 10, 1), new Date(2013, 10, 1),  location, details);

	}

	// Processes title tags in the feed.
	private String readTitle(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "title");
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "title");
		return title;
	}

	// Processes link tags in the feed.
	private String readLink(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "link");
		String link = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "link");
		return link;
	}

	// Processes summary tags in the feed.
	private String readDescription(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		parser.require(XmlPullParser.START_TAG, ns, "description");
		String summary = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "description");
		return summary;
	}

	// For the tags title and summary, extracts their text values.
	// issue: does not handle special characters like ' correctly
	private String readText(XmlPullParser parser) throws IOException,
			XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}

	// when there is a tag we don't care about, skip it
	private void skip(XmlPullParser parser) throws XmlPullParserException,
			IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
			case XmlPullParser.END_TAG:
				depth--;
				break;
			case XmlPullParser.START_TAG:
				depth++;
				break;
			}
		}
	}

}
