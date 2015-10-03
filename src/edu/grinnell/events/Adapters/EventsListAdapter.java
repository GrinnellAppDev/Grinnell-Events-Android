package edu.grinnell.events.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

import edu.grinnell.events.EventsListActivity;
import edu.grinnell.events.Model.EventContent.Event;
import edu.grinnell.events.R;

/**
 * List Adapter for EventListActivity
 */
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
        TextView location;
    }

    /**
     * Sets up the view for the individual events/rows
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        LayoutInflater li = mActivity.getLayoutInflater();
        convertView = li.inflate(R.layout.events_row, parent, false);
        holder = new ViewHolder();
        holder.title = (TextView) convertView.findViewById(R.id.titleText);
        holder.date = (TextView) convertView
                .findViewById(R.id.dateText);
        holder.location = (TextView) convertView
                .findViewById(R.id.locationText);
        convertView.setTag(holder);

        final Event a = mData.get(position);
        if (a != null) {

            holder.title.setText(a.getTitle());
            holder.title.setPadding(3, 3, 3, 3);

            SimpleDateFormat timeFormatter = new SimpleDateFormat("hh:mm aa");
            timeFormatter.setTimeZone(mActivity.mTimeZone);

            holder.date.setText(timeFormatter.format(a.getStartTime()) + " - " +
                    timeFormatter.format(a.getEndTime()));

            holder.location.setText(a.getLocation());
        }

        return convertView;
    }
}
