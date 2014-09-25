package edu.grinnell.events;

import com.crashlytics.android.Crashlytics;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import edu.grinnell.events.data.EventContent.Event;

public class EventsListActivity extends FragmentActivity implements EventsListFragment.Callbacks {

    String TAG = "EVENTS_LIST_ACTIVITY";

    //Date at which the pages start
    public static final Date baseDate = new GregorianCalendar(2014, 0, 1).getTime();
    boolean detailShowing = false;

    protected List<Event> mData = new ArrayList<Event>();
    protected Event mSelectedEvent;
    EventDayAdapter mEventDayAdapter;

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_events_list);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mEventDayAdapter = new EventDayAdapter(getSupportFragmentManager());

        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mEventDayAdapter);

		/* Open the events for today by default */
        Date today = new GregorianCalendar().getTime();

        int daysPastBase = daysBetween(today, baseDate);

        mViewPager.setCurrentItem(daysPastBase);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!detailShowing) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_events_list, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pick_date:
                showDatePickerDialog();
                return true;
            case android.R.id.home:
                // app icon in action bar clicked; go home
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (detailShowing) {
        //    mViewPager.setVisibility(View.VISIBLE);
            detailShowing = false;
            invalidateOptionsMenu();
            getSupportFragmentManager().popBackStack();
        }
    }

    /* Open the detail page for an event */
    @Override
    public void onItemSelected(String id) {
        FragmentManager fm = getSupportFragmentManager();
        EventsDetailFragment eventDetails = EventsDetailFragment.newInstance(id);
      //  mViewPager.setVisibility(View.INVISIBLE);
        detailShowing = true;
        fm.beginTransaction().setCustomAnimations(R.anim.left_slide_in, R.anim.left_slide_out, R.anim.right_slide_in, R.anim.right_slide_out)
                .replace(R.id.container, eventDetails).addToBackStack(null).commit();
    }

    /* The dialog to allow users to select a specific date */
    public void showDatePickerDialog() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
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

    public class EventDayAdapter extends FragmentStatePagerAdapter {

        public EventDayAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Date thisDay = positionToDate(position);

            return EventsListFragment.newInstance(thisDay.getTime());
        }

        @Override
        public int getCount() {
            return 30000;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Date thisDay = positionToDate(position);

            SimpleDateFormat formatter = new SimpleDateFormat("EEE MM/dd");

            return ("" + formatter.format(thisDay));
        }

    }
}
