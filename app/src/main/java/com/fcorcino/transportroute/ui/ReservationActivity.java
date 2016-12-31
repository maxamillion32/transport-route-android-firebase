package com.fcorcino.transportroute.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.leaderapps.transport.model.Reservation;
import com.leaderapps.transport.transportrouteclient.R;
import com.leaderapps.transport.ui.routes.RoutesListActivity;
import com.leaderapps.transport.utils.ApiUtils;
import com.leaderapps.transport.utils.BarcodeUtils;
import com.leaderapps.transport.utils.Constants;
import com.leaderapps.transport.utils.Utils;

public class ReservationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    /**
     * @var mLoadingIndicatorProgressBar progress bar that shows up to alert the user that something is running in background.
     */
    private ProgressBar mLoadingIndicatorProgressBar;

    /**
     * @var mIsReservationActive holds whether or not there is an active reservation.
     */
    private boolean mIsReservationActive = false;

    /**
     * @var mGoogleApiClient google services client.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * @var mLastLocation holds the last location known.
     */
    private Location mLastLocation;

    /**
     * @var mNoReservationMessageTextView text view to display a message when there is no reservation.
     */
    private TextView mNoReservationMessageTextView;

    /**
     * @var mReservationDetailLinearLayout the container for the reservation details.
     */
    private LinearLayout mReservationDetailLinearLayout;

    /**
     * @var mReservationTitleTextView this text view holds the reservation title.
     */
    private TextView mReservationTitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        setUpGoogleApiClient();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        String userId = Utils.getSharedPreference(this, Constants.SHARED_PREF_USER_ID_KEY);
        new GetCurrentReservationAsyncTask().execute(userId);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_preferences).setVisible(false);

        if (mIsReservationActive) {
            menu.findItem(R.id.action_map).setVisible(true);
        } else {
            menu.findItem(R.id.action_release_reservation).setVisible(false);
        }

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
            case R.id.action_map: {
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra(Constants.LAST_KNOWN_LOCATION_KEY, mLastLocation);
                startActivity(intent);
                return true;
            }
            case R.id.action_release_reservation: {
                String reservationId = Utils.getSharedPreference(getApplicationContext(), Constants.SHARED_PREF_RESERVATION_ID_KEY);
                new UpdateReservationAsyncTask().execute(reservationId);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CODE_SELECT_ROUTE && resultCode == Activity.RESULT_OK) {
            Reservation reservation = (Reservation) data.getBundleExtra(Constants.ROUTE_RESERVATION_KEY).getSerializable(Constants.ROUTE_RESERVATION_KEY);
            new MakeReservationAsyncTask().execute(reservation);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    // GoogleApiClient.ConnectionCallbacks implementation

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    // GoogleApiClient.OnConnectionFailedListener implementation

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    /**
     * This method sets up the google client object.
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
        setContentView(R.layout.activity_reservation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mReservationDetailLinearLayout = (LinearLayout) findViewById(R.id.reservation_detail_container);
        mNoReservationMessageTextView = (TextView) findViewById(R.id.no_reservation_message_text_view);
        mLoadingIndicatorProgressBar = (ProgressBar) findViewById(R.id.loading_indicator_progress_bar);
        mReservationTitleTextView = (TextView) findViewById(R.id.reservation_title_text_view);
        FloatingActionButton createReservationFAB = (FloatingActionButton) findViewById(R.id.create_reservation_fab);
        createReservationFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLastLocation != null) {
                    goToRoutesList();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.still_getting_location_text), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * This method handles the transition to RoutesListActivity.
     */
    private void goToRoutesList() {
        Intent intent = new Intent(this, RoutesListActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CODE_SELECT_ROUTE);
    }

    /**
     * This method renders the barcode.
     */
    private void renderBarCode(Reservation reservation) {
        Bitmap barcodeBitmap = BarcodeUtils.generateBarCode(reservation.getReservationId());
        ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
        barcodeImageView.setImageBitmap(barcodeBitmap);
        mIsReservationActive = true;
        TextView reservationTitleTextView = (TextView) findViewById(R.id.reservation_title_text_view);
        reservationTitleTextView.setVisibility(View.VISIBLE);
        mNoReservationMessageTextView.setVisibility(View.GONE);
        invalidateOptionsMenu();
    }

    /**
     * This method clears the barcode image from the screen.
     */
    private void clearBarcode() {
        mIsReservationActive = false;
        TextView reservationTitleTextView = (TextView) findViewById(R.id.reservation_title_text_view);
        reservationTitleTextView.setVisibility(View.GONE);
        mNoReservationMessageTextView.setVisibility(View.VISIBLE);
        invalidateOptionsMenu();
        ImageView barcodeImageView = (ImageView) findViewById(R.id.barcode_image_view);
        barcodeImageView.setImageDrawable(null);
        mReservationDetailLinearLayout.setVisibility(View.GONE);
    }

    /**
     * This method renders the reservation details.
     */
    private void renderReservationDetail(Reservation reservation) {
        mReservationDetailLinearLayout.setVisibility(View.VISIBLE);
        TextView reservationIdTextView = (TextView) mReservationDetailLinearLayout.findViewById(R.id.reservation_id_text_view);
        TextView quantityOfPersonTextView = (TextView) mReservationDetailLinearLayout.findViewById(R.id.quantity_of_person_text_view);
        TextView routePriceTextView = (TextView) mReservationDetailLinearLayout.findViewById(R.id.route_price_text_view);
        TextView reservationAmountTextView = (TextView) mReservationDetailLinearLayout.findViewById(R.id.reservation_amount_text_view);
        String peopleLabel = reservation.getQuantityOfPerson() > 1 ? getString(R.string.people_label) : getString(R.string.person_label);

        reservationIdTextView.setText(reservation.getReservationId());
        quantityOfPersonTextView.setText(String.format("%d %s", reservation.getQuantityOfPerson(), peopleLabel));
        routePriceTextView.setText(String.format("$%,.2f", reservation.getPrice()));
        reservationAmountTextView.setText(String.format("$%,.2f", reservation.getAmount()));
    }


    /**
     * This class handles the request to the make a reservation in a background thread.
     */
    private class MakeReservationAsyncTask extends AsyncTask<Reservation, Void, Reservation> {

        @Override
        protected void onPreExecute() {
            mLoadingIndicatorProgressBar.setVisibility(View.VISIBLE);
            mNoReservationMessageTextView.setVisibility(View.GONE);
        }

        @Override
        protected Reservation doInBackground(Reservation... params) {
            Reservation incompleteReservation = params[0];
            return ApiUtils.makeReservation(
                    getBaseContext(),
                    incompleteReservation,
                    mLastLocation.getLatitude(),
                    mLastLocation.getLongitude(),
                    Utils.getSharedPreference(getBaseContext(), Constants.SHARED_PREF_ROUTE_ID_KEY));
        }

        @Override
        protected void onPostExecute(Reservation reservation) {
            mLoadingIndicatorProgressBar.setVisibility(View.GONE);
            mNoReservationMessageTextView.setVisibility(View.VISIBLE);

            if (reservation != null) {
                renderBarCode(reservation);
            } else {
                Utils.showToast(getBaseContext(), getString(R.string.no_turn_available));
            }
        }
    }

    /**
     * This class handles the request to get the current reservation in a background thread.
     */
    private class GetCurrentReservationAsyncTask extends AsyncTask<String, Void, Reservation> {

        @Override
        protected void onPreExecute() {
            mLoadingIndicatorProgressBar.setVisibility(View.VISIBLE);
            mNoReservationMessageTextView.setVisibility(View.GONE);
            mReservationTitleTextView.setVisibility(View.GONE);
        }

        @Override
        protected Reservation doInBackground(String... params) {
            String userId = params[0];
            return ApiUtils.getCurrentReservation(getBaseContext(), userId);
        }

        @Override
        protected void onPostExecute(Reservation reservation) {
            mLoadingIndicatorProgressBar.setVisibility(View.GONE);

            if (reservation != null) {
                Utils.setSharedPreference(getApplicationContext(), Constants.SHARED_PREF_RESERVATION_ID_KEY, reservation.getReservationId());
                Utils.setSharedPreference(getApplicationContext(), Constants.SHARED_PREF_USER_TURN_ID_KEY, reservation.getTurnId());
                renderBarCode(reservation);
                renderReservationDetail(reservation);
            } else {
                clearBarcode();
                mNoReservationMessageTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * This class handles the request to update the reservation in a background thread.
     */
    private class UpdateReservationAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String reservationId = params[0];
            return ApiUtils.updateReservation(getBaseContext(), reservationId, Constants.STATUS_VALUE_DELIVERED);
        }

        @Override
        protected void onPostExecute(Boolean isUpdated) {
            if (isUpdated) {
                String userId = Utils.getSharedPreference(ReservationActivity.this, Constants.SHARED_PREF_USER_ID_KEY);
                new GetCurrentReservationAsyncTask().execute(userId);
                Utils.showToast(getBaseContext(), getString(R.string.reservation_released_message));
            } else {
                Utils.showToast(getBaseContext(), getString(R.string.invalid_reservation_message));
            }
        }
    }
}
