package com.example.narongpon.jonghhong;

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
    private Drawable checkRoom_ic, editProfile_ic, resvHistory_ic, setting_ic;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jh_drawer);

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

        Drawable background = getResources().getDrawable(R.drawable.bg);
        background.setAlpha(100);
        drawer.setProfile(new DrawerProfile()
                .setBackground(background)
                .setName("ณรงค์พล สุทธิมรรคผล")
                .setDescription("นักศึกษา"));

        checkRoom_ic = getResources().getDrawable(R.drawable.calendar_ic);
        drawer.addItem(new DrawerItem()
                .setImage(checkRoom_ic)
                .setTextPrimary("CheckRoom"));

        editProfile_ic = getResources().getDrawable(R.drawable.user_ic);
        drawer.addItem(new DrawerItem()
                .setImage(editProfile_ic)
                .setTextPrimary("EditProfile"));

        resvHistory_ic = getResources().getDrawable(R.drawable.history_ic);
        drawer.addItem(new DrawerItem()
                .setImage(resvHistory_ic)
                .setTextPrimary("ResvHistory"));

        drawer.addDivider();

        setting_ic = getResources().getDrawable(R.drawable.settings_ic);
        drawer.addItem(new DrawerItem()
                .setImage(setting_ic)
                .setTextPrimary("Setting"));


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
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
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
        if(position == 0) {
            fragment = new JHCheckRoom();
        }

        if(fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.drawer,fragment).commit();

            drawer.closeDrawer();
        }
    }

}
