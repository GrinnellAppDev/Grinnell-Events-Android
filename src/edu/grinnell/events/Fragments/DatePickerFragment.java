package edu.grinnell.events.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import edu.grinnell.events.EventsListActivity;


/**
 * Date Picking dialog fragment that appears when the calendar icon is pressed.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    DatePickerDialog dateDialog;



	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		dateDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        dateDialog.getDatePicker().setMinDate(EventsListActivity.baseDate.getTime());
        dateDialog.getDatePicker().setMinDate(EventsListActivity.baseDate.getTime() + (30000 * 86400000));


        return dateDialog;
	}

	/**
	 * On user's input, sets the current item in the activity
	 */
	public void onDateSet(DatePicker view, int year, int month, int day) {
		//set the events date to the new settings
		EventsListActivity mActivity = (EventsListActivity) getActivity();
		Calendar thisDate = new GregorianCalendar(year, month, day);
        thisDate.setTimeZone(TimeZone.getTimeZone("GMT-5"));

		mActivity.mViewPager.setCurrentItem(mActivity.daysBetween(thisDate.getTime(), mActivity.baseDate));
	}

}
