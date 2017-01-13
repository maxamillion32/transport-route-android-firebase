package com.fcorcino.transportroute.ui.routes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fcorcino.transportroute.R;
import com.fcorcino.transportroute.model.Route;
import com.fcorcino.transportroute.ui.ChooseStopsActivity;
import com.fcorcino.transportroute.utils.Constants;

import java.util.ArrayList;

public class RoutesListActivity extends AppCompatActivity {

    /**
     * @var mLoadingIndicatorProgressBar progress bar that shows up to alert the user that something is running in background.
     */
    private ProgressBar mLoadingIndicatorProgressBar;

    /**
     * @var mEmptyRoutesListMessageTextView this text view holds the empty list message.
     */
    private TextView mEmptyRoutesListMessageTextView;

    /**
     * @var mRoutesArrayAdapter the adapter for the route list view.
     */
    private RoutesAdapter mRoutesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
       // new GetRoutesAsyncTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_CHOOSE_STOPS && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK, data);
            finish();
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * This method initializes the UI.
     */
    private void initUI() {
        setContentView(R.layout.activity_routes_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLoadingIndicatorProgressBar = (ProgressBar) findViewById(R.id.loading_indicator_progress_bar);
        mRoutesAdapter = new RoutesAdapter(this, new ArrayList<Route>());
        mEmptyRoutesListMessageTextView = (TextView) findViewById(R.id.empty_routes_list_message_text_view);
        EditText filterRouteEditText = (EditText) findViewById(R.id.filter_route_edit_text);
        filterRouteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mRoutesAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        ListView routesListView = (ListView) findViewById(R.id.routes_list_view);
        routesListView.setAdapter(mRoutesAdapter);
        routesListView.setEmptyView(mEmptyRoutesListMessageTextView);
        routesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Route selectedRoute = (Route) mRoutesAdapter.getItem(position);
                goToChooseStopsActivity(selectedRoute);
            }
        });
    }

    /**
     * This method handles the transition to ChooseStopActivity.
     *
     * @param route the route to display the stops of.
     */
    private void goToChooseStopsActivity(Route route) {
        Intent intent = new Intent(this, ChooseStopsActivity.class);
        intent.putExtra(Constants.ROUTE_ID_KEY, route.getRouteId());
        intent.putExtra(Constants.ROUTE_PRICE_KEY, route.getPrice());
        startActivityForResult(intent, Constants.REQUEST_CODE_CHOOSE_STOPS);
    }

//    /**
//     * This class handles the request to get the routes in a background thread.
//     */
//    private class GetRoutesAsyncTask extends AsyncTask<Void, Void, ArrayList<Route>> {
//
//        @Override
//        protected void onPreExecute() {
//            mLoadingIndicatorProgressBar.setVisibility(View.VISIBLE);
//            mEmptyRoutesListMessageTextView.setVisibility(View.GONE);
//            mRoutesAdapter.clear();
//        }
//
//        @Override
//        protected ArrayList<Route> doInBackground(Void... voids) {
//            return ApiUtils.getRoutes(getBaseContext());
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<Route> routes) {
//            mLoadingIndicatorProgressBar.setVisibility(View.GONE);
//            mEmptyRoutesListMessageTextView.setVisibility(View.VISIBLE);
//
//            if (routes != null && !routes.isEmpty()) {
//                mRoutesAdapter.addAll(routes);
//            }
//        }
//    }
}
