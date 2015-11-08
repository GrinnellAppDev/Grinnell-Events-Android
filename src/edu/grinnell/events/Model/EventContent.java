package edu.grinnell.events.Model;

import java.util.Date;

public class EventContent {


	public static class Event {
		protected String id;
		protected String title;
		protected Date startTime;
		protected Date endTime;
		protected String location;
		protected String details;

		public Event(String id, String title, Date startTime, Date endTime, String location, String details) {
			this.id = id;
			this.title = title;
			this.startTime = startTime;
			this.endTime = endTime;
			this.location = location;
			this.details = details;
		}

		public String getID() {
			return id;
		}

		public String getTitle() {
			return title;
		}

		public Date getStartTime() {
			return startTime;
		}

		public Date getEndTime() {
			return endTime;
		}

		public String getLocation() {
			return location;
		}

		public String getDetails() {
			return details;
		}
	}
}
