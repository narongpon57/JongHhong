package com.example.narongpon.jonghhong;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dexafree.materialList.cards.BasicButtonsCard;
import com.dexafree.materialList.cards.OnButtonPressListener;
import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.model.Card;
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
import java.util.ArrayList;
import java.util.HashMap;


public class JHNotification extends Fragment {

    View rootView;
    IMaterialView materialView;
    String mUserID, mName, mRoom, mResvDate, mTime;
    private ArrayList<HashMap<String, String>> MyArrList = new ArrayList<>();
    HashMap<String, String> map;

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
                        mUserID = "รหัส: " + c.getString("u_resv_id");
                        mName = "ชื่อ - นามสกุล: " +c.getString("name");
                        mRoom = "ห้องประชุม: " +c.getString("r_name");
                        mResvDate = "วันที่จอง: " +c.getString("resv_date");
                        mTime = "เวลา: " + c.getString("resv_start_time") + " - " +c.getString("resv_end_time");

                        String tranID = c.getString("t_id");
                        String userID = c.getString("u_resv_id");
                        String name = c.getString("name");
                        String room = c.getString("r_name");
                        String resvDate = c.getString("resv_date");
                        String stTime = c.getString("resv_start_time");
                        String enTime = c.getString("resv_end_time");


                        addList(tranID, userID, name, room, resvDate, stTime, enTime);

                        Card card = getNotificationListCard();
                        materialView.add(card);

                    }
                } else {
                    TextView tv = (TextView)rootView.findViewById(R.id.tv_notification);
                    tv.setText("ไม่มีข้อมูลการแจ้งเตือน");
                    tv.setTextSize(20);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void addList(String tranID, String userID, String name, String room, String resvDate, String stTime,
                            String enTime) {

            map = new HashMap<>();
            map.put("TransactionID" , tranID);
            map.put("UserID" , userID);
            map.put("Name" , name);
            map.put("RoomName" , room);
            map.put("ResvDate" , resvDate);
            map.put("StartTime" , stTime);
            map.put("EndTime", enTime);
            MyArrList.add(map);
        }

        public Card getNotificationListCard(){

            SimpleCard card;
            card = new BasicButtonsCard(getActivity());
            card.setBackgroundColor(Color.parseColor("#F0F4C3"));
            card.setTitle(mResvDate);
            card.setDescription(mUserID + "\n" + mName +"\n" + mRoom + "\n" + mTime);

            ((BasicButtonsCard)card).setLeftButtonText("ยืนยัน");
            ((BasicButtonsCard)card).setRightButtonText("ยกเลิก");

            ((BasicButtonsCard)card).setOnLeftButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {

                }
            });

            ((BasicButtonsCard)card).setOnRightButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, Card card) {

                }
            });
            return card;
        }
    }
}
