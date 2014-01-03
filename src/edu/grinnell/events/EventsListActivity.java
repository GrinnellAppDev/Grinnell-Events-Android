package edu.grinnell.events;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.example.events_android.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import edu.grinnell.events.data.EventContent;
import edu.grinnell.events.data.EventContent.Event;

public class EventsListActivity extends SherlockFragmentActivity implements
		EventsListFragment.Callbacks {

	final public static String FEED = "http://schedule25wb.grinnell.edu/rssfeeds/memo.xml";
	public List<Event> mData = new ArrayList<Event>();
	String TAG = "EVENTS_LIST_ACTIVITY";

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events_list);

		Parse.initialize(getApplication(),
				"gxqIXbjvBCr7oYCYzNT2GYidbYv3Jiy4NJSJxxN3",
				"S0FQadLhLS5ine1wsDQ2YY3rnOKsAD2eEqNNwdY6");

		Calendar today = new GregorianCalendar();

		filterEventsByDay(today.get(Calendar.DAY_OF_MONTH),
				today.get(Calendar.MONTH), today.get(Calendar.YEAR));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_events_list, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_pick_date:
			showDatePickerDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onItemSelected(String id) {
		Intent detailIntent = new Intent(this, EventsDetailActivity.class);
		detailIntent.putExtra(EventsDetailFragment.ARG_ITEM_ID, id);
		startActivity(detailIntent);
	}

	public void retrieveDateFromParse(String selectedDate) {

		ParseQuery<ParseObject> event_query = ParseQuery.getQuery("Event");
		event_query.whereEqualTo("date", selectedDate);
		event_query.setCachePolicy(ParseQuery.CachePolicy.NETWORK_ELSE_CACHE);

		event_query.findInBackground(new FindCallback<ParseObject>() {
			public void done(List<ParseObject> eventList, ParseException e) {
				if (e == null) {
					Log.d(TAG, "Retrieved " + eventList.size() + " events");
					saveToList(eventList);
				} else {
					Log.d(TAG, "Error: " + e.getMessage());
				}
			}
		});
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
			Event new_event = new Event(eventid, title, startDate, endDate,
					location, details);

			EventContent.EventList.add(new_event);
		}

		mData = EventContent.EventList;

		populateList(mData);
	}

	public class EventComparator implements Comparator<Event> {
		@Override
		public int compare(Event e1, Event e2) {
			return e1.getStartTime().compareTo(e2.getStartTime());
		}
	}

	public void sortEventList() {
		Collections.sort(mData, new EventComparator());
	}

	public void showDatePickerDialog() {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	public void filterEventsByDay(int day, int month, int year) {

		String dateString = formDateString(day, month, year);

		retrieveDateFromParse(dateString);
	}

	public String formDateString(int day, int month, int year) {

		Calendar selectedDate = new GregorianCalendar(year, month, day);

		String day_of_week;
		switch (selectedDate.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.MONDAY:
			day_of_week = "Mon";
			break;
		case Calendar.TUESDAY:
			day_of_week = "Tue";
			break;
		case Calendar.WEDNESDAY:
			day_of_week = "Wed";
			break;
		case Calendar.THURSDAY:
			day_of_week = "Thu";
			break;
		case Calendar.FRIDAY:
			day_of_week = "Fri";
			break;
		case Calendar.SATURDAY:
			day_of_week = "Sat";
			break;
		case Calendar.SUNDAY:
			day_of_week = "Sun";
			break;
		default:
			day_of_week = "Unknown Day";
		}

		String monthString = "Unknown Month";
		switch (selectedDate.get(Calendar.MONTH)) {
		case Calendar.JANUARY:
			monthString = "Jan";
			break;
		case Calendar.FEBRUARY:
			monthString = "Feb";
			break;
		case Calendar.MARCH:
			monthString = "Mar";
			break;
		case Calendar.APRIL:
			monthString = "Apr";
			break;
		case Calendar.MAY:
			monthString = "May";
			break;
		case Calendar.JUNE:
			monthString = "Jun";
			break;
		case Calendar.JULY:
			monthString = "Jul";
			break;
		case Calendar.AUGUST:
			monthString = "Aug";
			break;
		case Calendar.SEPTEMBER:
			monthString = "Sep";
			break;
		case Calendar.OCTOBER:
			monthString = "Oct";
			break;
		case Calendar.NOVEMBER:
			monthString = "Nov";
			break;
		case Calendar.DECEMBER:
			monthString = "Dec";
			break;
		default:
			day_of_week = "Unknown Month";
		}

		String dateString = day_of_week + " " + monthString + " "
				+ selectedDate.get(Calendar.DAY_OF_MONTH) + " "
				+ selectedDate.get(Calendar.YEAR);

		Log.i(TAG, dateString);

		return dateString;
	}

	public void populateList(List<Event> data) {

		if (mData != null)
			Log.d("events", "Retrieved " + mData.size() + " events");
		else
			Log.d("events", "event list empty");

		sortEventList();

		FragmentManager fm = getSupportFragmentManager();
		EventsListFragment lstFrag = (EventsListFragment) fm
				.findFragmentById(R.id.fragment_container);
		if (lstFrag != null) {
			fm.beginTransaction().remove(lstFrag);
			lstFrag.getListView().removeAllViewsInLayout();
		}
		EventsListFragment eventList = new EventsListFragment();

		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, eventList).commit();
	}

}
