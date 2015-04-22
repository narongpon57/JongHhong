package com.example.narongpon.jonghhong;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.view.IMaterialView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class JHNotification extends Fragment {

    View rootView;
    IMaterialView materialView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.jh_notification, container, false);

        materialView = (IMaterialView)rootView.findViewById(R.id.listNotification);
        materialView.setCardAnimation(IMaterialView.CardAnimation.SWING_BOTTOM_IN);

        String url = "http://jonghhong.uinno.co.th/JHMobile/getListNotification.php";

        new GetListNotification().execute(url);

        return rootView;
    }

    public class GetListNotification extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            StringBuilder str = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(params[0]);

            try {
                HttpResponse response = client.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                Log.e("statusCode", String.valueOf(statusCode));
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while((line = reader.readLine()) != null) {
                        str.append(line);
                        Log.e("Str",line);
                    }
                } else {
                    Log.e("Log", "Fail to download Data..");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str.toString();
        }

        @Override
        protected void onPostExecute(String str) {
            showNotification(str);
        }

        public void showNotification(String str){
            try{
                JSONArray data = new JSONArray(str);
                JSONObject c;

                if(data.length() >= 1) {
                    for(int i = 0; i<data.length(); i++) {
                        c = data.getJSONObject(i);
                    }
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
