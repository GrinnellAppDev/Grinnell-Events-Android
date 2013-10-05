package edu.grinnell.events;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.events_android.R;

import edu.grinnell.events.data.EventContent.Event;
import edu.grinnell.events.data.PullEvents;

/**
 * A fragment representing a single Events detail screen. This fragment is
 * either contained in a {@link EventsListActivity} in two-pane mode (on
 * tablets) or a {@link EventsDetailActivity} on handsets.
 */
public class EventsDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The dummy content this fragment is presenting.
	 */
	private Event mItem;
	List<Event> mData = PullEvents.EventList;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public EventsDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {

			String ID = getArguments().getString(ARG_ITEM_ID);
			mItem = findEventByID(ID);

			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			// mItem = EventContent.ITEMS.get(0);

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_events_detail,
				container, false);

		// Show the dummy content as text in a TextView.
		if (mItem != null) {
			((TextView) rootView.findViewById(R.id.events_title)).setText(mItem
					.getTitle());
			((TextView) rootView.findViewById(R.id.events_detail))
					.setText(mItem.getDetails());
			((TextView) rootView.findViewById(R.id.events_location))
					.setText(mItem.getLocation());
			((TextView) rootView.findViewById(R.id.events_date)).setText(mItem
					.getDate());
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
