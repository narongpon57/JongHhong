package com.example.narongpon.jonghhong;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
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

public class JHFirstLogin extends ActionBarActivity {

    private String myName = "";
    private String myPermission = "";
    private String myID = "";
    private String permission = "";
    private String firstName = "";
    private String lastName = "";
    private String myUsername = "";

    private EditText edtTel, edtEmail;
    private ButtonRectangle btnFirstLogin;
    private Toolbar toolbar;

    MaterialDialog.Builder materialDialog;
    ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jh_firstlogin);

        myName = getIntent().getExtras().getString("myName");
        myPermission = getIntent().getExtras().getString("myPermission");
        myID = getIntent().getExtras().getString("myID");
        permission = getIntent().getExtras().getString("Permission");
        firstName = getIntent().getExtras().getString("myFirstName");
        lastName = getIntent().getExtras().getString("myLastName");
        myUsername = getIntent().getExtras().getString("myUsername");

        initWidget();

        btnFirstLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog = new MaterialDialog.Builder(JHFirstLogin.this);
                materialDialog.title("รอการยืนยัน");
                materialDialog.content("คุณต้องการบันทึกข้อมูลใช่หรือไม่ ?");
                materialDialog.positiveText("ใช่");
                materialDialog.negativeText("ไม่");
                materialDialog.callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String getTel = edtTel.getText().toString();
                        String getEmail = edtEmail.getText().toString();

                        materialDialog = new MaterialDialog.Builder(JHFirstLogin.this);
                        materialDialog.title("ข้อผิดพลาด");

                        String[] chkEmail = getEmail.split("@");

                        int findStr = getEmail.indexOf("@");

                        if(getTel.equals("") || getEmail.equals("")) {
                            materialDialog.content("กรุณากรอกข้อมูลให้ครบถ้วน");
                            materialDialog.negativeText("ปิด");
                            materialDialog.show();
                        }
                        else if(findStr == -1) {
                            materialDialog.content("กรุณากรอกข้อมูลอีเมล์ให้ถูกต้อง");
                            materialDialog.negativeText("ปิด");
                            materialDialog.show();

                            edtEmail.setText("");
                        }else if(!chkEmail[1].equals("gmail.com")) {
                            materialDialog.content("กรุณากรอกอีเมล์ที่เป็น Gmail เท่านั้น");
                            materialDialog.negativeText("ปิด");
                            materialDialog.show();

                            edtEmail.setText("");
                        }else {
                            String resultServer = "http://jonghhong.uinno.co.th/JHMobile/updateUser.php";
                            new SimpleTask().execute(resultServer);
                        }
                    }
                    @Override
                    public void onNegative(MaterialDialog dialog) {

                    }
                });
                materialDialog.show();
            }
        });


    }

    private void initWidget(){

        toolbar = (Toolbar)findViewById(R.id.toolbar_firstLogin);
        btnFirstLogin = (ButtonRectangle)findViewById(R.id.btn_firstLogin);
        edtTel = (EditText)findViewById(R.id.edt_tel);
        edtEmail = (EditText)findViewById(R.id.edt_email);

        setSupportActionBar(toolbar);
    }

    private class SimpleTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            mProgress = new ProgressDialog(JHFirstLogin.this);
            mProgress.setMessage("กำลังบันทึกข้อมูล..");
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
            params.add(new BasicNameValuePair("myTel" , edtTel.getText().toString()));
            params.add(new BasicNameValuePair("myEmail" , edtEmail.getText().toString()));
            params.add(new BasicNameValuePair("myID" , myID));

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
                        Log.e("SQL",line);
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

        private void updateUser(String str){
            JSONObject c;
            String strStatusID = "";
            String strError = "";

            try {
                c = new JSONObject(str);
                strStatusID = c.getString("StatusID");
                strError = c.getString("Error");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            materialDialog = new MaterialDialog.Builder(JHFirstLogin.this);
            if(strStatusID.equals("0")) {
                materialDialog.title("ข้อผิดพลาด");
                materialDialog.content("ไม่สามารถบันทึกข้อมูลได้");
                materialDialog.negativeText("ปิด");
                materialDialog.show();
            } else {
                SuperActivityToast.create(JHFirstLogin.this,"บันทึกข้อมูล เรียบร้อยแล้ว", SuperToast.Duration.MEDIUM).show();
                Intent i = new Intent(getApplicationContext(),MainDrawer.class);
                i.putExtra("myID" , myID);
                i.putExtra("myFirstName" , firstName);
                i.putExtra("myLastName" , lastName);
                i.putExtra("myName", myName);
                i.putExtra("myPermission", myPermission);
                i.putExtra("Permission" , permission);
                i.putExtra("myUsername" , myUsername);
                i.putExtra("myEmail" , edtEmail.getText().toString());
                i.putExtra("myTel" , edtTel.getText().toString());
                startActivity(i);
            }
        }
    }
}
