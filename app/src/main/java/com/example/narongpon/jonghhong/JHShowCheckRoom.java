package com.example.narongpon.jonghhong;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.cards.SmallImageCard;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.view.IMaterialView;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JHShowCheckRoom extends ActionBarActivity {

    private Toolbar toolbar;
    private String resvDate,roomID;
    private String strResvDate, strTime, strRoomName;
    IMaterialView mListView;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jh_showcheckroom);

        resvDate = getIntent().getExtras().getString("EXTRA_resvDate");
        roomID = getIntent().getExtras().getString("EXTRA_roomID");

        initWidget();

        String getListRoom = "http://jonghhong.uinno.co.th/JHMobile/getListRoom.php";
        new SimpleTask().execute(getListRoom);


    }

    public void initWidget() {
        toolbar = (Toolbar)findViewById(R.id.toolbar_showRoom);
        mListView = (IMaterialView)findViewById(R.id.listRoom);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView.setCardAnimation(MaterialListView.CardAnimation.SWING_BOTTOM_IN);

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

    private class SimpleTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder str = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urls[0]);

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("roomID", roomID));
            params.add(new BasicNameValuePair("resvDate", resvDate));

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(params));
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
            showRoom(str);
        }

        public void showRoom(String str) {
            try {

                JSONArray data = new JSONArray(str);
                JSONObject c;

                if(data.length() >= 1) {
                    for (int i = 0; i < data.length(); i++) {

                        c = data.getJSONObject(i);

                        strResvDate = "วันที่จอง : " + c.getString("resv_date");
                        strTime = "เวลาที่จอง : " + c.getString("resv_start_time") + " - " + c.getString("resv_end_time");
                        strRoomName = c.getString("r_name");

                        Card card = getCard();
                        mListView.add(card);
                    }
                } else {
                    TextView tv = (TextView)findViewById(R.id.tv_room);
                    tv.setText("ไม่มีข้อมูลการจองห้อง");
                    tv.setTextSize(20);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private Card getCard() {

            SimpleCard card;
            card = new SmallImageCard(JHShowCheckRoom.this);
            card.setBackgroundColor(Color.parseColor("#F0F4C3"));
            card.setTitle(strResvDate);
            card.setDescription(strRoomName + "\n" + strTime);

            return card;
        }

    }
}
