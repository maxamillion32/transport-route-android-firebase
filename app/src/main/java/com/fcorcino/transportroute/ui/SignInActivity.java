package com.fcorcino.transportroute.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.leaderapps.transport.model.User;
import com.leaderapps.transport.transportrouteclient.R;
import com.leaderapps.transport.ui.pendingstops.PendingStopsListActivity;
import com.leaderapps.transport.utils.ApiUtils;
import com.leaderapps.transport.utils.Constants;
import com.leaderapps.transport.utils.Utils;
import com.leaderapps.transport.utils.ViewUtils;

public class SignInActivity extends AppCompatActivity {

    /**
     * @var mUserNameEditText The edit text to type the user name to log in.
     */
    private EditText mUserNameEditText;

    /**
     * @var mPasswordEditText The edit text to type the user password to log in.
     */
    private EditText mPasswordEditText;

    /**
     * @var mLogInControlsContainer The parent container of all the views.
     */
    private LinearLayout mLogInControlsContainer;

    /**
     * @var mLoadingIndicatorProgressBar The progress bar that shows up to alert the user that something is running in background.
     */
    private ProgressBar mLoadingIndicatorProgressBar;

    /**
     * @var mLogInButton The button to kick off the log in process.
     */
    private Button mLogInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        validateUser();
        initUI();
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
        menu.findItem(R.id.action_log_out).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_preferences) {
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Verifies the if the user is already logged in.
     */
    private void validateUser() {
        if (Utils.getSharedPreference(this, Constants.SHARED_PREF_USER_ID_KEY) != null) {
            goToHomeScreen(Utils.getSharedPreference(this, Constants.SHARED_PREF_USER_TYPE_KEY));
            finish();
        }
    }

    /**
     * Initializes the UI.
     */
    private void initUI() {
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mUserNameEditText = (EditText) findViewById(R.id.user_name_edit_text);
        mUserNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus)
                    new ValidateUsernameAsyncTask().execute(ViewUtils.getTextFromEditText(mUserNameEditText));
            }
        });

        mPasswordEditText = (EditText) findViewById(R.id.password_edit_text);
        mPasswordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                Utils.hideKeyboard(SignInActivity.this);
                singInUser();
                return false;
            }
        });

        mLogInControlsContainer = (LinearLayout) findViewById(R.id.log_in_controls_container);
        mLoadingIndicatorProgressBar = (ProgressBar) findViewById(R.id.loading_indicator_progress_bar);
        mLogInButton = (Button) findViewById(R.id.log_in_button);
        mLogInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singInUser();
            }
        });

        TextView createUserTextView = (TextView) findViewById(R.id.create_user_text_view);
        createUserTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToSignUp();
            }
        });
    }

    /**
     * Takes the user to the create user activity.
     */
    private void goToSignUp() {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    /**
     * Takes the user to the home activity.
     */
    private void goToHomeScreen(String userType) {
        if (userType.equals(String.valueOf(Constants.USER_TYPE_USER))) {
            Intent intent = new Intent(SignInActivity.this, ReservationActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(SignInActivity.this, PendingStopsListActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Attempts to log in an user.
     */
    private void singInUser() {
        boolean areFieldsValid = true;
        String errorMessage = getString(R.string.require_field_error_message);
        EditText[] editTexts = {
                mUserNameEditText,
                mPasswordEditText,
        };

        for (EditText editText : editTexts) {
            areFieldsValid = ViewUtils.validateEditText(editText, errorMessage);
        }

        if (areFieldsValid) new LogInUserAsyncTask().execute(
                ViewUtils.getTextFromEditText(mUserNameEditText),
                ViewUtils.getTextFromEditText(mPasswordEditText));
    }

    /**
     * Handles the log in of an user in a background thread.
     */
    private class LogInUserAsyncTask extends AsyncTask<String, Void, User> {

        @Override
        protected void onPreExecute() {
            mLogInControlsContainer.setVisibility(View.GONE);
            mLoadingIndicatorProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected User doInBackground(String... params) {
            String userName = params[0];
            String password = params[1];
            return ApiUtils.logInUser(getBaseContext(), userName, password);
        }

        @Override
        protected void onPostExecute(User user) {
            if (user != null) {
                Utils.setSharedPreference(getApplicationContext(), Constants.SHARED_PREF_USER_ID_KEY, user.getUserId());
                Utils.setSharedPreference(getApplicationContext(), Constants.SHARED_PREF_USER_NAME_KEY, user.getName());
                Utils.setSharedPreference(getApplicationContext(), Constants.SHARED_PREF_USER_TYPE_KEY, user.getUserTypeId());
                goToHomeScreen(user.getUserTypeId());
                finish();
            } else {
                Utils.showToast(getBaseContext(), SignInActivity.this.getString(R.string.invalid_log_in_message));
            }

            mLogInControlsContainer.setVisibility(View.VISIBLE);
            mLoadingIndicatorProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Handles the request to validate the username in a background thread.
     */
    private class ValidateUsernameAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String userName = params[0];
            return ApiUtils.validateUsername(getBaseContext(), userName);
        }

        @Override
        protected void onPostExecute(Boolean valid) {
            TextInputLayout textInputLayout = (TextInputLayout) mUserNameEditText.getParent();

            if (!valid) {
                textInputLayout.setErrorEnabled(true);
                textInputLayout.setError(getString(R.string.user_already_taken));
                mLogInButton.setEnabled(false);
            } else {
                mLogInButton.setEnabled(true);
                textInputLayout.setErrorEnabled(false);
            }
        }
    }
}
