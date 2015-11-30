package edu.grinnell.events.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.grinnell.events.Activities.EventsListActivity;
import edu.grinnell.events.Model.EventContent.Event;
import edu.grinnell.events.R;

/**
 * List Adapter for EventListActivity
 */
public class EventsListAdapter extends ArrayAdapter<Event> {

    private EventsListActivity mActivity;
    //	private List<Event> mData = new ArrayList<Event>();
    private List<Event> mData;
    private List<Event> fullData; //will hold the unfiltered events
    List<Event> filterResultsData = new ArrayList<Event>();



    public EventsListAdapter(EventsListActivity a, int layoutId,
                             List<Event> data) {
        super(a, layoutId, data);
        mActivity = a;
        mData = data;
        fullData = data;
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

        final Event a = getItem(position);
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




    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {

                    results.values = fullData;
                    results.count = fullData.size();
                    mData=fullData;
                } else {
                    for (Event c : fullData) {
                        if (c.getTitle()
                                .toLowerCase(Locale.ENGLISH)
                                .contains(constraint)) {
                            filterResultsData.add(c);
                        }
                    }
                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                    mData = filterResultsData;
                }
                return results;
            }



            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mData = (List<Event>) results.values;
                EventsListAdapter.this.notifyDataSetChanged();
            }


        };
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Event getItem(int position) {
        return this.mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
