package com.example.narongpon.jonghhong;


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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.TimeZone;

public class JHCheckRoom extends Fragment implements DatePickerDialog.OnDateSetListener{

    View rootView;
    private ButtonRectangle btnSearch;
    private EditText edt_date, edt_room;
    private DatePickerDialog mDatePicker;
    private Calendar mCalendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Bangkok"));
    private ArrayList<HashMap<String , String>> MyArrList = new ArrayList<>();
    private String[] roomName;
    private String roomID = "";
    MaterialDialog mtr;
    MaterialDialog.Builder mtrBuilder;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.jh_checkroom, container, false);

        initWidget();

        mDatePicker = DatePickerDialog.newInstance(
                this,
                mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH),
                false);

        edt_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePicker.setYearRange(2014,2034);
                mDatePicker.show(getActivity().getSupportFragmentManager(), "DatePicker");
            }
        });

        edt_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getRoomURL = "http://jonghhong.uinno.co.th/JHMobile/selectRoom.php";
                new SimpleTask().execute(getRoomURL);
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String resvDate = edt_date.getText().toString();
                String room = edt_room.getText().toString();

                mtrBuilder = new MaterialDialog.Builder(getActivity());
                if(resvDate.equals("") || room.equals("")) {
                    mtrBuilder.title("ข้อผิลดพลาด");
                    mtrBuilder.content("กรุณาเลือกวันที่และห้องประชุมที่ต้องการตรวจสอบ");
                    mtrBuilder.negativeText("ปิด");
                    mtrBuilder.show();
                } else {

                    MainDrawer mainDrawer = (MainDrawer) getActivity();
                    mainDrawer.showCheckRoom(roomID,resvDate);
                }
            }
        });

        return rootView;


    }

    public void initWidget() {
        btnSearch = (ButtonRectangle)rootView.findViewById(R.id.btn_search);
        edt_date = (EditText)rootView.findViewById(R.id.date_search);
        edt_room = (EditText)rootView.findViewById(R.id.room_search);
    }

    @Override
    public void onDateSet(DatePickerDialog datePickerDialog, int year, int month, int day) {

        month++;
        String newMonth;
        String newDate;

        if(day >= 1 && day <= 9) {
            newDate = "0" + String.valueOf(day);
        } else {
            newDate = String.valueOf(day);
        }

        newMonth = "0" + String.valueOf(month);
        edt_date.setText(newDate + "/" + newMonth + "/" + year);
    }

    private class SimpleTask extends AsyncTask<String,Void,String> {

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
                Log.e("RR_StatusLine", String.valueOf(statusCode));
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
            getRoomName(str);
        }

        public void getRoomName(String str) {
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
                            roomID = MyArrList.get(i).get("r_id");
                            edt_room.setText(charSequence);
                        }
                    })
                    .show();
        }
    }
}

