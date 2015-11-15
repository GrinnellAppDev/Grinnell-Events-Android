package edu.grinnell.events.Fragments;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import edu.grinnell.events.Activities.EventsListActivity;
import edu.grinnell.events.Model.EventContent.Event;
import edu.grinnell.events.R;

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

    /**
     * Constructor created by the newInstance and takes in the Item ID
     */
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

        android.support.v7.app.ActionBar actionBar = ((EventsListActivity) getActivity()).getSupportActionBar();
        if(actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
        mActivity.invalidateOptionsMenu();
    }


    /**
     * Handles the setup of the DetailFragment view.
     * Inflates view, retrieves parse item, and returns view.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_events_detail,
                container, false);
        retrieveEventFromParse(mID);
        changeIconColor();
        return mView;
    }


    /**
     * Set drawable icons to image views in fragment.
     * Sets the color of the icon (originally gray) and adds a red tint to it
     */
    public void changeIconColor() {
        Drawable timeIcon = getResources().getDrawable(R.drawable.ic_action_timeicon);
        Drawable locationIcon = getResources().getDrawable(R.drawable.ic_action_locationicon);
        int red = getResources().getColor(R.color.Red);
        ColorFilter filter = new LightingColorFilter(red, red);
        timeIcon.setColorFilter(filter);
        locationIcon.setColorFilter(filter);
        ((ImageView) mView.findViewById(R.id.timeicon)).setImageDrawable(timeIcon);
        ((ImageView) mView.findViewById(R.id.locationicon)).setImageDrawable(locationIcon);
    }


    /**
     * Puts local parse data into the fragment view
     */
    public void showDetails() {
        if (mItem != null) {
            ((TextView) mView.findViewById(R.id.events_title)).setText(mItem
                    .getTitle());

            String details = mItem.getDetails();
            if (details != null && !details.isEmpty()) {
                ((TextView) mView.findViewById(R.id.events_detail))
                        .setText(Html.fromHtml(details));
            }

            ((TextView) mView.findViewById(R.id.events_location))
                    .setText(mItem.getLocation());

            mView.findViewById(R.id.calendar_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mItem != null) {
                        addEventToCalendar();
                    }
                }
            });


            SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM d yyyy", Locale.US);
            formatter.setTimeZone(mActivity.mTimeZone);

            Date date = mItem.getStartTime();
            String result = formatter.format(date);
            SimpleDateFormat timeFormatter = new SimpleDateFormat("h:mm a", Locale.US);
            timeFormatter.setTimeZone(mActivity.mTimeZone);

            String time = timeFormatter.format(date) + " - " +
                    timeFormatter.format(mItem.getEndTime());

            ((TextView) mView.findViewById(R.id.events_date)).setText(result);
            ((TextView) mView.findViewById(R.id.events_time)).setText(time);

        }
    }


    /**
     *   Query the events for a specific day from the Parse database
     **/
    public void retrieveEventFromParse(String id) {
        ParseQuery<ParseObject> event_query = ParseQuery.getQuery("Event2");
        event_query.whereEqualTo("objectId", id);

        final ScrollView scrollView = ((ScrollView) mView.findViewById(R.id.event_scroll_view));
        scrollView.setVisibility(View.GONE);

        event_query.setCachePolicy(ParseQuery.CachePolicy.CACHE_ELSE_NETWORK);
        event_query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject p_event, ParseException e) {
                if (e == null) {
                    String eventid = p_event.getObjectId();
                    String title = p_event.getString("title");
                    String location = p_event.getString("location");
                    Date startDate = p_event.getDate("startTime");
                    Date endDate = p_event.getDate("endTime");
                    String details = p_event.getString("detailDescription");
                    Event thisEvent = new Event(eventid, title, startDate, endDate, location, details);

                    mItem = thisEvent;
                    showDetails();
                    scrollView.setVisibility(View.VISIBLE);
                } else {
                    Log.d(TAG, "Error: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Add the event to a calendar selected by the user
     */
    public void addEventToCalendar() {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("beginTime", mItem.getStartTime().getTime());
        intent.putExtra("allDay", false);
        intent.putExtra("endTime", mItem.getEndTime().getTime());
        intent.putExtra("title", mItem.getTitle());
        intent.putExtra("eventLocation", mItem.getLocation());
        intent.putExtra("description", mItem.getDetails());
        mActivity.startActivity(intent);
    }
}
