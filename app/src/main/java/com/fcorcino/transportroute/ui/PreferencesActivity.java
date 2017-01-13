package com.fcorcino.transportroute.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;

import com.fcorcino.transportroute.R;

public class PreferencesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
    }

    /**
     * This method initializes the UI.
     */
    private void initUI() {
        setContentView(R.layout.activity_preferences);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * This fragment shows up the shared preferences UI.
     */
    public static class TransMobilePreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        public TransMobilePreferenceFragment() {}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference serverAddress = findPreference(getString(R.string.settings_server_address_key));
            bindPreferenceSummaryToValue(serverAddress);

            Preference serverPort = findPreference(getString(R.string.settings_server_port_key));
            bindPreferenceSummaryToValue(serverPort);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);
            return true;
        }

        /**
         * This method binds the summary to the preferences.
         *
         * @param preference the preference to bind the summary to.
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}
