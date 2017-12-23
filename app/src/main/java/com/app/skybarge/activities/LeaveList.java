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
import com.app.skybarge.aynctask.CommonAsyncTaskHashmap;
import com.app.skybarge.interfaces.ApiResponse;
import com.app.skybarge.interfaces.ConnectionDetector;
import com.app.skybarge.interfaces.JsonApiHelper;
import com.app.skybarge.interfaces.OnCustomItemClicListener;
import com.app.skybarge.models.ModelStudent;
import com.app.skybarge.utils.AppConstant;
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
        showDeleteConfirmation(position);
    }

    private void deleteLeave(int position) {
        if (AppUtils.isNetworkAvailable(context)) {
            try {
                HashMap<String, String> hm = new HashMap<>();

                hm.put("id", arrayList.get(position).getId());

                String url = JsonApiHelper.BASEURL + JsonApiHelper.delete_leave;
                new CommonAsyncTask(5, context, this).getqueryJson(url, hm, Request.Method.POST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }


    private void showDeleteConfirmation(final int position) {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                context);

        alertDialog.setTitle("DELETE !");

        alertDialog.setMessage("Are you sure you want to Delete this Leave Request?");

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                            deleteLeave(position);

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
                    if (arrayList.size() > 0) {
                    } else {
                        Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();

                    }
                } else {

                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } else if (method == 5) {
                if (response.getString("status").equalsIgnoreCase("1")) {

                    arrayList.remove(deletePosition);
                    adapterLeaveList.notifyDataSetChanged();
                    Toast.makeText(context, response.getString("message"), Toast.LENGTH_SHORT).show();
                }
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
