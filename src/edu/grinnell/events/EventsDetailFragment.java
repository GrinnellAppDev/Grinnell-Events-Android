package edu.grinnell.events;

import java.util.Date;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import edu.grinnell.events.data.EventContent.Event;

public class EventsDetailFragment extends Fragment {
	final String TAG = EventsDetailFragment.class.getSimpleName();
	static final String EVENT_ID = "EVENT_ID";

	EventsListActivity mActivity;
	Event mItem;
	String mID;
	View mView;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public EventsDetailFragment() {
	}

	public static EventsDetailFragment newInstance(String id) {
		EventsDetailFragment fragment = new EventsDetailFragment();
		Bundle args = new Bundle();
		args.putString(EVENT_ID, id);
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			mActivity = (EventsListActivity) getActivity();
			mID = getArguments().getString(EVENT_ID);
			retrieveEventFromParse(mID);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_events_detail,
				container, false);

		return mView;
	}
	
	public void showDetails() {
		if (mItem != null) {
			((TextView) mView.findViewById(R.id.events_title)).setText(mItem
					.getTitle());
			((TextView) mView.findViewById(R.id.events_detail))
					.setText(mItem.getDetails());
			((TextView) mView.findViewById(R.id.events_location))
					.setText(mItem.getLocation());
			((TextView) mView.findViewById(R.id.events_date)).setText(mItem
					.getStartTime().toString());
			((TextView) mView.findViewById(R.id.events_id)).setText(mItem
					.getID());

		}
	}
	
	/* Query the events for a specific day from the Parse database */
	public void retrieveEventFromParse(String id) {
		ParseQuery<ParseObject> event_query = ParseQuery.getQuery("Event2");
		event_query.whereEqualTo("eventid", id);

		event_query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);

		event_query.getFirstInBackground(new GetCallback<ParseObject>() {
			public void done(ParseObject p_event, ParseException e) {
				if (e == null) {
					String eventid = p_event.getString("eventid");
					String title = p_event.getString("title");
					String location = p_event.getString("location");
					Date startDate = p_event.getDate("startTime");
					Date endDate = p_event.getDate("endTime");
					String details = p_event.getString("detailDescription");
					Event thisEvent = new Event(eventid, title, startDate, endDate, location, details);
					
					mItem = thisEvent;
					showDetails();
				} else {
					Log.d(TAG, "Error: " + e.getMessage());
				}
			}
		});
	}
}
