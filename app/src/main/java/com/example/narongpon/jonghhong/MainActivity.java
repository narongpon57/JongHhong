package com.example.narongpon.jonghhong;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.afollestad.materialdialogs.MaterialDialog;
import com.gc.materialdesign.views.ButtonRectangle;


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

import javax.security.auth.login.LoginException;


public class MainActivity extends ActionBarActivity {

    private EditText edtUser;
    private EditText edtPass;
    private ButtonRectangle btnLogin;
    MaterialDialog.Builder mtrDialog;
    private ProgressDialog mProgress;
    SharedPreferences sp;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jh_login);

        initWidget();

        mtrDialog = new MaterialDialog.Builder(MainActivity.this);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txtUser = edtUser.getText().toString();
                String txtPass = edtPass.getText().toString();

                if(txtUser.equals("") || txtPass.equals("")) {
                    mtrDialog.title("ข้อผิดพลาด");
                    mtrDialog.content("กรุณากรอกชื่อผู้ใช้และรหัสผ่านให้ครบถ้วน");
                    mtrDialog.negativeText("ปิด");
                    mtrDialog.show();
                } else {
                    String serverURL = "http://jonghhong.uinno.co.th/JHMobile/checkLogin.php";
                    new SimpleTask().execute(serverURL);
                }
            }
        });
    }

    public void initWidget(){
        btnLogin = (ButtonRectangle)findViewById(R.id.btn_login);
        edtUser = (EditText)findViewById(R.id.edt_user);
        edtPass = (EditText)findViewById(R.id.edt_pass);

        edtUser.setText("");
        edtPass.setText("");
    }

    private class SimpleTask extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            mProgress = new ProgressDialog(MainActivity.this);
            mProgress.setMessage("กำลังเข้าสู่ระบบ..");
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
            params.add(new BasicNameValuePair("username" , edtUser.getText().toString()));
            params.add(new BasicNameValuePair("password" , edtPass.getText().toString()));

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
                        Log.e("SQL", line);
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
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String str) {
            mProgress.dismiss();
            isLoginSuccess(str);
        }

    }

    private void isLoginSuccess(String str){

        JSONObject jsonObject;
        String strFound = "0";
        String strError = "ไม่สามารถเชื่อมต่อ server ได้";
        String userID = "";
        String nameUser = "";
        String permission = "";
        String tel = "";
        String email = "";
        String namePermission = "";
        String password = "";


        try{
            jsonObject = new JSONObject(str);
            strFound = jsonObject.getString("Found");
            strError = jsonObject.getString("Error");
            userID = jsonObject.getString("u_id");
            nameUser = jsonObject.getString("name");
            tel = jsonObject.getString("tel");
            email = jsonObject.getString("email");
            permission = jsonObject.getString("per");
            password = edtPass.getText().toString();

            switch (permission) {
                case "1":
                    namePermission = "นักศึกษา";
                    break;
                case "2":
                    namePermission = "อาจารย์";
                    break;
                case "3":
                    namePermission = "เจ้าหน้าที่";
                    break;
            }

            Log.e("Permission",namePermission);
        }catch (JSONException e){
            e.printStackTrace();
        }

        if(strFound.equals("0")) {
            mtrDialog.title("ข้อผิดพลาด !");
            mtrDialog.content(strError);
            mtrDialog.negativeText("ปิด");
            mtrDialog.show();

            edtUser.setText("");
            edtPass.setText("");
        } else if(permission.equals("4")){
            mtrDialog.title("ข้อผิดพลาด !");
            mtrDialog.content("ผู้ดูแลระบบไม่สามารถใช้งานในส่วนนี้ได้");
            mtrDialog.negativeText("ปิด");

            mtrDialog.show();

            edtUser.setText("");
            edtPass.setText("");

        } else if(email.equals("") && tel.equals("")){
            Log.e("test", "test");
            Intent i = new Intent(getApplicationContext(),JHFirstLogin.class);
            i.putExtra("myID" , userID);
            i.putExtra("myName" , nameUser);
            i.putExtra("myPermission", namePermission);
            i.putExtra("Permission" , permission);
            i.putExtra("myUsername" , edtUser.getText().toString());
            i.putExtra("myPassword" , password);

            startActivity(i);
        } else{
            Intent i = new Intent(getApplicationContext(),MainDrawer.class);

            sp = getSharedPreferences("Jonghhong", Context.MODE_PRIVATE);
            editor = sp.edit();
            editor.putString("myID",userID);
            editor.putString("myName",nameUser);
            editor.putString("myPermission",namePermission);
            editor.putString("Permission",permission);
            editor.putString("myUsername",edtUser.getText().toString());
            editor.putString("myEmail",email);
            editor.putString("myTel", tel);
            editor.putString("myPassword", password);
            editor.putInt("pos",0);
            editor.commit();

            startActivity(i);
        }
    }

}


