package com.app.skybarge.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.skybarge.R;
import com.app.skybarge.aynctask.CommonAsyncTask;
import com.app.skybarge.interfaces.ApiResponse;
import com.app.skybarge.interfaces.JsonApiHelper;
import com.app.skybarge.interfaces.SwipeButtonCustomItems;
import com.app.skybarge.utils.AppUtils;
import com.app.skybarge.utils.GPSTracker;
import com.app.skybarge.utils.SwipeButton;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class DashboardHome extends AppCompatActivity implements ApiResponse, DatePickerDialog.OnDateSetListener {

    private Activity context;
    private DrawerLayout drawer;
    private View main_view;
    private Button btn_need_leave;
    private SwipeButton swipeButton;
    private TextView mTvHome, mTvLeaveDays, mTvchangepassword, mTvAttendanceDays, mTvyesterday_punched, mTvCredit_amount, mTvtodayPunchedTime, mTvProfile, mTvCalendar, mTvHolidayList, mTvNewLeaves, mTvLeavePolicy, mTvGm;
    private String TAG = DashboardHome.class.getSimpleName();
    private int PERMISSION_ALL = 1;
    private String[] PERMISSIONS = {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.CAMERA,
    };
    String latitude = "0.0", longitude = "0.0";
    private String formatted_address = "";
    ArrayAdapter<String> adapterLeaveTypes;
    ArrayList<String> leaveList = new ArrayList<>();
    ArrayList<String> leaveListId = new ArrayList<>();
    private Spinner spinner_leave;
    private RelativeLayout mRlSwipePunchin;
    private TextView mTvFromDate, mTvToDate, mTvtype_of_leave, mTvSwipe, mTvLogout;
    private static DashboardHome mInstance;
    private Button btn_swipe;
    private SwitchCompat punchin_switch;

    /***********************************************
     * Function Name : getInstance
     * Description : This function will return the instance of this activity.
     *
     * @return
     */
    public static DashboardHome getInstance() {
        if (mInstance == null)
            mInstance = new DashboardHome();
        return mInstance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_home);
        context = this;
        mInstance = DashboardHome.this;
        init();
        setListner();
        getLeaveType();
        getattendancedata();
    }

    private void setListner() {
        punchin_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkGps();
            }
        });
     /*   punchin_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });*/

        btn_swipe.setOnTouchListener(new OnSwipeTouchListener(context) {
            public void onSwipeTop() {
                //      Toast.makeText(context, "top", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeRight() {
                animImage(context);
                checkGps();
                //   Toast.makeText(context, "right", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeLeft() {
                //   Toast.makeText(context, "left", Toast.LENGTH_SHORT).show();
            }

            public void onSwipeBottom() {
                //   Toast.makeText(context, "bottom", Toast.LENGTH_SHORT).show();
            }

        });

        mTvHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        mTvCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CalendarActivity.class);
                startActivity(intent);
            }
        });
        mTvHolidayList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HolidayList.class);
                startActivity(intent);
            }
        });
        mTvNewLeaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LeaveList.class);
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
        mTvchangepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChangePassword.class);
                startActivity(intent);
            }
        }); mTvLeavePolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StockLIst.class);
                startActivity(intent);
            }
        });
        mTvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLogoutBox();

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
        if (timeOfDay >= 0 && timeOfDay < 12) {
            mTvGm.setText("Good Morning");
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            mTvGm.setText("Good afternoon");
        } else if (timeOfDay >= 16 && timeOfDay < 21) {
            mTvGm.setText("Good evening");
        } else if (timeOfDay >= 21) {
            mTvGm.setText("Good night");
        }

        SwipeButtonCustomItems swipeButtonSettings = new SwipeButtonCustomItems() {
            @Override
            public void onSwipeConfirm() {
                Log.d("NEW_STUFF", "New swipe confirm callback");
                checkGps();
            }
        };
        if (AppUtils.getPunchInId(context).equalsIgnoreCase("")) {
            swipeButtonSettings
                    .setGradientColor1(android.R.color.transparent)
                    .setGradientColor2(android.R.color.transparent)
                    .setGradientColor2Width(60)
                    .setGradientColor3(android.R.color.transparent)
                    .setPostConfirmationColor(R.drawable.swipe_out_bg)
                    .setActionConfirmDistanceFraction(0.6)

                    .setActionConfirmText(getResources().getString(R.string.swipe_to_punch_out));
        } else {
            swipeButtonSettings
                    .setGradientColor1(android.R.color.transparent)
                    .setGradientColor2(android.R.color.transparent)
                    .setGradientColor2Width(60)
                    .setGradientColor3(android.R.color.transparent)
                    .setPostConfirmationColor(R.drawable.swipbg)
                    .setActionConfirmDistanceFraction(0.6)
                    .setActionConfirmText(getResources().getString(R.string.swipe_to_punch_in));

        }

        if (swipeButton != null) {
            swipeButton.setSwipeButtonCustomItems(swipeButtonSettings);
        }
    }

    private void removeSwipeColors() {
        SwipeButtonCustomItems swipeButtonSettings = new SwipeButtonCustomItems() {
            @Override
            public void onSwipeConfirm() {

            }
        };
        if (AppUtils.getPunchInId(context).equalsIgnoreCase("")) {
            swipeButtonSettings
                    .setGradientColor1(android.R.color.transparent)
                    .setGradientColor2(android.R.color.transparent)
                    .setGradientColor2Width(60)
                    .setGradientColor3(android.R.color.transparent)
                    .setPostConfirmationColor(R.drawable.swipe_out_bg)
                    .setActionConfirmDistanceFraction(0.6)
                    .setActionConfirmText(getResources().getString(R.string.swipe_to_punch_out));
        } else {
            swipeButtonSettings
                    .setGradientColor1(android.R.color.transparent)
                    .setGradientColor2(android.R.color.transparent)
                    .setGradientColor2Width(60)
                    .setGradientColor3(android.R.color.transparent)
                    .setPostConfirmationColor(R.drawable.swipbg)
                    .setActionConfirmDistanceFraction(0.6)
                    .setActionConfirmText(getResources().getString(R.string.swipe_to_punch_in));

        }

        if (swipeButton != null) {
            swipeButton.setSwipeButtonCustomItems(swipeButtonSettings);
        }

    }

    /**
     * Animate the swipe rightto left
     *
     * @param context context of the activity or fragment
     */
    private void animImage(final Context context) {
        // Load the animation like this
        final Animation animRightToLeft = AnimationUtils.loadAnimation(context, R.anim.slide_right);
        btn_swipe.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        // Start the animation like this
        btn_swipe.startAnimation(animRightToLeft);
    }

    public void checkGps() {
        GPSTracker gps = new GPSTracker(context);
        if (gps.isGPSEnabled) {
            latitude = gps.getLatitude() + "";
            longitude = gps.getLongitude() + "";
            if (formatted_address.equalsIgnoreCase("")) {
                setCurrentLocation();
            }
            Log.e("punchinId", "**" + AppUtils.getPunchInId(context));
            if (AppUtils.getPunchInId(context).equalsIgnoreCase("")) {
                punchIn();
            } else {
                punchOut();
            }

        } else {
            showSettingsAlert();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        GPSTracker gps = new GPSTracker(context);
        if (gps.isGPSEnabled) {
            latitude = gps.getLatitude() + "";
            longitude = gps.getLongitude() + "";
            setCurrentLocation();
        } else {
            showSettingsAlert();
        }
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    public void showSettingsAlert() {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog
                    .setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(intent);
                        }
                    });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

            // Showing Alert Message
            alertDialog.show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * Function to show settings alert dialog On pressing Settings button will
     * lauch Settings Options
     */
    public void showSuccessMessageDialog(String title, String message) {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

            // Setting Dialog Title
            alertDialog.setTitle(title);

            // Setting Dialog Message
            alertDialog
                    .setMessage(message);

            // On pressing Settings button
            alertDialog.setPositiveButton("Ok",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            // Showing Alert Message
            alertDialog.show();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void getattendancedata() {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        String date1 = dateFormat.format(calendar.getTime());
        if (AppUtils.isNetworkAvailable(context)) {
            try {
                HashMap<String, String> hm = new HashMap<>();
                hm.put("user_id", AppUtils.getUserId(context));
                hm.put("date", date1);
                hm.put("auth_key", AppUtils.getAuthKey(context));

                // http://dev.stackmindz.com/sky/api/leave-type
                String url = JsonApiHelper.BASEURL + JsonApiHelper.ATTENDANCEDETAILS;
                new CommonAsyncTask(4, context, this).getqueryJsonNoProgress(url, hm, Request.Method.POST);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }

    private void getLeaveType() {

        if (AppUtils.isNetworkAvailable(context)) {
            try {
                HashMap<String, String> hm = new HashMap<>();

                // http://dev.stackmindz.com/sky/api/leave-type
                String url = JsonApiHelper.BASEURL + JsonApiHelper.LEAVE_TYPE;
                new CommonAsyncTask(3, context, this).getqueryJsonNoProgress(url, hm, Request.Method.GET);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }

    private void punchOut() {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        String date1 = dateFormat.format(calendar.getTime());

        DateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
        String seleted_time = timeFormat.format(calendar.getTime());

        if (AppUtils.isNetworkAvailable(context)) {
            try {
                HashMap<String, String> hm = new HashMap<>();
                // id, user_id,out_time,latitude,longitude,location
                hm.put("user_id", AppUtils.getUserId(context));
                hm.put("out_time", seleted_time);
                hm.put("id", AppUtils.getPunchInId(context));
                hm.put("latitude", latitude);
                hm.put("longitude", longitude);
                hm.put("location", formatted_address);
                //  http://dev.stackmindz.com/sky/api/attendancepunchout
                String url = JsonApiHelper.BASEURL + JsonApiHelper.ATTANDANCE_PUNCHOUT;
                new CommonAsyncTask(5, context, this).getqueryJson(url, hm, Request.Method.POST);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }


    private void punchIn() {
        Calendar calendar = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        String date1 = dateFormat.format(calendar.getTime());

        DateFormat timeFormat = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
        String seleted_time = timeFormat.format(calendar.getTime());

        if (AppUtils.isNetworkAvailable(context)) {
            try {
                HashMap<String, String> hm = new HashMap<>();
                // user_id, in_time, atten_date,latitude,longitude,location
                hm.put("user_id", AppUtils.getUserId(context));
                hm.put("in_time", seleted_time);
                hm.put("atten_date", date1);
                hm.put("latitude", latitude);
                hm.put("longitude", longitude);
                hm.put("location", formatted_address);
                //  http://dev.stackmindz.com/sky/api/attendancepunchin
                String url = JsonApiHelper.BASEURL + JsonApiHelper.ATTANDANCE_PUNCHIN;
                new CommonAsyncTask(2, context, this).getqueryJson(url, hm, Request.Method.POST);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(context, getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    private void init() {
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
        mTvGm = (TextView) findViewById(R.id.mTvGoodMorning);
        mTvHome = (TextView) findViewById(R.id.mTvHome);
        mTvProfile = (TextView) findViewById(R.id.mTvProfile);
        mTvchangepassword = (TextView) findViewById(R.id.mTvchangepassword);
        mTvCalendar = (TextView) findViewById(R.id.mTvCalendar);
        mTvHolidayList = (TextView) findViewById(R.id.mTvHolidayList);

        mTvLogout = (TextView) findViewById(R.id.mTvLogout);
        mTvNewLeaves = (TextView) findViewById(R.id.mTvNewLeaves);
        mTvLeavePolicy = (TextView) findViewById(R.id.mTvLeavePolicy);
        mTvSwipe = (TextView) findViewById(R.id.mTvSwipe);
        mRlSwipePunchin = (RelativeLayout) findViewById(R.id.mRlSwipePunchin);
        btn_need_leave = (Button) findViewById(R.id.btn_need_leave);
        swipeButton = (SwipeButton) findViewById(R.id.swipeBtn);
        btn_swipe = (Button) findViewById(R.id.btn_swipe);

        mTvtodayPunchedTime = (TextView) findViewById(R.id.mTvtodayPunchedTime);
        mTvCredit_amount = (TextView) findViewById(R.id.mTvCredit_amount);
        mTvyesterday_punched = (TextView) findViewById(R.id.mTvYesterdayPunchedTime);
        mTvLeaveDays = (TextView) findViewById(R.id.mTvLeaveDays);
        mTvAttendanceDays = (TextView) findViewById(R.id.mTvAttendanceDays);


        punchin_switch = (SwitchCompat) findViewById(R.id.punchin_switch);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.left_menu);
        toolbar.setNavigationIcon(drawable);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (AppUtils.getPunchInId(context).equalsIgnoreCase("")) {
            //     mRlSwipePunchin.setBackgroundResource(R.drawable.swipe_out_bg);
            punchin_switch.setChecked(false);
            mTvSwipe.setText(getResources().getString(R.string.swipe_to_punch_in));
        } else {
            punchin_switch.setChecked(true);
            mTvSwipe.setText(getResources().getString(R.string.swipe_to_punch_out));
            //   mRlSwipePunchin.setBackgroundResource(R.drawable.swipbg);
        }

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

    private void showLogoutBox() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                DashboardHome.this);

        alertDialog.setTitle("LOG OUT !");

        alertDialog.setMessage("Are you sure you want to Logout?");

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        AppUtils.setUserId(context, "");


                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }

                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }
                });

        alertDialog.show();

    }

    /**
     * Open dialog for the apply leave
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
            mTvFromDate = (TextView) view.findViewById(R.id.mTvFromDate);
            mTvToDate = (TextView) view.findViewById(R.id.mTvToDate);
            mTvtype_of_leave = (TextView) view.findViewById(R.id.mTvtype_of_leave);
            spinner_leave = (Spinner) view.findViewById(R.id.spinner_leave);
            Button btnSubmit = (Button) view.findViewById(R.id.btn_submit);
            Button btnCancel = (Button) view.findViewById(R.id.btn_cancel);

            spinner_leave.setAdapter(adapterLeaveTypes);
            mTvtype_of_leave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spinner_leave.performClick();
                }
            });
            mTvFromDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar now = Calendar.getInstance();

                    DatePickerDialog dpd = DatePickerDialog.newInstance(DashboardHome.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.setMinDate(now);
                    dpd.show(getFragmentManager(), "fromdate");

                }
            });
            spinner_leave.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    mTvtype_of_leave.setText(spinner_leave.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });


            mTvToDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Calendar now = Calendar.getInstance();

                    DatePickerDialog dpd = DatePickerDialog.newInstance(DashboardHome.this,
                            now.get(Calendar.YEAR),
                            now.get(Calendar.MONTH),
                            now.get(Calendar.DAY_OF_MONTH)
                    );
                    dpd.setMinDate(now);
                    dpd.show(getFragmentManager(), "todate");

                }
            });

            btnSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (!mTvToDate.getText().toString().equalsIgnoreCase("") && !mEdtComment.getText().toString().equalsIgnoreCase("") && !mTvtype_of_leave.getText().toString().equalsIgnoreCase("") && !mTvFromDate.getText().toString().equalsIgnoreCase("")) {
                        applyLeave(mEdtComment.getText().toString());
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "Please fill all details", Toast.LENGTH_SHORT).show();
                    }

                }
            });
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

    public void applyLeave(String remark) {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, String> hm = new HashMap<>();
            // user_id,leave_type_id,leave_date_from,leave_date_to, latitude,longitude,location,remark
            hm.put("user_id", AppUtils.getUserId(context));
            hm.put("leave_type_id", leaveListId.get(spinner_leave.getSelectedItemPosition()));
            hm.put("leave_date_from", mTvFromDate.getText().toString());
            hm.put("leave_date_to", mTvToDate.getText().toString());
            hm.put("latitude", latitude);
            hm.put("longitude", longitude);
            hm.put("location", formatted_address);
            hm.put("remark", remark);
            //  http://dev.stackmindz.com/sky/api/apply-leave
            String url = JsonApiHelper.BASEURL + JsonApiHelper.APPLY_LEAVE;
            new CommonAsyncTask(1, context, this).getqueryJson(url, hm, Request.Method.POST);


        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onPostSuccess(int method, JSONObject response) {
        try {
            if (method == 3) {
                if (response.getString("status").equalsIgnoreCase("1")) {
                    JSONObject leaveType = response.getJSONObject("LeaveType");
                    JSONObject data = leaveType.getJSONObject("data");
                    JSONArray array = data.getJSONArray("leaveType");
                    leaveList.clear();
                    leaveListId.clear();
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject jo = array.getJSONObject(i);
                        leaveListId.add(jo.getString("id"));
                        leaveList.add(jo.getString("name"));
                    }
                    adapterLeaveTypes = new ArrayAdapter<String>(context, R.layout.row_spinner, R.id.textview, leaveList);
                }
            } else if (method == 2) {
                if (response.getString("status").equalsIgnoreCase("1")) {
                    //    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                    JSONObject data = response.getJSONObject("data");

                    AppUtils.setPunchInId(context, data.getString("punchIn_id"));
                    //   mRlSwipePunchin.setBackgroundResource(R.drawable.swipe_out_bg);
                    mTvSwipe.setText(getResources().getString(R.string.swipe_to_punch_out));
                    showSuccessMessageDialog("Thank You", "Punch In Sucessfully");
                   /* "punchIn_id":"64",
                            "punchIn_status":1,
                            "yesterday_in_time":"",
                            "yesterday_out_time":"",
                            "present_days":3,
                            "leave_days":0,
                            "salary":"0"*/
                    getattendancedata();
                } else {
                    showSuccessMessageDialog("Warning", "You can Punch In and Punch out only once a day.");
                    // Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                    if (punchin_switch.isChecked()) {
                        punchin_switch.setChecked(false);
                    } else {
                        punchin_switch.setChecked(true);
                    }
                }
            } else if (method == 4) {
                if (response.getString("status").equalsIgnoreCase("1")) {
                    JSONObject data = response.getJSONObject("data");
                    mTvLeaveDays.setText(data.getString("leave_days"));
                    mTvAttendanceDays.setText(data.getString("present_days"));
                    mTvCredit_amount.setText(data.getString("salary"));
                    if (data.getString("today_punch_out").equalsIgnoreCase("")) {
                        mTvtodayPunchedTime.setText(data.getString("today_punch_in"));
                    } else {
                        mTvtodayPunchedTime.setText(data.getString("today_punch_in") + " - " + data.getString("today_punch_out"));
                    }
                    if (data.getString("yesterday_out_time").equalsIgnoreCase("")) {
                        mTvyesterday_punched.setText(data.getString("yesterday_in_time"));
                    } else {
                        mTvyesterday_punched.setText(data.getString("yesterday_in_time") + " - " + data.getString("yesterday_out_time"));
                    }

                } else {
                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                }
            } else if (method == 5) {
                if (response.getString("status").equalsIgnoreCase("1")) {
                    //  Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                    JSONObject data = response.getJSONObject("data");
                    AppUtils.setPunchInId(context, "");
                    //   mRlSwipePunchin.setBackgroundResource(R.drawable.swipbg);
                    mTvSwipe.setText(getResources().getString(R.string.swipe_to_punch_in));
                    showSuccessMessageDialog("Thank You", "Punch Out Sucessfully");
                    //    AppUtils.setData(context, data.getString("punchIn_id"), AppConstant.PUNCHOUT_ID);
                    getattendancedata();
                } else {
                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                    if (punchin_switch.isChecked()) {
                        punchin_switch.setChecked(false);
                    } else {
                        punchin_switch.setChecked(true);
                    }
                }
            } else if (method == 1) {
                if (response.getString("status").equalsIgnoreCase("1")) {
                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPostFail(int method, String response) {

    }

    private void setCurrentLocation() {

        // TODO Auto-generated method stub
        GPSTracker gps = new GPSTracker(context);
        if (gps.canGetLocation) {
            latitude = "" + gps.getLatitude();
            longitude = "" + gps.getLongitude();

            GetAddressFromURLTask1 task1 = new GetAddressFromURLTask1();
            task1.execute(new String[]{latitude, longitude});

        } else {
            /*Toast.makeText(context, "Could not found lat long",
                    Toast.LENGTH_LONG).show();*/
        }

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        int month = monthOfYear + 1;
        String date = dayOfMonth + "-" + month + "-" + year;
        if (view.getTag().equalsIgnoreCase("todate")) {
            mTvToDate.setText(date);
        } else {
            mTvFromDate.setText(date);
        }
    }

    private class GetAddressFromURLTask1 extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            super.onPreExecute();

        }

        protected String doInBackground(String... urls) {

            String response = "";
            HttpResponse response2 = null;
            StringBuilder stringBuilder = new StringBuilder();

            try {

                HttpGet httpGet = new HttpGet("https://maps.google.com/maps/api/geocode/json?latlng=" + urls[0] + "," + urls[1] + "&ln=en");
                HttpClient client = new DefaultHttpClient();
                Log.e("Url ", "http://maps.google.com/maps/api/geocode/json?ln=en&latlng=" + urls[0] + "," + urls[1]);
                try {
                    response2 = client.execute(httpGet);

                    HttpEntity entity = response2.getEntity();

                    char[] buffer = new char[2048];
                    Reader reader = new InputStreamReader(entity.getContent(), "UTF-8");

                    while (true) {
                        int n = reader.read(buffer);
                        if (n < 0) {
                            break;
                        }
                        stringBuilder.append(buffer, 0, n);
                    }

                    Log.e("Url response1", stringBuilder.toString());

                } catch (ClientProtocolException e) {
                } catch (IOException e) {
                }

                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject = new JSONObject(stringBuilder.toString());

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            } catch (Exception e) {

                e.printStackTrace();
                Log.e("Error 2 :>>", "error in doINBackground OUTER");
                //infowindow.setText("Error in connecting to Google Server... try again later");
            }
            return stringBuilder.toString();
            //return jsonObject;
        }


        protected void onPostExecute(String result) {

            try {
                if (result != null) {
                    //result=	Html.fromHtml(result).toString();
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray resultsObject = jsonObject.getJSONArray("results");
                    JSONObject formattedAddress = (JSONObject) resultsObject.get(0);
                    formatted_address = formattedAddress.getString("formatted_address");

                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }


    }

}
