package com.example.narongpon.jonghhong;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.heinrichreimersoftware.materialdrawer.DrawerFrameLayout;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerItem;
import com.heinrichreimersoftware.materialdrawer.structure.DrawerProfile;


public class MainDrawer extends ActionBarActivity {
    private Toolbar toolbar;
    private DrawerFrameLayout drawer;
    private ActionBarDrawerToggle drawerToggle;
    private Drawable checkRoom_ic, editProfile_ic, resvHistory_ic, setting_ic, resvRoom_ic, logout_ic;

    private String myName, myPermission, myID, myTel, myEmail, permission;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jh_drawer);

        Bundle bundle;
        bundle = getIntent().getExtras();

        if(bundle != null) {
            myName = getIntent().getExtras().getString("myName");
            myPermission = getIntent().getExtras().getString("myPermission");
            myID = getIntent().getExtras().getString("myID");
            myTel = getIntent().getExtras().getString("myTel");
            myEmail = getIntent().getExtras().getString("myEmail");
            permission = getIntent().getExtras().getString("Permission");
        }
        toolbar = (Toolbar)findViewById(R.id.toolbar);
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
        background.setAlpha(100);
        drawer.setProfile(new DrawerProfile()
                .setBackground(background)
                .setName(myName)
                .setDescription(myPermission));

        checkRoom_ic = getResources().getDrawable(R.drawable.calendar_ic);
        drawer.addItem(new DrawerItem()
                .setImage(checkRoom_ic)
                .setTextPrimary("ตรวจสอบเวลาห้องประชุม"));


        editProfile_ic = getResources().getDrawable(R.drawable.user_ic);
        drawer.addItem(new DrawerItem()
                .setImage(editProfile_ic)
                .setTextPrimary("แก้ไขข้อมูลส่วนตัว"));


        resvRoom_ic = getResources().getDrawable(R.drawable.reservation_ic);
        drawer.addItem(new DrawerItem()
                .setImage(resvRoom_ic)
                .setTextPrimary("จองห้องประชุม"));

        resvHistory_ic = getResources().getDrawable(R.drawable.history_ic);
        drawer.addItem(new DrawerItem()
                .setImage(resvHistory_ic)
                .setTextPrimary("ประวัติจองห้องประชุม"));

        drawer.addDivider();

        setting_ic = getResources().getDrawable(R.drawable.settings_ic);
        drawer.addItem(new DrawerItem()
                .setImage(setting_ic)
                .setTextPrimary("ตั้งค่า"));

        logout_ic = getResources().getDrawable(R.drawable.logout_ic);
        drawer.addItem(new DrawerItem()
                .setImage(logout_ic)
                .setTextPrimary("ออกจากระบบ"));

        drawer.setOnItemClickListener(new DrawerItem.OnItemClickListener() {
            @Override
            public void onClick(DrawerItem drawerItem, int i, int position) {
                selectFragment(position);
            }
        });

        selectFragment(0);
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
            bundle.putString("myID" , myID);
            bundle.putString("Permission" , permission);
            bundle.putString("mCommand" , "Insert");
            fragment = new JHResvRoom();
            fragment.setArguments(bundle);
        }else if(position == 3){
            bundle.putString("myID" , myID);
            bundle.putString("Permission" , permission);
            fragment = new JHResvHistory();
            fragment.setArguments(bundle);
        }else if(position == 4){

        }else if(position == 7) {
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
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

    public void showCheckRoom() {
        Intent i = new Intent(getApplicationContext(),JHShowCheckRoom.class);
        startActivity(i);
    }

}
