package com.app.skybarge.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.app.skybarge.adapters.AdapterStockList;
import com.app.skybarge.aynctask.CommonAsyncTask;
import com.app.skybarge.interfaces.ApiResponse;
import com.app.skybarge.interfaces.ConnectionDetector;
import com.app.skybarge.interfaces.JsonApiHelper;
import com.app.skybarge.interfaces.OnCustomItemClicListener;
import com.app.skybarge.models.ModelStock;
import com.app.skybarge.utils.AppUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by admin on 06-01-2016.
 */
public class StockLIst extends AppCompatActivity implements OnCustomItemClicListener, ApiResponse {

    Context context;
    RecyclerView mRecyclerView;
    ModelStock itemList;
    AdapterStockList adapterStockList;
    ArrayList<ModelStock> arrayList;
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
        stockListRefresh();
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
                stockListRefresh();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    public void stockListRefresh() {

        if (AppUtils.isNetworkAvailable(context)) {


            // http://dev.stackmindz.com/sky/api/viewcalender
            String url = JsonApiHelper.BASEURL + JsonApiHelper.STOCK_LIST + "/" + AppUtils.getUserId(context);
            new CommonAsyncTask(1, context, this).getqueryJsonNoProgress(url, null, Request.Method.GET);
        } else {
            Toast.makeText(context, context.getResources().getString(R.string.message_network_problem), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onItemClickListener(int position, int flag) {

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
                        itemList = new ModelStock();

                        itemList.setId(jo.getString("id"));
                        itemList.setName(jo.getString("name"));
                        itemList.setQuantity(jo.getString("quantity"));
                        itemList.setIssue_date(jo.getString("issue_date"));
                        itemList.setRowType(1);

                        arrayList.add(itemList);
                    }
                    adapterStockList = new AdapterStockList(context, this, arrayList);
                    mRecyclerView.setAdapter(adapterStockList);
                    if (swipe_refresh != null) {
                        swipe_refresh.setRefreshing(false);
                    }
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
}
