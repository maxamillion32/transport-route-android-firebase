package com.fcorcino.transportroute;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

public class TransportRoute extends Application {
    private static FirebaseDatabase sFirebaseDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        sFirebaseDatabase = FirebaseDatabase.getInstance();
    }

    /**
     * Gets a singleton reference of the firebase database.
     *
     * @return A reference to the firebase database.
     */
    public static FirebaseDatabase getFirebaseDatabase() {
        if (sFirebaseDatabase == null) {
            sFirebaseDatabase = FirebaseDatabase.getInstance();
        }

        return sFirebaseDatabase;
    }
}
