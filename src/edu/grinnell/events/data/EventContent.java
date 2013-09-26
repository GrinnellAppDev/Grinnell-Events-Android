package edu.grinnell.events.data;

import java.util.ArrayList;
import java.util.List;

public class EventContent {

	public static List<Event> ITEMS = new ArrayList<Event>();

	static {
		// Add 3 sample items.
		addEvent(new Event("1", "Item 1", "1/1/13", "Noyce 3818", "chillin"));
		addEvent(new Event("2", "Item 2", "6/6/1666", "yo mamas house", "killin"));
		addEvent(new Event("3", "Item 3", "1/2/3456", "mordor", "makin a million"));
	}

	private static void addEvent(Event event) {
		ITEMS.add(event);
	}

	public static class Event {
		protected String id;
		protected String title;
		protected String date;
		protected String location;
		protected String details;

		public Event(String id, String title, String date, String location, String details) {
			this.id = id;
			this.title = title;
			this.date = date;
			this.location = location;
			this.details = details;
		}
		
		public String getID(){
			return id;
		}
		
		public String getTitle(){
			return title;
		}
		
		public String getDate(){
			return date;
		}
		
		public String getLocation(){
			return location;
		}
		
		public String getDetails(){
			return details;
		}
	}
}
