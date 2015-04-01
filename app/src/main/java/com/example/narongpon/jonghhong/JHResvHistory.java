package com.example.narongpon.jonghhong;


import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.dexafree.materialList.view.MaterialListView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class JHResvHistory extends Fragment {

    private String strTransactionID = "";
    private String strResvDate = "";
    private String strPermission = "";
    private String strTime = "";
    private String strStatus = "";
    private String myUserID = "";
    private String strRoomName = "";
    private String chkEvent = "";
    private String strDate = "";
    private int position;

    private ProgressDialog mProgress;

    private ArrayList<HashMap<String, String>> MyArrList = new ArrayList<>();
    HashMap<String, String> map;

    View rootView;
    IMaterialView mListView;
    MaterialDialog.Builder mtrDialog;



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.jh_resvhistory, container, false);

        myUserID = getArguments().getString("myID");
        strPermission = getArguments().getString("Permission");

        mListView = (IMaterialView)rootView.findViewById(R.id.listHistory);
        mListView.setCardAnimation(MaterialListView.CardAnimation.SWING_BOTTOM_IN);

        String getTransaction = "http://jonghhong.uinno.co.th/JHMobile/getTransaction.php";

        new SimpleTask().execute(getTransaction);

        return rootView;
    }

    private class SimpleTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            if(chkEvent.equals("cancel")) {
                mProgress = new ProgressDialog(getActivity());
                mProgress.setMessage("กำลังบันทึกข้อมูล..");
                mProgress.setIndeterminate(false);
                mProgress.setCancelable(false);
                mProgress.show();
            }
        }

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder str = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urls[0]);

            List<NameValuePair> params = new ArrayList<>();

            if(chkEvent.equals("cancel")) {
                params.add(new BasicNameValuePair("tID", strTransactionID));
            }else {
                params.add(new BasicNameValuePair("id", myUserID));
                params.add(new BasicNameValuePair("permission", strPermission));
            }
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse response = client.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                Log.e("statusCode" , String.valueOf(statusCode));
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
            if(chkEvent.equals("cancel")) {
                mProgress.dismiss();
                isCancelSuccess(str);
            }else {
                getResvHistory(str);
            }
        }

        private void isCancelSuccess(String str) {

            JSONObject c;
            String msg = "";
            try {
                c = new JSONObject(str);
                msg = c.getString("Message");

            } catch (JSONException e){
                e.printStackTrace();
            }

            if(msg.equals("success")) {
                SuperActivityToast.create(getActivity(), "ยกเลิกการจองเรียบร้อยแล้ว", SuperToast.Duration.SHORT).show();

                MainDrawer mainActivity = (MainDrawer) getActivity();
                mainActivity.showResvHistory(myUserID,strPermission);

            } else {
                SuperActivityToast.create(getActivity(), "ไม่สามารถยกเลิกการจองได้ กรุณาติดต่อเจ้าหน้าที่" , SuperToast.Duration.SHORT).show();
            }

        }

        private void getResvHistory(String str){

            try {

                JSONArray data = new JSONArray(str);
                JSONObject c;

                if(data.length() >= 1) {
                    for (int i = 0; i < data.length(); i++) {

                        c = data.getJSONObject(i);

                        String txtStatus;
                        strResvDate = "วันที่จอง : " + c.getString("resv_date");
                        strTime = "เวลาที่จอง : " + c.getString("resv_start_time") + " - " + c.getString("resv_end_time");
                        strRoomName = c.getString("r_name");

                        String tranID = c.getString("t_id");
                        String roomID = c.getString("r_id");
                        String resvDate = c.getString("resv_date");
                        String stTime = c.getString("resv_start_time");
                        String enTime = c.getString("resv_end_time");
                        String status = c.getString("status_");

                        addArrayList(tranID, roomID, resvDate, stTime, enTime, status, strRoomName);



                        if (c.getString("status_").equals("wait")) {
                            txtStatus = "รอการยืนยัน";
                        } else {
                            txtStatus = "ยืนยัน";
                        }

                        strStatus = "สถานะการจอง : " + txtStatus;

                        Card card = getCard();
                        mListView.add(card);
                    }
                } else {
                    TextView tv = (TextView)rootView.findViewById(R.id.tv_history);
                    tv.setText("ไม่มีข้อมูลการจองห้อง");
                    tv.setTextSize(20);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        private void addArrayList(String tranID, String roomID, String resvDate, String stTime, String enTime,
                                  String status, String roomName) {

            map = new HashMap<>();
            map.put("TransactionID" , tranID);
            map.put("RoomID", roomID);
            map.put("ResvDate", resvDate);
            map.put("ResvStTime", stTime);
            map.put("ResvEnTime", enTime);
            map.put("Status", status);
            map.put("RoomName", roomName);
            MyArrList.add(map);
        }

        private Card getCard() {

            SimpleCard card;

            card = new BasicButtonsCard(getActivity());
            card.setTitle(strResvDate);
            card.setDescription(strRoomName + "\n" + strTime + "\n" + strStatus);

            ((BasicButtonsCard)card).setLeftButtonText("ย้าย");
            ((BasicButtonsCard)card).setRightButtonText("ยกเลิก");

            ((BasicButtonsCard)card).setOnLeftButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, final Card card) {
                    mtrDialog = new MaterialDialog.Builder(getActivity());
                    mtrDialog.title("รอการยืนยัน");
                    mtrDialog.content("ต้องการย้ายการจองใช่หรือไม่ ?");
                    mtrDialog.negativeText("ไม่");
                    mtrDialog.positiveText("ใช่");
                    mtrDialog.callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {

                            if(isOneDate(strDate)) {
                                mtrDialog = new MaterialDialog.Builder(getActivity());
                                mtrDialog.title("ข้อผิดพลาด");
                                mtrDialog.content("ไม่สามารถย้ายการจองห้องได้ ต้องทำการล่วงหน้าอย่างน้อย 1 วัน กรุณาติดต่อเจ้าหน้าที่");
                                mtrDialog.negativeText("ปิด");
                                mtrDialog.show();
                            } else {
                                String txt = "Edit";

                                position = mListView.getPosition(card);
                                strTransactionID = MyArrList.get(position).get("TransactionID");
                                strDate = MyArrList.get(position).get("ResvDate");

                                String timeStart = MyArrList.get(position).get("ResvStTime");
                                String timeEnd = MyArrList.get(position).get("ResvEnTime");
                                String rName = MyArrList.get(position).get("RoomName");
                                String rID = MyArrList.get(position).get("RoomID");

                                MainDrawer mainActivity = (MainDrawer) getActivity();
                                mainActivity.editResvRoomFragment(strTransactionID, myUserID, txt,
                                        timeStart, timeEnd, rName, strPermission, strDate, rID);
                            }
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {

                        }
                    });
                    mtrDialog.show();
                }
            });

            ((BasicButtonsCard)card).setOnRightButtonPressedListener(new OnButtonPressListener() {
                @Override
                public void onButtonPressedListener(View view, final Card card) {
                    mtrDialog = new MaterialDialog.Builder(getActivity());
                    mtrDialog.title("รอการยืนยัน");
                    mtrDialog.content("ต้องการยกเลิกการจองใช่หรือไม่ ?");
                    mtrDialog.negativeText("ไม่");
                    mtrDialog.positiveText("ใช่");
                    mtrDialog.callback(new MaterialDialog.ButtonCallback() {
                        @Override
                        public void onPositive(MaterialDialog dialog) {

                            position = mListView.getPosition(card);
                            strTransactionID = MyArrList.get(position).get("TransactionID");
                            strDate = MyArrList.get(position).get("ResvDate");

                            if(isOneDate(strDate)) {
                                mtrDialog = new MaterialDialog.Builder(getActivity());
                                mtrDialog.title("ข้อผิดพลาด");
                                mtrDialog.content("ไม่สามารถย้ายการจองห้องได้ ต้องทำการล่วงหน้าอย่างน้อย 1 วัน กรุณาติดต่อเจ้าหน้าที่");
                                mtrDialog.negativeText("ปิด");
                                mtrDialog.show();
                            } else {
                                chkEvent = "cancel";
                                String cancelTran = "http://jonghhong.uinno.co.th/JHMobile/delTransaction.php";
                                new SimpleTask().execute(cancelTran);
                            }
                        }

                        @Override
                        public void onNegative(MaterialDialog dialog) {

                        }
                    });

                    mtrDialog.show();
                }
            });
            return card;
        }
    }

    public boolean isOneDate(String strDate) {

        boolean chk = false;

        String newDate = "วันที่จอง : ";
        String strDate1;
        if(!strDate.equals("")) {

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            sdf.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
            Calendar c = Calendar.getInstance();

            String currentDate = sdf.format(c.getTime());
            String tmpDate;

            try {
                c.setTime(sdf.parse(currentDate));
                c.add(Calendar.DATE, 0);
                tmpDate = sdf.format(c.getTime());
                strDate1 = newDate + tmpDate;
                if (strDate1.equals(strDate)) {
                    chk = true;
                } else{
                    c.setTime(sdf.parse(currentDate));
                    c.add(Calendar.DATE, 1);
                    tmpDate = sdf.format(c.getTime());
                    strDate1 = newDate + tmpDate;
                    if (strDate1.equals(strDate)) {
                        chk = true;
                    }
                }
            }catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return chk;
    }

}
