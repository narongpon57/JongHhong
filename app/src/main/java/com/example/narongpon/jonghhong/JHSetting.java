package com.example.narongpon.jonghhong;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;


public class JHSetting extends ActionBarActivity {

    private static final String PREF_NAME = "Jonghhong";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jh_preference);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar_setting);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(R.id.content_setting, new Preference()).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public static class Preference extends PreferenceFragment {

        CheckBoxPreference appCheckbox;
        CheckBoxPreference smsCheckbox;
        SharedPreferences sp;
        SharedPreferences.Editor editor;
        boolean chkApp;
        boolean chkSms;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.jh_preference);

            appCheckbox = (CheckBoxPreference)getPreferenceManager().findPreference("application_notification");
            smsCheckbox = (CheckBoxPreference)getPreferenceManager().findPreference("sms_notification");

            sp = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            editor = sp.edit();

            getPref();

            appCheckbox.setOnPreferenceChangeListener(new android.preference.Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(android.preference.Preference preference, Object newValue) {
                    editor.putBoolean("get_boolean_app",(Boolean)newValue);
                    editor.commit();
                    return true;
                }
            });

            smsCheckbox.setOnPreferenceChangeListener(new android.preference.Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(android.preference.Preference preference, Object newValue) {
                    editor.putBoolean("get_boolean_sms", (Boolean)newValue);
                    editor.commit();
                    return true;
                }
            });


        }

        public void getPref() {
            chkApp = sp.getBoolean("get_boolean_app", true);
            chkSms = sp.getBoolean("get_boolean_sms", false);

            appCheckbox.setChecked(chkApp);
            smsCheckbox.setChecked(chkSms);
        }
    }



}
