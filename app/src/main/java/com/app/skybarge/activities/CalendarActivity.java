package com.app.skybarge.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.app.skybarge.R;
import com.app.skybarge.decorators.MySelectorDecorator;
import com.app.skybarge.iclasses.HeaderViewManager;
import com.app.skybarge.interfaces.HeaderViewClickListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class CalendarActivity extends AppCompatActivity implements OnDateSelectedListener {

    private Activity mActivity;
    private MaterialCalendarView widget;
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        mActivity = this;
        init();
        manageHeaderView();
        setListener();
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

               /* String date1 = YearMonthdateFormat.format(date.getDate());
                attendanceList(date1);*/
            }
        });
    }

    private void init() {
        widget = (MaterialCalendarView) findViewById(R.id.calendarView);
        widget.setOnDateChangedListener(this);
        Calendar calendar = Calendar.getInstance();
        widget.setSelectedDate(calendar.getTime());
        widget.addDecorator(new MySelectorDecorator(mActivity));
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

/*
    private class ApiSimulator extends AsyncTask<Void, Void, List<CalendarDay>> {

        @Override
        protected List<CalendarDay> doInBackground(@NonNull Void... voids) {

            Calendar calendar = Calendar.getInstance();
            presentDates = new ArrayList<>();
            leaveDates = new ArrayList<>();
            absentDates = new ArrayList<>();
            holidayDates = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {

                CalendarDay day = CalendarDay.from(fromDateToCalendar(arrayList.get(i).getAttn_date()));
                if (arrayList.get(i).getAttn_status().equalsIgnoreCase("1")) {
                    presentDates.add(day);
                } else if (arrayList.get(i).getAttn_status().equalsIgnoreCase("2")) {
                    absentDates.add(day);
                } else if (arrayList.get(i).getAttn_status().equalsIgnoreCase("3")) {
                    leaveDates.add(day);
                } else if (arrayList.get(i).getAttn_status().equalsIgnoreCase("4")) {
                    holidayDates.add(day);
                }

            }

            return presentDates;
        }

        @Override
        protected void onPostExecute(@NonNull List<CalendarDay> calendarDays) {
            super.onPostExecute(calendarDays);

         //   widget.addDecorator(new EventDecorator(getResources().getColor(R.color.green_color), presentDates));

        }
    }
*/


}
