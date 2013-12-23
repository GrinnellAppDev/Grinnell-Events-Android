package edu.grinnell.events;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.events_android.R;

import edu.grinnell.events.data.EventContent;
import edu.grinnell.events.data.EventContent.Event;

/**
 * An activity representing a single Events detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {@link EventsListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link EventsDetailFragment}.
 */
public class EventsDetailActivity extends SherlockFragmentActivity {

	public List<Event> mData = EventContent.EventList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_events_detail);

		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(EventsDetailFragment.ARG_ITEM_ID, getIntent()
					.getStringExtra(EventsDetailFragment.ARG_ITEM_ID));
			EventsDetailFragment fragment = new EventsDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.events_detail_container, fragment).commit();
		}
	}

	public void addEventToCalendar(View view) {

		EventsDetailFragment detailFragment = (EventsDetailFragment) getSupportFragmentManager()
				.findFragmentById(R.id.events_detail_container);
		Event mItem = detailFragment.mItem;

		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("beginTime", mItem.getStartTime().getTime());
		intent.putExtra("allDay", false);
		intent.putExtra("endTime", mItem.getEndTime().getTime());
		intent.putExtra("title", mItem.getTitle());
		intent.putExtra("eventLocation", mItem.getLocation());
		intent.putExtra("description", mItem.getDetails());
		startActivity(intent);
	}
}
