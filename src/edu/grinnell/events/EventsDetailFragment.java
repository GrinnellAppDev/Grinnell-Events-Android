package edu.grinnell.events;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.grinnell.events.data.EventContent.Event;

public class EventsDetailFragment extends Fragment {
	static final String EVENT_ID = "EVENT_ID";

	EventsListActivity mActivity;
	public Event mItem;
	public String mID;

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
			mItem = mActivity.mSelectedEvent;
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
}
