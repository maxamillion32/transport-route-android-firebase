package com.fcorcino.transportroute.ui.pendingstops;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.leaderapps.transport.utils.ApiUtils;
import com.leaderapps.transport.utils.Constants;
import com.leaderapps.transport.utils.Utils;

import java.util.List;

public class GeoFenceTransitionsIntentService extends IntentService {

    /**
     * @var TAG the tag to log messages.
     */
    private static final String TAG = GeoFenceTransitionsIntentService.class.getSimpleName();

    /**
     * Class constructor.
     */
    public GeoFenceTransitionsIntentService() {
        super("GeoFenceTransitionsIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if (geofencingEvent.hasError()) {
            String errorMessage = geofencingEvent.toString() + " " + geofencingEvent.getErrorCode();
            Log.e(TAG, errorMessage);
            return;
        }

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();

        if (geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            String turnId = Utils.getSharedPreference(getBaseContext(), Constants.SHARED_PREF_USER_TURN_ID_KEY);
            List triggeringGeoFences = geofencingEvent.getTriggeringGeofences();

            for (Object object : triggeringGeoFences) {
                Geofence geofence = (Geofence) object;
                String stopId = geofence.getRequestId();
                ApiUtils.updateTurnLastStop(getBaseContext(), turnId, stopId);
            }
        }
    }
}
