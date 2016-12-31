package com.fcorcino.transportroute.ui.pendingstops;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.leaderapps.transport.model.PendingStop;
import com.leaderapps.transport.model.Stop;
import com.leaderapps.transport.model.Turn;
import com.leaderapps.transport.transportrouteclient.R;
import com.leaderapps.transport.ui.SignInActivity;
import com.leaderapps.transport.utils.ApiUtils;
import com.leaderapps.transport.utils.Constants;
import com.leaderapps.transport.utils.Utils;

import java.util.ArrayList;

public class PendingStopsListActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /**
     * @var SCAN_REQUEST_CODE this holds the scanner activity request code.
     */
    private static final int SCAN_REQUEST_CODE = 49374;

    /**
     * @var SCAN_REQUEST_SUCCESS this holds the scanner activity successful request result.
     */
    private static final int SCAN_REQUEST_SUCCESS = -1;

    /**
     * @var mEmptyListTextView this text view display the empty message when the list is empty.
     */
    private TextView mEmptyListTextView;

    /**
     * @var mLoadingIndicatorProgressBar progress bar that shows up to alert the user that something is running in background.
     */
    private ProgressBar mLoadingIndicatorProgressBar;

    /**
     * @var mPendingStopsArrayAdapter the adapter for the list view.
     */
    private PendingStopsArrayAdapter mPendingStopsArrayAdapter;

    /**
     * @var mGoogleApiClient the google api client to connect to google play services.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * @var mLocationRequest the location request to handle location updated.
     */
    private LocationRequest mLocationRequest;

    /**
     * @var REFRESH_DELAY_TIME this holds the time to re-get the pending stops.
     */
    private static final int REFRESH_DELAY_TIME = 30000;

    /**
     * @var mPendingStopsHandler the handler to kick of the async task.
     */
    private Handler mPendingStopsHandler;

    /**
     * @var mStopsArrayList this holds the stop to pass by.
     */
    private ArrayList<Stop> mStopsArrayList;

    /**
     * @var GEO_FENCE_RANGE the range of the geo fences.
     */
    private static final int GEO_FENCE_RANGE = 100;

    /**
     * @var GEO_FENCE_RANGE the expiration time of the geo fences.
     */
    private static final int GEO_FENCE_EXPIRATION_TIME = 1000 * 60 * 60 * 2;

    private PendingIntent mGeoFencingPendingIntent;

    private ArrayList<Geofence> mGeoFencesArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final String turnId = Utils.getSharedPreference(this, Constants.SHARED_PREF_USER_TURN_ID_KEY);

        if (turnId != null) {
            if (mStopsArrayList == null) {
                new GetStopsByRouteAsyncTask().execute();
            }

            mPendingStopsHandler = new Handler();
            mPendingStopsHandler.post(new Runnable() {
                @Override
                public void run() {
                    new GetPendingStopsByTurnAsyncTask().execute(turnId);
                    mPendingStopsHandler.postDelayed(this, REFRESH_DELAY_TIME);
                }
            });
        }

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();

        if (mPendingStopsHandler != null) {
            mPendingStopsHandler.removeCallbacksAndMessages(null);
        }

        if (mGoogleApiClient != null && mGeoFencingPendingIntent != null) {
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    mGeoFencingPendingIntent
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_map).setVisible(false);
        menu.findItem(R.id.action_release_reservation).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_log_out: {
                logOutUser();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCAN_REQUEST_CODE) {
            if (resultCode == SCAN_REQUEST_SUCCESS) {
                IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (scanResult != null) {
                    String reservationId = scanResult.getContents();
                    new UpdateReservationAsyncTask().execute(reservationId);
                }
            } else {
                Utils.showToast(this, getString(R.string.scan_canceled));
            }
        }
    }

    // GoogleApiClient.ConnectionCallbacks implementation.

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates();

        if (mGeoFencingPendingIntent == null && mStopsArrayList != null) {
            registerGeoFences(mStopsArrayList);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    // GoogleApiClient.OnConnectionFailedListener implementation.

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    // LocationListener implementation.

    @Override
    public void onLocationChanged(Location location) {
        new UpdateTurnLocationAsyncTask().execute(location.getLatitude() + "," + location.getLongitude());
    }

    /**
     * This method starts the location updates.
     */
    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * This method stops the location updates.
     */
    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * This method logs out the user.
     */
    private void logOutUser() {
        Utils.cleanUpPreferences(this);
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * This method initializes the UI.
     */
    private void initUI() {
        setContentView(R.layout.activity_pending_stops);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mEmptyListTextView = (TextView) findViewById(R.id.empty_view);
        mLoadingIndicatorProgressBar = (ProgressBar) findViewById(R.id.loading_indicator_progress_bar);
        mPendingStopsArrayAdapter = new PendingStopsArrayAdapter(this, new ArrayList<PendingStop>());
        ListView mPendingStopsListView = (ListView) findViewById(R.id.pending_stops_list_view);
        mPendingStopsListView.setEmptyView(mEmptyListTextView);
        mPendingStopsListView.setAdapter(mPendingStopsArrayAdapter);
        mPendingStopsListView.setSelector(android.R.color.transparent);
        FloatingActionButton startScannerFAB = (FloatingActionButton) findViewById(R.id.start_scanner_fab);
        startScannerFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToScannerActivity();
            }
        });

        if (Utils.getSharedPreference(this, Constants.SHARED_PREF_USER_TURN_ID_KEY) == null) {
            new GetTurnByDriverAsyncTask().execute();
        }
    }

    /**
     * This method initializes the BE.
     */
    private void init() {
        setUpGoogleApiClient();
        setUpLocationRequest();
    }

    /**
     * This method sets up the GoogleApiClient.
     */
    private void setUpGoogleApiClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    /**
     * This method sets up the LocationRequest.
     */
    private void setUpLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * This method handles the transition to the scanner activity.
     */
    private void goToScannerActivity() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.addExtra(Constants.SCANNER_SCAN_WIDTH_KEY, 800);
        integrator.addExtra(Constants.SCANNER_SCAN_HEIGHT_KEY, 800);
        integrator.addExtra(Constants.SCANNER_PROMPT_MESSAGE_KEY, getString(R.string.scan_activity_prompt));
        integrator.initiateScan();
    }

    /**
     * This method register the geo fences.
     *
     * @param stops the stops to be registered as geo fences.
     */
    private void registerGeoFences(ArrayList<Stop> stops) {
        mGeoFencesArrayList.clear();

        for (Stop stop : stops) {
            String[] locationArray = stop.getLocation().split(",");
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(stop.getStopId())
                    .setCircularRegion(
                            Double.valueOf(locationArray[0]),
                            Double.valueOf(locationArray[1]),
                            GEO_FENCE_RANGE)
                    .setExpirationDuration(GEO_FENCE_EXPIRATION_TIME)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .build();

            mGeoFencesArrayList.add(geofence);
        }

        GeofencingRequest geofencingRequest = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofences(mGeoFencesArrayList)
                .build();

        mGeoFencingPendingIntent = getGeoFencingPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        LocationServices.GeofencingApi.addGeofences(
                mGoogleApiClient,
                geofencingRequest,
                mGeoFencingPendingIntent
        );
    }

    /**
     * This method gets the pending intent that will handle the geo fence alerts.
     *
     * @return
     */
    private PendingIntent getGeoFencingPendingIntent() {
        if (mGeoFencingPendingIntent != null) {
            return mGeoFencingPendingIntent;
        }

        Intent intent = new Intent(this, GeoFenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This class handles the request to get the pending stops by turn in a background thread.
     */
    private class GetPendingStopsByTurnAsyncTask extends AsyncTask<String, Void, ArrayList<PendingStop>> {

        @Override
        protected void onPreExecute() {
            mLoadingIndicatorProgressBar.setVisibility(View.VISIBLE);
            mPendingStopsArrayAdapter.clear();
        }

        @Override
        protected ArrayList<PendingStop> doInBackground(String... params) {
            String turnId = params[0];
            return ApiUtils.getPendingStopsByTurn(getBaseContext(), turnId);
        }

        @Override
        protected void onPostExecute(ArrayList<PendingStop> pendingStops) {
            if (pendingStops != null && !pendingStops.isEmpty()) {
                mPendingStopsArrayAdapter.addAll(pendingStops);
            }

            mLoadingIndicatorProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * This class handles the request to get the turn by driver in a background thread.
     */
    private class GetTurnByDriverAsyncTask extends AsyncTask<Void, Void, Turn> {

        @Override
        protected void onPreExecute() {
            mLoadingIndicatorProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Turn doInBackground(Void... voids) {
            String driverId = Utils.getSharedPreference(getApplicationContext(), Constants.SHARED_PREF_USER_ID_KEY);
            return ApiUtils.getTurnByDriver(getBaseContext(), driverId);
        }

        @Override
        protected void onPostExecute(Turn turn) {
            if (turn != null) {
                Utils.setSharedPreference(
                        PendingStopsListActivity.this,
                        Constants.SHARED_PREF_USER_TURN_ID_KEY,
                        String.valueOf(turn.getTurnId()));

                Utils.setSharedPreference(
                        PendingStopsListActivity.this,
                        Constants.SHARED_PREF_ROUTE_ID_KEY,
                        String.valueOf(turn.getRouteId()));

                new GetPendingStopsByTurnAsyncTask().execute(turn.getTurnId());

                if (mStopsArrayList == null) {
                    new GetStopsByRouteAsyncTask().execute();
                }
            }

            mLoadingIndicatorProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * This class handles the request to update the turn location in a background thread.
     */
    private class UpdateTurnLocationAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String location = params[0];
            String turnId = Utils.getSharedPreference(getApplicationContext(), Constants.SHARED_PREF_USER_TURN_ID_KEY);
            ApiUtils.updateTurnLocation(getBaseContext(), turnId, location);
            return null;
        }
    }

    /**
     * This class handles the request to update the reservation in a background thread.
     */
    private class UpdateReservationAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String reservationId = params[0];
            return ApiUtils.updateReservation(getBaseContext(), reservationId, Constants.STATUS_VALUE_PICK_UP);
        }

        @Override
        protected void onPostExecute(Boolean isUpdated) {
            if (isUpdated) {
                Utils.showToast(getApplicationContext(), getString(R.string.valid_reservation_message));
            } else {
                Utils.showToast(getApplicationContext(), getString(R.string.invalid_reservation_message));
            }
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
            String routeId = Utils.getSharedPreference(getBaseContext(), Constants.SHARED_PREF_ROUTE_ID_KEY);
            return ApiUtils.getStopsByRoute(getBaseContext(), routeId);
        }

        @Override
        protected void onPostExecute(ArrayList<Stop> stops) {
            if (stops != null && !stops.isEmpty()) {
                mStopsArrayList = stops;

                if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                    registerGeoFences(mStopsArrayList);
                }
            }
        }
    }
}
