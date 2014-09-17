package edu.grinnell.events;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import edu.grinnell.events.data.EventContent;
import edu.grinnell.events.data.EventContent.Event;

public class EventsListActivity extends FragmentActivity implements EventsListFragment.Callbacks {

	String TAG = "EVENTS_LIST_ACTIVITY";
	
	//Date at which the pages start
	final Date baseDate = new GregorianCalendar(2014, 0, 0).getTime();

	protected List<Event> mData = new ArrayList<Event>();
	protected String mEventID;
	protected Event mSelectedEvent;
	EventDayAdapter mEventDayAdapter;

	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events_list);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mEventDayAdapter = new EventDayAdapter(getSupportFragmentManager());

		mViewPager.setOffscreenPageLimit(3);
		mViewPager.setAdapter(mEventDayAdapter);

		mViewPager.setCurrentItem(0);


		/* Open the events for today by default */
		Date today = new GregorianCalendar().getTime();
		
		int daysPastBase = daysBetween(today, baseDate);
		
		mViewPager.setCurrentItem(daysPastBase);
		
		retrieveDateFromParse(today);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
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

	/* Open the detail page for an event */
	@Override
	public void onItemSelected(String id) {

		mEventID = id;
		mSelectedEvent = findEventByID(id);

		FragmentManager fm = getSupportFragmentManager();
		EventsDetailFragment eventDetails = new EventsDetailFragment();
		fm.beginTransaction().replace(R.id.container, eventDetails).addToBackStack("EventList").commit();
	}

	// Return the event cooresponding with a given ID
	public Event findEventByID(String ID) {
		ListIterator<Event> iter = mData.listIterator();
		while (iter.hasNext()) {
			if (ID.compareTo(iter.next().getID()) == 0) {
				return iter.previous();
			}
		}
		return null;
	}

	/* Query the events for a specific day from the Parse database */
	public void retrieveDateFromParse(Date selectedDate) {
		ParseQuery<ParseObject> event_query = ParseQuery.getQuery("Event2");		
		event_query.whereEqualTo("startTime", selectedDate);
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

	/* Save the parse objects to a list of event objects */
	protected void saveToList(List<ParseObject> p_event_list) {
		String eventid;
		String title;
		String location;
		String details;
		Date startDate;
		Date endDate;

		EventContent.EventList.clear();

		Iterator<ParseObject> event_saver = p_event_list.iterator();
		while (event_saver.hasNext()) {
			ParseObject p_event = event_saver.next();
			eventid = p_event.getString("eventid");
			title = p_event.getString("title");
			location = p_event.getString("location");
			startDate = p_event.getDate("startTime");
			endDate = p_event.getDate("endTime");
			details = p_event.getString("detailDescription");
			Event new_event = new Event(eventid, title, startDate, endDate, location, details);

			EventContent.EventList.add(new_event);
		}
		mData = EventContent.EventList;
	//	populateList(mData);
	}

	/* sort the event list so that later events come after earlier events */
	public void sortEventList() {
		Collections.sort(mData, new EventComparator());
	}

	public class EventComparator implements Comparator<Event> {
		@Override
		public int compare(Event e1, Event e2) {
			return e1.getStartTime().compareTo(e2.getStartTime());
		}
	}

	/* The dialog to allow users to select a specific date */
	public void showDatePickerDialog() {
		DialogFragment datePicker = new DialogFragment();
		datePicker.show(getSupportFragmentManager(), "datePicker");
	}
	
	public int daysBetween(Date date1, Date date2) {
		Long daysBetween;
		
		// 86400000 milliseconds in in a day
		daysBetween = (date1.getTime() - date2.getTime()) / 86400000;		
		
		return (int) Math.floor(daysBetween);
	}
	
	public Date positionToDate(int position) {
		//calculate how many milliseconds since first page, each page is one day
		
		long numMillis = (long) position * 86400000;
				
		return new Date(baseDate.getTime() + numMillis);
	}
	
	/* Add the event to a calendar selected by the user */
	public void addEventToCalendar(View view) {
		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("beginTime", mSelectedEvent.getStartTime().getTime());
		intent.putExtra("allDay", false);
		intent.putExtra("endTime", mSelectedEvent.getEndTime().getTime());
		intent.putExtra("title", mSelectedEvent.getTitle());
		intent.putExtra("eventLocation", mSelectedEvent.getLocation());
		intent.putExtra("description", mSelectedEvent.getDetails());
		startActivity(intent);
	}

	public class EventDayAdapter extends FragmentStatePagerAdapter {

		public EventDayAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			
			Date thisDay = positionToDate(position);
			//retrieveDateFromParse(today);

			return new EventsListFragment();
			}

		@Override
		public int getCount() {
			return 10000;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return positionToDate(position).toGMTString();
		}

	}

}



/*
public void filterEventsByDay(int day, int month, int year) {
	String dateString = formDateString(day, month, year);
	retrieveDateFromParse(dateString);
}
*/

/*
 * This method will clear the previous event list fragment and insert a new
 * list cooresponding with the new event list
 */
/*
public void populateList(List<Event> data) {

	sortEventList();
	FragmentManager fm = getSupportFragmentManager();
	EventsListFragment eventList = new EventsListFragment();
	fm.beginTransaction().replace(R.id.container, eventList).commit();
}
*/

/*
 * Form at date string formated to be the same as how the event data is
 * stored in parse. TODO once we have the real event data we should store
 * the date as a date type instead of a string so that this conversion is no
 * longer nessesary.
 */
/*
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
		monthString = "Unknown Month";
	}

	String dateString = day_of_week + " " + monthString + " " + selectedDate.get(Calendar.DAY_OF_MONTH) + " " + selectedDate.get(Calendar.YEAR);

	Log.i(TAG, dateString);

	return dateString;
}
*/
