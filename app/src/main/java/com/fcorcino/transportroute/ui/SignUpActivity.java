package com.fcorcino.transportroute.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.leaderapps.transport.model.User;
import com.leaderapps.transport.transportrouteclient.R;
import com.leaderapps.transport.utils.ApiUtils;
import com.leaderapps.transport.utils.Constants;
import com.leaderapps.transport.utils.Utils;
import com.leaderapps.transport.utils.ViewUtils;

public class SignUpActivity extends AppCompatActivity {

    /**
     * @var mNameEditText the edit text to type the name to be crated.
     */
    private EditText mNameEditText;

    /**
     * @var mUserNameEditText the edit text to type the user name to be created.
     */
    private EditText mUserNameEditText;

    /**
     * @var mPasswordEditText the edit text to type the user password to be created.
     */
    private EditText mPasswordEditText;

    /**
     * @var mCreditCardNumberEditText the edit text to type the user credit card number to be created.
     */
    private EditText mCreditCardNumberEditText;

    /**
     * @var mLoadingIndicatorProgressBar progress bar that shows up to alert the user that something is running in background.
     */
    private ProgressBar mLoadingIndicatorProgressBar;

    /**
     * @var mFormFieldContainer the parent container of all the views.
     */
    private LinearLayout mFormFieldContainer;

    /**
     * @var mContext the context to be reused in the entire class.
     */
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        mContext = getApplicationContext();
    }

    /**
     * This method initializes the UI.
     */
    private void initUI() {
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mNameEditText = (EditText) findViewById(R.id.name_edit_text);
        mUserNameEditText = (EditText) findViewById(R.id.user_name_edit_text);
        mPasswordEditText = (EditText) findViewById(R.id.password_edit_text);
        mCreditCardNumberEditText = (EditText) findViewById(R.id.credit_card_number_edit_text);
        mLoadingIndicatorProgressBar = (ProgressBar) findViewById(R.id.loading_indicator_progress_bar);
        mFormFieldContainer = (LinearLayout) findViewById(R.id.form_fields_container);
        Button signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser();
            }
        });
    }

    /**
     * This method attempts to create an user.
     */
    private void createUser() {
        boolean areFieldsValid = true;
        String errorMessage = getString(R.string.require_field_error_message);

        EditText[] editTexts = {
                mNameEditText,
                mUserNameEditText,
                mPasswordEditText,
                mCreditCardNumberEditText,
        };

        for (EditText editText : editTexts) {
            areFieldsValid = ViewUtils.validateEditText(editText, errorMessage);
        }

        if (areFieldsValid) new SignUpUserAsyncTask().execute(prepareUser());
    }

    /**
     * This method creates an User with the data input to the fields.
     *
     * @return an User with the data provided in the fields..
     */
    private User prepareUser() {
        User user = new User();
        user.setName(mNameEditText.getText().toString().trim());
        user.setUserName(mUserNameEditText.getText().toString().trim());
        user.setPassword(mPasswordEditText.getText().toString().trim());
        user.setStatus(Constants.STATUS_VALUE_ACTIVE);
        user.setUserTypeId(Constants.USER_TYPE_USER);
        user.setCreditCard(mCreditCardNumberEditText.getText().toString().trim());

        return user;
    }

    /**
     * This class handles the request to the server to create an user in a background thread.
     */
    private class SignUpUserAsyncTask extends AsyncTask<User, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            mFormFieldContainer.setVisibility(View.GONE);
            mLoadingIndicatorProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(User... users) {
            User user = users[0];
            return ApiUtils.createUser(getBaseContext(), user);
        }

        @Override
        protected void onPostExecute(Boolean isInserted) {
            mFormFieldContainer.setVisibility(View.VISIBLE);
            mLoadingIndicatorProgressBar.setVisibility(View.GONE);

            if (isInserted) {
                Utils.showToast(mContext, mContext.getString(R.string.user_created_message));
                SignUpActivity.this.finish();
            } else {
                Utils.showToast(mContext, mContext.getString(R.string.user_not_created_message));
            }
        }
    }
}
