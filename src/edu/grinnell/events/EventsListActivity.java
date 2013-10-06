package edu.grinnell.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.example.events_android.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import edu.grinnell.events.data.EventContent;
import edu.grinnell.events.data.EventContent.Event;

/**
 * An activity representing a list of Events. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link EventsDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link EventsListFragment} and the item details (if present) is a
 * {@link EventsDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link EventsListFragment.Callbacks} interface to listen for item selections.
 */

public class EventsListActivity extends FragmentActivity implements
		EventsListFragment.Callbacks {

	final public static String FEED = "http://schedule25wb.grinnell.edu/rssfeeds/memo.xml";
	public List<Event> mData = new ArrayList<Event>();

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events_list);

		if (findViewById(R.id.events_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((EventsListFragment) getSupportFragmentManager().findFragmentById(
					R.layout.fragment_events_list))
					.setActivateOnItemClick(true);

		}
		retrieveFromParse();
}	
	
	
	public void retrieveFromParse() {
		Parse.initialize(this, "gxqIXbjvBCr7oYCYzNT2GYidbYv3Jiy4NJSJxxN3",
				"S0FQadLhLS5ine1wsDQ2YY3rnOKsAD2eEqNNwdY6");

		ParseQuery<ParseObject> event_query = ParseQuery.getQuery("Event");
		event_query.setLimit(500);
		event_query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);
		event_query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> eventList, ParseException e) {
				if (e == null) {
					Log.d("score", "Retrieved " + eventList.size() + " events");
					// p_event_list = eventList;
					saveToList(eventList);
				} else {
					Log.d("score", "Error: " + e.getMessage());
				}
			}
		});
	}
	/**
	 * Callback method from {@link EventsListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(String id) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			
			EventsDetailFragment fragment = new EventsDetailFragment();

			Bundle arguments = new Bundle();
			arguments.putString(EventsDetailFragment.ARG_ITEM_ID, id);
			
			fragment.setArguments(arguments);
			
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.events_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, EventsDetailActivity.class);
			detailIntent.putExtra(EventsDetailFragment.ARG_ITEM_ID, id);
			startActivity(detailIntent);
		}
	}

	protected void saveToList(List<ParseObject> p_event_list) {
		String eventid;
		String title;
		String location;
		String details;
		Date startDate;
		Date endDate;

		Iterator<ParseObject> event_saver = p_event_list.iterator();
		while (event_saver.hasNext()) {
			ParseObject p_event = event_saver.next();
			eventid = p_event.getString("eventid");
			title = p_event.getString("title");
			location = p_event.getString("location");
			startDate = p_event.getDate("startTime");
			endDate = p_event.getDate("endTime");
			details = p_event.getString("detailDescription");
			Event new_event = new Event(eventid, title, startDate, endDate, location,
					details);
			
			EventContent.EventList.add(new_event);
		}

		mData = EventContent.EventList;
		sortEventList();
		
		if (mData != null)
			Log.d("events", "Retrieved " + mData.size() + " events");
		else
			Log.d("events", "event list empty");
		

		EventsListFragment eventList = new EventsListFragment();
		eventList.mData = mData;

		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, eventList).commit();
				
	}
		
	public class EventComparator implements Comparator<Event> {
		@Override
		public int compare(Event e1, Event e2){
			return e1.getStartTime().compareTo(e2.getStartTime());
		}
	}
	
	public void sortEventList(){
		Collections.sort(mData, new EventComparator());
	}
	
}
