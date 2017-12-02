package com.app.skybarge.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.skybarge.R;
import com.app.skybarge.aynctask.CommonAsyncTask;
import com.app.skybarge.decorators.EventDecorator;
import com.app.skybarge.decorators.MySelectorDecorator;
import com.app.skybarge.iclasses.HeaderViewManager;
import com.app.skybarge.interfaces.ApiResponse;
import com.app.skybarge.interfaces.HeaderViewClickListener;
import com.app.skybarge.interfaces.JsonApiHelper;
import com.app.skybarge.models.ModelStudent;
import com.app.skybarge.utils.AppUtils;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;


public class CalendarActivity extends AppCompatActivity implements OnDateSelectedListener, ApiResponse {

    private Activity mActivity;
    private MaterialCalendarView widget;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    DateFormat YeardateFormat = new SimpleDateFormat("yyyy", Locale.ENGLISH);
    DateFormat MonthdateFormat = new SimpleDateFormat("MM", Locale.ENGLISH);
    ArrayList<ModelStudent> arrayList = new ArrayList<>();
    ArrayList<CalendarDay> presentDates = new ArrayList<>();
    ArrayList<CalendarDay> absentDates = new ArrayList<>();
    ArrayList<CalendarDay> leaveDates = new ArrayList<>();
    ArrayList<CalendarDay> holidayDates = new ArrayList<>();
    ArrayList<String> listSessionDate = new ArrayList<>();
    private RelativeLayout rl_leaves, rl_salary;
    private boolean isLeaveOpen = false, isSalryOpen = false;
    private TextView mTvLeaves, mTvlast_month_credited, mTvTotalHolidayleaves, mTvTotalleavesCount, mTvTotalUrgentleaves, mTvTotalSickleaves;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        mActivity = this;
        init();
        manageHeaderView();
        setListener();
        Calendar c = Calendar.getInstance();
        String month = MonthdateFormat.format(c.getTime());
        String year = YeardateFormat.format(c.getTime());
        getCalenderData(month, year);
    }

    private void getCalenderData(String month, String year) {
        if (AppUtils.isNetworkAvailable(mActivity)) {
            try {
                HashMap<String, String> hm = new HashMap<>();
                //  user_id, year, month
                hm.put("user_id", AppUtils.getUserId(mActivity));
                hm.put("year", year);
                hm.put("month", month);
                // http://dev.stackmindz.com/sky/api/viewcalender
                String url = JsonApiHelper.BASEURL + JsonApiHelper.VIEW_CALENDER;
                new CommonAsyncTask(1, mActivity, this).getqueryJson(url, hm, Request.Method.POST);

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(mActivity, getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }


    /*******************************************************************
     * Function name - manageHeaderView
     * Description - manage the initialization, visibility and click
     * listener of view fields on Header view
     *******************************************************************/
    private void manageHeaderView() {
        HeaderViewManager.getInstance().InitializeHeaderView(mActivity, null, manageHeaderClick());
        HeaderViewManager.getInstance().setHeading(true, getResources().getString(R.string.calendar));
        HeaderViewManager.getInstance().setLeftSideHeaderView(true, R.drawable.left_arrow);
        HeaderViewManager.getInstance().setRightSideHeaderView(false, R.drawable.search);
        HeaderViewManager.getInstance().setLogoView(false);
        HeaderViewManager.getInstance().setProgressLoader(false, false);

    }

    /*****************************************************************************
     * Function name - manageHeaderClick
     * Description - manage the click on the left and right image view of header
     *****************************************************************************/
    private HeaderViewClickListener manageHeaderClick() {
        return new HeaderViewClickListener() {
            @Override
            public void onClickOfHeaderLeftView() {
                mActivity.onBackPressed();
            }

            @Override
            public void onClickOfHeaderRightView() {
                //   Toast.makeText(mActivity, "Coming Soon", Toast.LENGTH_SHORT).show();
            }
        };
    }


    private void setListener() {
        widget.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {

                String month = MonthdateFormat.format(date.getDate());
                String year = YeardateFormat.format(date.getDate());
                getCalenderData(month, year);
            }
        });
        mTvLeaves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLeaveOpen) {
                    rl_leaves.setVisibility(View.GONE);
                    isLeaveOpen = false;
                } else {
                    rl_leaves.setVisibility(View.VISIBLE);
                    isLeaveOpen = true;
                }
            }
        });
        mTvlast_month_credited.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSalryOpen) {
                    rl_salary.setVisibility(View.GONE);
                    isSalryOpen = false;
                } else {
                    rl_salary.setVisibility(View.VISIBLE);
                    isSalryOpen = true;
                }
            }
        });

    }


    private void init() {
        widget = (MaterialCalendarView) findViewById(R.id.calendarView);
        widget.setOnDateChangedListener(this);
        Calendar calendar = Calendar.getInstance();
        widget.setSelectedDate(calendar.getTime());
        widget.addDecorator(new MySelectorDecorator(mActivity));
        rl_leaves = (RelativeLayout) findViewById(R.id.rl_leaves);
        rl_salary = (RelativeLayout) findViewById(R.id.rl_salary);
        mTvLeaves = (TextView) findViewById(R.id.mTvLeaves);
        mTvTotalHolidayleaves = (TextView) findViewById(R.id.mTvTotalHolidayleaves);
        mTvTotalleavesCount = (TextView) findViewById(R.id.mTvTotalleavesCount);
        mTvTotalUrgentleaves = (TextView) findViewById(R.id.mTvTotalUrgentleaves);
        mTvTotalSickleaves = (TextView) findViewById(R.id.mTvTotalSickleaves);

        mTvlast_month_credited = (TextView) findViewById(R.id.mTvlast_month_credited);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarGradiant(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            Drawable background = activity.getResources().getDrawable(R.drawable.topbg);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
            window.setBackgroundDrawable(background);
        }
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {

    }

    @Override
    public void onPostSuccess(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("status").equalsIgnoreCase("1")) {
                    //      Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                    JSONObject data = response.getJSONObject("data");
                    JSONArray array = data.getJSONArray("calender");
                    arrayList.clear();
                    listSessionDate.clear();
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject jo = array.getJSONObject(i);
                        ModelStudent itemList = new ModelStudent();

                        itemList.setId(jo.getString("id"));
                        itemList.setRemark(jo.getString("remark"));
                        itemList.setName(jo.getString("name"));
                        itemList.setDate(jo.getString("date"));
                        listSessionDate.add(jo.getString("date"));
                        itemList.setStatus(jo.getString("status"));

                        arrayList.add(itemList);
                    }

                    mTvTotalSickleaves.setText(data.getString("presentCount"));
                    mTvTotalleavesCount.setText(data.getString("leaveCount"));
                    mTvTotalUrgentleaves.setText(data.getString("absentCount"));
                    mTvTotalHolidayleaves.setText(data.getString("holidayCount"));

                    if (arrayList.size() > 0) {
                        new ApiSimulator().executeOnExecutor(Executors.newSingleThreadExecutor());
                    }

                } else {
                    mTvTotalSickleaves.setText("0");
                    mTvTotalleavesCount.setText("0");
                    mTvTotalUrgentleaves.setText("0");
                    mTvTotalHolidayleaves.setText("0");
                    Toast.makeText(mActivity, response.getString("message"), Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPostFail(int method, String response) {

    }

    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {

            Calendar calendar = Calendar.getInstance();
            presentDates = new ArrayList<>();
            leaveDates = new ArrayList<>();
            absentDates = new ArrayList<>();
            holidayDates = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                /*1=>Present
                2=>leave
                3=>absent
                4=>holiday  */
                CalendarDay day = CalendarDay.from(fromDateToCalendar(arrayList.get(i).getDate()));
                if (arrayList.get(i).getStatus().equalsIgnoreCase("1")) {
                    presentDates.add(day);
                } else if (arrayList.get(i).getStatus().equalsIgnoreCase("2")) {
                    leaveDates.add(day);
                } else if (arrayList.get(i).getStatus().equalsIgnoreCase("3")) {
                    absentDates.add(day);
                } else if (arrayList.get(i).getStatus().equalsIgnoreCase("4")) {
                    holidayDates.add(day);
                }
            }
            return presentDates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);
            Log.e("presentDates", ": " + presentDates.size());
            widget.addDecorator(new EventDecorator(getResources().getColor(R.color.green_color), presentDates));
            widget.addDecorator(new EventDecorator(getResources().getColor(R.color.blue_color), holidayDates));
            widget.addDecorator(new EventDecorator(getResources().getColor(R.color.yellow_color), leaveDates));
            widget.addDecorator(new EventDecorator(getResources().getColor(R.color.red_color), absentDates));

        }
    }

    private Calendar fromDateToCalendar(String date) {
        Calendar cal = Calendar.getInstance();
        try {
            DateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date date1 = format.parse(date);
            cal.setTime(date1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return cal;
    }


}
