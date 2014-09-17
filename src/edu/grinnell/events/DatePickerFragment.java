package edu.grinnell.events;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		//set the events date to the new settings
		EventsListActivity mActivity = (EventsListActivity) getActivity();
		/*
		mActivity.mDay = day;
		mActivity.mMonth = month;
		mActivity.mYear = year;
		*/
		
		Date thisDate = new GregorianCalendar(year, month, day).getTime();
		mActivity.retrieveDateFromParse(thisDate);
	}

}
