package edu.grinnell.events;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import edu.grinnell.events.data.EventContent.Event;

public class EventsListFragment extends ListFragment {
	final String TAG = EventsListFragment.class.getSimpleName();

	static final String DATE_VALUE = "DATE_VALUE";

	public EventsListActivity mActivity;
	public List<Event> mData;
	public Event mEvent = null;
    View mView;

	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	private Callbacks mCallbacks = sDummyCallbacks;

	private int mActivatedPosition = ListView.INVALID_POSITION;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(String id);
	}

	/**
	 * <uses-permission android:name="android.permission.INTERNET" />
	 * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"
	 * /> <uses-permission
	 * android:name="android.permission.CHANGE_NETWORK_STATE" />
	 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
	 * /> A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public EventsListFragment() {
	}

	public static EventsListFragment newInstance(long date) {
		EventsListFragment fragment = new EventsListFragment();
		Bundle args = new Bundle();
		args.putLong(DATE_VALUE, date);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mData = new ArrayList<Event>();
		mActivity = (EventsListActivity) getActivity();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		mEvent = mData.get(position);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected(mEvent.getID());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != AdapterView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(activateOnItemClick ? AbsListView.CHOICE_MODE_SINGLE : AbsListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == AdapterView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_events_list,
                container, false);

        long thisDay = getArguments().getLong(DATE_VALUE);
        Calendar calendarDay = new GregorianCalendar(Locale.US);

        calendarDay.setTime(new Date(thisDay));
        calendarDay.setTimeZone(mActivity.mTimeZone);

        calendarDay.set(Calendar.HOUR, 0);
        calendarDay.set(Calendar.MINUTE, 0);
        calendarDay.set(Calendar.SECOND, 0);
        calendarDay.set(Calendar.MILLISECOND, 0);

        retrieveDateFromParse(calendarDay);

		return mView;
	}

	/* Query the events for a specific day from the Parse database */
	public void retrieveDateFromParse(Calendar selectedDate) {
		ParseQuery<ParseObject> event_query = ParseQuery.getQuery("Event2");

        Date todayDate = selectedDate.getTime();
		event_query.whereGreaterThanOrEqualTo("startTime", todayDate);

        //Calendar tomorrowDate = selectedDate;
        Date tomorrowDate = new Date(todayDate.getTime() + 86400000);
		event_query.whereLessThan("startTime", tomorrowDate);

        final RelativeLayout relativeLayout = (RelativeLayout) mView.findViewById(R.id.event_list_layout);
        relativeLayout.setVisibility(View.GONE);

        final ProgressBar progressBar = (ProgressBar) mView.findViewById(R.id.list_progress_bar);
        event_query.addAscendingOrder("startTime");
        event_query.setCachePolicy(ParseQuery.CachePolicy.CACHE_THEN_NETWORK);
		event_query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> eventList, ParseException e) {
				if (e == null) {
					Log.d(TAG, "Retrieved " + eventList.size() + " events");
					parseToList(eventList);
                    relativeLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
				} else {
					Log.d(TAG, "Error: " + e.getMessage());
				}
			}
		});
	}

	/* Save the parse objects to a list of event objects */
	protected void parseToList(List<ParseObject> p_event_list) {
		String eventid;
		String title;
		String location;
		String details;
		Date startDate;
		Date endDate;

		mData.clear();

		Iterator<ParseObject> event_saver = p_event_list.iterator();
		while (event_saver.hasNext()) {
			ParseObject p_event = event_saver.next();
			eventid = p_event.getObjectId();
			title = p_event.getString("title");
			location = p_event.getString("location");
			startDate = p_event.getDate("startTime");
			endDate = p_event.getDate("endTime");
			details = p_event.getString("detailDescription");
			Event new_event = new Event(eventid, title, startDate, endDate, location, details);

			mData.add(new_event);
		}
		EventsListAdapter adapter = new EventsListAdapter(mActivity, R.layout.events_row, mData);
		setListAdapter(adapter);
	}

}
