package com.app.skybarge.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.app.skybarge.R;

public class DashboardHome extends AppCompatActivity {

    private Activity context;
    private DrawerLayout drawer;
    private View main_view;
    private TextView mTvHome, mTvProfile, mTvCalendar, mTvHolidayList, mTvNewLeaves, mTvLeavePolicy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_home);
        context = this;
        init();
        setListner();
    }

    private void setListner() {

        mTvCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CalendarActivity.class);
                startActivity(intent);
            }
        });
    }

    private void init() {
        mTvHome = (TextView) findViewById(R.id.mTvHome);
        mTvProfile = (TextView) findViewById(R.id.mTvProfile);
        mTvCalendar = (TextView) findViewById(R.id.mTvCalendar);
        mTvHolidayList = (TextView) findViewById(R.id.mTvHolidayList);
        mTvNewLeaves = (TextView) findViewById(R.id.mTvNewLeaves);
        mTvLeavePolicy = (TextView) findViewById(R.id.mTvLeavePolicy);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.left_menu);
        toolbar.setNavigationIcon(drawable);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    /*
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();*/
        main_view = findViewById(R.id.main_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                main_view.setTranslationX(slideOffset * drawerView.getWidth());
                drawer.bringChildToFront(drawerView);
                drawer.requestLayout();
                //below line used to remove shadow of drawer
                drawer.setScrimColor(Color.TRANSPARENT);
            }//this method helps you to aside menu drawer
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


}
