package com.app.skybarge.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.app.skybarge.R;
import com.app.skybarge.interfaces.SwipeButtonCustomItems;
import com.app.skybarge.utils.SwipeButton;

import java.util.Calendar;

public class DashboardHome extends AppCompatActivity {

    private Activity context;
    private DrawerLayout drawer;
    private View main_view;
    private Button btn_need_leave;
    private SwipeButton swipeButton;
    private TextView mTvHome, mTvProfile, mTvCalendar, mTvHolidayList, mTvNewLeaves, mTvLeavePolicy,mTvGm;
    private String TAG = DashboardHome.class.getSimpleName();


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
        mTvProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfile.class);
                startActivity(intent);
            }
        });


        btn_need_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openNeedLeaveDialog();
            }
        });
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        if (timeOfDay >= 0 && timeOfDay<12){
            mTvGm.setText("Good Morning");
        }else if (timeOfDay >= 12 && timeOfDay <16 ){
            mTvGm.setText("Good afternoon");
        }else if (timeOfDay >=16 && timeOfDay <21){
            mTvGm.setText("Good evening");
        }else if (timeOfDay>=21){
            mTvGm.setText("Good night");
        }

        swipeButton.setText(getString(R.string.swipe_to_punch_in));
        SwipeButtonCustomItems swipeButtonSettings = new SwipeButtonCustomItems() {
            @Override
            public void onSwipeConfirm() {
                Log.d("NEW_STUFF", "New swipe confirm callback");
            }
        };
        swipeButtonSettings
                .setButtonPressText(getString(R.string.swipe_process))
                .setGradientColor1(0xFF00e600)
                .setGradientColor2(0xFF008000)
                .setGradientColor2Width(60)
                .setGradientColor3(0xFF00b300)
                .setPostConfirmationColor(0xFF888888)
                .setActionConfirmDistanceFraction(0.7)
                .setActionConfirmText(getString(R.string.swipe_punch_out));

        if (swipeButton != null){
            swipeButton.setSwipeButtonCustomItems(swipeButtonSettings);
        }

    }

    /*private void punchIn(){
        if (AppUtils.isNetworkAvailable(context)){
            try{
                HashMap<String, String> hm = new HashMap<>();
                hm.put("user_id",AppUtils.getUserId(AppConstant.user_id,null));
            }catch (Exception e){

            }

        }else{
            Toast.makeText(context, getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }*/

    private void init() {
        mTvGm = (TextView)findViewById(R.id.mTvGoodMorning);
        mTvHome = (TextView) findViewById(R.id.mTvHome);
        mTvProfile = (TextView) findViewById(R.id.mTvProfile);
        mTvCalendar = (TextView) findViewById(R.id.mTvCalendar);
        mTvHolidayList = (TextView) findViewById(R.id.mTvHolidayList);
        mTvNewLeaves = (TextView) findViewById(R.id.mTvNewLeaves);
        mTvLeavePolicy = (TextView) findViewById(R.id.mTvLeavePolicy);


        btn_need_leave = (Button) findViewById(R.id.btn_need_leave);
        swipeButton = (SwipeButton)findViewById(R.id.swipeBtn);
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


    /**
     * Open dialog for the edit comment
     */
    private void openNeedLeaveDialog() {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

            // inflate the layout dialog_layout.xml and set it as contentView
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.need_leave_dialog, null, false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(view);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            final EditText mEdtComment = (EditText) view.findViewById(R.id.mEdtComment);
            Button btnSubmit = (Button) view.findViewById(R.id.btn_submit);
            Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

/*
            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!edt_comment.getText().toString().equalsIgnoreCase("")) {
                        updateFeed(edt_comment.getText().toString(), id);
                        dialog.dismiss();
                    } else {
                        edt_comment.setError("Please enter comment");
                        edt_comment.requestFocus();
                    }

                }
            });
*/
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            if (dialog != null && !dialog.isShowing()) {
                dialog.show();
            }
        } catch (Exception e) {
            Log.e(TAG, " Exception error : " + e);
        }
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
