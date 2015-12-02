package edu.grinnell.events.Activities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import edu.grinnell.events.Fragments.DatePickerFragment;
import edu.grinnell.events.Fragments.EventsDetailFragment;
import edu.grinnell.events.Fragments.EventsListFragment;
import edu.grinnell.events.R;


public class EventsListActivity extends AppCompatActivity implements EventsListFragment.Callbacks {

    public static final Date baseDate = new GregorianCalendar(2014, 0, 1).getTime();
    boolean detailShowing = false;
    EventDayAdapter mEventDayAdapter;
    public ViewPager mViewPager;
    public TimeZone mTimeZone;
    android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_events_list);

        mViewPager = (ViewPager) findViewById(R.id.pager);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        mEventDayAdapter = new EventDayAdapter(getSupportFragmentManager());
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mEventDayAdapter);

        /* Open the events for today by default */

        if(!isNetworkConnected())
            Toast.makeText(EventsListActivity.this, "P", Toast.LENGTH_SHORT).show();

        mViewPager.setCurrentItem(getDefaultDate());

    }

    /**
     * Inflates menu space for the event list button.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!detailShowing) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.activity_events_list, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handles the actions when the menu items are clicked
     **/
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

    /**
     * Handles back button clicks
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (detailShowing) {
            detailShowing = false;
            if(getSupportActionBar() != null)
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            invalidateOptionsMenu();
            getSupportFragmentManager().popBackStack();
        }
    }


    /**
     * Checks to see if there is a network connection on the phone
     * @return True: Network connections exits
     *         False: Network connections doesn't exist
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    /**
     * Current date in the person's timezone
     * @return int, number of days between the baseDate and current Date
     */
    public int getDefaultDate(){
        GregorianCalendar todayCalendar = new GregorianCalendar(Locale.US);

        // Correct for time zones and DST
        if (todayCalendar.getTimeZone().inDaylightTime(new Date())) {
            mTimeZone = TimeZone.getTimeZone("GMT-5");
        }
        else {
            mTimeZone = TimeZone.getTimeZone("GMT-6");
        }

        todayCalendar.setTimeZone(mTimeZone);

        Date today = todayCalendar.getTime();

        return daysBetween(today, baseDate);
    }

    /**
     * Open the EventDetailFragment for an event
     * @param id: the id of the item selected by the user within the EventList
     */
    @Override
    public void onItemSelected(String id) {
        FragmentManager fm = getSupportFragmentManager();
        /* Creates fragment and passes the fragment ID*/
        EventsDetailFragment eventDetails = EventsDetailFragment.newInstance(id);
        detailShowing = true;
        fm.beginTransaction().setCustomAnimations(R.anim.left_slide_in_fast, R.anim.left_slide_out_fast, R.anim.right_slide_in_fast, R.anim.right_slide_out_fast)
                .replace(R.id.container, eventDetails).addToBackStack(null).commit();
    }


    /**
     * The dialog to allow users to select a specific date
     */
    public void showDatePickerDialog() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getSupportFragmentManager(), "datePicker");
    }


    /**
     * Find the number of days between two given dates.
     * @param date1: base date
     * @param date2: second date
     * @return: int: number of days
     */
    public int daysBetween(Date date1, Date date2) {
        Long daysBetween;

        // 86400000 milliseconds in in a day
        daysBetween = (date1.getTime() - date2.getTime()) / 86400000;

        return (int) Math.floor(daysBetween);
    }

    /**
     * calculate how many milliseconds since first page
     * @param position
     * @return Date
     */
    public Date positionToDate(int position) {
        //calculate how many milliseconds since first page, each page is one day
        long numMillis = (long) position * 86400000;
        return new Date(baseDate.getTime() + numMillis);
    }

    public class EventDayAdapter extends FragmentStatePagerAdapter {

        public EventDayAdapter(FragmentManager fm) {
            super(fm);
        }


        /**
         * Gets the fragment
         * @param position
         * @return
         */
        @Override
        public Fragment getItem(int position) {
            Date thisDay = positionToDate(position);

            Calendar calendarDay = new GregorianCalendar(Locale.US);
            calendarDay.setTime(thisDay);
            calendarDay.setTimeZone(mTimeZone);

            return EventsListFragment.newInstance(thisDay.getTime());
        }

        /**
         *
         * @return int, 30000
         */
        @Override
        public int getCount() {
            return 30000;
        }


        /**
         * Format date shown on top of the ListFrangment as the title
         * @param position: int, date of the ListFragement
         * @return CharSequence, formatted date
         */
        @Override
        public CharSequence getPageTitle(int position) {
            Date thisDay = positionToDate(position);
            SimpleDateFormat formatter = new SimpleDateFormat("EEE MM/dd");
            String date = formatter.format(thisDay);
            Log.d("CURRENT PAGE", date);
            return ("" + date);
        }

    }
}
