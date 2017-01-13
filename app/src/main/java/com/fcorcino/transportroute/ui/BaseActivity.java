package com.fcorcino.transportroute.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BaseActivity extends AppCompatActivity {

    /**
     * Holds the firebase authentication object.
     */
    protected FirebaseAuth mFirebaseAuth;

    /**
     * Holds the firebase authentication state listener.
     */
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (!(BaseActivity.this instanceof SignInActivity) && !(BaseActivity.this instanceof SignUpActivity) && user == null){
                    takeUserToSignInActivity();
                } else if ((BaseActivity.this instanceof SignInActivity) && !(BaseActivity.this instanceof SignUpActivity) && user != null) {
                    Intent intent = new Intent(BaseActivity.this, ReservationActivity.class);
                    startActivity(intent);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    /**
     * Takes the user to the sign in activity.
     */
    private void takeUserToSignInActivity() {
        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
        finish();
    }
}
