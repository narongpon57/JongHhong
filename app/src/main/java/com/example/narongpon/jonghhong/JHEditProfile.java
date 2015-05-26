package com.example.narongpon.jonghhong;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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


public class JHEditProfile extends Fragment{

    View rootView;
    private String myName, myID, myPermission, myTel, myEmail, permission;

    private EditText edtTel, edtEmail, edtUserID, edtNameUser, edtPermission;
    private ButtonRectangle btnUpdate;
    private ProgressDialog mProgress;

    MaterialDialog.Builder mtrDialog;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.jh_editprofile, container, false);

        myID = getArguments().getString("myID");
        myName = getArguments().getString("myName");
        myPermission = getArguments().getString("myPermission");
        sp = getActivity().getSharedPreferences("Jonghhong", Context.MODE_PRIVATE);
        myEmail = sp.getString("myEmail","");
        myTel = sp.getString("myTel","");



        initWidget();


        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mtrDialog = new MaterialDialog.Builder(getActivity());
                mtrDialog.title("รอการยืนยัน");
                mtrDialog.content("ต้องการแก้ไขข้อมูลใช่หรือไม่ ?");
                mtrDialog.positiveText("ใช่");
                mtrDialog.negativeText("ไม่");
                mtrDialog.callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        if (edtTel.getText().toString().equals("") || edtEmail.getText().toString().equals("")) {
                            mtrDialog = new MaterialDialog.Builder(getActivity());
                            mtrDialog.title("ข้อผิดพลาด");
                            mtrDialog.content("กรุณากรอกข้อมูลให้ครบถ้วน");
                            mtrDialog.negativeText("ปิด");
                            mtrDialog.show();
                        } else if (!edtTel.getText().toString().equals("") && edtTel.getText().toString().length() != 10) {
                            mtrDialog = new MaterialDialog.Builder(getActivity());
                            mtrDialog.title("ข้อผิดพลาด");
                            mtrDialog.content("กรุณากรอกเบอร์โทรศัพท์ให้ครบถ้วน");
                            mtrDialog.negativeText("ปิด");
                            mtrDialog.show();
                        } else {
                            String getEmail = edtEmail.getText().toString();
                            String[] chkEmail = getEmail.split("@");
                            int findStr = getEmail.indexOf("@");

                            if (findStr == -1) {
                                mtrDialog.content("กรุณากรอกข้อมูลอีเมล์ให้ถูกต้อง");
                                mtrDialog.negativeText("ปิด");
                                mtrDialog.show();

                                edtEmail.setText("");
                            } else if (!chkEmail[1].equals("gmail.com")) {
                                mtrDialog.content("กรุณากรอกอีเมล์ที่เป็น Gmail เท่านั้น");
                                mtrDialog.negativeText("ปิด");
                                mtrDialog.show();

                                edtEmail.setText("");
                            } else {
                                String serverURL = "http://jonghhong.uinno.co.th/JHMobile/updateUser.php";
                                new SimpleTask().execute(serverURL);
                            }
                        }
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }
                });
                mtrDialog.show();
            }
        });
        return rootView;
    }

    private void initWidget() {
        edtUserID = (EditText)rootView.findViewById(R.id.userID_edt);
        edtNameUser = (EditText)rootView.findViewById(R.id.name_edt);
        edtPermission = (EditText)rootView.findViewById(R.id.permission_edt);
        edtEmail = (EditText)rootView.findViewById(R.id.email_edt);
        edtTel = (EditText)rootView.findViewById(R.id.tel_edt);
        btnUpdate = (ButtonRectangle)rootView.findViewById(R.id.btn_editProfile);

        edtUserID.setText(myID);
        edtNameUser.setText(myName);
        edtPermission.setText(myPermission);
        edtEmail.setText(myEmail);
        edtTel.setText(myTel);
    }

    private class SimpleTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            mProgress = new ProgressDialog(getActivity());
            //mProgress.setTitle("กำลังโหลด...");
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
            params.add(new BasicNameValuePair("myID" , edtUserID.getText().toString()));
            params.add(new BasicNameValuePair("myEmail" , edtEmail.getText().toString()));
            params.add(new BasicNameValuePair("myTel" , edtTel.getText().toString()));

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(params,"UTF-8"));
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
        protected void onPostExecute(String str) {
            mProgress.dismiss();
            updateUser(str);
        }
    }

    private void updateUser(String str) {
        JSONObject jsonObject;
        String strStatusID = "0";
        String strError = "ไม่สามารถเชื่อมต่อ server ได้";

        try{
            jsonObject = new JSONObject(str);
            strStatusID = jsonObject.getString("StatusID");
            strError = jsonObject.getString("Error");

        }catch (JSONException e) {
            e.printStackTrace();
        }

        if(strStatusID.equals("0")) {
            mtrDialog = new MaterialDialog.Builder(getActivity());
            mtrDialog.title("ข้อผิดพลาด !");
            mtrDialog.content(strError);
            mtrDialog.negativeText("ปิด");
            mtrDialog.show();
        } else {
            SuperActivityToast.create(getActivity(),"บันทึกข้อมูลเรียบร้อยแล้ว", SuperToast.Duration.SHORT).show();
            editor = sp.edit();
            editor.putString("myEmail",edtEmail.getText().toString());
            editor.putString("myTel",edtTel.getText().toString());
            editor.commit();
        }
    }
}

