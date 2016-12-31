package com.fcorcino.transportroute.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.leaderapps.transport.model.Reservation;
import com.leaderapps.transport.model.Stop;
import com.leaderapps.transport.transportrouteclient.R;
import com.leaderapps.transport.utils.ApiUtils;
import com.leaderapps.transport.utils.Constants;
import com.leaderapps.transport.utils.Utils;
import com.leaderapps.transport.utils.ViewUtils;

import java.util.ArrayList;

public class ChooseStopsActivity extends AppCompatActivity {

    /**
     * @var mLoadingIndicatorProgressBar progress bar that shows up to alert the user that something is running in background.
     */
    private ProgressBar mLoadingIndicatorProgressBar;

    /**
     * @var mRouteId the route id to display the stops.
     */
    private String mRouteId;

    /**
     * @var mRouteFromSpinner the spinner to show the from route.
     */
    private Spinner mRouteFromSpinner;

    /**
     * @var mRouteToSpinner the spinner to show the to route.
     */
    private Spinner mRouteToSpinner;

    /**
     * @var mStopsArrayList this holds the stops list.
     */
    private ArrayList<Stop> mStopsArrayList = new ArrayList<>();

    /**
     * @var mFilteredStopsArrayList this holds the filtered stops.
     */
    private ArrayList<Stop> mFilteredStopsArrayList = new ArrayList<>();

    /**
     * @var stopNameArrayAdapter the adapter for the spinner.
     */
    private ArrayAdapter<String> stopNameArrayAdapter;

    /**
     * @var mStopsNameArrayList this holds the stop names.
     */
    private ArrayList<String> mStopsNameArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetStopsByRouteAsyncTask().execute(mRouteId);
    }

    /**
     * This method initializes the UI.
     */
    private void initUI() {
        setContentView(R.layout.activity_choose_stops);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLoadingIndicatorProgressBar = (ProgressBar) findViewById(R.id.loading_indicator_progress_bar);
        mRouteId = getIntent().getExtras().getString(Constants.ROUTE_ID_KEY);
        Button makeReservationButton = (Button) findViewById(R.id.make_reservation_button);
        makeReservationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeReservation();
            }
        });
    }

    /**
     * This method attempts to make a reservation.
     */
    private void makeReservation() {
        EditText quantityOfPersonEditText = (EditText) findViewById(R.id.quantity_of_person_edit_text);

        if (ViewUtils.validateEditText(quantityOfPersonEditText, getString(R.string.require_field_error_message))) {
            int quantityOfPerson = Integer.valueOf(quantityOfPersonEditText.getText().toString().trim());
            String routeFromStopId = mStopsArrayList.get(mRouteFromSpinner.getSelectedItemPosition()).getStopId();
            String routeToStopId = mFilteredStopsArrayList.get(mRouteToSpinner.getSelectedItemPosition()).getStopId();
            float routePrice = getIntent().getFloatExtra(Constants.ROUTE_PRICE_KEY, 0);
            String userId = Utils.getSharedPreference(this, Constants.SHARED_PREF_USER_ID_KEY);

            Utils.setSharedPreference(getBaseContext(), Constants.SHARED_PREF_ROUTE_ID_KEY, mRouteId);
            Reservation reservation = new Reservation();
            reservation.setStopFromId(routeFromStopId);
            reservation.setStopToId(routeToStopId);
            reservation.setQuantityOfPerson(quantityOfPerson);
            reservation.setPrice(routePrice);
            reservation.setAmount(quantityOfPerson * routePrice);
            reservation.setUserId(userId);
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.ROUTE_RESERVATION_KEY, reservation);
            intent.putExtra(Constants.ROUTE_RESERVATION_KEY, bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    /**
     * This class handles the request to get the stops by a route in a background thread.
     */
    private class GetStopsByRouteAsyncTask extends AsyncTask<String, Void, ArrayList<Stop>> {

        @Override
        protected void onPreExecute() {
            mLoadingIndicatorProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Stop> doInBackground(String... params) {
            String routeId = params[0];
            return ApiUtils.getStopsByRoute(getBaseContext(), routeId);
        }

        @Override
        protected void onPostExecute(ArrayList<Stop> stops) {
            mLoadingIndicatorProgressBar.setVisibility(View.GONE);

            if (stops != null && !stops.isEmpty()) {
                mStopsArrayList.addAll(stops);

                for (Stop stop : mStopsArrayList) {
                    mStopsNameArrayList.add(stop.getName());
                }

                stopNameArrayAdapter = new ArrayAdapter<>(ChooseStopsActivity.this, android.R.layout.simple_spinner_dropdown_item, mStopsNameArrayList);
                stopNameArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mRouteFromSpinner = (Spinner) findViewById(R.id.route_from_spinner);
                mRouteFromSpinner.setAdapter(stopNameArrayAdapter);
                mRouteFromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        ArrayList<String> filteredStopsArrayList = new ArrayList<>();
                        Stop selectedStop = mStopsArrayList.get(position);
                        int arrayLength = mStopsArrayList.size() - 1;

                        for (int index = 0; index <= arrayLength; index++) {
                            Stop stop = mStopsArrayList.get(index);

                            if (stop.getSequence() > selectedStop.getSequence() || index == arrayLength) {
                                mFilteredStopsArrayList.add(stop);
                                filteredStopsArrayList.add(stop.getName());
                            }
                        }

                        mRouteToSpinner = (Spinner) findViewById(R.id.route_to_spinner);
                        ArrayAdapter<String> stopNameArrayAdapter = new ArrayAdapter<>(ChooseStopsActivity.this, android.R.layout.simple_spinner_dropdown_item, filteredStopsArrayList);
                        stopNameArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        mRouteToSpinner.setAdapter(stopNameArrayAdapter);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
        }
    }
}
