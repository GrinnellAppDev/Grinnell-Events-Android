package edu.grinnell.events;

import java.net.MalformedURLException;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.events_android.R;

import edu.grinnell.events.data.EventContent.Event;
import edu.grinnell.events.data.PullEvents;

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
	public List<Event> mData;

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
			((EventsListFragment) getSupportFragmentManager()
					.findFragmentById(R.layout.fragment_events_list))
					.setActivateOnItemClick(true);

		}

		PullEvents parse = new PullEvents();
		
		try {
			parse.pullFeed(FEED);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		EventsListFragment eventList = new EventsListFragment();

		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment_container, eventList).commit();
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
			Bundle arguments = new Bundle();
			arguments.putString(EventsDetailFragment.ARG_ITEM_ID, id);
			EventsDetailFragment fragment = new EventsDetailFragment();
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
}
