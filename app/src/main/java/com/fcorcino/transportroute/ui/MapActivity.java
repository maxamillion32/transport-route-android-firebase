package com.fcorcino.transportroute.ui;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.Toolbar;

import com.fcorcino.transportroute.R;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.fcorcino.transportroute.model.Turn;
import com.fcorcino.transportroute.utils.Constants;

public class MapActivity extends BaseActivity implements OnMapReadyCallback {

    /**
     * @var mMapFragment the map fragment.
     */
    private SupportMapFragment mMapFragment;

    /**
     * @var mGoogleMap the map object.
     */
    private GoogleMap mGoogleMap;

    /**
     * @var UPDATES_DELAY_TIME this holds the time to re-get the turn location.
     */
    private static final int UPDATES_DELAY_TIME = 5000;

    /**
     * @var mLocationHandler the handler to kick of the async task.
     */
    private Handler mLocationHandler;

    /**
     * @var mBusMarker this shows the marker of the bus.
     */
    private Marker mBusMarker;

    /**
     * @var mUserMarker this shows the marker of the user.
     */
    private Marker mUserMarker;

    /**
     * @var firstTimeRendered holds if is the first time rendered.
     */
    private boolean firstTimeRendered = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mLocationHandler != null) {
            mLocationHandler.removeCallbacksAndMessages(null);
        }
    }

    // OnMapReadyCallback implementation.

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mLocationHandler = new Handler();
        mLocationHandler.post(new Runnable() {
            @Override
            public void run() {
               // new GetTurnAsyncTask().execute();

                mLocationHandler.postDelayed(this, UPDATES_DELAY_TIME);
            }
        });

        Location userLocation = getIntent().getParcelableExtra(Constants.LAST_KNOWN_LOCATION_KEY);

        if (userLocation != null) {
            renderUserMarker(userLocation.getLatitude(), userLocation.getLongitude());
        }
    }

    /**
     * This method renders the user marker on the map.
     *
     * @param latitude  the latitude to place the marker.
     * @param longitude the longitude to place the marker.
     */
    private void renderUserMarker(double latitude, double longitude) {
        renderMarker(latitude, longitude, null, null, R.drawable.ic_user_marker, mUserMarker);
    }

    /**
     * This method renders the bus marker on the map.
     *
     * @param turn the turn that is being displayed..
     */
    private void renderBusMarker(Turn turn) {
        String[] locationArray = turn.getLocation().split(",");
        String lastStopName = turn.getLastStopName();
        renderMarker(
                Double.valueOf(locationArray[0]),
                Double.valueOf(locationArray[1]),
                lastStopName,
                String.valueOf(turn.getDistance()),
                R.drawable.ic_bus_marker,
                mBusMarker);
    }

    /**
     * This method renders a marker with a custom icon on the map.
     *
     * @param latitude       the latitude to place the marker.
     * @param longitude      the longitude to place the marker.
     * @param titleMessage   the message to be displayed in the title.
     * @param snippetMessage the message to be displayed in the snippet.
     * @param iconResource   the icon resource to use with the marker.
     * @param marker         the marker object to hold the reference of the new marker.
     */
    private void renderMarker(double latitude, double longitude, String titleMessage, String snippetMessage, int iconResource, Marker marker) {
        LatLng busLatLng = new LatLng(latitude, longitude);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(10);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(busLatLng)
                .title(getString(R.string.reservation_marker_title_message, titleMessage))
                .icon(BitmapDescriptorFactory.fromResource(iconResource));

        if (snippetMessage != null)
            markerOptions.snippet(getString(R.string.reservation_marker_snippet_message, snippetMessage));

        if (marker != null) marker.remove();

        marker = mGoogleMap.addMarker(markerOptions);

        if (titleMessage != null) marker.showInfoWindow();

        if (firstTimeRendered) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(busLatLng);
            mGoogleMap.moveCamera(cameraUpdate);
            mGoogleMap.animateCamera(zoom);
            firstTimeRendered = false;
        }
    }

    /**
     * This method initializes the UI.
     */

    private void initUI() {
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mMapFragment.getMapAsync(this);
    }

    /**
     * This method creates a LatLng object from a string object.
     *
     * @param latLngString the string representing the latitude and longitude coordinates separated by comma.
     * @return a LatLng object.
     */
    private LatLng getLatLngFromString(String latLngString) {
        return new LatLng(Double.valueOf(latLngString.split(",")[0]), Double.valueOf(latLngString.split(",")[1]));
    }

//    /**
//     * This class handles to get the turn in a background thread.
//     */
//    private class GetTurnAsyncTask extends AsyncTask<Void, Void, Turn> {
//
//        @Override
//        protected Turn doInBackground(Void... voids) {
//            String turnId = Utils.getSharedPreference(getApplicationContext(), Constants.SHARED_PREF_USER_TURN_ID_KEY);
//            return ApiUtils.getTurn(getBaseContext(), turnId);
//        }
//
//        @Override
//        protected void onPostExecute(Turn turn) {
//            if (turn != null) {
//                if (firstTimeRendered) new GetStopsByRouteAsyncTask().execute();
//                renderBusMarker(turn);
//                Utils.setSharedPreference(getApplicationContext(), Constants.SHARED_PREF_ROUTE_ID_KEY, turn.getRouteId());
//            } else {
//                Utils.showToast(getBaseContext(), getString(R.string.unknown_turn_location));
//            }
//        }
//    }
//
//    /**
//     * This class handles the request to get the stops by a route in a background thread.
//     */
//    private class GetStopsByRouteAsyncTask extends AsyncTask<Void, Void, ArrayList<Stop>> {
//
//        @Override
//        protected ArrayList<Stop> doInBackground(Void... params) {
//            String routeId = Utils.getSharedPreference(getApplicationContext(), Constants.SHARED_PREF_ROUTE_ID_KEY);
//            return ApiUtils.getStopsByRoute(getBaseContext(), routeId);
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<Stop> stops) {
//            if (stops != null && !stops.isEmpty()) {
//                PolylineOptions polylineOptions = new PolylineOptions();
//                ArrayList<LatLng> stopsLatLngs = new ArrayList<>();
//
//                for (Stop stop : stops) {
//                    MarkerOptions markerOptions = new MarkerOptions()
//                            .position(getLatLngFromString(stop.getLocation()))
//                            .title(getString(R.string.stop_name_marker_title, stop.getName()));
//                    mGoogleMap.addMarker(markerOptions);
//                    stopsLatLngs.add(getLatLngFromString(stop.getLocation()));
//                }
//
//                polylineOptions.addAll(stopsLatLngs);
//
//                mGoogleMap.addPolyline(polylineOptions);
//            } else {
//                Utils.showToast(getBaseContext(), getString(R.string.unable_to_draw_route_message));
//            }
//        }
//    }
}
