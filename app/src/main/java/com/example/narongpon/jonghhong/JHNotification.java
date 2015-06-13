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

import com.afollestad.materialdialogs.MaterialDialog;
import com.dexafree.materialList.cards.BasicButtonsCard;
import com.dexafree.materialList.cards.OnButtonPressListener;
import com.dexafree.materialList.cards.SimpleCard;
import com.dexafree.materialList.model.Card;
import com.dexafree.materialList.view.IMaterialView;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;

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


public class JHNotification extends Fragment {

    View rootView;
    IMaterialView materialView;
    String mUserID, mName, mRoom, mResvDate, mTime, myUserID, selectedID;
    String chkEvent = "";
    private int position;
    private ArrayList<HashMap<String, String>> MyArrList = new ArrayList<>();
    HashMap<String, String> map;
    MaterialDialog.Builder mtr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.jh_notification, container, false);

        myUserID = getArguments().getString("myID");

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
        protected String doInBackground(String... urls) {
            StringBuilder str = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urls[0]);

            List<NameValuePair> params = new ArrayList<>();

            try {
                if(chkEvent.equals("confirm") || chkEvent.equals("cancel")) {
                    params.add(new BasicNameValuePair("id", myUserID));
                    params.add(new BasicNameValuePair("event", chkEvent));
                    params.add(new BasicNameValuePair("tranID", selectedID));
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }
                HttpResponse response = client.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                Log.e("statusCode_Notification", String.valueOf(statusCode));
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
            if(chkEvent.equals("confirm") || chkEvent.equals("cancel")) {
                refreshNotification(str);
            } else {
                showNotification(str);
            }
        }

        public void showNotification(String str){
            try{
                JSONArray data = new JSONArray(str);
                JSONObject c;

                if(data.length() >= 1) {
                    for(int i = 0; i<data.length(); i++) {
                        c = data.getJSONObject(i);
                        String staffID = c.getString("u_id");
                        Log.e("staff",staffID);
                        int found = 0;
                        if (staffID.equals(myUserID)) {
                            found = 1;
                            mUserID = "รหัส: " + c.getString("u_resv_id");
                            mName = "ชื่อ - นามสกุล: " + c.getString("name");
                            mRoom = "ห้องประชุม: " + c.getString("r_name");
                            mResvDate = "วันที่จอง: " + c.getString("resv_date");
                            mTime = "เวลา: " + c.getString("resv_start_time") + " - " + c.getString("resv_end_time");

                            String tranID = c.getString("t_id");
                            String userID = c.getString("u_resv_id");
                            String name = c.getString("name");
                            String room = c.getString("r_name");
                            String resvDate = c.getString("resv_date");
                            String stTime = c.getString("resv_start_time");
                            String enTime = c.getString("resv_end_time");
                            String roomID = c.getString("r_id");

                            addList(tranID, userID, name, room, resvDate, stTime, enTime, roomID);

                            Card card = getNotificationListCard();
                            materialView.add(card);

                        } else if(found != 1) {
                            TextView tv = (TextView)rootView.findViewById(R.id.tv_notification);
                            tv.setText("ไม่มีข้อมูลการแจ้งเตือน");
                            tv.setTextSize(20);
                        }
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

        private void refreshNotification(String str) {
            String statusID;
            JSONObject c;
            try{
                c = new JSONObject(str);
                statusID = c.getString("StatusID");
                if(statusID.equals("1")) {
                    SuperActivityToast.create(getActivity(), "ทำรายการเรียบร้อยแล้ว", SuperToast.Duration.MEDIUM).show();
                    MainDrawer mainActivity = (MainDrawer) getActivity();
                    Log.e("456",statusID);
                    mainActivity.showNotification(myUserID);
                } else {
                    SuperActivityToast.create(getActivity(), "ไม่สามารถทำรายการดังกล่าวได้", SuperToast.Duration.MEDIUM).show();
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void addList(String tranID, String userID, String name, String room, String resvDate, String stTime,
                            String enTime, String roomID) {

            map = new HashMap<>();
            map.put("TransactionID" , tranID);
            map.put("UserID" , userID);
            map.put("Name" , name);
            map.put("RoomName" , room);
            map.put("ResvDate" , resvDate);
            map.put("StartTime" , stTime);
            map.put("EndTime", enTime);
            map.put("RoomID", roomID);
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
                public void onButtonPressedListener(View view, final Card card) {
                    mtr = new MaterialDialog.Builder(getActivity());
                    mtr.title("รอการยืนยัน");
                    mtr.content("ต้องการยืนยันการจองใช่หรือไม่ ?");
                    mtr.negativeText("ไม่");
                    mtr.positiveText("ใช่");
                    mtr.callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            position = materialView.getPosition(card);
                            selectedID = MyArrList.get(position).get("TransactionID");
                            chkEvent = "confirm";
                            String urlCon = "http://jonghhong.uinno.co.th/JHMobile/confirmTran.php";
                            new GetListNotification().execute(urlCon);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            super.onNegative(dialog);
                        }
                    });
                   mtr.show();
                }
            });

            ((BasicButtonsCard)card).setOnRightButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, final Card card) {
                    mtr = new MaterialDialog.Builder(getActivity());
                    mtr.title("รอการยืนยัน");
                    mtr.content("กรุณาเลือกรายการที่ต้องการ ?");
                    mtr.negativeText("ย้ายห้องประชุม");
                    mtr.positiveText("ยกเลิกการจองห้อง");
                    mtr.callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {
                            position = materialView.getPosition(card);
                            selectedID = MyArrList.get(position).get("TransactionID");
                            chkEvent = "cancel";
                            String urlCon = "http://jonghhong.uinno.co.th/JHMobile/confirmTran.php";
                            new GetListNotification().execute(urlCon);
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {
                            position = materialView.getPosition(card);
                            String transactionID = MyArrList.get(position).get("TransactionID");
                            String resvDate = MyArrList.get(position).get("ResvDate");
                            String startTime = MyArrList.get(position).get("StartTime");
                            String endTime = MyArrList.get(position).get("EndTime");
                            String userID = MyArrList.get(position).get("UserID");
                            String roomName = MyArrList.get(position).get("RoomName");
                            String roomID = MyArrList.get(position).get("RoomID");
                            String staffID = myUserID;
                            String mCommand = "changeRoomByStaff";

                            MainDrawer mainActivity = (MainDrawer) getActivity();
                            mainActivity.changeRoomByStaff(transactionID,resvDate,startTime,endTime,userID,roomName,mCommand,roomID,staffID);
                        }
                    });
                    mtr.show();
                }
            });
            return card;
        }
    }
}
