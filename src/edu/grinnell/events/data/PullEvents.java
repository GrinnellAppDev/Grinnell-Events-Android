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
	
	public void pullFeed(String feed) throws MalformedURLException {
		URL feedURL = new URL(feed);
		new downloadFeed().execute(feedURL);
		

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private class downloadFeed extends AsyncTask<URL, Integer, List<Event>> {

		protected List<Event> doInBackground(URL... urls) {

			try {
				URLConnection urlConnection = urls[0].openConnection();
				InputStream in = new BufferedInputStream(
						urlConnection.getInputStream());
				EventParser parser = new EventParser();
				List<Event> events;

				events = parser.parse(in);
				
				return events;

			} catch (XmlPullParserException e) {
				e.printStackTrace();
				return null;

			} catch (IOException e) {
				e.printStackTrace();
				return null;

			}
		}

		protected void onPostExecute(List<Event> events) {
			// save events list somewhere
		}
	}

}
