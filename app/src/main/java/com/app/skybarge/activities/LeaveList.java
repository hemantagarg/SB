package com.app.skybarge.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.app.skybarge.R;
import com.app.skybarge.adapters.AdapterLeaveList;
import com.app.skybarge.aynctask.CommonAsyncTask;
import com.app.skybarge.interfaces.ApiResponse;
import com.app.skybarge.interfaces.ConnectionDetector;
import com.app.skybarge.interfaces.JsonApiHelper;
import com.app.skybarge.interfaces.OnCustomItemClicListener;
import com.app.skybarge.models.ModelStudent;
import com.app.skybarge.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by admin on 06-01-2016.
 */
public class LeaveList extends AppCompatActivity implements OnCustomItemClicListener, ApiResponse {

    Context context;
    RecyclerView mRecyclerView;
    ModelStudent itemList;
    AdapterLeaveList adapterLeaveList;
    ArrayList<ModelStudent> arrayList;
    ConnectionDetector cd;
    RelativeLayout rl_main_layout, rl_network;
    LinearLayoutManager layoutManager;
    Toolbar toolbar;
    private Button btn_need_leave;
    private Spinner spinner_leave;
    FloatingActionButton btn_addevent;
    private BroadcastReceiver broadcastReceiver;
    SwipeRefreshLayout swipe_refresh;
    private int deletePosition;
    private TextView mTvFromDate, mTvToDate, mTvtype_of_leave;
    ArrayAdapter<String> adapterLeaveTypes;
    ArrayList<String> leaveList = new ArrayList<>();
    ArrayList<String> leaveListId = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leavelist);

        context = this;
        init();
        setListener();
        leaveList();
    }

    private void init() {

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.package.ACTION_LOGOUT");

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("onReceive", "Logout in progress");
                //At this point you should start the login activity and finish this one
                finish();
            }
        };
        registerReceiver(broadcastReceiver, intentFilter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        cd = new ConnectionDetector(context);
        arrayList = new ArrayList<>();

        rl_main_layout = (RelativeLayout) findViewById(R.id.rl_main_layout);
        rl_network = (RelativeLayout) findViewById(R.id.rl_network);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(context);
        btn_need_leave = (Button) findViewById(R.id.btn_need_leave);
        mRecyclerView.setLayoutManager(layoutManager);
        btn_addevent = (FloatingActionButton) findViewById(R.id.btn_addevent);
        btn_addevent.setVisibility(View.GONE);
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipe_refresh.setColorSchemeColors(getResources().getColor(R.color.colorPrimary), getResources().getColor(R.color.colorPrimaryDark));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);

    }

    public void setListener() {
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                leaveListRefresh();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btn_need_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNeedLeaveDialog();
            }
        });
    }

    /**
     * Open dialog for the apply leave
     */
    private void openNeedLeaveDialog() {
/*
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

                    DatePickerDialog dpd = DatePickerDialog.newInstance(LeaveList.this,
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

                    DatePickerDialog dpd = DatePickerDialog.newInstance(LeaveList.this,
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
            Log.e("Leave List", " Exception error : " + e);
        }
*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 21 && resultCode == RESULT_OK) {

            leaveListRefresh();
        }
    }


    public void leaveList() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, String> hm = new HashMap<>();
            hm.put("user_id", AppUtils.getUserId(context));
            // http://dev.stackmindz.com/sky/api/viewcalender
            String url = JsonApiHelper.BASEURL + JsonApiHelper.LEAVE_LIST;
            new CommonAsyncTask(1, context, this).getqueryJson(url, hm, Request.Method.POST);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }

    }

    public void leaveListRefresh() {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, String> hm = new HashMap<>();
            hm.put("user_id", AppUtils.getUserId(context));
            // http://dev.stackmindz.com/sky/api/viewcalender
            String url = JsonApiHelper.BASEURL + JsonApiHelper.LEAVE_LIST;
            new CommonAsyncTask(1, context, this).getqueryJsonNoProgress(url, hm, Request.Method.POST);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onItemClickListener(int position, int flag) {

    }

/*
    public void deleteLeave(int position) {

        if (AppUtils.isNetworkAvailable(context)) {

            HashMap<String, Object> hm = new HashMap<>();
            hm.put("authkey", Constant.AUTHKEY);
            hm.put("id", arrayList.get(position).getId());

            String url = getResources().getString(R.string.base_url) + getResources().getString(R.string.delete_leave);
            new CommonAsyncTaskHashmap(2, context, this).getquery(url, hm);

        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }
*/

    private void showDeleteConfirmation(final int position) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                context);

        alertDialog.setTitle("DELETE !");

        alertDialog.setMessage("Are you sure you want to Delete this Leave Request?");

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        //    deleteLeave(position);

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


    @Override
    public void onPostSuccess(int method, JSONObject response) {
        try {
            if (method == 1) {
                if (response.getString("status").equalsIgnoreCase("1")) {

                    JSONArray array = response.getJSONArray("data");
                    arrayList.clear();
                    for (int i = 0; i < array.length(); i++) {

                        JSONObject jo = array.getJSONObject(i);
                        itemList = new ModelStudent();

                        itemList.setId(jo.getString("id"));

                        itemList.setId(jo.getString("id"));
                        itemList.setName(jo.getString("name"));
                        itemList.setStart_date(jo.getString("start_date"));
                        itemList.setRowType(1);
                        itemList.setEnd_date(jo.getString("end_date"));
                        itemList.setStatus(jo.getString("status"));
                        itemList.setIs_status(jo.getString("is_status"));
                        itemList.setRemark(jo.getString("remark"));
                        itemList.setReason(jo.getString("reason"));
                        itemList.setApply_date(jo.getString("apply_date"));

                        arrayList.add(itemList);
                    }
                    adapterLeaveList = new AdapterLeaveList(context, this, arrayList);
                    mRecyclerView.setAdapter(adapterLeaveList);
                    if (swipe_refresh != null) {
                        swipe_refresh.setRefreshing(false);
                    }

                } else {

                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }

            } else if (method == 2) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    arrayList.remove(deletePosition);
                    adapterLeaveList.notifyDataSetChanged();
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } else if (method == 4) {
                if (response.getString("response").equalsIgnoreCase("1")) {

                    leaveListRefresh();
                    Toast.makeText(context, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }
            } else if (method == 3) {
                JSONArray array = response.getJSONArray("data");
                leaveList.clear();
                leaveListId.clear();
                for (int i = 0; i < array.length(); i++) {

                    JSONObject jo = array.getJSONObject(i);
                    leaveListId.add(jo.getString("id"));
                    leaveList.add(jo.getString("label"));
                }
                adapterLeaveTypes = new ArrayAdapter<String>(context, R.layout.row_spinner, R.id.textview, leaveList);

            }
        } catch (
                Exception e)

        {
            e.printStackTrace();
        }

    }

    @Override
    public void onPostFail(int method, String response) {

    }
}
