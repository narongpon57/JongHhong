package com.example.narongpon.jonghhong;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ButtonRectangle;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class JHChangePassword extends Fragment {
    View rootView;
    SharedPreferences sp;
    private static final String TAG = "Jonghhong";
    private String oldPassword, userID;
    private EditText edt_old, edt_new, edt_conNew;
    private ButtonRectangle btnSave;
    MaterialDialog.Builder mtr;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.jh_changepassword, container, false);

        sp = getActivity().getSharedPreferences(TAG, Context.MODE_PRIVATE);
        oldPassword = sp.getString("myPassword", "");
        userID = sp.getString("myID","");
        Log.e("oldpass",oldPassword);
        initWidget();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newPassword = edt_new.getText().toString();
                String conNewPassword = edt_conNew.getText().toString();

                mtr = new MaterialDialog.Builder(getActivity());
                mtr.title("ข้อผิดพลาด");
                if (edt_old.getText().equals("") || newPassword.equals("") || conNewPassword.equals("")) {
                    mtr.content("กรุณากรอกข้อมูลให้ครบถ้วน");
                    mtr.negativeText("ปิด");
                    mtr.show();
                } else if (!oldPassword.equals(edt_old.getText().toString())) {
                    mtr.content("กรุณากรอกรหัสผ่านเดิมให้ถูกต้อง");
                    mtr.negativeText("ปิด");
                    mtr.show();
                } else if (!newPassword.equals(conNewPassword)) {
                    mtr.content("กรุณากรอกรหัสผ่านใหม่ให้ตรงกัน");
                    mtr.negativeText("ปิด");
                    mtr.show();
                } else {
                    new ChangePassword().execute(JHConfig.ChangePassURL);
                }
            }
        });

        return rootView;
    }

    public void initWidget() {
        edt_old = (EditText)rootView.findViewById(R.id.edt_oldPassword);
        edt_new = (EditText)rootView.findViewById(R.id.edt_newPassword);
        edt_conNew = (EditText)rootView.findViewById(R.id.edt_confirm);
        btnSave = (ButtonRectangle)rootView.findViewById(R.id.btn_changePassword);
    }

    private class ChangePassword extends AsyncTask<String,Void,String> {

        ProgressDialog mProgress;

        @Override
        protected void onPreExecute() {
            mProgress = new ProgressDialog(getActivity());
            mProgress.setMessage("กำลังแก้ไขข้อมูล..");
            mProgress.setIndeterminate(false);
            mProgress.setCancelable(false);
            mProgress.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder str = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(urls[0]);

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("newPassword", edt_new.getText().toString()));
            params.add(new BasicNameValuePair("userID" , userID));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse response = client.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                Log.e("StatusCode", String.valueOf(statusCode));
                if(statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        str.append(line);
                        Log.e("Result",line);
                    }
                } else {
                    Log.e("Log", "Failed to download result..");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            mProgress.dismiss();
            getResult(s);
        }

        private void getResult(String s){
            String StatusID = "0";
            String strError = "ไม่สามารถเชื่อมต่อ Server ได้";
            JSONObject c;
            try {
                c = new JSONObject(s);
                StatusID = c.getString("StatusID");
                strError = c.getString("Error");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (StatusID.equals("1")) {
                SuperActivityToast.create(getActivity(), "บันทึกข้อมูลเรียบร้อยแล้ว", SuperToast.Duration.SHORT).show();
                edt_old.setText("");
                edt_new.setText("");
                edt_conNew.setText("");
            } else {
                mtr = new MaterialDialog.Builder(getActivity());
                mtr.title("ข้อผิดพลาด !");
                mtr.content(strError);
                mtr.negativeText("ปิด");
                mtr.show();
            }
        }
    }
}

