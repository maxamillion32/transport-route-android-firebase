package com.fcorcino.transportroute.ui.pendingstops;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fcorcino.transportroute.R;
import com.fcorcino.transportroute.model.PendingStop;
import com.fcorcino.transportroute.utils.Constants;

import java.util.ArrayList;

public class PendingStopsArrayAdapter extends ArrayAdapter<PendingStop> {

    /**
     * Class constructor.
     *
     * @param context      to be used by the super class.
     * @param pendingStops the list of pending stops to display.
     */
    public PendingStopsArrayAdapter(Context context, ArrayList<PendingStop> pendingStops) {
        super(context, 0, pendingStops);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_pending_stop, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PendingStop pendingStop = getItem(position);

        if (pendingStop != null) {
            viewHolder.stopNameTextView.setText(pendingStop.getStopName());
            int textResource = pendingStop.getStopType().equals(Constants.STATUS_VALUE_PICK_UP) ? R.string.to_pick_up_text : R.string.to_deliver_text;
            viewHolder.stopPeopleWaitingTextView.setText(String.format(getContext().getString(textResource, pendingStop.getPeopleWaiting())));
        }

        return convertView;
    }

    /**
     * This class is used to implement the view holder pattern.
     */
    private class ViewHolder {

        /**
         * @var stopNameTextView this text view displays the stop name.
         */
        public TextView stopNameTextView;

        /**
         * @var stopPeopleWaitingTextView this text view displays the peaople waiting in the stop.
         */
        public TextView stopPeopleWaitingTextView;

        /**
         * Constructor of the class.
         *
         * @param rootView the view to be used to get the subviews.
         */
        public ViewHolder(View rootView) {
            stopNameTextView = (TextView) rootView.findViewById(R.id.stop_name_text_view);
            stopPeopleWaitingTextView = (TextView) rootView.findViewById(R.id.stop_people_waiting_text_view);
        }
    }
}
