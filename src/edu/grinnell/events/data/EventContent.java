package edu.grinnell.events.data;

import java.util.ArrayList;
import java.util.List;

public class EventContent {

	public static List<Event> EventList = new ArrayList<Event>();

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
