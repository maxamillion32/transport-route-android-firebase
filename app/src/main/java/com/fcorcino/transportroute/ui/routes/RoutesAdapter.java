package com.fcorcino.transportroute.ui.routes;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.leaderapps.transport.model.Route;
import com.leaderapps.transport.transportrouteclient.R;

import java.util.ArrayList;

public class RoutesAdapter extends BaseAdapter implements Filterable {

    private Context mContext;
    private ArrayList<Route> mRoutes;
    private ArrayList<Route> mFilteredRoutes;
    private Filter mFilter;

    /**
     * Constructor of the class.
     *
     * @param context the context to be used by the super class.
     * @param routes  the list of routes to display.
     */
    public RoutesAdapter(Context context, ArrayList<Route> routes) {
        mContext = context;
        mRoutes = routes;
        mFilteredRoutes = routes;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_route, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Route route = (Route) getItem(position);

        if (route != null) {
            viewHolder.routeNameTextView.setText(route.getName());
            viewHolder.routeFromTextView.setText(route.getFrom());
            viewHolder.routeToTextView.setText(route.getTo());
            viewHolder.routePriceTextView.setText(String.format("%.2f", route.getPrice()));
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return mFilteredRoutes.size();
    }

    @Override
    public Object getItem(int i) {
        return mFilteredRoutes.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public Filter getFilter() {
        return mFilter == null ? new RouteFilter() : mFilter;
    }

    public void clear() {
        mRoutes.clear();
        mFilteredRoutes.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<Route> routes) {
        mRoutes.addAll(routes);
        notifyDataSetChanged();
    }

    private class RouteFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence keyword) {
            FilterResults filterResults = new FilterResults();
            ArrayList<Route> filteredRoutes = new ArrayList<>();

            if (mRoutes == null) mRoutes = new ArrayList<>();

            if (TextUtils.isEmpty(keyword)) {
                filterResults.values = mRoutes;
                filterResults.count = mRoutes.size();
            } else {
                for (Route route : mRoutes) {
                    if (route.getFrom().toLowerCase().contains(keyword.toString().toLowerCase())) filteredRoutes.add(route);
                }

                filterResults.values = filteredRoutes;
                filterResults.count = filteredRoutes.size();
            }

            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mFilteredRoutes = (ArrayList<Route>) filterResults.values;
            notifyDataSetChanged();
        }
    }

    /**
     * This class is used to implement the view holder patter.
     */
    private class ViewHolder {

        /**
         * @var routeNameTextView this text view displays the route name.
         */
        public TextView routeNameTextView;

        /**
         * @var routeFromTextView this text view displays the route from stop.
         */
        public TextView routeFromTextView;

        /**
         * @var routeToTextView this text view displays the route to stop.
         */
        public TextView routeToTextView;

        /**
         * @var routePriceTextView this text view displays the route price.
         */
        public TextView routePriceTextView;

        /**
         * Constructor of the class.
         *
         * @param rootView the view to be used to get the subviews.
         */
        public ViewHolder(View rootView) {
            routeNameTextView = (TextView) rootView.findViewById(R.id.route_name_text_view);
            routeFromTextView = (TextView) rootView.findViewById(R.id.route_from_text_view);
            routeToTextView = (TextView) rootView.findViewById(R.id.route_to_text_view);
            routePriceTextView = (TextView) rootView.findViewById(R.id.route_price_text_view);
        }
    }
}
