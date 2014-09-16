package edu.grinnell.events;

import android.app.Application;

import com.parse.Parse;

public class EventsApp extends Application {

	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, getString(R.string.parse_app_id),
				getString(R.string.parse_client_key));

	}
}
