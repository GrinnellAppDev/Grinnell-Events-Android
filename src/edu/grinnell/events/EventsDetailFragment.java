package edu.grinnell.events;

import java.util.List;
import java.util.ListIterator;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.example.events_android.R;

import edu.grinnell.events.data.EventContent;
import edu.grinnell.events.data.EventContent.Event;

public class EventsDetailFragment extends SherlockFragment {

	public static final String ARG_ITEM_ID = "item_id";

	EventsListActivity mActivity;
	public Event mItem;
	public List<Event> mData;
	public String mID;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public EventsDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

			mActivity = (EventsListActivity) getActivity();
			mData = mActivity.mData;
			mID = mActivity.mEventID;
			mItem = findEventByID(mID);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_events_detail,
				container, false);

		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.events_title)).setText(mItem
					.getTitle());
			((TextView) rootView.findViewById(R.id.events_detail))
					.setText(mItem.getDetails());
			((TextView) rootView.findViewById(R.id.events_location))
					.setText(mItem.getLocation());
			((TextView) rootView.findViewById(R.id.events_date)).setText(mItem
					.getStartTime().toString());
			((TextView) rootView.findViewById(R.id.events_id)).setText(mItem
					.getID());

		}

		return rootView;
	}

	public Event findEventByID(String ID) {
		ListIterator<Event> iter = mData.listIterator();
		while (iter.hasNext()) {
			if (ID.compareTo(iter.next().getID()) == 0) {
				return iter.previous();
			}
		}
		return null;
	}
}
