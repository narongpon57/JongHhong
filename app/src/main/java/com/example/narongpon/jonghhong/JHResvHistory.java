package com.example.narongpon.jonghhong;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.cards.BasicButtonsCard;
import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.view.MaterialListView;

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

public class JHResvHistory extends Fragment {

    private String strTransactionID = "";
    private String strResvDate = "";
    private String strPermission = "";
    private String strTime = "";
    private String strStatus = "";
    private String myUserID = "";
    private String strDate = "";


    View rootView;
    MaterialListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.jh_resvhistory, container, false);

        myUserID = getArguments().getString("myID");
        strPermission = getArguments().getString("Permission");

        mListView = (MaterialListView)rootView.findViewById(R.id.listHistory);

        String getTransaction = "http://jonghhong.uinno.co.th/JHMobile/getTransaction.php";

        return rootView;
    }

    private class SimpleTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder str = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urls[0]);

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("id" , myUserID));
            params.add(new BasicNameValuePair("permission" , strPermission));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse response = client.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while((line = reader.readLine()) != null) {
                        str.append(line);
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
        protected void onPostExecute(String s) {

        }
    }

}
