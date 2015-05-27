package com.example.narongpon.jonghhong;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.heinrichreimersoftware.materialdrawer.DrawerFrameLayout;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;


public class MainDrawer extends ActionBarActivity {
    private DrawerFrameLayout drawer;
    private ActionBarDrawerToggle drawerToggle;

    private String myName, myPermission, myID, myTel, myEmail, permission;
    private String regID;
    private static final String REG_ID = "regID";
    //private static final String TAG = "register";
    GoogleCloudMessaging gcm;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jh_drawer);

        SharedPreferences sp = getSharedPreferences("Jonghhong", Context.MODE_PRIVATE);
        myName = sp.getString("myName","");
        myPermission = sp.getString("myPermission","");
        myID = sp.getString("myID","");
        myTel = sp.getString("myTel","");
        myEmail = sp.getString("myEmail","");
        permission = sp.getString("Permission","");
        int position = sp.getInt("pos", 0);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerFrameLayout)findViewById(R.id.drawer);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ){
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View view) {
                invalidateOptionsMenu();
            }
        };

        setSupportActionBar(toolbar);

        drawer.setDrawerListener(drawerToggle);
        drawer.closeDrawer();

        Drawable background = getResources().getDrawable(R.drawable.bg2);
        if (background != null) {
            background.setAlpha(100);
        }
        drawer.setProfile(new DrawerProfile()
                .setBackground(background)
                .setName(myName)
                .setDescription(myPermission));

        Drawable checkRoom_ic = getResources().getDrawable(R.drawable.calendar_ic);
        drawer.addItem(new DrawerItem()
                .setImage(checkRoom_ic)
                .setTextPrimary("ตรวจสอบเวลาห้องประชุม"));


        Drawable editProfile_ic = getResources().getDrawable(R.drawable.user_ic);
        drawer.addItem(new DrawerItem()
                .setImage(editProfile_ic)
                .setTextPrimary("แก้ไขข้อมูลส่วนตัว"));

        Drawable password_ic = getResources().getDrawable(R.drawable.ic_lock);
        drawer.addItem(new DrawerItem()
                .setImage(password_ic)
                .setTextPrimary("แก้ไขรหัสผ่าน"));

        Drawable resvRoom_ic = getResources().getDrawable(R.drawable.reservation_ic);
        drawer.addItem(new DrawerItem()
                .setImage(resvRoom_ic)
                .setTextPrimary("จองห้องประชุม"));

        Drawable resvHistory_ic = getResources().getDrawable(R.drawable.history_ic);
        drawer.addItem(new DrawerItem()
                .setImage(resvHistory_ic)
                .setTextPrimary("ประวัติจองห้องประชุม"));

        if(permission.equals("3")) {
            Drawable notification_ic = getResources().getDrawable(R.drawable.ic_notification);
            drawer.addItem(new DrawerItem()
                    .setImage(notification_ic)
                    .setTextPrimary("รายการแจ้งเตือน"));
        }
        drawer.addDivider();


        Drawable setting_ic = getResources().getDrawable(R.drawable.settings_ic);
        drawer.addItem(new DrawerItem()
                .setImage(setting_ic)
                .setTextPrimary("ตั้งค่า"));

        Drawable logout_ic = getResources().getDrawable(R.drawable.logout_ic);
        drawer.addItem(new DrawerItem()
                .setImage(logout_ic)
                .setTextPrimary("ออกจากระบบ"));

        drawer.setOnItemClickListener(new DrawerItem.OnItemClickListener() {
            @Override
            public void onClick(DrawerItem drawerItem, int i, int position) {
                selectFragment(position);
            }
        });

        selectFragment(position);

        regID = registerGCM();
        new SendRegID().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return drawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    public void selectFragment(int position) {

        Fragment fragment = null;
        Bundle bundle;
        bundle = new Bundle();
        if(position == 0) {
            fragment = new JHCheckRoom();
        }else if(position == 1){
            bundle.putString("myID" , myID);
            bundle.putString("myName" , myName);
            bundle.putString("myPermission" , myPermission);
            bundle.putString("myTel" , myTel);
            bundle.putString("myEmail" , myEmail);
            fragment = new JHEditProfile();
            fragment.setArguments(bundle);
        }else if(position == 2){
            fragment = new JHChangePassword();
        }else if(position == 3){
            bundle.putString("myID" , myID);
            bundle.putString("Permission" , permission);
            bundle.putString("mCommand" , "Insert");
            fragment = new JHResvRoom();
            fragment.setArguments(bundle);
        }else if(position == 4){
            bundle.putString("myID" , myID);
            bundle.putString("Permission" , permission);
            fragment = new JHResvHistory();
            fragment.setArguments(bundle);
        }else if(position == 6 && !permission.equals("3")){
            Intent i = new Intent(getApplicationContext(),JHSetting.class);
            startActivity(i);
        }else if(position == 7 && !permission.equals("3")) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
        }else {
            if(position == 5) {
                bundle.putString("myID", myID);
                fragment = new JHNotification();
                fragment.setArguments(bundle);
            }else if(position == 7) {
                Intent i = new Intent(getApplicationContext(),JHSetting.class);
                startActivity(i);
            }else if(position == 8) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        }

        if(fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.drawer,fragment).commit();

            drawer.closeDrawer();
        }
    }

    public void editResvRoomFragment(String TranID, String UserID, String isEdit,
                                     String stTime, String enTime, String rName, String permission, String rDate, String rID) {

        Bundle bundle = new Bundle();
        bundle.putString("mTranID" , TranID);
        bundle.putString("myID" , UserID);
        bundle.putString("stTime" , stTime);
        bundle.putString("enTime" , enTime);
        bundle.putString("rName" , rName);
        bundle.putString("mCommand" , isEdit);
        bundle.putString("Permission" , permission);
        bundle.putString("resvDate" , rDate);
        bundle.putString("rID", rID);

        Fragment fragment = new JHResvRoom();
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.drawer , fragment).commit();

        drawer.closeDrawer();
    }

    public void showResvHistory(String mUserID, String permission) {
        Bundle bundle = new Bundle();

        bundle.putString("myID" , mUserID);
        bundle.putString("Permission" , permission);
        Fragment fragment = new JHResvHistory();
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.drawer , fragment).commit();

        drawer.closeDrawer();
    }

    public void showCheckRoom(String roomID, String resvDate) {
        Intent i = new Intent(getApplicationContext(),JHShowCheckRoom.class);
        i.putExtra("EXTRA_roomID",roomID);
        i.putExtra("EXTRA_resvDate", resvDate);
        startActivity(i);
    }

    public void showNotification(String staffID) {
        Bundle bundle = new Bundle();

        bundle.putString("myID" , staffID);
        Fragment fragment = new JHNotification();
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.drawer , fragment).commit();

        drawer.closeDrawer();
    }

    public void changeRoomByStaff(String TranID, String resvDate, String stTime, String enTime, String userID,
                                  String roomName, String mCommand, String roomID, String staffID) {
        Bundle bundle = new Bundle();
        bundle.putString("myID", userID);
        bundle.putString("mTranID", TranID);
        bundle.putString("resvDate", resvDate);
        bundle.putString("stTime", stTime);
        bundle.putString("enTime", enTime);
        bundle.putString("rName", roomName);
        bundle.putString("mCommand", mCommand);
        bundle.putString("rID", roomID);
        bundle.putString("staffID",staffID);
        Fragment fragment = new JHResvRoom();
        fragment.setArguments(bundle);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.drawer, fragment).commit();

        drawer.closeDrawer();
    }

    public String registerGCM() {
        gcm = GoogleCloudMessaging.getInstance(this);
        regID = getRegistrationID();
        if(regID.isEmpty()) {
            new GcmRegistration().execute();
        }
        return regID;
    }

    public String getRegistrationID() {
        String registrationID;
        final SharedPreferences prefs = getSharedPreferences("GCMRegID", Context.MODE_PRIVATE);
        registrationID = prefs.getString(REG_ID,"");
        return registrationID;
    }

    public class GcmRegistration extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... params) {
            String msg;
            try{
                if(gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                }
                regID = gcm.register(JHConfig.GOOGLE_PROJECT_ID);
                msg = "Device registerd, regID = " +regID;
                storeRegistrationID(regID);
            } catch (IOException e) {
                msg = e.getMessage();
            }
            return msg;
        }
    }

    private void storeRegistrationID(String regID) {
        final SharedPreferences prefs = getSharedPreferences("GCMRegID", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(REG_ID, regID);
        editor.apply();
    }

    public class SendRegID extends AsyncTask<Void,Void,String> {

        @Override
        protected String doInBackground(Void... urls) {
            StringBuilder str = new StringBuilder();
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(JHConfig.SAVE_REGID_URL);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("regID" , regID));
            params.add(new BasicNameValuePair("userID" , myID));

            try {
                httpPost.setEntity(new UrlEncodedFormEntity(params));
                HttpResponse response = client.execute(httpPost);
                StatusLine statusLine = response.getStatusLine();
                int statusCode = statusLine.getStatusCode();
                Log.e("statusCode_MainDrawer",String.valueOf(statusCode));
                if (statusCode == 200) {
                    HttpEntity entity = response.getEntity();
                    InputStream content = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        str.append(line);
                    }
                }else {
                    Log.e("Log","Failed to download result..");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str.toString();
        }
    }
}
