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
import android.widget.EditText;


import com.afollestad.materialdialogs.MaterialDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.gc.materialdesign.views.ButtonRectangle;
import com.github.johnpersano.supertoasts.SuperActivityToast;
import com.github.johnpersano.supertoasts.SuperToast;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

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

public class JHResvRoom extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{

    View rootVieW;

    private EditText edtResvDate, edtStTime, edtEnTime, edtRoom;
    private ButtonRectangle btnResvRoom;
    private String chkTime = "";
    private String sR_ID = "";
    private String mRoomID;
    private String myUserID;
    private String permission;
    private String mCommand;
    private String mTranID;
    private String mResvDate;
    private String mStTime;
    private String mEnTime;
    private String mRoomName;
    private ProgressDialog mProgress;
    private ArrayList<HashMap<String , String>> MyArrList = new ArrayList<>();
    private String[] roomName;

    private DatePickerDialog mDatePicker;
    private TimePickerDialog mTimePicker;
    private Calendar mCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Bangkok"));

    MaterialDialog.Builder mtrDialog;
    MaterialDialog mtr;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootVieW = inflater.inflate(R.layout.jh_resvroom, container , false);

        mCommand = getArguments().getString("mCommand");

        Log.e("command", mCommand);

        if(mCommand.equals("Edit")){
            myUserID = getArguments().getString("myID");
            permission = getArguments().getString("Permission");
            mTranID = getArguments().getString("mTranID");
            mResvDate = getArguments().getString("resvDate");
            mStTime = getArguments().getString("stTime");
            mEnTime = getArguments().getString("enTime");
            mRoomName = getArguments().getString("rName");
            mRoomID = getArguments().getString("rID");
        }else {
            myUserID = getArguments().getString("myID");
            permission = getArguments().getString("Permission");
        }

        initWidget();

        mDatePicker = DatePickerDialog.newInstance(
                this,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH),
                false);

        mTimePicker = TimePickerDialog.newInstance(
                this,
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE),
                true,
                false);

        edtResvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePicker.setYearRange(2014,2034);
                mDatePicker.show(getActivity().getSupportFragmentManager(), "DatePicker");
            }
        });

        edtStTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkTime = "st";
                mTimePicker.show(getActivity().getSupportFragmentManager(), "TimePicker");
            }
        });

        edtEnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chkTime = "en";
                mTimePicker.show(getActivity().getSupportFragmentManager(), "TimePicker");
            }
        });

        edtRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getRoomURL = "http://jonghhong.uinno.co.th/JHMobile/selectRoom.php";
                String setupTask = "getRoom";

                final SimpleTask task = new SimpleTask(setupTask);
                task.execute(getRoomURL);
            }
        });

        btnResvRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtrDialog = new MaterialDialog.Builder(getActivity());
                mtrDialog.title("ข้อผิดพลาด");
                mtrDialog.negativeText("ปิด");

                String txtResvDate = edtResvDate.getText().toString();
                String txtStTime = edtStTime.getText().toString();
                String txtEnTime = edtEnTime.getText().toString();
                String txtRoom = edtRoom.getText().toString();

                if (txtResvDate.equals("") || txtStTime.equals("") || txtEnTime.equals("") || txtRoom.equals("")) {
                    mtrDialog.content("กรุณากรอกข้อมูลให้ครบ");
                    mtrDialog.show();
                } else if (checkResvTime()) {
                    mtrDialog.content("ไม่สามารถจองห้องในช่วงเวลานี้ได้ กรุณาเลือกช่วงเวลาใหม่");
                    mtrDialog.show();

                    edtStTime.setText("");
                    edtEnTime.setText("");
                } else{

                    String resultServer;

                    if(mCommand.equals("Edit")){
                        resultServer = "http://jonghhong.uinno.co.th/JHMobile/editRoom.php";
                    }else {
                        resultServer = "http://jonghhong.uinno.co.th/JHMobile/resvRoom.php";
                    }
                    String setupTask = "resvRoom";

                    SimpleTask simpleTask = new SimpleTask(setupTask);
                    simpleTask.execute(resultServer);
                }

            }
        });

        return rootVieW;
    }

    public void initWidget() {
        edtResvDate = (EditText)rootVieW.findViewById(R.id.resvRoom_edt);
        edtStTime = (EditText)rootVieW.findViewById(R.id.stTime_edt);
        edtEnTime = (EditText)rootVieW.findViewById(R.id.enTime_edt);
        btnResvRoom = (ButtonRectangle)rootVieW.findViewById(R.id.btn_resvRoom);
        edtRoom = (EditText)rootVieW.findViewById(R.id.room_edt);

        if(mCommand.equals("Edit")) {
            edtResvDate.setText(mResvDate);
            edtStTime.setText(mStTime);
            edtEnTime.setText(mEnTime);
            edtRoom.setText(mRoomName);
        }
    }


    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

        month++;
        String newDate;
        String newMonth;

        mtrDialog = new MaterialDialog.Builder(getActivity());
        mtrDialog.title("ข้อผิดพลาด");
        mtrDialog.negativeText("ปิด");

        if (day >= 1 && day <= 9) {
            newDate = "0" + String.valueOf(day);
        } else {
            newDate = String.valueOf(day);
        }

        if (month >= 1 && month <= 9) {
            newMonth = "0" + String.valueOf(month);
        } else {
            newMonth = String.valueOf(month);
        }

        if(chkInputDate(newDate,newMonth,year).equals("3date")) {
            mtrDialog.content("กรุณาจองห้องล่วงหน้าอย่างน้อย 3 วัน");
            mtrDialog.show();
        } else if (!chkInputDate(newDate,newMonth,year).equals("30date")) {
            mtrDialog.content("กรุณาจองห้องล่วงหน้าไม่เกิน 30 วัน");
            mtrDialog.show();
        } else {
            edtResvDate.setText(newDate + "/" + newMonth + "/" + year);
        }
    }

    @Override
    public void onTimeSet(RadialPickerLayout view,int hourOfDay, int minute) {

        mtrDialog = new MaterialDialog.Builder(getActivity());
        mtrDialog.title("ข้อผิดพลาด");
        mtrDialog.negativeText("ปิด");

        String newMinute;
        String tmpMinute;

        if (minute >= 0 && minute <= 9){
            tmpMinute = String.valueOf(minute);
            newMinute = "0" + tmpMinute;
        } else {
            newMinute = String.valueOf(minute);
        }

        if(!checkTime(hourOfDay,minute)) {
            mtrDialog.content("ไม่สามารถจองห้องได้ในเวลานี้");
            mtrDialog.show();
        } else {
            if (chkTime.equals("st")) {
                edtStTime.setText(hourOfDay + "." + newMinute);
            } else if (chkTime.equals("en")) {
                edtEnTime.setText(hourOfDay + "." + newMinute);
            }
        }
    }

    public boolean checkTime(int hour , int minute) {

        int libStTime = 9;
        int libEnTime = 18;
        boolean check = false;

        if(libStTime <= hour && libEnTime > hour) {
            check = true;
        }
        else if(libEnTime == hour && minute == 0) {
            check = true;
        }

        return check;
    }

    public boolean checkResvTime() {
        boolean chk = false;

        String getStTime = edtStTime.getText().toString();
        String getEnTime = edtEnTime.getText().toString();

        String[] splStTime = getStTime.split("\\.");
        String[] splEnTime = getEnTime.split("\\.");

        int getStHour = Integer.parseInt(splStTime[0]);
        int tmpStHour = getStHour + 1;
        int getStMinute = Integer.parseInt(splStTime[1]);

        int getEnHour = Integer.parseInt(splEnTime[0]);
        int getEnMinute = Integer.parseInt(splEnTime[1]);

        if (tmpStHour > getEnHour) {
            chk = true;
        } else if (tmpStHour == getEnHour) {
            if(getStMinute > getEnMinute) {
                chk = true;
            }
        }

        return chk;
    }



    public String chkInputDate(String date , String month , int year){

        String chkDate = "";
        String strYear = String.valueOf(year);
        String newResvDate = date + "/" + month + "/" + strYear;
        SimpleDateFormat dfm = new SimpleDateFormat("dd/MM/yyyy");
        dfm.setTimeZone(TimeZone.getTimeZone("Asia/Bangkok"));
        Calendar c = Calendar.getInstance();

        String currentDate = dfm.format(c.getTime());
        String tmpDate;

        try {
            c.setTime(dfm.parse(currentDate));
            c.add(Calendar.DATE , 0);
            tmpDate = dfm.format(c.getTime());
            if(tmpDate.equals(newResvDate)){
                chkDate = "3date";

            } else {
                for (int i = 0; i < 30; i++) {
                    c.add(Calendar.DATE, 1);
                    tmpDate = dfm.format(c.getTime());

                    if (i < 2 && newResvDate.equals(tmpDate)) {
                        chkDate = "3date";

                    }else if (newResvDate.equals(tmpDate)) {
                        chkDate = "30date";

                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return chkDate;
    }


    private class SimpleTask extends AsyncTask<String,Void,String> {

        String chk = "";

        public SimpleTask(String chk){
            this.chk = chk;
        }

        @Override
        protected void onPreExecute() {
            if(chk.equals("resvRoom")) {
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

            if(sR_ID.equals("")){
                sR_ID = mRoomID;
            }

            try{
                if(chk.equals("resvRoom")) {
                    List<NameValuePair> params = new ArrayList<>();
                    params.add(new BasicNameValuePair("id" , myUserID));
                    params.add(new BasicNameValuePair("permission" , permission));
                    params.add(new BasicNameValuePair("room" , sR_ID));
                    params.add(new BasicNameValuePair("resvDate" , edtResvDate.getText().toString()));
                    params.add(new BasicNameValuePair("start" , edtStTime.getText().toString()));
                    params.add(new BasicNameValuePair("end" , edtEnTime.getText().toString()));
                    if(mCommand.equals("Edit")){
                        params.add(new BasicNameValuePair("tranID", mTranID));
                    }

                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }
                HttpResponse response = client.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                Log.e("RR_StatusLine",String.valueOf(statusCode));
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

        @Override
        protected void onPostExecute(String str) {

            if(chk.equals("getRoom")) {
                getRoomName(str);
            } else {
                mProgress.dismiss();
                resvRoom(str);
            }
        }

        private void getRoomName(String str) {
            try {
                JSONArray data = new JSONArray(str);
                roomName = new String[data.length()];

                HashMap<String, String> map;

                for (int i = 0; i < data.length(); i++) {
                    JSONObject c = data.getJSONObject(i);

                    roomName[i] = c.getString("r_name");
                    map = new HashMap<>();
                    map.put("r_id", c.getString("r_id"));
                    MyArrList.add(map);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }

            mtr = new MaterialDialog.Builder(getActivity())
                    .items(roomName)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                            sR_ID = MyArrList.get(i).get("r_id");
                            edtRoom.setText(charSequence);
                        }
                    })
                    .show();
        }

        private void resvRoom(String str) {

            mtrDialog = new MaterialDialog.Builder(getActivity());
            mtrDialog.title("ข้อผิดพลาด");
            mtrDialog.negativeText("ปิด");

            String strStatusID = "0";
            String strError = "ไม่สามารถเชื่อมต่อ server ได้";
            JSONObject jsonResvRoom;
            try{
                jsonResvRoom = new JSONObject(str);
                strStatusID = jsonResvRoom.getString("StatusID");
                strError = jsonResvRoom.getString("Error");
                Log.e("test",jsonResvRoom.getString("Error"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(strStatusID.equals("0")) {
                mtrDialog.content(strError);
                mtrDialog.show();
            } else {
                SuperActivityToast.create(getActivity(),"บันทึกข้อมูลเรียบร้อยแล้ว", SuperToast.Duration.MEDIUM).show();

                edtResvDate.setText("");
                edtStTime.setText("");
                edtEnTime.setText("");
                edtRoom.setText("");

                MainDrawer mainActivity = (MainDrawer) getActivity();
                mainActivity.showResvHistory(myUserID,permission);
            }
        }
    }


}
