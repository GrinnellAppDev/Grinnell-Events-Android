package edu.grinnell.events;

import java.text.SimpleDateFormat;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import edu.grinnell.events.data.EventContent.Event;

public class EventsListAdapter extends ArrayAdapter<Event> {

	private EventsListActivity mActivity;
//	private List<Event> mData = new ArrayList<Event>();
	private List<Event> mData;


	public EventsListAdapter(EventsListActivity a, int layoutId,
			List<Event> data) {
		super(a, layoutId, data);
		mActivity = a;
		mData = data;
	}

	private class ViewHolder {
		TextView title;
		TextView date;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;

		LayoutInflater li = mActivity.getLayoutInflater();
		convertView = li.inflate(R.layout.events_row, parent, false);
		holder = new ViewHolder();
		holder.title = (TextView) convertView.findViewById(R.id.titleText);
		holder.date = (TextView) convertView
				.findViewById(R.id.dateText);
		convertView.setTag(holder);

		final Event a = mData.get(position);

		if (a != null) {

			holder.title.setText(a.getTitle());
			holder.title.setPadding(3, 3, 3, 3);
			
			SimpleDateFormat formatter = new SimpleDateFormat("hh:mm aa");

			holder.date.setText(formatter.format(a.getStartTime()));
		}

		return convertView;
	}
}
