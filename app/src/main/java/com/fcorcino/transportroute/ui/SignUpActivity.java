package com.fcorcino.transportroute.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.fcorcino.transportroute.R;
import com.fcorcino.transportroute.TransportRoute;
import com.fcorcino.transportroute.model.User;
import com.fcorcino.transportroute.utils.Constants;
import com.fcorcino.transportroute.utils.TextUtils;
import com.fcorcino.transportroute.utils.Utils;
import com.fcorcino.transportroute.utils.ViewUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.database.DatabaseReference;

public class SignUpActivity extends BaseActivity {

    /**
     * @var mNameEditText the edit text to type the name to be crated.
     */
    private EditText mNameEditText;

    /**
     * @var mEmailEditText the edit text to type the user name to be created.
     */
    private EditText mEmailEditText;

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
        mEmailEditText = (EditText) findViewById(R.id.email_edit_text);
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
                mEmailEditText,
                mPasswordEditText,
                mCreditCardNumberEditText,
        };

//        for (EditText editText : editTexts) {
//            areFieldsValid = ViewUtils.validateEditText(editText, errorMessage);
//        }

        if (areFieldsValid) {
            final String email = ViewUtils.getTextFromEditText(mEmailEditText);

            mFirebaseAuth.createUserWithEmailAndPassword(
                    email,
                    ViewUtils.getTextFromEditText(mPasswordEditText)
            ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Utils.showToast(getApplicationContext(), getString(R.string.user_not_created_message));
                    } else {
                        User newUser = prepareUser();
                        DatabaseReference databaseReference = TransportRoute.getFirebaseDatabase().getReference(Constants.FIREBASE_KEY_USERS);
                        databaseReference.child(TextUtils.decodeEmail(email)).setValue(newUser);
                    }
                }
            });
        }
    }

    /**
     * This method creates an User with the data input to the fields.
     *
     * @return an User with the data provided in the fields..
     */
    private User prepareUser() {
        User user = new User();
        user.setName(mNameEditText.getText().toString().trim());
        user.setEmail(mEmailEditText.getText().toString().trim());
        user.setPassword(mPasswordEditText.getText().toString().trim());
        user.setStatus(Constants.STATUS_VALUE_ACTIVE);
        user.setUserTypeId(Constants.USER_TYPE_USER);
        user.setCreditCard(mCreditCardNumberEditText.getText().toString().trim());

        return user;
    }
}
