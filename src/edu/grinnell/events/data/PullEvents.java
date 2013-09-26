package edu.grinnell.events.data;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.os.AsyncTask;
import android.util.Log;
import edu.grinnell.events.data.EventContent.Event;

public class PullEvents {	
	
	public static List<Event> EventList;
	
	public void pullFeed(String feed) throws MalformedURLException {
		URL feedURL = new URL(feed);
		new downloadFeed().execute(feedURL);
		

	}

	private class downloadFeed extends AsyncTask<URL, Integer, List<Event>> {

		protected List<Event> doInBackground(URL... urls) {

			try {
				
				URLConnection urlConnection = urls[0].openConnection();
				InputStream in = new BufferedInputStream(
						urlConnection.getInputStream());
				

				EventParser parser = new EventParser();
				List<Event> events;
				
				events = new ArrayList<Event>(parser.parse(in));
				
				PullEvents.EventList = new ArrayList<Event>(events);

				return events;

			} catch (XmlPullParserException e) {
				e.printStackTrace();
				return null;

			} catch (IOException e) {
				e.printStackTrace();
				return null;

			}
		}
	}

}
