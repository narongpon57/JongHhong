package com.example.narongpon.jonghhong;


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


public class JHCheckRoom extends ActionBarActivity{

    private Toolbar toolbar;
    private DrawerFrameLayout drawer;
    private ActionBarDrawerToggle drawerToggle;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jh_checkroom);

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

        drawer.addItem(new DrawerItem()
                .setTextPrimary("CheckRoom"));

        drawer.addItem(new DrawerItem()
                .setTextPrimary("EditProfile"));

        drawer.addItem(new DrawerItem()
                .setTextPrimary("ResvHistory"));
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

}

