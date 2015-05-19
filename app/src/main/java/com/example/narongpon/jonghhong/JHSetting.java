package com.example.narongpon.jonghhong;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


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
        String sms;
        String userID;

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
                    sms = String.valueOf(newValue);
                    new SettingSMS().execute(JHConfig.SetttingSMS_URL);
                    return true;
                }
            });


        }

        public void getPref() {
            chkApp = sp.getBoolean("get_boolean_app", true);
            chkSms = sp.getBoolean("get_boolean_sms", false);
            userID = sp.getString("myID","");

            appCheckbox.setChecked(chkApp);
            smsCheckbox.setChecked(chkSms);
        }

        public class SettingSMS extends AsyncTask<String,Void,String> {
            @Override
            protected String doInBackground(String... urls) {
                StringBuilder str = new StringBuilder();
                HttpClient client = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(urls[0]);

                try {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("sms" , sms));
                    params.add(new BasicNameValuePair("userID" , userID));

                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                    HttpResponse response = client.execute(httpPost);
                    StatusLine statusLine = response.getStatusLine();
                    int statusCode = statusLine.getStatusCode();
                    Log.e("SMS_StatusLine", String.valueOf(statusCode));
                    if (statusCode == 200) {
                        HttpEntity entity = response.getEntity();
                        InputStream content = entity.getContent();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                        String line;
                        while((line = reader.readLine()) != null) {
                            str.append(line);
                            Log.e("Simple" , line);
                        }
                    }else {
                        Log.e("Log","Failed to download result..");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return  str.toString();
            }

        }
    }
}


